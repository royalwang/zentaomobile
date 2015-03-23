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
    
    // Singleton
    private struct SingletonKeeper {
        static let instance = ZentaoApp()
    }
    
    class var sharedInstance: ZentaoApp {
        get {
            return SingletonKeeper.instance
        }
    }
    
    private init() {}
    
    
    var profile: UserProfile {
        return UserProfile.sharedInstance
    }
    
    var dataStore: CoreDataStore {
        return CoreDataStore.sharedInstance
    }
    
    var user: User? {
        return profile.tempUser ?? profile.getUser()
    }
    
    func getUser(var identify: String = "", allowTemp: Bool = true) -> User? {
        return profile.getUser(identify: identify, allowTemp: allowTemp)
    }
    
    func switchUser(address: String, account: String, password: String) {
        if let tempUser = user {
            if User.createIdentify(address, thisAccount: account) == tempUser.identify {
                tempUser.setPassword(password)
                return
            }
        }
        profile.tempUser = User(address: address, account: account, password: password)
    }
    
    func login(complete: ((result: Bool, error: String, message: String) -> Void)) {
        let u = self.user
        if u != nil && u!.hasLoginCredentials {
            ZentaoAPI.login(user!) {
                (r, error, message) in
                if r {
                    self.profile.saveUser()
                }
                complete(result: r, error: error, message: message)
            }
            return
        }
        complete(result: false, error: "USER_INFO_REQUIRED", message: "用户信息不完整.")
    }
    
    func checkLogin(complete: (result: Bool) -> Void) {
        let status = user?.status ?? .Unknown
        var result = false
        Log.info("Check user login, status=\(status)")
        if status == .Offline {
            login() {
                (r, error, message) in
                println("Login in backgournd: result=\(result), error=\(error), message=\(message)")
                complete(result: r)
            }
            return
        } else {
            result = status == .Online
        }
        complete(result: result)
    }
}