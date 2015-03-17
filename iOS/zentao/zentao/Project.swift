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
