//
//  Entry.swift
//  zentao
//
//  Created by Sun Hao on 15/3/16.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

class DataEntry: Entry {
    
    var entryType: EntryType {
        return .Default
    }
    
    func getRequired(zentao: String, id: Int) {
        self.zentao = zentao
        self.id = id
    }
}