//
//  Entry.swift
//  zentao
//
//  Created by Sun Hao on 15/3/16.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Entry: NSManagedObject {

    @NSManaged var id: Int
    @NSManaged var zentao: String
    @NSManaged var unread: Bool
    @NSManaged var lastSyncTime: NSTimeInterval
//    @NSManaged var deleted: Bool

}