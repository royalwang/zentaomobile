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
        failure:((NSError, HTTPResponse?) -> Void)!) {
        let request = HTTPTask()
        if HTTP.withGZip {
            request.requestSerializer = HTTPRequestSerializer()
            request.requestSerializer.headers["Accept-Encoding"] = "gzip"
        }
        request.GET(url, parameters: nil, success: {
            Log.v("🌏HTTP GET \(url) (SUCCESS)")
            Log.v("  HTTP GET ResponseText=\($0.text()?.limitLength(100))")
            success($0)
        }, failure: {
            Log.w("🌏HTTP GET \(url) (FAILED)")
            Log.w("  HTTP GET Error=\($1)")
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