//
//  EventCenter.swift
//  zentao
//
//  Created by Sun Hao on 15/3/24.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

class Event {
    
    struct IdCounter {
        private static var id = 0
        static func newId() -> Int {
            return IdCounter.id++
        }
    }
    
    enum Queue {
        case Main, Background, LowPriority
    }
    
    typealias ClosureVoid = () -> Void
    typealias ClosureWithSender = (AnyObject?) -> Void
    typealias ClosureWithSenderAndUserInfo = (AnyObject?, [NSObject : AnyObject]?) -> Void
    
    private var handler: ClosureWithSenderAndUserInfo
    var times: UInt = UInt.max
    let id = IdCounter.newId()
    var queue:Queue
    var name: String?
    var targetId: UInt?
    
    var isOnce: Bool {
        get {
            return times > 0
        }
        set {
            if newValue {
                times = 1
            } else if times == 1 {
                times = UInt.max
            }
        }
    }
    
    var hasChance: Bool {
        return times > 0
    }
    
    init(queue: Queue, handler: ClosureWithSenderAndUserInfo) {
        self.queue = queue
        self.handler = handler
    }
    
    init(queue: Queue, handler: ClosureWithSender) {
        self.queue = queue
        self.handler = {
            (sender, _) in
            handler(sender)
        }
    }
    
    init(queue: Queue, handler: ClosureVoid) {
        self.queue = queue
        self.handler = {
            (_, _) in
            handler()
        }
    }
    
    convenience init(handler: ClosureWithSenderAndUserInfo) {
        self.init(queue: Queue.Main, handler)
    }
    
    convenience init(handler: ClosureWithSender) {
        self.init(queue: Queue.Main, handler)
    }
    
    convenience init(handler: ClosureVoid) {
        self.init(queue: Queue.Main, handler)
    }
    
    func execute(sender: AnyObject?, userInfo: [NSObject : AnyObject]?) -> Bool {
        if !hasChance {
            return false
        }
        
        switch queue {
        case .Main:
            handler(sender, userInfo)
        case .Background:
            dispatch_async(dispatch_get_main_queue(), {
                self.handler(sender, userInfo)
            })
        case .LowPriority:
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_LOW, 0), {
                self.handler(sender, userInfo)
            })
        }
        
        if times < UInt.max {
            times--
        }
        
        return true
    }
}

class EventCenter {
    
    private struct SingletonKeeper {
        static let instance = EventCenter()
    }
    
    class var shared: EventCenter {
        get {
            return SingletonKeeper.instance
        }
    }
    
    // events[name][target]
    private lazy var events: [String: [UInt: [Event]]] = {return [:]}()
    private let GLOBAL_TRAGET_ID = UInt.min
    private var tempTarget: AnyObject?
    private var tempName: String?
    private lazy var observers: [String: NSObjectProtocol] = {return [:]}()
    
    private init() {
    }
    
    private func handleEvents(name: String, sender: AnyObject?, userInfo: [NSObject : AnyObject]?) -> Int {
        var result = 0
        if events.has(name) {
            var eventsForRemove: [Event] = []
            for targetEvents in events[name]!.values {
                for event in targetEvents {
                    if event.execute(sender, userInfo: userInfo) {
                        result++
                    }
                    if !event.hasChance {
                        eventsForRemove.append(event)
                    }
                }
            }
            if eventsForRemove.count > 0 {
                off(eventsForRemove)
            }
        }
        Log.i("HANDLE 🌟\(result) events [\(name)] from sender '\(sender)' with userInfo=\(userInfo)")
        return result
    }
    
    func on(name: String, target: AnyObject?, event: Event) {
        let names = split(name) {$0 == " "}
        let targetId = target != nil ? ObjectIdentifier(target!).uintValue() : GLOBAL_TRAGET_ID
        let notificationCenterCallback: (NSNotification!) -> Void = {
            (notify) -> Void in
            self.handleEvents(name, sender: notify.object, userInfo: notify.userInfo)
            return
        }
        event.name = name
        event.targetId = targetId
        
        for n in names {
            if events.has(n) {
                if events[n]!.has(targetId) {
                    events[n]![targetId]?.append(event)
                } else {
                    events[n]![targetId] = [event]
                }
            } else {
                events[n] = [targetId: [event]]
            }
            
            if !observers.has(n) {
                observers[n] = NSNotificationCenter.defaultCenter().addObserverForName(n, object: nil, queue: NSOperationQueue.mainQueue(), usingBlock: notificationCenterCallback)
            }
        }
        
        Log.i("ON [\(name)], BIND on target=\(target)")
    }
    
