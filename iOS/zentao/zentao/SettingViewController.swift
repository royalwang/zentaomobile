//
//  SettingViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/31.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class SettingViewController: ZentaoViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.accentSwatch = MaterialColor.Blue
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onDoneClick(sender: AnyObject) {
        self.dismissViewControllerAnimated(true, nil)
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
