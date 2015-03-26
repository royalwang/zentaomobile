//
//  Story.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

@objc(Story)
class Story: Entity {
    
    enum PageTab: Int, EntityPageTab {
        case AssignedTo = 0
        case OpenedBy
        case ReviewBy
        
        var entityType: EntityType {return EntityType.Story}
        static let names = ["指派给我", "由我创建", "由我评审"]
        static let values: [PageTab] = [.AssignedTo, .OpenedBy, .ReviewBy]
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
        return .Story
    }

    @NSManaged var assignedDate: NSDate
    @NSManaged var assignedTo: String
    @NSManaged var closedBy: String
    @NSManaged var closedDate: NSDate
    @NSManaged var closedReason: String
    @NSManaged var duplicateStory: NSNumber
    @NSManaged var estimate: NSNumber
    @NSManaged var fromBug: NSNumber
    @NSManaged var keywords: String
    @NSManaged var lastEditedBy: String
    @NSManaged var lastEditedDate: NSDate
    @NSManaged var mailto: String
    @NSManaged var module: NSNumber
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSDate
    @NSManaged var plan: NSNumber
    @NSManaged var pri: NSNumber
    @NSManaged var product: NSNumber
    @NSManaged var reviewedBy: String
    @NSManaged var reviewedDate: NSDate
    @NSManaged var source: String
    @NSManaged var spec: String
    @NSManaged var stage: String
    @NSManaged var status: String
    @NSManaged var title: String
    @NSManaged var toBug: NSNumber
    @NSManaged var verify: String
    @NSManaged var version: NSNumber

}
