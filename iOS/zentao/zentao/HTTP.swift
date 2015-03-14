//
//  HTTP.swift
//  zentao
//
//  Created by Sun Hao on 15/3/12.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

/// Http helpers
struct HTTP {
    static var withGZip = true
    static var debug = 1
   
    static func get(url: String, success:((HTTPResponse) -> Void)!,
        failure:((NSError, HTTPResponse?) -> Void)!) {
        let request = HTTPTask()
        if HTTP.withGZip {
            request.requestSerializer = HTTPRequestSerializer()
            request.requestSerializer.headers["Accept-Encoding"] = "gzip"
        }
        request.GET(url, parameters: nil, success: {
            if HTTP.debug > 0 {
                println("HTTP GET \(url) (SUCCESS)")
                println("HTTP GET ResponseText=\($0.text())")
            }
            success($0)
        }, failure: {
            if HTTP.debug > 0 {
                println("HTTP GET \(url) (FAILED)")
                println("HTTP GET Error=\($1)")
            }
            failure($0, $1)
        })
    }
    
    static func get(url: String, success:((String?) -> Void)!,
        failure:((NSError, HTTPResponse?) -> Void)!) {
        get(url, success: {(response: HTTPResponse) -> Void in
            success(response.text())
            }, failure: failure)
    }
    
    static func get(url: String, complete:((String?) -> Void)!, gzip: Bool = true) {
        get(url, success: complete, failure: {(error: NSError, response: HTTPResponse?) -> Void in
            complete(nil)
        })
    }
    
    static func get(url: String, success:((JSON?) -> Void)!,
        failure:((NSError, HTTPResponse?) -> Void)!) {
        get(url, success: {(response: HTTPResponse) -> Void in
            success(response.responseObject != nil ? JSON(data: response.responseObject as NSData, options: NSJSONReadingOptions.AllowFragments, error: nil) : nil)
        }, failure: failure)
    }
    
    static func get(url: String, complete: ((JSON?) -> Void)!) {
        get(url, success: complete, failure: {(error: NSError, response: HTTPResponse?) -> Void in
            complete(nil)
        })
    }
}