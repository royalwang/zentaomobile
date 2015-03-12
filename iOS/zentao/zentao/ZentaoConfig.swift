//
//  ZentaoConfig.swift
//  zentao
//
//  Created by Sun Hao on 15/3/12.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

///
/// ZentaoConfig
///
/// config as json like: {"version":"6.3","requestType":"PATH_INFO","pathType":"clean",
/// "requestFix":"-","moduleVar":"m","methodVar":"f","viewVar":"t","sessionVar":"sid",
/// "sessionName":"sid","sessionID":"joj7nhuq6mk0snot551oaju405","rand":4396,"expiredTime":"1440"}
struct ZentaoConfig {
    let version: String?
    let requestType: String?
    let requestFix: String?
    let moduleVar: String?
    let methodVar: String?
    let viewVar: String?
    let sessionName: String?
    let sessionID: String?
    let rand: Int?
    let expiredTime: String?
    
    var isPro: Bool {
        get {
            return version!.lowercaseString.rangeOfString("pro") != nil
        }
    }
    
    var versionNumber: Float {
        get {
            return self.version!.extractNumber().floatValue
        }
    }
    
    var isPathInfoRequestType: Bool {
        get {
            return requestType!.uppercaseString == "PATH_INFO";
        }
    }
    
    init(json: JSON) {
        version = json["version"].stringValue
        requestType = json["requestType"].stringValue
        requestFix = json["requestFix"].stringValue
        moduleVar = json["moduleVar"].stringValue
        methodVar = json["methodVar"].stringValue
        viewVar = json["viewVar"].stringValue
        sessionName = json["sessionName"].stringValue
        sessionID = json["sessionID"].stringValue
        rand = json["rand"].int
        expiredTime = json["expiredTime"].stringValue
    }
}