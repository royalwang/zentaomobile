//
//  NamedEnum.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

protocol NamedEnum {
    typealias EnumType
    
    class var names: [String] {get}
    class var values: [EnumType] {get}
    class func fromName(name: String, ignoreCase: Bool) -> EnumType?
    
    var rawValue: Int {get}
    var index: Int {get}
    var name: String {get}
}