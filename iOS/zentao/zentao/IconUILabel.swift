//
//  IconUILabel.swift
//  zentao
//
//  Created by Sun Hao on 15/3/19.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class IconUILabel: UILabel {

    // Only override drawRect: if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func drawRect(rect: CGRect) {
        
        // Change the font name to "fontawesome"
        self.font = UIFont(name: FontIcon.name, size: self.font.pointSize)
        // Drawing code
        super.drawRect(rect)
    }

}
