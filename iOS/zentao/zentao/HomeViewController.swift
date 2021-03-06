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
    
    var dataSet: [CoreDataStore.EntitySummery]?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.accentSwatch = MaterialColor.Blue
        self.showNavigationBarShadow = false
        
        // Register class to tableview
        tableView.dataSource = self
        tableView.delegate = self

        sayHelloToUser()
        EventCenter.shared.bind(self).on(R.Event.login_success) += {
            self.sayHelloToUser()
            Log.s("say hello to user from event center")
        }
        
        loadData()
        
        EventCenter.shared.bind(self).on(R.Event.data_stored) += {
            self.loadData()
        }
    }
    
    func loadData() {
        let task = TaskQueue()
        task.tasks +=! {
            _, next in
            let store = self.app.dataStore
            let user = self.app.getUser()!
            self.dataSet = store.getSummery(user)
            next(nil)
        }
        
        task.tasks += {
            self.tableView.reloadData()
        }
        
        task.run()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
        return dataSet?.count ?? (EntityType.values.count - 1);
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        var cell: UITableViewCell = tableView.dequeueReusableCellWithIdentifier("cell") as UITableViewCell
        let entityType = dataSet?[indexPath.row].type ?? EntityType.values[indexPath.row]
        let contentView = cell.contentView
        let icon = contentView.viewWithTag(R.Tag.icon) as IconUILabel
        let title = contentView.viewWithTag(R.Tag.title) as UILabel
        let subtitle = contentView.viewWithTag(R.Tag.subtitle) as UILabel
        let amount = contentView.viewWithTag(R.Tag.amount) as UILabel
        let description = contentView.viewWithTag(R.Tag.description) as UILabel
        
        icon.text        = entityType.icon.text
        icon.textColor   = entityType.swatch.P400.hue.color
        title.text       = entityType.displayName
        amount.text      = "0"
        amount.textColor = Color.lightGrayColor()
        
        if let summery = dataSet?[indexPath.row] {
            subtitle.text = summery.subtitle
            amount.text = "\(summery.amount)"
            description.text = summery.amountType
            if summery.amount > 0 {
                amount.textColor = entityType.swatch.primary.hue.color
            }
        }
        
        return cell
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        self.app.shareBundle["SelectedTab"] = EntityType.values[indexPath.row + 1]
    }
    
//    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
//        if segue.identifier == "homeToList" {
//            let vc = segue.destinationViewController as ListViewController
//            Log.d("Entity tab set to \(choosedTab.name) in 'Home'.")
//        }
//    }


    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
}
