//
//  NameValueEnum.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

protocol NameValueEnum: NamedEnum {
    
    typealias EnumType
    
    class var values: [EnumType] {get}
    class func fromName(name: String, ignoreCase: Bool) -> EnumType?
}