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
    
    enum PageTab: Int, EntityPageTab {
        
        case Working = 0
        case Closed
        
        static let entityType = EntityType.Product
        static let names = ["研发中", "已关闭"]
        static let values: [PageTab] = [.Working, .Closed]
        static var defaultTab: EntityPageTab {
            return PageTab.Working
        }
        static var all: [EntityPageTab] {
            //            return values
            return values.map {
                (var val) -> EntityPageTab in
                return val
            }
        }
        
        var index: Int {
            return rawValue
        }
        
        var name: String {
            return PageTab.names[rawValue]
        }
        
        var prev: EntityPageTab? {
            if index == 0 {
                return nil
            }
            return PageTab.values[index - 1]
        }
        
        var next: EntityPageTab? {
            if index == (PageTab.values.count - 1) {
                return nil
            }
            return PageTab.values[index + 1]
        }
    }
    
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
