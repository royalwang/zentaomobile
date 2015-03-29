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
    
    /**
    * Project status enum
    */
    enum Status: Int, AccentIconProtocol, NamedEnum {
        
        case unknown = 0
        case normal
        case closed
        
        static let names = ["unknown", "normal", "closed"]
        static let values = [Status.unknown, .normal, .closed]
        static let displayNames = ["未知", "正常", "已关闭"]
        private static let accentIconMap = [
            AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.clock_o),
            AccentIcon(swatch: MaterialColor.Pink, icon: FontIcon.play),
            AccentIcon(swatch: MaterialColor.Orange, icon: FontIcon.pause),
            AccentIcon(swatch: MaterialColor.Green, icon: FontIcon.check)
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
        return .Product
    }
    
    var bugCount = 0
    var storyInfo: (active: Int, changed: Int, draft: Int)?
    
    var storyInfoText: String {
        if let info = storyInfo {
            var text = "需求 \(info.active)"
            if info.changed > 0 {
                text += "   已变更 \(info.changed)"
            }
            if info.draft > 0 {
                text += "   草稿 \(info.draft)"
            }
            return text
        }
        return ""
    }
    
    override var displayName: String {
        return name
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
