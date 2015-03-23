//
//  Log.swift
//  zentao
//
//  Created by Sun Hao on 15/3/23.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

struct Log {
    
    enum Type: String {
        case Verbose = "🐤"
        case Debug = "🐞"
        case Success = "✅"
        case Info = "⚡️"
        case Warning = "⚠️"
        case Error = "❌"
        case Head = "▶️"
        
        var icon: String {
            return self.rawValue
        }
    }
    
    static let _lock = NSObject()
    
    static var printHandler: (Type, Any!, String, String, Int) -> Void = {type, body, filename, functionName, line in
        if let body = body as? String {
            if countElements(body) == 0 {
                println("") // print break
                return
            }
        }
        
        if type == .Head {
            let bodyString = body != nil ? "\(body)" : ""
            println("\(type.icon)[\(filename).\(functionName):\(line)] \(bodyString)")
        } else {
            println("\(type.icon)[\(filename):\(line)] \(body)")
        }
    }
    
    static func print(_ type: Type = .Head, _ body: Any! = nil, var filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        #if DEBUG
            objc_sync_enter(_lock)
            
            filename = filename.lastPathComponent.stringByDeletingPathExtension
            self.printHandler(type, body, filename, functionName, line)
            
            objc_sync_exit(_lock)
            
        #endif
    }

    static func printObject(type: Type , _ body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__, funcString: String = "Log.printObject") {
        #if DEBUG
            if let reader = DebugLog.FileReader(filePath: filename) {
                let logBody = "\(reader.readLogLine(line, funcString: funcString)) = \(body)"
                print(type, logBody, filename: filename, functionName: functionName, line: line)
            }
        #endif
    }
    
    static func printClass(type: Type, _ body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        #if DEBUG
            let reader = DebugLog.FileReader(filePath: filename)
            
            let classInfo: DebugLog.ParsedClass = DebugLog.parseClass(body)
            let classString = classInfo.moduleName != nil ? "\(classInfo.moduleName!).\(classInfo.name)" : "\(classInfo.name)"
            
            print(type, classString, filename: filename, functionName: functionName, line: line)
        #endif
    }
    
    
    static func verbose(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Verbose, body, filename: filename, functionName: functionName, line: line, funcString: "Log.verbose")
    }
    
    static func v(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Verbose, body, filename: filename, functionName: functionName, line: line, funcString: "Log.v")
    }
    
    static func verbose(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Verbose, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func v(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Verbose, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func verbose(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Verbose, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func v(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Verbose, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func debug(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Debug, body, filename: filename, functionName: functionName, line: line, funcString: "Log.debug")
    }
    
    static func d(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Debug, body, filename: filename, functionName: functionName, line: line, funcString: "Log.d")
    }
    
    static func debug(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Debug, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func d(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Debug, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func debug(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Debug, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func d(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Debug, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func success(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Success, body, filename: filename, functionName: functionName, line: line, funcString: "Log.success")
    }
    
    static func s(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Success, body, filename: filename, functionName: functionName, line: line, funcString: "Log.s")
    }
    
    static func success(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Success, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func s(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Success, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func success(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Success, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func s(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Success, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func info(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Info, body, filename: filename, functionName: functionName, line: line, funcString: "Log.info")
    }
    
    static func i(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Info, body, filename: filename, functionName: functionName, line: line, funcString: "Log.i")
    }
    
    static func info(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Info, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func i(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Info, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func info(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Info, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func i(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Info, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func warning(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Warning, body, filename: filename, functionName: functionName, line: line, funcString: "Log.warning")
    }
    
    static func w(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Warning, body, filename: filename, functionName: functionName, line: line, funcString: "Log.w")
    }
    
    static func warning(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Warning, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func w(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Warning, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func warning(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Warning, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func w(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Warning, body, filename: filename, functionName: functionName, line: line)
    }
    static func error(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Error, body, filename: filename, functionName: functionName, line: line, funcString: "Log.error")
    }
    
    static func e(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Error, body, filename: filename, functionName: functionName, line: line, funcString: "Log.e")
    }
    
    static func error(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Error, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func e(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Error, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func error(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Error, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func e(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Error, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func head(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Head, body, filename: filename, functionName: functionName, line: line, funcString: "Log.head")
    }
    
    static func h(body: Any, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printObject(.Head, body, filename: filename, functionName: functionName, line: line, funcString: "Log.h")
    }
    
    static func head(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Head, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func h(body: AnyClass, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        printClass(.Head, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func head(body: String, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Head, body, filename: filename, functionName: functionName, line: line)
    }
    
    static func h(body: String! = nil, filename: String = __FILE__, var functionName: String = __FUNCTION__, line: Int = __LINE__) {
        print(.Head, body, filename: filename, functionName: functionName, line: line)
    }
}