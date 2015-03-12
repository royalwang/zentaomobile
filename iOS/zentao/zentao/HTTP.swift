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
   
    static func get(url: String, success:((HTTPResponse) -> Void)!,
        failure:((NSError, HTTPResponse?) -> Void)!, gzip: Bool = withGZip) {
        let request = HTTPTask()
        if gzip {
            request.requestSerializer = HTTPRequestSerializer()
            request.requestSerializer.headers["Accept-Encoding"] = "gzip"
        }
        request.GET(url, parameters: nil, success: success, failure: failure)
    }
    
    static func get(url: String, success:((String?) -> Void)!,
        failure:((NSError, HTTPResponse?) -> Void)!, gzip: Bool = true) {
        get(url, success: {(response: HTTPResponse) -> Void in
            success(response.text())
            }, failure: failure, gzip: gzip)
    }
    
    static func get(url: String, complete:((String?) -> Void)!, gzip: Bool = true) {
        get(url, success: complete, failure: {(error: NSError, response: HTTPResponse?) -> Void in
            complete(nil)
        }, gzip: gzip)
    }
    
    static func get(url: String, success:((JSON?) -> Void)!,
        failure:((NSError, HTTPResponse?) -> Void)!, gzip: Bool = true) {
        get(url, success: {(response: HTTPResponse) -> Void in
            success(response.responseObject != nil ? JSON(data: response.responseObject as NSData, options: NSJSONReadingOptions.AllowFragments, error: nil) : nil)
        }, failure: failure, gzip: gzip)
    }
    
    static func get(url: String, complete: ((JSON?) -> Void)!, gzip: Bool = true) {
        get(url, success: complete, failure: {(error: NSError, response: HTTPResponse?) -> Void in
            complete(nil)
        }, gzip: gzip)
    }
}