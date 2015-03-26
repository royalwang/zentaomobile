//
//  Bug.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

@objc(Bug)
class Bug: Entity {
    
    enum PageTab: Int, EntityPageTab {
        case AssignedTo = 0
        case OpenedBy
        case SolvedBy
        
        static let entityType = EntityType.Bug
        static let names = ["指派给我", "由我创建", "由我解决"]
        static let values: [PageTab] = [.AssignedTo, .OpenedBy, .SolvedBy]
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
        return .Bug
    }

    @NSManaged var activatedCount: NSNumber
    @NSManaged var assignedDate: NSDate
    @NSManaged var assignedTo: String
    @NSManaged var browser: String
    @NSManaged var closedBy: String
    @NSManaged var closedDate: NSDate
    @NSManaged var confirmed: NSNumber
    @NSManaged var duplicateBug: NSNumber
    @NSManaged var found: String
    @NSManaged var hardware: String
    @NSManaged var keywords: String
    @NSManaged var lastEditedBy: String
    @NSManaged var lastEditedDate: NSDate
    @NSManaged var mailto: String
    @NSManaged var module: NSNumber
    @NSManaged var openedBuild: String
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSDate
    @NSManaged var os: String
    @NSManaged var plan: NSNumber
    @NSManaged var pri: NSNumber
    @NSManaged var product: NSNumber
    @NSManaged var project: NSNumber
    @NSManaged var resolution: String
    @NSManaged var resolvedBuild: String
    @NSManaged var resolvedBy: String
    @NSManaged var resolvedDate: NSDate
    @NSManaged var severity: NSNumber
    @NSManaged var status: String
    @NSManaged var steps: String
    @NSManaged var story: NSNumber
    @NSManaged var storyVersion: NSNumber
    @NSManaged var task: NSNumber
    @NSManaged var title: String
    @NSManaged var toStory: NSNumber
    @NSManaged var toTask: NSNumber
    @NSManaged var type: String

}
