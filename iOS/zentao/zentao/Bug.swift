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
