//
//  Bug.swift
//  zentao
//
//  Created by Sun Hao on 15/3/16.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Bug: Entry {
    
    override var entryType: EntryType {
        return .Bug
    }

    @NSManaged var activatedCount: Int32
    @NSManaged var assignedDate: NSTimeInterval
    @NSManaged var assignedTo: String
    @NSManaged var browser: String
    @NSManaged var closedBy: String
    @NSManaged var closedDate: NSTimeInterval
    @NSManaged var confirmed: Bool
    @NSManaged var duplicateBug: Int32
    @NSManaged var found: String
    @NSManaged var hardware: String
    @NSManaged var keywords: String
    @NSManaged var lastEditedBy: String
    @NSManaged var lastEditedDate: NSTimeInterval
    @NSManaged var mailto: String
    @NSManaged var module: Int32
    @NSManaged var openedBuild: String
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSTimeInterval
    @NSManaged var os: String
    @NSManaged var plan: Int32
    @NSManaged var pri: Int16
    @NSManaged var product: Int32
    @NSManaged var project: Int32
    @NSManaged var resolution: String
    @NSManaged var resolvedBuild: String
    @NSManaged var resolvedBy: String
    @NSManaged var resolvedDate: NSTimeInterval
    @NSManaged var severity: Int16
    @NSManaged var status: String
    @NSManaged var steps: String
    @NSManaged var story: Int32
    @NSManaged var storyVersion: Int32
    @NSManaged var task: Int32
    @NSManaged var title: String
    @NSManaged var toStory: Int32
    @NSManaged var toTask: Int32
    @NSManaged var type: String

}
