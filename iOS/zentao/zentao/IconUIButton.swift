//
//  IconUIButton.swift
//  zentao
//
//  Created by Sun Hao on 15/3/20.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class IconUIButton: UIButton {

    // Only override drawRect: if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func drawRect(rect: CGRect) {
        // Change the font name to "fontawesome"
        if let label = self.titleLabel {
            label.font = UIFont(name: FontIcon.name, size: label.font.pointSize)
        }
        // Drawing code
        super.drawRect(rect)
    }
}
