//
//  HomeViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/19.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class HomeViewController: ZentaoViewController {

    @IBOutlet weak var companyLabel: UILabel!
    @IBOutlet weak var helloUserLabel: UILabel!
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.accentSwatch = MaterialColor.Blue
        self.showNavigationBarShadow = false
        
        sayHelloToUser()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func onLoginSuccess() {
        println("login success message!")
        sayHelloToUser()
    }
    
    func sayHelloToUser() {
        if let user = app.user {
            companyLabel.text = user.company

            let hour = NSDate().hour()
            var hello: String
            if hour < 11 {
                hello = "上午好"
            } else if hour < 14 {
                hello = "中午好"
            } else if hour < 18 {
                hello = "下午好"
            } else {
                hello = "晚上好"
            }
            
            helloUserLabel.text = "\(hello)，\(user.displayName)"
        }
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
