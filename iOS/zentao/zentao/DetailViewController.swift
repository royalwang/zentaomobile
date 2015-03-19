//
//  DetailViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/11.
//  Copyright (c) 2015年 SunHao@cnezsoft.com. All rights reserved.
//

import UIKit

class DetailViewController: UIViewController {

    @IBOutlet weak var detailDescriptionLabel: UILabel!


    var detailItem: AnyObject? {
        didSet {
            // Update the view.
            self.configureView()
        }
    }

    func configureView() {
        // Update the user interface for the detail item.
        if let detail: AnyObject = self.detailItem {
            if let label = self.detailDescriptionLabel {
                label.text = detail.valueForKey("timeStamp")!.description
            }
        }
        
        var label = UILabel(frame: CGRectMake(0, 90, 120, 40))
        label.font = UIFont(name: "FontAwesome", size: 40)
        label.text = String(format: "%C", 0xf001)
        label.textColor = UIColor.redColor()
        self.view.addSubview(label)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.configureView()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

