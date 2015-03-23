//
//  HomeViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/19.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class HomeViewController: ZentaoViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var companyLabel: UILabel!
    @IBOutlet weak var helloUserLabel: UILabel!
    @IBOutlet weak var tableView: UITableView!
    
    let items = EntityType.values - .Default
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.accentSwatch = MaterialColor.Blue
        self.showNavigationBarShadow = false
        
        // Register class to tableview
        tableView.dataSource = self
        tableView.delegate = self

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
        if let user = app.getUser() {
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
            
            helloUserLabel.text = "\(user.displayName)，\(hello)！"
        }
    }
    
    // MARK: - Table View
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.items.count;
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        var cell: UITableViewCell = tableView.dequeueReusableCellWithIdentifier("cell") as UITableViewCell
        let entityType = self.items[indexPath.row]
        let contentView = cell.contentView
        let icon = contentView.viewWithTag(R.Tag.icon) as IconUILabel
        let title = contentView.viewWithTag(R.Tag.title) as UILabel
        let subtitle = contentView.viewWithTag(R.Tag.subtitle) as UILabel
        let amount = contentView.viewWithTag(R.Tag.amount) as UILabel
        let description = contentView.viewWithTag(R.Tag.description) as UILabel
        
        icon.text        = entityType.icon.text
        icon.textColor   = entityType.swatch.P400.hue.color
        title.text       = entityType.displayName
        subtitle.text    = entityType.name
        amount.text      = "0"
        amount.textColor = entityType.swatch.primary.hue.color
        
        Log.d(cell.frame.height)
        return cell
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        Log.d("You selected cell #\(indexPath.row)!")
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
