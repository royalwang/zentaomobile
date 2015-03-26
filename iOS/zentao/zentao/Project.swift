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
    
    enum PageTab: Int, EntityPageTab {
        
        case AssignedTo = 0
        case Going
        case Closed
        
        static let entityType = EntityType.Project
        static let names = ["我负责的", "活动中", "已关闭"]
        static let values: [PageTab] = [.AssignedTo, .Going, .Closed]
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
