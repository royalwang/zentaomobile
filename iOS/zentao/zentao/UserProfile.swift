//
//  UserProfile.swift
//  zentao
//
//  Created by Sun Hao on 15/3/15.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

class UserProfile {
    
    /// singleton
    private struct SingletonKeeper {
        static let instance = UserProfile()
    }
    
    class var sharedInstance: UserProfile {
        get {
            return SingletonKeeper.instance
        }
    }
    
    let CURRENT_IDENTIFY = "#CURRENT_IDENTIFY"
    var onIdentifyChange: [(identify: String) -> Void]?
    let profile: NSUserDefaults
    var identify: String {
        didSet {
            if !identify.isEmpty {
                profile[CURRENT_IDENTIFY] = identify
                save()
            }
            if oldValue != identify {
                if let identifyChangeListeners = onIdentifyChange {
                    for listener in identifyChangeListeners {
                        listener(identify: identify)
                    }
                }
            }
        }
    }
    
    private init() {
        profile = NSUserDefaults.standardUserDefaults()
        identify = profile[CURRENT_IDENTIFY].string ?? ""
    }
    
    private func getIdentifyName(shortName: String) -> String {
        return "\(identify)::\(shortName)"
    }
    
    private func getShortNameFromIdentifyName(identifyName: String) -> String {
        let prefix = "\(identify)::"
        if identifyName.hasPrefix(prefix) {
            return identifyName.subString(prefix.length)
        }
        return identifyName
    }
    
    subscript(name: String) -> NSUserDefaults.Proxy {
        get {
            return profile[getIdentifyName(name)]
        }
    }
    
    subscript(name: String) -> Any? {
        get {
            return self[name]
        }
        set {
            profile[getIdentifyName(name)] = newValue
        }
    }
    
    func save() -> Bool {
        return profile.synchronize()
    }
    
    func getUser(var identify: String = "") -> User? {
        if identify.isEmpty {
            identify = self.identify
            if identify.isEmpty {
                return nil
            }
        } else {
            self.identify = identify
        }
        
        let address = self["address"].string ?? ""
        let account = self["account"].string ?? ""
        let password = self["password"].string ?? ""
        let user = User(address: address, account: account, password: password)
        user.lastLoginTime = self["lastLoginTime"].date
        user.lastSyncTime = self["lastSyncTime"].date
        user.notifyEnable = self["notifyEnable"].bool ?? user.notifyEnable
        user.syncFrequency = self["syncFrequency"].int ?? user.syncFrequency
        user.id = self["id"].string
        user.company = self["company"].string
        user.gender = self["gender"].string
        user.role = self["role"].string
        user.realName = self["realName"].string
        user.email = self["email"].string
        return user
    }
    
    func saveUser(user: User) -> Bool {
        if let identify = user.identify {
            self.identify = identify
            self["address"] = user.address
            self["account"] = user.account
            self["password"] = user.passwordMD5WithFlag
            self["lastLoginTime"] = user.lastLoginTime
            self["lastSyncTime"] = user.lastSyncTime
            self["notifyEnable"] = user.notifyEnable
            self["syncFrequency"] = user.syncFrequency
            self["id"] = user.id
            self["company"] = user.company
            self["gender"] = user.gender
            self["role"] = user.role
            self["realName"] = user.realName
            self["email"] = user.email
            return save()
        }
        return false
    }
}