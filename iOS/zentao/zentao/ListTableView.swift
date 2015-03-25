//
//  ListTableView.swift
//  zentao
//
//  Created by Sun Hao on 15/3/25.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class ListTableView: UITableView {
    
    // Only override drawRect: if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func drawRect(rect: CGRect) {
        separatorColor = MaterialColor.Grey.P300.hue.color
        tableFooterView = UIView(frame:CGRectZero)
        
        super.drawRect(rect)
        // Drawing code
    }

}
