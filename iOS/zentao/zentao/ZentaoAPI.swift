//
//  ZentaoAPI.swift
//  zentao
//
//  Created by Sun Hao on 15/3/12.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

struct ZentaoAPI {

    /// Concat api url
    static func concatUrl(params: [String:String], withUser user: User) -> String {
        let moduleName = params["module"] ?? "api"
        let methodName: String = params["method"]!
        let moduleNameIdentifier = moduleName.lowercaseString
        let methodNameIdentifier = methodName.lowercaseString
        let viewType = params["viewType"] ?? "json"
        var url = user.address.hasSuffix("/") ? user.address : user.address + "/";
        
        if(user.config!.isPathInfoRequestType) {
            if moduleNameIdentifier == "user" && methodNameIdentifier == "login" {
                url += "user-login.\(viewType)"
                    + "?account=\(user.account)&password=\(user.passwordMD5WithRand)"
                    + "&\(user.config!.sessionName!)=\(user.config!.sessionID!)"
                return url;
            }
            
            url += "\(moduleName)-\(methodName)-"
            
            if moduleNameIdentifier == "api" {
                if methodNameIdentifier == "mobilegetlist" {
                    url += (params["type"] ?? "full") + "-"
                    url += (params["object"] ?? "all") + "-"
                    url += (params["range"] ?? "0") + "-"
                    url += (params["last"] ?? "") + "-"
                    url += (params["records"] ?? "1000") + "-"
                    url += (params["format"] ?? "index")
                } else if methodNameIdentifier == "mobilegetinfo" {
                    url += params["id"]! + "-" + params["type"]!
                }
            }
            
            if url.hasSuffix("-") {
                url = (url as NSString).substringToIndex(countElements(url) - 1)
            }
            
            url += ".\(viewType)?\(user.config!.sessionName!)=\(user.config!.sessionID!)"
        } else {
            url += "/index.php?"
            if moduleNameIdentifier == "user" && methodNameIdentifier == "login" {
                url += "m=user&f=login&account=\(user.account)"
                url += "&password=\(user.passwordMD5WithRand)"
                url += "&\(user.config!.sessionName)"
                url += "=\(user.config!.sessionID)"
                url += "&t=\(viewType)"
                return url;
            }
            
            url += "m=\(moduleName)&f=\(methodName)"
            
            if moduleNameIdentifier == "api" {
                if methodNameIdentifier == "mobilegetlist" {
                    url += "&type=" + (params["type"] ?? "full")
                    url += "&object=" + (params["object"] ?? "all")
                    url += "&range=" + (params["range"] ?? "0")
                    url += "&last=" + (params["last"] ?? "")
                    url += "&records=" + (params["records"] ?? "all")
                    url += "&format=" + (params["format"] ?? "index");
                } else if methodNameIdentifier == "mobilegetinfo" {
                    url += "&id=" + params["id"]! + "&type=" + params["type"]!
                }
            }
            
            url += "&type=\(viewType)&\(user.config!.sessionName!)=\(user.config!.sessionID!)"
        }
        
        return url;
    }
    
    static func getConfig(address: String, complete: ((ZentaoConfig?) -> Void)) {
        HTTP.get(address + "/index.php?mode=getconfig", complete: {(json: JSON?) -> Void in
            if let jsonObj = json {
                complete(ZentaoConfig(json:jsonObj))
            } else {
                complete(nil)
            }
        })
    }
    
    static func login(user: User, complete: ((result: Bool, error: String, message: String) -> Void)) {
        getConfig(user.address) {
            if let config = $0 {
                user.config = config
                if config.isZentaoAPIAvailable {
                    HTTP.get(self.concatUrl(["module": "user", "method": "login"], withUser: user), complete: {
                        (json: JSON?) -> Void in
                        if let jsonObj = json {
                            if jsonObj["status"].stringValue == "success" {
                                user.fromJSON(jsonObj["user"])
                                user.status = .Online
                                user.lastLoginTime = NSDate()
                                complete(result: true, error: "", message: "登录成功！")
                            } else {
                                complete(result: false, error: "WRONG_ACCOUNT", message: jsonObj["message"].string
                                    ?? "账号或密码错误。")
                            }
                        } else {
                            complete(result: false, error: "LOGIN_FAILED", message: "暂时无法登录到禅道服务器。")
                        }
                    })
                } else {
                    complete(result: false, error: "WRONG_VERSION", message: "此禅道版本不受支持，需要禅道专业版4.5及以上。")
                }
            } else {
                complete(result: false, error: "WRONG_CONNECT", message: "暂时无法连接到禅道服务器。")
            }
        }
    }
    
    static func getItem(entityType: EntityType, id: String, user: User, complete: ((result: Bool, jsonData: JSON?, message: String?) -> Void)) {
        let params = [
            "module": "api",
            "method": "mobileGetInfo",
            "type": entityType.name.lowercaseString,
            "id": id]
        HTTP.get(self.concatUrl(params, withUser: user)) {
            (json: JSON?) in
            if let jsonObj = json {
                if jsonObj["status"].stringValue == "success" {
                    let jsonData = jsonObj["data"]
                    if jsonData.isNullOrEmpty {
                        complete(result: true, jsonData: nil, message: "Success, but no new data there.")
                    } else {
                        complete(result: true, jsonData: jsonData, message: nil)
                    }
                } else {
                    complete(result: false, jsonData: nil, message: "The response from zentao server return in a wrong format.")
                }
                return
            }
            complete(result: false, jsonData: nil, message: "Can't connect to zentao server.")
        }
    }
    
    static func getItem(entityType: EntityType, id: Int, user: User, complete: ((result: Bool, jsonData: JSON?, message: String?) -> Void)) {
        getItem(entityType, id: String(id), user: user, complete)
    }
    
    static func getItemList(entityType: EntityType, user: User,
        options: (type: String, range: Int, records: Int, format: String), complete:
        ((result: Bool, jsonData: JSON?, message: String?) -> Void)) {
            
            let params = [
                "module": "api",
                "method": "mobileGetList",
                "object": entityType == .Default ? "all" : entityType.name.lowercaseString,
                "type": options.type,
                "range": String(options.range),
                "last": !user.isNeverSynced ? String(Int(round(user.lastSyncTime!.timeIntervalSince1970))) : "0",
                "records": String(options.records),
                "format": String(options.format)]
            
            HTTP.get(self.concatUrl(params, withUser: user)) {
                (json: JSON?) in
                if let jsonObj = json {
                    if jsonObj["status"].stringValue == "success" {
                        let jsonData = jsonObj["data"]
                        if jsonData.isNullOrEmpty {
                            complete(result: true, jsonData: nil, message: "Success, but no new data there.")
                        } else {
                            complete(result: true, jsonData: jsonData, message: nil)
                        }
                    } else {
                        complete(result: false, jsonData: nil, message: "The response from zentao server return in a wrong format.")
                    }
                    return
                }
                complete(result: false, jsonData: nil, message: "Can't connect to zentao server.")
            }
    }
    
    static func getItemList(entityType: EntityType, user: User, complete:
        ((result: Bool, jsonData: JSON?, message: String?) -> Void)) {
            getItemList(entityType, user: user, options: ("increment", 0, 1000, "index"), complete)
    }
    
    static func getItemList(user: User, complete:
        ((result: Bool, jsonData: JSON?, message: String?) -> Void)) {
            getItemList(.Default, user: user, options: ("increment", 0, 1000, "index"), complete)
    }
}
