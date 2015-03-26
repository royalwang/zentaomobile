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
    
    enum PageTab: Int, EntityPageTab {
        case AssignedTo = 0
        case OpenedBy
        case FinishedBy
        
        var entityType: EntityType {return EntityType.Task}
        static let names = ["指派给我", "由我创建", "由我完成"]
        static let values: [PageTab] = [.AssignedTo, .OpenedBy, .FinishedBy]
        static var defaultTab: EntityPageTab {
            return PageTab.AssignedTo
        }
        static var all: [EntityPageTab] {
            //            return values
            return values.map {
                (var val) -> EntityPageTab in
                return val
            }
        }
        
        var index: Int {
            return rawValue
        }
        
        var name: String {
            return PageTab.names[rawValue]
        }
        
        var prev: EntityPageTab? {
            if index == 0 {
                return nil
            }
            return PageTab.values[index - 1]
        }
        
        var next: EntityPageTab? {
            if index == (PageTab.values.count - 1) {
                return nil
            }
            return PageTab.values[index + 1]
        }
    }
    
    override var entityType: EntityType {
        return .Task
    }

    @NSManaged var assignedBy: String
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
