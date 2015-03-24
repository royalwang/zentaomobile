//
//  Synchronizer.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

class Synchronizer {
   
    lazy var app: ZentaoApp = {
        return ZentaoApp.sharedInstance
    }()
    
    init(){
        EventCenter.shared.bind(self).on(R.Event.timer_tick) += {
            self.sync()
        }
    }
    
    func sync() {
        sync(.Default) {
            result in
            Log.v(result)
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
                            self.app.profile.save()
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
        var deepSyncConfig:[EntityType: Int] = [:]
        for entityType in EntityType.values {
            deepSyncConfig[entityType] = 0
        }
        
        var range = 0
        var thisSyncTime = NSDate()
        let user = app.getUser()!
        var deepSyncRequest: ((Bool) -> Void) -> Void = {_ in}
        deepSyncRequest = { (requestComplete: ((result: Bool) -> Void)) in
            var isOver = true
            for (entityType, var range) in  deepSyncConfig {
                if range < 0 {continue}
                isOver = false
                ZentaoAPI.getItemList(entityType, user: user, options: (type: "increment", range: range, records: 500, format: "index")) {
                    (result: Bool, jsonData: JSON?, message: String?) in
                    if result {
                        if let data = jsonData {
                            let (result, count, minIdKey) = self.saveData(data)
                            if minIdKey == Int.max || count < 500 {
                                range = -1
                            } else {
                                range = minIdKey
                            }
                            deepSyncConfig[entityType] = range
                        }
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
    
    func saveData(data: JSON) -> (result: Bool, count: Int, minIdKey: Int) {
        let dataStore = app.dataStore
        let user = app.getUser()!
        var count = 0, minIdKey = Int.max
        for entityType in EntityType.values {
            let set = data[entityType.name.lowercaseString]
            let keySet = set["key"].arrayObject as [String]?
            let valueSet = set["set"].array
            if let keys = keySet {
                if let values = valueSet {
                    for itemValues in values {
                        if let id = getIdFrom(jsonArry: itemValues, keys: keys) {
                            let entity = dataStore.entityForSave(entityType, user: user, id: id)
                            entity.from(jsonArry: itemValues, keys: keys)
                            count++
                            minIdKey = min(minIdKey, id)
                        }
                    }
                }
            }
        }
        return (dataStore.saveContext(), count, minIdKey)
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