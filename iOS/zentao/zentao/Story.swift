//
//  Story.swift
//  zentao
//
//  Created by Sun Hao on 15/3/16.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Story: Entry {
    
    override var entryType: EntryType {
        return .Story
    }

    @NSManaged var assignedDate: NSTimeInterval
    @NSManaged var assignedTo: String
    @NSManaged var closedBy: String
    @NSManaged var closedDate: NSTimeInterval
    @NSManaged var closedReason: String
    @NSManaged var duplicateStory: Int32
    @NSManaged var estimate: Float
    @NSManaged var fromBug: Int32
    @NSManaged var keywords: String
    @NSManaged var lastEditedBy: String
    @NSManaged var lastEditedDate: NSTimeInterval
    @NSManaged var mailto: String
    @NSManaged var module: Int32
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSTimeInterval
    @NSManaged var plan: Int32
    @NSManaged var pri: Int16
    @NSManaged var product: Int32
    @NSManaged var reviewedBy: String
    @NSManaged var reviewedDate: NSTimeInterval
    @NSManaged var source: String
    @NSManaged var spec: String
    @NSManaged var stage: String
    @NSManaged var status: String
    @NSManaged var title: String
    @NSManaged var toBug: Int32
    @NSManaged var verify: String
    @NSManaged var version: Int32

}
