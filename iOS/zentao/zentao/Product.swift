//
//  Product.swift
//  zentao
//
//  Created by Sun Hao on 15/3/16.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Product: Entry, DataEntry {
    
    let entryType = EntryType.Product
    
    @NSManaged var acl: String
    @NSManaged var code: String
    @NSManaged var createdBy: String
    @NSManaged var createdDate: NSTimeInterval
    @NSManaged var createdVersion: String
    @NSManaged var desc: String
    @NSManaged var name: String
    @NSManaged var po: String
    @NSManaged var qd: String
    @NSManaged var rd: String
    @NSManaged var status: String
    @NSManaged var whitelist: String

}
