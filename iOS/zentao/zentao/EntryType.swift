//
//  EntryType.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

enum EntryType : Int, AccentIconProtocol {
    
    case Default = 0
    case Todo
    case Task
    case Bug
    case Story
    case Product
    case Project
    
    private static let accentIconMap = [
        AccentIcon(swatch: MaterialColorSwatch.Grey, icon: FontIcon.question),
        AccentIcon(swatch: MaterialColorSwatch.LightBlue, icon: FontIcon.check_square_o),
        AccentIcon(swatch: MaterialColorSwatch.Green, icon: FontIcon.tasks),
        AccentIcon(swatch: MaterialColorSwatch.Pink, icon: FontIcon.bug),
        AccentIcon(swatch: MaterialColorSwatch.Purple, icon: FontIcon.lightbulb_o),
        AccentIcon(swatch: MaterialColorSwatch.Indigo, icon: FontIcon.folder_o),
        AccentIcon(swatch: MaterialColorSwatch.Teal, icon: FontIcon.cube)
    ]
    
    static let map = [Default: "Default", Todo: "Todo", Task: "Task", Bug: "Bug", Story: "Story", Product: "Product", Project: "Project"]

    static var values: [EntryType] {
        get {
            return Array(map.keys)
        }
    }
    
    static var names: [String] {
        get {
            return Array(map.values)
        }
    }
    
    static func fromName(name: String) -> EntryType? {
        let lowerName = name.lowercaseString
        for (type, name) in map {
            if name.lowercaseString == lowerName {
                return type
            }
        }
        return nil
    }
    
    var swatch: MaterialColorSwatch {
        get {
            return EntryType.accentIconMap[self.rawValue].swatch
        }
    }
    
    var icon: IconVal {
        get {
            return EntryType.accentIconMap[self.rawValue].icon
        }
    }
    
    var name: String {
        get {
            return EntryType.map[self]!
        }
    }
}