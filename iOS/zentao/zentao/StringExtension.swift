//
//  StringExtension.swift
//  zentao
//
//  Created by Sun Hao on 15/3/12.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

extension String {
    var floatValue: Float {
        return (self as NSString).floatValue
    }
    
    func extractNumber(dotted: Bool = true) -> String {
        var excludeDot = !dotted
        var result = "";
        for char in self {
            switch char {
            case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9":
                result.append(char)
            case ".":
                if !excludeDot {
                    result.append(char)
                    excludeDot = true
                }
            default:
                continue
            }
        }
        
        return result;
    }
    
    var md5: String! {
        let str = self.cStringUsingEncoding(NSUTF8StringEncoding)
        let strLen = CC_LONG(self.lengthOfBytesUsingEncoding(NSUTF8StringEncoding))
        let digestLen = Int(CC_MD5_DIGEST_LENGTH)
        let result = UnsafeMutablePointer<CUnsignedChar>.alloc(digestLen)
        
        CC_MD5(str!, strLen, result)
        
        var hash = NSMutableString()
        for i in 0..<digestLen {
            hash.appendFormat("%02x", result[i])
        }
        
        result.dealloc(digestLen)
        
        return String(format: hash)
    }
}