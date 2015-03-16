//
//  Todo.swift
//  zentao
//
//  Created by Sun Hao on 15/3/16.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Todo: Entry, DataEntry {
    
    let entryType = EntryType.Todo
    
    @NSManaged var name: String
    @NSManaged var begin: NSTimeInterval
    @NSManaged var end: NSTimeInterval
    @NSManaged var type: String
    @NSManaged var status: String
    @NSManaged var pri: Int16
    @NSManaged var idvalue: Int32
    @NSManaged var desc: String
    @NSManaged var account: String
    
    func getRequired(zentao: String, account: String, id: Int, name: String) {
        self.zentao = zentao
        self.account = account
        self.id = id
        self.name = name
    }
}
