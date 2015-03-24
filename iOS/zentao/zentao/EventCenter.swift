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
    
    class var sharedInstance: EventCenter {
        get {
            return SingletonKeeper.instance
        }
    }
    
    // events[name][target]
    private lazy var events: [String: [UInt: [Event]]] = {return [:]}()
    private let GLOBAL_TRAGET_ID = UInt.min
    private var tempTarget: AnyObject?
    private lazy var observers: [String: NSObjectProtocol] = {return [:]}()
    
    private init() {
    }
    
    private func handleEvents(name: String, sender: AnyObject?, userInfo: [NSObject : AnyObject]?) {
        if events.has(name) {
            var eventsForRemove: [Event] = []
            for targetEvents in events[name]!.values {
                for event in targetEvents {
                    event.execute(sender, userInfo: userInfo)
                    if !event.hasChance {
                        eventsForRemove.append(event)
                    }
                }
            }
            if eventsForRemove.count > 0 {
                off(eventsForRemove)
            }
        }
    }
    
    func on(name: String, target: AnyObject?, event: Event) {
        let targetId = target != nil ? ObjectIdentifier(target!).uintValue() : GLOBAL_TRAGET_ID
        event.targetId = targetId
        event.name = name
        if events.has(name) {
            if events[name]!.has(targetId) {
                events[name]![targetId]?.append(event)
            } else {
                events[name]![targetId] = [event]
            }
        } else {
            events[name] = [targetId: [event]]
        }
        
        if !observers.has(name) {
            observers[name] = NSNotificationCenter.defaultCenter().addObserverForName(name, object: nil, queue: NSOperationQueue.mainQueue()) { (notify) -> Void in
                self.handleEvents(name, sender: notify.object, userInfo: notify.userInfo)
            }
        }
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
    
    func off(name: String) {
        if observers.has(name) {
            NSNotificationCenter.defaultCenter().removeObserver(observers[name]!)
        }
        events[name] = nil
    }
    
    func off(target: AnyObject) {
        let targetId = ObjectIdentifier(target).uintValue()
        for (name, _) in events {
            events[name]![targetId] = nil
            offIfEmpty(name)
        }
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
        NSNotificationCenter.defaultCenter().postNotificationName(name, object: sender, userInfo: userInfo)
    }
    
    func bind(target: AnyObject) -> EventCenter {
        tempTarget = target
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
}