//
//  AccentIcon.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

protocol AccentIconProtocol {
    var swatch: MaterialColorSwatch {get}
    var icon: IconVal {get}
}

struct AccentIcon : AccentIconProtocol {
    let swatch: MaterialColorSwatch
    let icon: IconVal
}