    func clear() {
        let center = NSNotificationCenter.defaultCenter()
        for name in events.keys {
            if observers.has(name) {
                center.removeObserver(observers[name]!)
            }
        }
        observers = [:]
        events = [:]
    }
    
    private func offIfEmpty(name: String) {
        if events[name] != nil && events[name]!.isEmpty {
            off(name)
        }
    }
    
    func off(target: AnyObject) {
        let targetId = ObjectIdentifier(target).uintValue()
        for (name, _) in events {
            events[name]![targetId] = nil
            offIfEmpty(name)
        }
    }
    
    func off(name: String) {
        if observers.has(name) {
            NSNotificationCenter.defaultCenter().removeObserver(observers[name]!)
        }
        events[name] = nil
    }

    func off(target: AnyObject, name: String) {
        let targetId = ObjectIdentifier(target).uintValue()
        if events.has(name) {
            events[name]![targetId] = nil
            offIfEmpty(name)
        }
    }
    
    func off(id: Int) -> Bool {
        for (name, namedEvents) in events {
            for (targetId, namedTargetEvents) in namedEvents {
                for (index, event) in enumerate(namedTargetEvents) {
                    if event.id == id {
                        events[name]![targetId]!.removeAtIndex(index)
                        if events[name]![targetId]!.isEmpty {
                            events[name]![targetId] = nil
                        }
                        offIfEmpty(name)
                        return true
                    }
                }
            }
        }
        return false
    }
    
    func off(target: AnyObject, name: String, id: Int) -> Bool {
        let targetId = ObjectIdentifier(target).uintValue()
        if events.has(name) {
            if events[name]!.has(targetId) {
                for (index, event) in enumerate(events[name]![targetId]!) {
                    if event.id == id {
                        events[name]![targetId]!.removeAtIndex(index)
                        if events[name]![targetId]!.isEmpty {
                            events[name]![targetId] = nil
                        }
                        offIfEmpty(name)
                        return true
                    }
                }
            }
        }
        return false
    }
    
    func off(event: Event) -> Bool {
        if let targetId = event.targetId {
            if let name = event.name {
                return off(targetId, name: name, id: event.id)
            }
        }
        return off(event.id)
    }
    
    func off(eventsforRemove: [Event]) -> Int {
        var result = 0
        for event in eventsforRemove {
            if off(event) {
                result++
            }
        }
        return result
    }
    
    func trigger(name: String, sender: AnyObject? = nil, userInfo: [NSObject : AnyObject]? = nil) {
        if observers.has(name) {
            NSNotificationCenter.defaultCenter().postNotificationName(name, object: sender, userInfo: userInfo)
            Log.i("TRIGGER [\(name)] from '\(sender)' with userInfo=\(userInfo)")
        } else {
            Log.w("TRIGGER [\(name)] failed, because observer not found.")
        }
    }
    
    func trigger(name: String, sender: AnyObject?, userInfo: [NSString : AnyObject]) {
        trigger(name, sender: sender, userInfo: userInfo as [NSObject : AnyObject])
    }
    
    func trigger(name: String, sender: AnyObject?, userInfo: AnyObject) {
        trigger(name, sender: sender, userInfo: [NSString(string: "userInfo"): userInfo])
    }
    
    func bind(target: AnyObject) -> EventCenter {
        tempTarget = target
        return self
    }
    
    func unbind(target: AnyObject) {
        off(target)
    }
    
    func on(name: String) -> EventCenter {
        tempName = name
        return self
    }
    
    func on(event: Event) -> EventCenter {
        assert(tempName != nil, "Should given a name first.")
        on(tempName!, target: tempTarget, event: event)
        tempName = nil
        return self
    }
    
    func on(queue: Event.Queue, handler: Event.ClosureWithSenderAndUserInfo) -> EventCenter {
        on(Event(queue: queue, handler))
        return self
    }
    
    func on(queue: Event.Queue, handler: Event.ClosureWithSender) -> EventCenter {
        on(Event(queue: queue, handler))
        return self
    }
    
    func on(queue: Event.Queue, handler: Event.ClosureVoid) -> EventCenter {
        on(Event(queue: queue, handler))
        return self
    }
    
    func on(handler: Event.ClosureWithSenderAndUserInfo) -> EventCenter {
        on(Event(handler))
        return self
    }
    
    func on(handler: Event.ClosureWithSender) -> EventCenter {
        on(Event(handler))
        return self
    }
    
    func on(handler: Event.ClosureVoid) -> EventCenter {
        on(Event(handler))
        return self
    }
    
