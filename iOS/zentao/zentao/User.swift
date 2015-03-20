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
        case Unknown, Offline, Online
        
        var description: String {
            switch self {
            case .Unknown:
                return "Unknown"
            case .Offline:
                return "Offline"
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
    var syncFrequency: Int = 300 // seconds
    var id: String?
    var company: String?
    var gender: String?
    var role: String?
    var realName: String?
    var email: String?
    var config: ZentaoConfig?
    var lastSyncTime: NSDate? {
        didSet {
            println("User setted lastSyncTime to " + (lastSyncTime != nil ? lastSyncTime!.toString() : "nil"))
        }
    }
    var lastLoginTime: NSDate? {
        didSet {
            println("User setted lastLoginTime to " + (lastLoginTime != nil ? lastLoginTime!.toString() : "nil"))
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
            
            return currentStatus
        }
        set {
            currentStatus = newValue
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