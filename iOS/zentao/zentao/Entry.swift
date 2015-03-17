//
//  Entry.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Entry: NSManagedObject {

    @NSManaged var delete: NSNumber
    @NSManaged var id: NSNumber
    @NSManaged var lastSyncTime: NSDate
    @NSManaged var unread: NSNumber
    @NSManaged var zentao: String
    
    var entryType: EntryType {
        return .Default
    }
    
    func setRequired(zentao: String, id: Int) {
        self.zentao = zentao
        self.id = id
    }
    
    subscript(attrName: String) -> AnyObject? {
        return valueForKey(attr.name)
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
                setValue(newValue as? NSDate, forKey: attr.name)
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
        for attr in entryType.attributes {
            if values.has(attr.apiName) {
                self[attr] = values[attr.apiName]
            }
        }
    }
}
