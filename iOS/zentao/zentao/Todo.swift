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
    
    enum PageTab: Int, EntityPageTab {
        case Today = 0
        case Undone
        case Done
        
        var entityType: EntityType {return EntityType.Todo}
        static let names = ["今天", "未完成", "已完成"]
        static let values: [PageTab] = [.Today, .Undone, .Done]
        static var defaultTab: EntityPageTab {
            return PageTab.Undone
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
