//
//  Project.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

@objc(Project)
class Project: Entity {
    
    enum PageTab: Int, EntityQueryType {
        
        case AssignedTo = 0
        case Going
        case Finished
        
        var entityType: EntityType {return EntityType.Project}
        static let displayNames = ["我负责的", "活动中", "已完成"]
        static let names = ["assignedTo", "going", "finished"]
        static let values: [PageTab] = [.AssignedTo, .Going, .Finished]
        static var defaultTab: EntityQueryType {
            return PageTab.Going
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
        
        case wait = 0
        case doing
        case suspended
        case done
        
        static let names = ["wait", "doing", "suspended", "done"]
        static let values = [Status.wait, .doing, .suspended, .done]
        static let displayNames = ["未开始", "进行中", "挂起", "已完成"]
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
        return Status.fromName(status) ?? .wait
    }
    
    override var entityType: EntityType {
        return .Project
    }

    @NSManaged var acl: String
    @NSManaged var begin: NSDate
    @NSManaged var canceledBy: String
    @NSManaged var canceledDate: NSDate
    @NSManaged var closeDate: NSDate
    @NSManaged var closedBy: String
    @NSManaged var code: String
    @NSManaged var days: NSNumber
    @NSManaged var desc: String
    @NSManaged var end: NSDate
    @NSManaged var isCat: NSNumber
    @NSManaged var name: String
    @NSManaged var openedBy: String
    @NSManaged var openedDate: NSDate
    @NSManaged var openedVersion: String
    @NSManaged var parent: NSNumber
    @NSManaged var pm: String
    @NSManaged var po: String
    @NSManaged var pri: NSNumber
    @NSManaged var qd: String
    @NSManaged var rd: String
    @NSManaged var statge: String
    @NSManaged var status: String
    @NSManaged var team: String
    @NSManaged var type: String
    @NSManaged var whitelist: String
    
    var friendlyTimeString: String {
        var date: NSDate
        let status = statusValue
        var pattern: String = status.displayName + " "
        switch status {
        case .doing:
            date = end
            pattern += "至"
        case .suspended:
            date = begin
            pattern += "开始于"
        case .done:
            date = end
            pattern += "于"
        default:
            date = begin
            pattern += "开始于"
        }
        let now = NSDate()
        if date.isSameYearAsDate(now) {
            if date.isSameMonthAsDate(now) {
                pattern += date.toString(formatStr: "本月d日")
            } else {
                pattern += date.toString(formatStr: "M月d日")
            }
        } else {
            pattern += date.toString(formatStr: "yyyy年M月d日")
        }
        return pattern
    }
    
    var bugCount = 0
    var hours: (estimate: Float, consumed: Float, left: Float, progress: Float, hour: Float)?

}
