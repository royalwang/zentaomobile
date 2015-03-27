//
//  PageTab.swift
//  zentao
//
//  Created by Sun Hao on 15/3/26.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

protocol EntityQueryType: NamedEnum {
    
    var entityType: EntityType {get}
    class var all: [EntityQueryType] {get}
    class var defaultTab: EntityQueryType {get}
    class var displayNames: [String] {get}
    
    var displayName: String {get}
    var next: EntityQueryType? {get}
    var prev: EntityQueryType? {get}
    
    func equalTo(tab: EntityQueryType?) -> Bool
    func equalTypeTo(tab: EntityQueryType?) -> Bool
}