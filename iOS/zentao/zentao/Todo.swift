//
//  Todo.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class Todo: Entry {
    
    override var entryType: EntryType {
        return .Todo
    }

    @NSManaged var account: String
    @NSManaged var begin: NSDate
    @NSManaged var desc: String
    @NSManaged var end: NSDate
    @NSManaged var idvalue: NSNumber
    @NSManaged var name: String
    @NSManaged var pri: NSNumber
    @NSManaged var status: String
    @NSManaged var type: String

    func setRequired(zentao: String, account: String, id: Int, name: String) {
        super.setRequired(zentao, id: id)
        self.account = account
        self.name = name
    }
}
