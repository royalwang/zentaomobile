//
//  Entry.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Entity: NSManagedObject, Printable {

    @NSManaged var delete: NSNumber
    @NSManaged var id: NSNumber
    @NSManaged var lastSyncTime: NSDate
    @NSManaged var unread: NSNumber
    @NSManaged var zentao: String
    
    var entityType: EntityType {
        return .Default
    }
    
    var displayName: String {
        return ""
    }
    
    func setRequired(zentao: String, id: Int) {
        self.zentao = zentao
        self.id = id
    }
    
    subscript(attrName: String) -> AnyObject? {
        return valueForKey(attrName)
    }
    
    subscript(attr: EntryAttribute) -> AnyObject? {
        get {
            return self[attr.name]
        }
        set {
            switch attr.type {
            case .String, .Html, .Enum:
                setValue(newValue as? String, forKey: attr.name)
            case .Int:
                setValue(newValue as? NSNumber, forKey: attr.name)
            case .Bool:
                setValue(newValue as? Bool, forKey: attr.name)
            case .Float:
                setValue(newValue as? Float, forKey: attr.name)
            case .Date:
                var date: NSDate?
                if let d = newValue as? NSDate {
                    date = d
                } else if let dateInt = newValue as? Int {
                    date = NSDate(timeIntervalSince1970: Double(dateInt))
                } else if let dateStr = newValue as? String {
                    date = NSDate(fromString: dateStr, format: DateFormat.Custom("yyyy-MM-dd hh:mm:ss"))
                } else {
                    date = nil
                }
                setValue(date, forKey: attr.name)
            }
        }
    }
    
    func from(#jsonArry: JSON, keys: [String]) {
        var values: [String: AnyObject] = [:]
        for (index, value) in enumerate(jsonArry.object as [AnyObject]) {
            values[keys[index]] = value
        }
        from(apiValues: values)
    }
    
    func from(apiValues values: [String: AnyObject]) {
        for attr in entityType.attributes {
            if values.has(attr.apiName) {
                self[attr] = values[attr.apiName]
            }
        }
    }
    
    override var description: String {
        var desc = "{"
        var valStr: String
        for attr in entityType.attributes {
            valStr = self[attr] == nil ? "nil" : "\(self[attr]!)"
            desc += "\(attr.name): \(valStr), "
        }
        return desc + "objectID: \(objectID)}"
    }
}
