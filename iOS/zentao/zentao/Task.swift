//
//  Task.swift
//  zentao
//
//  Created by Sun Hao on 15/3/16.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Task: Entry, DateEntry {
    
    let entryType = EntryType.Task

    @NSManaged var assignedBy: String
    @NSManaged var assignedDate: NSTimeInterval
    @NSManaged var canceledBy: String
    @NSManaged var canceledDate: NSTimeInterval
    @NSManaged var closedBy: String
    @NSManaged var closedDate: NSTimeInterval
    @NSManaged var closeReason: String
    @NSManaged var consumed: Float
    @NSManaged var deadline: NSTimeInterval
    @NSManaged var desc: String
    @NSManaged var doc: String
    @NSManaged var estimate: Float
    @NSManaged var estStarted: NSTimeInterval
    @NSManaged var finishedBy: String
    @NSManaged var finishedDate: NSTimeInterval
    @NSManaged var fromBug: Int32
    @NSManaged var lastEditedBy: String
    @NSManaged var lastEditedDate: NSTimeInterval
    @NSManaged var left: Float
    @NSManaged var mialto: String
    @NSManaged var module: Int32
    @NSManaged var name: String
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSTimeInterval
    @NSManaged var pri: Int16
    @NSManaged var project: Int32
    @NSManaged var realStarted: NSTimeInterval
    @NSManaged var status: String
    @NSManaged var story: Int32
    @NSManaged var storyVersion: Int32
    @NSManaged var type: String

}
