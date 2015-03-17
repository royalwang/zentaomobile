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
