//
//  Column.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

struct EntryAttribute {
    
    enum DataType {
        case String, Enum, Html, Int, Float, Bool, Date
    }
    
    let name: String
    let type: DataType
    let isApi: Bool
    private let apiNameValue: String?
    
    var apiName: String {
        return apiNameValue ?? name
    }
    
    init(name: String, type: DataType = .String, isApi: Bool = true, apiName: String? = nil) {
        self.name = name
        self.type = type
        self.isApi = isApi
        self.apiNameValue = apiName
    }
    
    init(name: String, type: DataType, apiName: String) {
        self.name = name
        self.type = type
        self.isApi = true
        self.apiNameValue = apiName
    }
    
    static func enumerate(name: String) -> EntryAttribute {
        return EntryAttribute(name: name, type: .Enum)
    }
    
    static func string(name: String) -> EntryAttribute {
        return EntryAttribute(name: name, type: .String)
    }
    
    static func date(name: String) -> EntryAttribute {
        return EntryAttribute(name: name, type: .Date)
    }
    
    static func bool(name: String) -> EntryAttribute {
        return EntryAttribute(name: name, type: .Bool)
    }
    
    static func int(name: String) -> EntryAttribute {
        return EntryAttribute(name: name, type: .Int)
    }
    
    static func float(name: String) -> EntryAttribute {
        return EntryAttribute(name: name, type: .Float)
    }
    
    static func html(name: String) -> EntryAttribute {
        return EntryAttribute(name: name, type: .Html)
    }
}
