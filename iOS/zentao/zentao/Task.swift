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
class Task: Entry {
    
    override var entryType: EntryType {
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
