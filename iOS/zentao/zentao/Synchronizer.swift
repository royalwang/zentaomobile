//
//  Synchronizer.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

class Synchronizer {
   
    let MAX_SYNC_ITEM_COUNT = 300
    
    lazy var app: ZentaoApp = {
        return ZentaoApp.sharedInstance
    }()
    
    var isRunning = false
    
    init(){
        let trySyncBlock: Event.ClosureVoid = {
            if !self.isRunning {
                self.sync()
            } else {
                Log.w("Synchronizer is running and skip this tick.")
            }
        }

        EventCenter.shared.bind(self).on(R.Event.timer_tick) +=! trySyncBlock
        EventCenter.shared.bind(self).on(R.Event.try_sync) +=! trySyncBlock
    }
    
    func sync() {
        isRunning = true
        EventCenter.shared.trigger(R.Event.sync_start, sender: self)
        sync(.Default) {
            result in
            EventCenter.shared.trigger(R.Event.sync_finish, sender: self, userInfo: ["result" : result])
            if result {
                Log.s("SYNC SUCCESS.")
            } else {
                Log.s("SYNC FAILED.")
            }
            self.isRunning = false
        }
    }
    
    func sync(entityType: EntityType, complete: ((result: Bool) -> Void)) {
        app.checkLogin() {
            isLogined in
            if isLogined {
                let user = self.app.getUser()!
                if !user.isNeverSynced {
                    let thisSyncTime = NSDate()
                    ZentaoAPI.getItemList(entityType, user: user) {
                        (result: Bool, jsonData: JSON?, message: String?) in
                        if result {
                            if let data = jsonData {
                                self.saveData(data)
                            }
                            user.lastSyncTime = thisSyncTime
                            self.app.profile.saveUser(user)
                        }
                        complete(result: result)
                    }
                } else {
                    self.deepSync(complete)
                }
                return
            }
            complete(result: false)
        }
    }
    
    func deepSync(complete: ((result: Bool) -> Void)) {
        Log.h("Deep sync start.")
        var deepSyncConfig:[EntityType: Int] = [:]
        for entityType in EntityType.values {
            if entityType != .Default {
                deepSyncConfig[entityType] = 0
            }
        }
        
        var thisSyncTime = NSDate()
        let user = app.getUser()!
        var deepSyncRequest: ((Bool) -> Void) -> Void = {_ in}
        deepSyncRequest = { (requestComplete: ((result: Bool) -> Void)) in
            var isOver = true
            for (entityType, var range) in  deepSyncConfig {
                if range < 0 {continue}
                isOver = false
                Log.d(">>> DEEP SYNC [\(entityType.name)], range=\(range)")
                ZentaoAPI.getItemList(entityType, user: user, options: (type: "increment", range: range, records: self.MAX_SYNC_ITEM_COUNT, format: "index")) {
                    (result: Bool, jsonData: JSON?, message: String?) in
                    if result {
                        if let data = jsonData {
                            let (result, count, minIdKey, _) = self.saveData(data)
                            if minIdKey == Int.max || count < self.MAX_SYNC_ITEM_COUNT {
                                range = -1
                            } else {
                                range = minIdKey
                            }
                            deepSyncConfig[entityType] = range
                            Log.s(">>>           SUCCESS, result=\(result), count=\(count), minIdKey=\(minIdKey).")
                        } else {
                            Log.w(">>>           SUCCESS, bu no data.")
                        }
                    } else {
                        Log.e(">>>           FAILED, message=\(message)")
                    }
                    deepSyncRequest(requestComplete)
                }
                break
            }
            if isOver {
                user.lastSyncTime = thisSyncTime
                self.app.profile.save()
                requestComplete(result: true)
            }
        }
        
        deepSyncRequest(complete)
    }
    
    func saveData(data: JSON) -> (result: Bool, count: Int, minIdKey: Int, operations: [String: [String: Int]]) {
        let dataStore = app.dataStore
        let user = app.getUser()!
        var count = 0, minIdKey = Int.max
        var operations: [String: [String: Int]] = [:]
        for entityType in EntityType.values {
            let set = data[entityType.name.lowercaseString]
            let keySet = set["key"].arrayObject as [String]?
            var operation: [String: Int] = [
                "add": 0,
                "delete": 0,
                "update": 0,
                "total": 0
            ]
            if let keys = keySet {
                let valueSet = set["set"].array
                if let values = valueSet {
                    for itemValues in values {
                        if let id = getIdFrom(jsonArry: itemValues, keys: keys) {
                            let entity = dataStore.entityForSave(entityType, user: user, id: id)
                            operation[entity.inserted ? "add" : "update"]!++
                            entity.from(jsonArry: itemValues, keys: keys)
                            count++
                            minIdKey = min(minIdKey, id)
                        }
                    }
                }
            }
            
            let total = operation["add"]! + operation["delete"]! + operation["update"]!
            if total > 0 {
                operation["total"] = total
                operations[entityType.name] = operation
            }
        }
        
        #if DEBUG
        Log.v("READY to save data: ")
        for (name, operation) in operations {
            let add = operation["add"]!
            let update = operation["update"]!
            let delete = operation["delete"]!
            let total = operation["total"]!
            Log.v("\t\t\(name)\t\t+ \(add)\t\t* \(update)\t\t- \(delete)\t\t= \(total)")
        }
        #endif
        
        let result = dataStore.saveContext()
        
        if result && count > 0 {
            EventCenter.shared.trigger(R.Event.data_stored, sender: self, userInfo: operations)
        }
        
        return (result, count, minIdKey, operations)
    }
    
    func getIdFrom(#jsonArry: JSON, keys: [String]) -> Int? {
        if let index = keys.indexOf("id") {
            if let values = jsonArry.arrayObject {
                return values[index] as? Int
            }
        }
        return nil
    }
    
    deinit {
        EventCenter.shared.unbind(self)
    }
}