//
//  Project.swift
//  zentao
//
//  Created by Sun Hao on 15/3/16.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Project: DataEntry {
    
    override var entryType: EntryType {
        return .Project
    }

    @NSManaged var acl: String
    @NSManaged var begin: NSTimeInterval
    @NSManaged var canceledBy: String
    @NSManaged var canceledDate: NSTimeInterval
    @NSManaged var closeDate: NSTimeInterval
    @NSManaged var closedBy: String
    @NSManaged var code: String
    @NSManaged var days: Int32
    @NSManaged var desc: String
    @NSManaged var end: NSTimeInterval
    @NSManaged var isCat: Bool
    @NSManaged var name: String
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSTimeInterval
    @NSManaged var openedVersion: String
    @NSManaged var parent: Int32
    @NSManaged var pm: String
    @NSManaged var po: String
    @NSManaged var pri: Int16
    @NSManaged var qd: String
    @NSManaged var rd: String
    @NSManaged var statge: String
    @NSManaged var status: String
    @NSManaged var team: String
    @NSManaged var type: String
    @NSManaged var whitelist: String

}
