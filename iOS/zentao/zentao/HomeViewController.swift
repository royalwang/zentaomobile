//
//  HomeViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/19.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class HomeViewController: ZentaoViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.accentSwatch = MaterialColorSwatch.Blue
        self.showNavigationBarShadow = false
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func onLoginSuccess() {
        println("login success message!")
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
