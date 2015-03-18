//
//  ZentaoApp.swift
//  zentao
//
//  Created by Sun Hao on 15/3/17.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import UIKit

class ZentaoApp {
    
    var profile = UserProfile.sharedInstance
    
    var dataStore: CoreDataStore {
        return (UIApplication.sharedApplication().delegate as AppDelegate).dataStore
    }
    
    var user: User? {
        return profile.tempUser
    }
    
    func getUser(var identify: String = "", allowTemp: Bool = true) -> User? {
        return profile.getUser(identify: identify, allowTemp: allowTemp)
    }
    
    func login(complete: ((result: Bool, error: String, message: String) -> Void)) {
        let u = self.user
        if u != nil && u!.hasLoginCredentials {
            ZentaoAPI.login(user!) {
                (r, error, message) in
                if r {
                    self.profile.save()
                }
                complete(result: r, error: error, message: message)
            }
            return
        }
        complete(result: false, error: "USER_INFO_REQUIRED", message: "The user info is not completion.")
    }
    
    func checkLogin(complete: (result: Bool) -> Void) {
        let status = user?.status ?? .Unknown
        var result = false
        if status == .Offline {
            login() {
                (r, error, message) in
                complete(result: r)
            }
            return
        } else {
            result = status == .Online
        }
        complete(result: result)
    }
}