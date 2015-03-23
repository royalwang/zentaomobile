//
//  AccentIcon.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

protocol AccentIconProtocol {
    var swatch: MaterialColor.Swatch {get}
    var icon: IconVal {get}
}

struct AccentIcon : AccentIconProtocol {
    let swatch: MaterialColor.Swatch
    let icon: IconVal
}