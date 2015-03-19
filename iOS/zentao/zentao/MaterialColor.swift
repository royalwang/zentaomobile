//
//  MaterialColorName.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import UIKit

extension MaterialColorSwatch {
    
    struct Color: Printable {
        let val: Int
        
        var uicolor: UIColor {
            get {
                return UIColor(hex: val)
            }
        }
        
        var red: Int {
            get {
                return (val & 0xFF0000) >> 16
            }
        }

        var green: Int {
            get {
                return (val & 0xFF00) >> 8
            }
        }

        var blue: Int {
            get {
                return val & 0xFF
            }
        }

        var isInverseFront: Bool {
            get {
                return ((val & 0xF000000) >> 24) > 0
            }
        }

        var inverseFronUIColor: UIColor {
            get {
                return isInverseFront ? UIColor.whiteColor() : UIColor.blackColor();
            }
        }
        
        var description: String {
            let hex = NSString(format:"%2X", val) as String
            return "{\(hex), R: \(red), G: \(green), B: \(blue)}"
        }
    }
    
    subscript(name: Name) -> Color {
        get {
            return Color(val: self[name.rawValue])
        }
    }
    
    var primaryColor: Color {
        get {
            return self[.C500]
        }
    }
}