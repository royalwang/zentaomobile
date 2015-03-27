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
    
    enum PageTab: Int, EntityQueryType {
        
        case Working = 0
        case Closed
        
        var entityType: EntityType {return EntityType.Product}
        static let names = ["working", "closed"]
        static let displayNames = ["研发中", "已关闭"]
        static let values: [PageTab] = [.Working, .Closed]
        static var defaultTab: EntityQueryType {
            return PageTab.Working
        }
        static var all: [EntityQueryType] {
            //            return values
            return values.map {
                (var val) -> EntityQueryType in
                return val
            }
        }
        
        var index: Int {
            return rawValue
        }
        
        var name: String {
            return PageTab.names[rawValue]
        }
        
        var displayName: String {
            return PageTab.displayNames[rawValue]
        }
        
        var prev: EntityQueryType? {
            if index == 0 {
                return nil
            }
            return PageTab.values[index - 1]
        }
        
        var next: EntityQueryType? {
            if index == (PageTab.values.count - 1) {
                return nil
            }
            return PageTab.values[index + 1]
        }
        
        func equalTo(tab: EntityQueryType?) -> Bool {
            if let t = tab {
                return self.entityType == t.entityType && self.index == t.index
            }
            return false
        }
        
        func equalTypeTo(tab: EntityQueryType?) -> Bool {
            if let t = tab {
                return self.entityType == t.entityType
            }
            return false
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
