//
//  Project.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

@objc(Project)
class Project: Entity {
    
    enum PageTab: Int, EntityQueryType {
        
        case AssignedTo = 0
        case Going
        case Finished
        
        var entityType: EntityType {return EntityType.Project}
        static let displayNames = ["我负责的", "活动中", "已完成"]
        static let names = ["assignedTo", "going", "finished"]
        static let values: [PageTab] = [.AssignedTo, .Going, .Finished]
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
        
        var name: String {
            return PageTab.names[rawValue]
        }
        
        var displayName: String {
            return PageTab.displayNames[rawValue]
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
        
        func equalTo(tab: EntityQueryType?) -> Bool {
            if let t = tab {
                return self.entityType == t.entityType && self.index == t.index
            }
            return false
        }
        
        func equalTypeTo(tab: EntityQueryType?) -> Bool {
            if let t = tab {
                return self.entityType == t.entityType
            }
            return false
        }
    }
    
    override var entityType: EntityType {
        return .Project
    }

    @NSManaged var acl: String
    @NSManaged var begin: NSDate
    @NSManaged var canceledBy: String
    @NSManaged var canceledDate: NSDate
    @NSManaged var closeDate: NSDate
    @NSManaged var closedBy: String
    @NSManaged var code: String
    @NSManaged var days: NSNumber
    @NSManaged var desc: String
    @NSManaged var end: NSDate
    @NSManaged var isCat: NSNumber
    @NSManaged var name: String
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSDate
    @NSManaged var openedVersion: String
    @NSManaged var parent: NSNumber
    @NSManaged var pm: String
    @NSManaged var po: String
    @NSManaged var pri: NSNumber
    @NSManaged var qd: String
    @NSManaged var rd: String
    @NSManaged var statge: String
    @NSManaged var status: String
    @NSManaged var team: String
    @NSManaged var type: String
    @NSManaged var whitelist: String

}
