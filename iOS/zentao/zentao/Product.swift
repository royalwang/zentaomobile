//
//  Product.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

@objc(Product)
class Product: Entity {
    
    override var entityType: EntityType {
        return .Product
    }

    @NSManaged var acl: String
    @NSManaged var code: String
    @NSManaged var createdBy: String
    @NSManaged var createdDate: NSDate
    @NSManaged var createdVersion: String
    @NSManaged var desc: String
    @NSManaged var name: String
    @NSManaged var po: String
    @NSManaged var qd: String
    @NSManaged var rd: String
    @NSManaged var status: String
    @NSManaged var whitelist: String

}