    func on(name: String, event: Event) -> EventCenter {
        on(name, target: tempTarget, event: event)
        return self
    }
    
    func on(name: String, queue: Event.Queue, handler: Event.ClosureWithSenderAndUserInfo) -> EventCenter {
        on(name, target: tempTarget, event: Event(queue: queue, handler))
        return self
    }
    
    func on(name: String, queue: Event.Queue, handler: Event.ClosureWithSender) -> EventCenter {
        on(name, target: tempTarget, event: Event(queue: queue, handler))
        return self
    }
    
    func on(name: String, queue: Event.Queue, handler: Event.ClosureVoid) -> EventCenter {
        on(name, target: tempTarget, event: Event(queue: queue, handler))
        return self
    }
    
    func on(name: String, handler: Event.ClosureWithSenderAndUserInfo) -> EventCenter {
        on(name, target: tempTarget, event: Event(handler))
        return self
    }
    
    func on(name: String, handler: Event.ClosureWithSender) -> EventCenter {
        on(name, target: tempTarget, event: Event(handler))
        return self
    }
    
    func on(name: String, handler: Event.ClosureVoid) -> EventCenter {
        on(name, target: tempTarget, event: Event(handler))
        return self
    }
    
    func then(name: String, sender: AnyObject? = nil, userInfo: [NSObject : AnyObject]? = nil) -> Int {
        let event = Event({
            self.trigger(name, sender: sender, userInfo: userInfo)
        })
        on(event)
        return event.id
    }
    
    func then(name: String, sender: AnyObject?, userInfo: [NSString : AnyObject]) -> Int {
        return then(name, sender: sender, userInfo: userInfo as [NSObject : AnyObject])
    }
    
    func then(name: String, sender: AnyObject?, userInfo: AnyObject) -> Int {
        return then(name, sender: sender, userInfo: [NSString(string: "userInfo"): userInfo])
    }
}

//infix operator  +=~ {}
//infix operator  +=! {}

func += (center: EventCenter, event: Event) -> Int {
    center.on(event)
    return event.id
}

func += (center: EventCenter, handler: Event.ClosureWithSenderAndUserInfo) -> Int {
    return center += Event(handler)
}

func += (center: EventCenter, handler: Event.ClosureWithSender) -> Int {
    return center += Event(handler)
}

func += (center: EventCenter, handler: Event.ClosureVoid) -> Int {
    return center += Event(handler)
}

func >> (center: EventCenter, trigger: String) -> Int {
    return center.then(trigger)
}

func >> (center: EventCenter, trigger: (name: String, sender: AnyObject)) -> Int {
    return center.then(trigger.name, sender: trigger.sender)
}

func >> (center: EventCenter, trigger: (name: String, sender: AnyObject, userInfo: AnyObject)) -> Int {
    return center.then(trigger.name, sender: trigger.sender, userInfo: trigger.userInfo)
}

func +=! (center: EventCenter, event: Event) -> Int {
    event.queue = .Background
    return center += event
}

func +=! (center: EventCenter, handler: Event.ClosureWithSenderAndUserInfo) -> Int {
    return center += Event(queue: .Background, handler)
}

func +=! (center: EventCenter, handler: Event.ClosureWithSender) -> Int {
    return center += Event(queue: .Background, handler)
}

func +=! (center: EventCenter, handler: Event.ClosureVoid) -> Int {
    return center += Event(queue: .Background, handler)
}

func +=~ (center: EventCenter, event: Event) -> Int {
    event.queue = .LowPriority
    return center += event
}

func +=~ (center: EventCenter, handler: Event.ClosureWithSenderAndUserInfo) -> Int {
    return center += Event(queue: .LowPriority, handler)
}

func +=~ (center: EventCenter, handler: Event.ClosureWithSender) -> Int {
    return center += Event(queue: .LowPriority, handler)
}

func +=~ (center: EventCenter, handler: Event.ClosureVoid) -> Int {
    return center += Event(queue: .LowPriority, handler)
}

func -= (center: EventCenter, target: AnyObject) {
    center.off(target)
}

func -= (center: EventCenter, name: String) {
    center.off(name)
}

func -= (center: EventCenter, id: Int) -> Bool {
    return center.off(id)
}

func -= (center: EventCenter, event: Event) -> Bool {
    return center.off(event)
}

func -= (center: EventCenter, eventInfo: (target: AnyObject, name: String)) {
    center.off(eventInfo.target, name: eventInfo.name)
}

func -= (center: EventCenter, eventInfo: (target: AnyObject, name: String, id: Int)) {
    center.off(eventInfo.target, name: eventInfo.name, id: eventInfo.id)
}
