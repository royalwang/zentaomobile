//
//  Story.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

@objc(Story)
class Story: Entity {
    
    enum PageTab: Int, EntityQueryType {
        case AssignedTo = 0
        case OpenedBy
        case ReviewBy
        
        var entityType: EntityType {return EntityType.Story}
        static let displayNames = ["指派给我", "由我创建", "由我评审"]
        static let names = ["assignedTo", "openedBy", "reviewedBy"]
        static let values: [PageTab] = [.AssignedTo, .OpenedBy, .ReviewBy]
        static var defaultTab: EntityQueryType {
            return PageTab.AssignedTo
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
    
    /**
    * Bug status enum
    */
    enum Status: Int, AccentIconProtocol, NamedEnum {
        
        case unknown = 0
        case draft
        case active
        case closed
        case changed
        
        static let names = ["unknown", "draft", "active", "closed", "changed"]
        static let values = [Status.unknown, .draft, .active, .closed, .changed]
        static let displayNames = ["未知", "草稿", "激活", "已关闭", "已变更"]
        private static let accentIconMap = [
            AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.question),
            AccentIcon(swatch: MaterialColor.Purple, icon: FontIcon.pencil),
            AccentIcon(swatch: MaterialColor.Brown, icon: FontIcon.flag),
            AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.dot_circle_o),
            AccentIcon(swatch: MaterialColor.Red, icon: FontIcon.random)
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
        return Status.fromName(status) ?? .unknown
    }
    
    override var entityType: EntityType {
        return .Story
    }
    
    override var displayName: String {
        return title
    }

    @NSManaged var assignedDate: NSDate
    @NSManaged var assignedTo: String
    @NSManaged var closedBy: String
    @NSManaged var closedDate: NSDate
    @NSManaged var closedReason: String
    @NSManaged var duplicateStory: NSNumber
    @NSManaged var estimate: NSNumber
    @NSManaged var fromBug: NSNumber
    @NSManaged var keywords: String
    @NSManaged var lastEditedBy: String
    @NSManaged var lastEditedDate: NSDate
    @NSManaged var mailto: String
    @NSManaged var module: NSNumber
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSDate
    @NSManaged var plan: NSNumber
    @NSManaged var pri: NSNumber
    @NSManaged var product: NSNumber
    @NSManaged var reviewedBy: String
    @NSManaged var reviewedDate: NSDate
    @NSManaged var source: String
    @NSManaged var spec: String
    @NSManaged var stage: String
    @NSManaged var status: String
    @NSManaged var title: String
    @NSManaged var toBug: NSNumber
    @NSManaged var verify: String
    @NSManaged var version: NSNumber

}
