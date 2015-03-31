//
//  User.swift
//  zentao
//
//  Created by Sun Hao on 15/3/12.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

class User: Printable {
    
    enum Status: Printable {
        case Unknown, Offline, Online, ForceOffline
        
        var description: String {
            switch self {
            case .Unknown:
                return "Unknown"
            case .Offline:
                return "Offline"
            case .ForceOffline:
                return "ForceOffline"
            case .Online:
                return "Online"
            }
        }
    }
    
    class func createIdentify(thisAddress: String, thisAccount: String) -> String? {
        if thisAddress.isEmpty || thisAccount.isEmpty {
            return nil
        }
        return "\(thisAccount)@\(thisAddress)"
    }
    
    class func getAccountFromIdentify(identify: String) -> String? {
        return identify.length > 1 ? identify[0...identify.indexOf("@")] : nil;
    }
    
    class func getAdressFromIdentify(identify: String) -> String? {
        return identify.length > 1 ? identify.subString(identify.indexOf("@")) : nil;
    }
    
    let password_with_md5_flag = "%%%PWD_FLAG%%% "
    let password_with_md5_flag_length = 15
    
    let account: String
    let address: String
    private var password: String
    var notifyEnable: Bool = true
    var syncFrequency: Int = 15 // seconds
    var id: String?
    var company: String?
    var gender: String?
    var role: String?
    var realName: String?
    var email: String?
    var config: ZentaoConfig?
    var lastSyncTime: NSDate? {
        didSet {
            Log.v("User set lastSyncTime=" + (lastSyncTime != nil ? "\(lastSyncTime!)" : "nil"))
        }
    }
    var lastLoginTime: NSDate? {
        didSet {
            Log.v("User set lastLoginTime=" + (lastLoginTime != nil ? "\(lastLoginTime!)" : "nil"))
        }
    }
    
    init(var address: String, account: String, password: String) {
        self.account = account
        
        address = address.lowercaseString
        if !address.hasPrefix("http://") && !address.hasPrefix("https://") {
            address = "http://" + address
        }
        self.address = address
        
        self.password = password
        setPassword(password)
    }
    
    func fromJSON(json: JSON) {
        if !json.isNullOrEmpty {
            id = json["id"].string
            email = json["email"].string
            company = json["company"].string
            realName = json["realname"].string
            gender = json["gender"].string
            role = json["role"].string
        }
    }
    
    var lastSyncTimeText: String {
        return lastSyncTime?.timeAgo ?? "从未同步"
    }
    
    var syncFrequencyText: String {
        let hour: Int = syncFrequency / 3600
        let min: Int = (syncFrequency % 3600) / 60
        let seconds: Int = syncFrequency % 60
        var text = ""
        if hour > 0 {
            text += "\(hour)小时"
        }
        if min > 0 {
            text += "\(min)分钟"
        }
        if seconds > 0 {
            text += "\(seconds)秒"
        }
        return text
    }
    
    var displayName: String {
        return realName ?? account
    }
    
    var displayAccount: String {
        if realName == nil || realName! == account {
            return account
        }
        return "\(realName!) (\(account))"
    }
    
    private var currentStatus = Status.Unknown
    
    var status: Status {
        get {
            if config == nil && currentStatus == .Online {
                currentStatus = .Offline
            }
            
            if !hasLoginCredentials {
                currentStatus = .Unknown
            } else if currentStatus == .Unknown {
                currentStatus = .Offline
            }
            
            if currentStatus == .ForceOffline {
                return .Unknown
            }
            
            return currentStatus
        }
        set {
            currentStatus = newValue
            Log.d("User status set to \(newValue.description)")
        }
    }
    
    var isOnline: Bool {
        return status == .Online
    }
    
    var isOffline: Bool {
        return status != .Online
    }
    
    var zentao: String {
        return address.lowercaseString
    }
    
    var description: String {
        return "{address: \(address), account: \(account), password: \(passwordMD5WithFlag), lastSyncTime: \(lastSyncTime), lastLoginTime: \(lastLoginTime), notifyEnable: \(notifyEnable), syncFrequency: \(syncFrequency), id: \(id), company: \(company), gender: \(gender), role: \(role), realName: \(realName), email: \(email)}"
    }
    
    var identify: String? {
        return User.createIdentify(address, thisAccount: account)
    }
    
    var hasLoginCredentials: Bool {
        get {
            return !address.isEmpty && !account.isEmpty && !passwordMD5.isEmpty
        }
    }
    
    var isNeverSynced: Bool {
        get {
            return lastSyncTime == nil
        }
    }
    
    var isNeverLogined: Bool {
        get {
            return lastLoginTime == nil
        }
    }
    
    var passwordMD5WithRand: String {
        get {
            if let cfg = config {
                return (passwordMD5 + String(cfg.rand!)).md5
            }
            return "";
        }
    }
    
    var passwordMD5WithFlag: String {
        get {
            if !password.isEmpty && !password.hasPrefix(password_with_md5_flag) {
                return password_with_md5_flag + password
            }
            return password
        }
    }
    
    var passwordMD5: String {
        get {
            if password.hasPrefix(password_with_md5_flag) {
                return (password as NSString).substringFromIndex(password_with_md5_flag_length)
            }
            return password
        }
    }
    
    func setPassword(var passwordStr: String) {
        if !passwordStr.isEmpty && !passwordStr.hasPrefix(password_with_md5_flag) {
            passwordStr = password_with_md5_flag + passwordStr.md5!
        }
        password = passwordStr
    }
}