//
//  Task.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

@objc(Task)
class Task: Entity {
    
    enum PageTab: Int, EntityQueryType {
        case AssignedTo = 0
        case OpenedBy
        case FinishedBy
        
        var entityType: EntityType {return EntityType.Task}
        static let displayNames = ["指派给我", "由我创建", "由我完成"]
        static let names = ["assignedTo", "openedBy", "finishedBy"]
        static let values: [PageTab] = [.AssignedTo, .OpenedBy, .FinishedBy]
        static var defaultTab: EntityQueryType {
            return PageTab.AssignedTo
        }
        static var all: [EntityQueryType] {
            //            return values
            return values.map {
                (var val) -> EntityQueryType in
                return val
            }
        }
        
        var index: Int {
            return rawValue
        }
        
        var displayName: String {
            return PageTab.displayNames[rawValue]
        }
        
        var name: String {
            return PageTab.names[rawValue]
        }
        
        var prev: EntityQueryType? {
            if index == 0 {
                return nil
            }
            return PageTab.values[index - 1]
        }
        
        var next: EntityQueryType? {
            if index == (PageTab.values.count - 1) {
                return nil
            }
            return PageTab.values[index + 1]
        }
        
        func equalTypeTo(tab: EntityQueryType?) -> Bool {
            if let t = tab {
                return self.entityType == t.entityType
            }
            return false
        }
        
        func equalTo(tab: EntityQueryType?) -> Bool {
            if let t = tab {
                return self.entityType == t.entityType && self.index == t.index
            }
            return false
        }
    }
    
    /**
    * Task status enum
    */
    enum Status: Int, AccentIconProtocol, NamedEnum {
        
        case wait = 0
        case doing
        case done
        case pause
        case cancel
        case closed
        
        
        static let names = ["wait", "doing", "done", "pause", "cancel", "closed"]
        static let values = [Status.wait, .doing, .done, .pause, .cancel, .closed]
        static let displayNames = ["未开始", "进行中", "已完成", "已暂停", "已取消", "已关闭"]
        private static let accentIconMap = [
            AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.clock_o),
            AccentIcon(swatch: MaterialColor.Pink, icon: FontIcon.play),
            AccentIcon(swatch: MaterialColor.Green, icon: FontIcon.check),
            AccentIcon(swatch: MaterialColor.Orange, icon: FontIcon.pause),
            AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.ban),
            AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.dot_circle_o)
        ]
        
        static func fromName(name: String, ignoreCase: Bool = true) -> Status? {
            if ignoreCase {
                let lowerName = name.lowercaseString
                for (id, thisName) in enumerate(names) {
                    if thisName.lowercaseString == lowerName {
                        return values[id]
                    }
                }
            } else {
                for (id, thisName) in enumerate(names) {
                    if name == thisName {
                        return values[id]
                    }
                }
            }
            
            return nil
        }
        
        var swatch: MaterialColor.Swatch {
            get {
                return Status.accentIconMap[self.rawValue].swatch
            }
        }
        
        var icon: IconVal {
            get {
                return Status.accentIconMap[self.rawValue].icon
            }
        }
        
        var name: String {
            get {
                return Status.names[self.rawValue]
            }
        }
        
        var displayName: String {
            return Status.displayNames[self.rawValue]
        }
        
        var index: Int {
            get {
                return rawValue
            }
        }
    }
    
    var statusValue: Status {
        return Status.fromName(status) ?? .wait
    }
    
    override var entityType: EntityType {
        return .Task
    }

    @NSManaged var assignedTo: String
    @NSManaged var assignedDate: NSDate
    @NSManaged var canceledBy: String
    @NSManaged var canceledDate: NSDate
    @NSManaged var closedBy: String
    @NSManaged var closedDate: NSDate
    @NSManaged var closeReason: String
    @NSManaged var consumed: NSNumber
    @NSManaged var deadline: NSDate
    @NSManaged var desc: String
    @NSManaged var doc: String
    @NSManaged var estimate: NSNumber
    @NSManaged var estStarted: NSDate
    @NSManaged var finishedBy: String
    @NSManaged var finishedDate: NSDate
    @NSManaged var fromBug: NSNumber
    @NSManaged var lastEditedBy: String
    @NSManaged var lastEditedDate: NSDate
    @NSManaged var left: NSNumber
    @NSManaged var mialto: String
    @NSManaged var module: NSNumber
    @NSManaged var name: String
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSDate
    @NSManaged var pri: NSNumber
    @NSManaged var project: NSNumber
    @NSManaged var realStarted: NSDate
    @NSManaged var status: String
    @NSManaged var story: NSNumber
    @NSManaged var storyVersion: NSNumber
    @NSManaged var type: String

}
