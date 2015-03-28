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
        static let displayNames = ["今天", "未完成", "已完成"]
        static let names = ["today", "undone", "done"]
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
    
    /**
    * Todo status enum
    */
    enum Status: Int, AccentIconProtocol, NamedEnum {
        
        case wait = 0
        case done
        case doing
        
        static let names = ["wait", "done", "doing"]
        static let values = [Status.wait, .done, .doing]
        static let displayNames = ["未开始", "已完成", "进行中"]
        private static let accentIconMap = [
            AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.clock_o),
            AccentIcon(swatch: MaterialColor.Green, icon: FontIcon.check),
            AccentIcon(swatch: MaterialColor.Pink, icon: FontIcon.play)
        ]
        
        static func fromName(name: String, ignoreCase: Bool = true) -> Status? {
            if ignoreCase {
                let lowerName = name.lowercaseString
                for (id, thisName) in enumerate(names) {
                    if thisName.lowercaseString == lowerName {
                        return values[id]
                    }
                }
            } else {
                for (id, thisName) in enumerate(names) {
                    if name == thisName {
                        return values[id]
                    }
                }
            }
            
            return nil
        }
        
        var swatch: MaterialColor.Swatch {
            get {
                return Status.accentIconMap[self.rawValue].swatch
            }
        }
        
        var icon: IconVal {
            get {
                return Status.accentIconMap[self.rawValue].icon
            }
        }
        
        var name: String {
            get {
                return Status.names[self.rawValue]
            }
        }
        
        var displayName: String {
            return Status.displayNames[self.rawValue]
        }
        
        var index: Int {
            get {
                return rawValue
            }
        }
    }
    
    var statusValue: Status {
        return Status.fromName(status) ?? .wait
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
