//
//  EntryType.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

enum EntryType : Int, AccentIconProtocol, NamedEnum {
    
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
    
    static let values = [Default, Todo, Task, Bug, Story, Product, Project]
    static let names = ["Default", "Todo", "Task", "Bug", "Story", "Product", "Project"]
    
    static func fromName(name: String, ignoreCase: Bool = true) -> EntryType? {
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
            return EntryType.names[self.rawValue]
        }
    }
    
    var index: Int {
        get {
            return rawValue
        }
    }
}