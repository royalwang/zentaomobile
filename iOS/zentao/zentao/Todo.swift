//
//  Todo.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

@objc(Todo)
class Todo: Entity {
    
    enum PageTab: Int, EntityQueryType {
        case Today = 0
        case Undone
        case Done
        
        var entityType: EntityType {return EntityType.Todo}
        static let names = ["今天", "未完成", "已完成"]
        static let values: [PageTab] = [.Today, .Undone, .Done]
        static var defaultTab: EntityQueryType {
            return PageTab.Undone
        }
        static var all: [EntityQueryType] {
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
        return .Todo
    }

    @NSManaged var account: String
    @NSManaged var begin: NSDate
    @NSManaged var desc: String
    @NSManaged var end: NSDate
    @NSManaged var idvalue: NSNumber
    @NSManaged var name: String
    @NSManaged var pri: NSNumber
    @NSManaged var status: String
    @NSManaged var type: String

    func setRequired(zentao: String, account: String, id: Int, name: String) {
        super.setRequired(zentao, id: id)
        self.account = account
        self.name = name
    }
}
