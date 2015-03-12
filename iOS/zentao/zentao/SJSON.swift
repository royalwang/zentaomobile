//
//  SJSON.swift
//  zentao
//
//  Created by Sun Hao on 15/3/12.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

public class SJSON {
    let json:JSON
    
    init(json: JSON) {
        self.json = json
    }
    
    init(data:NSData, options opt: NSJSONReadingOptions = .AllowFragments, error: NSErrorPointer = nil) {
        self.json = JSON(data: data, options: opt, error: error);
    }
}