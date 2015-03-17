//
//  NamedEnum.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

protocol NamedEnum {

    class var names: [String] {get}
  
    var rawValue: Int {get}
    var index: Int {get}
    var name: String {get}
}