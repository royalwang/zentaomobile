//
//  Bug.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

@objc(Bug)
class Bug: Entity {
    
    enum PageTab: Int, EntityQueryType {
        case AssignedTo = 0
        case OpenedBy
        case SolvedBy
        
        var entityType: EntityType {return EntityType.Bug}
        static let names = ["assignedTo", "openedBy", "resolvedBy"]
        static let displayNames = ["指派给我", "由我创建", "由我解决"]
        static let values: [PageTab] = [.AssignedTo, .OpenedBy, .SolvedBy]
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
        case active
        case resolved
        case closed
        
        static let names = ["unknown", "active", "resolved", "closed"]
        static let values = [Status.unknown, .active, .resolved, .closed]
        static let displayNames = ["未知", "激活", "已解决", "已关闭"]
        private static let accentIconMap = [
            AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.question),
            AccentIcon(swatch: MaterialColor.Purple, icon: FontIcon.flag),
            AccentIcon(swatch: MaterialColor.Green, icon: FontIcon.check),
            AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.dot_circle_o)
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
        return .Bug
    }
    
    override var displayName: String {
        return title
    }

    @NSManaged var activatedCount: NSNumber
    @NSManaged var assignedDate: NSDate
    @NSManaged var assignedTo: String
    @NSManaged var browser: String
    @NSManaged var closedBy: String
    @NSManaged var closedDate: NSDate
    @NSManaged var confirmed: NSNumber
    @NSManaged var duplicateBug: NSNumber
    @NSManaged var found: String
    @NSManaged var hardware: String
    @NSManaged var keywords: String
    @NSManaged var lastEditedBy: String
    @NSManaged var lastEditedDate: NSDate
    @NSManaged var mailto: String
    @NSManaged var module: NSNumber
    @NSManaged var openedBuild: String
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSDate
    @NSManaged var os: String
    @NSManaged var plan: NSNumber
    @NSManaged var pri: NSNumber
    @NSManaged var product: NSNumber
    @NSManaged var project: NSNumber
    @NSManaged var resolution: String
    @NSManaged var resolvedBuild: String
    @NSManaged var resolvedBy: String
    @NSManaged var resolvedDate: NSDate
    @NSManaged var severity: NSNumber
    @NSManaged var status: String
    @NSManaged var steps: String
    @NSManaged var story: NSNumber
    @NSManaged var storyVersion: NSNumber
    @NSManaged var task: NSNumber
    @NSManaged var title: String
    @NSManaged var toStory: NSNumber
    @NSManaged var toTask: NSNumber
    @NSManaged var type: String

}
