//
//  EntryType.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

enum EntityType : Int, AccentIconProtocol, NameValueEnum {
    
    case Default = 0
    case Todo
    case Task
    case Bug
    case Story
    case Product
    case Project
    
    private static let accentIconMap = [
        AccentIcon(swatch: MaterialColor.Grey, icon: FontIcon.question),
        AccentIcon(swatch: MaterialColor.Blue, icon: FontIcon.check_square_o),
        AccentIcon(swatch: MaterialColor.Green, icon: FontIcon.tasks),
        AccentIcon(swatch: MaterialColor.Pink, icon: FontIcon.bug),
        AccentIcon(swatch: MaterialColor.Purple, icon: FontIcon.lightbulb_o),
        AccentIcon(swatch: MaterialColor.Indigo, icon: FontIcon.folder_o),
        AccentIcon(swatch: MaterialColor.Teal, icon: FontIcon.cube)
    ]
    
    static let values = [Default, Todo, Task, Bug, Story, Product, Project]
    static let names = ["Default", "Todo", "Task", "Bug", "Story", "Product", "Project"]
    static let displayNames = ["默认", "待办", "任务", "Bug", "需求", "产品", "项目"]
    
    static func fromName(name: String, ignoreCase: Bool = true) -> EntityType? {
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
            return EntityType.accentIconMap[self.rawValue].swatch
        }
    }
    
    var icon: IconVal {
        get {
            return EntityType.accentIconMap[self.rawValue].icon
        }
    }
    
    var name: String {
        get {
            return EntityType.names[self.rawValue]
        }
    }
    
    var displayName: String {
        get {
            return EntityType.displayNames[self.rawValue]
        }
    }
    
    var index: Int {
        get {
            return rawValue
        }
    }
    
    var tabs: [EntityPageTab] {
        switch self {
        case .Todo:
            return zentao.Todo.PageTab.all
        case .Task:
            return zentao.Task.PageTab.all
        case .Bug:
            return zentao.Bug.PageTab.all
        case .Story:
            return zentao.Story.PageTab.all
        case .Product:
            return zentao.Product.PageTab.all
        case .Project:
            return zentao.Project.PageTab.all
        default:
            return []
        }
    }
    
    
    // Attributes
    static let basicAttributes = [
        EntryAttribute(name: "id", type: .Int),
        EntryAttribute(name: "zentao", type: .Int, isApi: false),
        EntryAttribute(name: "unread", type: .Int, isApi: false),
        EntryAttribute(name: "lastSyncTime", type: .Int, isApi: false),
        EntryAttribute(name: "delete", type: .Int, apiName: "deleted")
    ]
    
    static let todoAttributes = EntityType.basicAttributes + [
        EntryAttribute(name: "name"),
        EntryAttribute(name: "begin", type: .Date),
        EntryAttribute(name: "end", type: .Date),
        EntryAttribute(name: "type", type: .Enum),
        EntryAttribute(name: "status", type: .Enum),
        EntryAttribute.int("pri"),
        EntryAttribute.int("idvalue"),
        EntryAttribute.html("desc"),
        EntryAttribute.string("account")
    ]
    
    static let taskAttributes = EntityType.basicAttributes + [
        EntryAttribute.string("assignedBy"),
        EntryAttribute.date("assignedDate"),
        EntryAttribute.string("canceledBy"),
        EntryAttribute.date("canceledDate"),
        EntryAttribute.string("closedBy"),
        EntryAttribute.date("closedDate"),
        EntryAttribute.enumerate("closeReason"),
        EntryAttribute.float("consumed"),
        EntryAttribute.date("deadline"),
        EntryAttribute.string("desc"),
        EntryAttribute.string("doc"),
        EntryAttribute.float("estimate"),
        EntryAttribute.date("estStarted"),
        EntryAttribute.string("finishedBy"),
        EntryAttribute.date("finishedDate"),
        EntryAttribute.int("fromBug"),
        EntryAttribute.string("lastEditedBy"),
        EntryAttribute.date("lastEditedDate"),
        EntryAttribute.float("left"),
        EntryAttribute.string("mialto"),
        EntryAttribute.int("module"),
        EntryAttribute.string("name"),
        EntryAttribute.string("openedBy"),
        EntryAttribute.date("openedDate"),
        EntryAttribute.int("pri"),
        EntryAttribute.int("project"),
        EntryAttribute.string("realStarted"),
        EntryAttribute.enumerate("status"),
        EntryAttribute.int("story"),
        EntryAttribute.int("storyVersion"),
        EntryAttribute.enumerate("type")
    ]
    
    static let storyAttributes = EntityType.basicAttributes + [
        EntryAttribute.date("assignedDate"),
        EntryAttribute.string("assignedTo"),
        EntryAttribute.string("closedBy"),
        EntryAttribute.date("closedDate"),
        EntryAttribute.enumerate("closedReason"),
        EntryAttribute.int("duplicateStory"),
        EntryAttribute.float("estimate"),
        EntryAttribute.int("fromBug"),
        EntryAttribute.string("keywords"),
        EntryAttribute.string("lastEditedBy"),
        EntryAttribute.date("lastEditedDate"),
        EntryAttribute.string("mailto"),
        EntryAttribute.int("module"),
        EntryAttribute.string("openedBy"),
        EntryAttribute.date("openedDate"),
        EntryAttribute.int("plan"),
        EntryAttribute.int("pri"),
        EntryAttribute.int("product"),
        EntryAttribute.string("reviewedBy"),
        EntryAttribute.date("reviewedDate"),
        EntryAttribute.enumerate("source"),
        EntryAttribute.html("spec"),
        EntryAttribute.enumerate("stage"),
        EntryAttribute.enumerate("status"),
        EntryAttribute.string("title"),
        EntryAttribute.int("toBug"),
        EntryAttribute.html("verify"),
        EntryAttribute.int("version")
    ]
    
    static let bugAttributes = EntityType.basicAttributes + [
        EntryAttribute.int("activatedCount"),
        EntryAttribute.date("assignedDate"),
        EntryAttribute.string("assignedTo"),
        EntryAttribute.enumerate("browser"),
        EntryAttribute.string("closedBy"),
        EntryAttribute.date("closedDate"),
        EntryAttribute.bool("confirmed"),
        EntryAttribute.int("duplicateBug"),
        EntryAttribute.string("found"),
        EntryAttribute.string("hardware"),
        EntryAttribute.string("keywords"),
        EntryAttribute.string("lastEditedBy"),
        EntryAttribute.date("lastEditedDate"),
        EntryAttribute.string("mailto"),
        EntryAttribute.int("module"),
        EntryAttribute.string("openedBuild"),
        EntryAttribute.string("openedBy"),
        EntryAttribute.date("openedDate"),
        EntryAttribute.enumerate("os"),
        EntryAttribute.int("plan"),
        EntryAttribute.int("pri"),
        EntryAttribute.int("product"),
        EntryAttribute.int("project"),
        EntryAttribute.enumerate("resolution"),
        EntryAttribute.string("resolvedBuild"),
        EntryAttribute.string("resolvedBy"),
        EntryAttribute.date("resolvedDate"),
        EntryAttribute.int("severity"),
        EntryAttribute.enumerate("status"),
        EntryAttribute.html("steps"),
        EntryAttribute.int("story"),
        EntryAttribute.int("storyVersion"),
        EntryAttribute.int("task"),
        EntryAttribute.string("title"),
        EntryAttribute.int("toStory"),
        EntryAttribute.int("toTask"),
        EntryAttribute.enumerate("type")
    ]
    
    static let productAttributes = EntityType.basicAttributes + [
        EntryAttribute.enumerate("acl"),
        EntryAttribute.string("code"),
        EntryAttribute.string("createdBy"),
        EntryAttribute.date("createdDate"),
        EntryAttribute.string("createdVersion"),
        EntryAttribute.html("desc"),
        EntryAttribute.string("name"),
        EntryAttribute(name: "po", type: .String, apiName: "PO"),
        EntryAttribute(name: "qd", type: .String, apiName: "QD"),
        EntryAttribute(name: "rd", type: .String, apiName: "RD"),
        EntryAttribute.enumerate("status"),
        EntryAttribute.string("whitelist")
    ]
    
    static let projectAttributes = EntityType.basicAttributes + [
        EntryAttribute.enumerate("acl"),
        EntryAttribute.date("begin"),
        EntryAttribute.string("canceledBy"),
        EntryAttribute.date("canceledDate"),
        EntryAttribute.date("closeDate"),
        EntryAttribute.string("closedBy"),
        EntryAttribute.string("code"),
        EntryAttribute.int("days"),
        EntryAttribute.string("desc"),
        EntryAttribute.date("end"),
        EntryAttribute.bool("isCat"),
        EntryAttribute.string("name"),
        EntryAttribute.string("openedBy"),
        EntryAttribute.date("openedDate"),
        EntryAttribute.string("openedVersion"),
        EntryAttribute.int("parent"),
        EntryAttribute.int("pri"),
        EntryAttribute(name: "po", type: .String, apiName: "PO"),
        EntryAttribute(name: "qd", type: .String, apiName: "QD"),
        EntryAttribute(name: "rd", type: .String, apiName: "RD"),
        EntryAttribute(name: "pm", type: .String, apiName: "PM"),
        EntryAttribute.string("statge"),
        EntryAttribute.enumerate("status"),
        EntryAttribute.string("team"),
        EntryAttribute.enumerate("type"),
        EntryAttribute.string("whitelist")
    ]
    
    var attributes: [EntryAttribute] {
        switch self {
        case .Default:
            return EntityType.basicAttributes
        case .Todo:
            return EntityType.todoAttributes
        case .Task:
            return EntityType.taskAttributes
        case .Bug:
            return EntityType.bugAttributes
        case .Story:
            return EntityType.storyAttributes
        case .Product:
            return EntityType.productAttributes
        case .Project:
            return EntityType.projectAttributes
        }
    }
}