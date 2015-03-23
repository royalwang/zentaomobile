//
//  UIColorExtension.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import UIKit

extension UIColor {
   
    func toImage() -> UIImage {
        let rect = CGRectMake(0, 0, 1, 1)
        UIGraphicsBeginImageContextWithOptions(rect.size, true, 0)
        self.setFill()
        UIRectFill(rect)
        var image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }
}