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
                    + "&password=\(user.passwordMD5WithRand)"
                    + "&\(user.config!.sessionName)"
                    + "=\(user.config!.sessionID)"
                    + "&t=\(viewType)"
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
                                complete(result: true, error: "", message: "")
                            } else {
                                complete(result: false, error: "WRONG_ACCOUNT", message: jsonObj["message"].string
                                    ?? "The account or password is not correct.")
                            }
                        } else {
                            complete(result: false, error: "LOGIN_FAILED", message: "Can't login to zentao server now.")
                        }
                    })
                } else {
                    complete(result: false, error: "WRONG_VERSION", message: "The Zentao version is not supported.")
                }
            } else {
                complete(result: false, error: "WRONG_CONNECT", message: "Unable to connect to Zentao server.")
            }
        }
    }
}
