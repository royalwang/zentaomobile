//
//  User.swift
//  zentao
//
//  Created by Sun Hao on 15/3/12.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

class User {
    let password_with_md5_flag = "%%%PWD_FLAG%%% "
    let password_with_md5_flag_length = 15
    
    let account: String
    let address: String
    private var password: String
    var config: ZentaoConfig?
    
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