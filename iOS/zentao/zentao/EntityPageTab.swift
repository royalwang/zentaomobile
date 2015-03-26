//
//  PageTab.swift
//  zentao
//
//  Created by Sun Hao on 15/3/26.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

protocol EntityPageTab: NamedEnum {
    
    class var entityType: EntityType {get}
    class var all: [EntityPageTab] {get}
    
    var next: EntityPageTab? {get}
    var prev: EntityPageTab? {get}
}