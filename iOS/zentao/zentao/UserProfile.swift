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
    
    class func getUser(var identify: String = "", allowTemp: Bool = true) -> User? {
        return sharedInstance.getUser(identify: identify, allowTemp: allowTemp)
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
    
    var isTempUserSave = false
    
    var tempUser: User? {
        didSet {
            isTempUserSave = false
        }
    }
    
    var user: User? {
        return tempUser
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
    
    func getUser(var identify: String = "", allowTemp: Bool = true) -> User? {
        if identify.isEmpty {
            identify = self.identify
            if identify.isEmpty {
                return nil
            }
        } else {
            self.identify = identify
        }
        
        if allowTemp && tempUser != nil && tempUser!.identify != nil
            && tempUser!.identify! == identify {
            return tempUser
        }
        
        let address = self["address"].string ?? ""
        let account = self["account"].string ?? ""
        let password = self["password"].string ?? ""
        tempUser = User(address: address, account: account, password: password)
        tempUser!.lastLoginTime = self["lastLoginTime"].date
        tempUser!.lastSyncTime = self["lastSyncTime"].date
        tempUser!.notifyEnable = self["notifyEnable"].bool ?? tempUser!.notifyEnable
        tempUser!.syncFrequency = self["syncFrequency"].int ?? tempUser!.syncFrequency
        tempUser!.id = self["id"].string
        tempUser!.company = self["company"].string
        tempUser!.gender = self["gender"].string
        tempUser!.role = self["role"].string
        tempUser!.realName = self["realName"].string
        tempUser!.email = self["email"].string
        
        Log.v("👵 GET USER: \(user)")
        return tempUser
    }
    
    func saveUser() -> Bool {
        if let user = tempUser {
            isTempUserSave = true
            return saveUser(user)
        }
        return false
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
            tempUser = user
            Log.v("👵 SAVE USER: \(user)")
            return save()
        }
        return false
    }
}