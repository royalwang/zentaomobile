//
//  SettingTableViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/31.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit
import MessageUI

class SettingTableViewController: UITableViewController, MFMailComposeViewControllerDelegate {
    
    let app: ZentaoApp = (UIApplication.sharedApplication().delegate as AppDelegate).app
    
    @IBOutlet weak var versionText: UILabel!
    @IBOutlet weak var userAddressBtn: UIButton!
    @IBOutlet weak var userAccountText: UILabel!
    @IBOutlet weak var syncFreqBtn: IconUIButton!
    @IBOutlet weak var lastSyncTimeText: UILabel!
    @IBOutlet weak var syncStatusText: UILabel!
    @IBOutlet weak var syncBtn: UIButton!

    var appVersion: String = ""
    let freqTypes: [(String, Int)] = [
        ("5秒钟", 5),
        ("30秒钟", 30),
        ("1分钟", 60),
        ("5分钟", 300),
        ("30分钟", 1800),
        ("1小时", 3600),
        ("6小时", 21600)
    ]
    
    @IBAction func changeSyncFreq(sender: AnyObject) {
        let alertController = UIAlertController(title: "请选择同步频率", message: nil, preferredStyle: .Alert)
        
        let defaultAction = UIAlertAction(title: "OK", style: .Default, handler: nil)
        for (name, value) in freqTypes {
            alertController.addAction(UIAlertAction(title: name, style: .Default, handler: {
                _ in
                if let user = self.app.getUser() {
                    user.syncFrequency = value
                    if self.app.profile.saveUser(user) {
                        EventCenter.shared.trigger(R.Event.start_timer, sender: self)
                    }
                }
            }))
        }
       
        presentViewController(alertController, animated: true, completion: nil)
    }
    
    @IBAction func syncNow(sender: AnyObject) {
        EventCenter.shared.trigger(R.Event.try_sync, sender: self)
    }
    
    @IBAction func openUserAddress(sender: AnyObject) {
        UIApplication.sharedApplication().openURL(NSURL(string: userAddressBtn.titleLabel!.text!)!)
    }
    
    @IBAction func onLoginOut(sender: AnyObject) {
        app.logout() {
            if let settingController = self.parentViewController as? SettingViewController {
                settingController.onDoneClick(self)
            }
        }
    }
    
    @IBAction func resetSyncTime(sender: AnyObject) {
        if let user = app.getUser() {
            user.lastSyncTime = nil
            app.profile.saveUser(user)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        if let version = NSBundle.mainBundle().infoDictionary?["CFBundleShortVersionString"] as? String {
            appVersion = version
            versionText.text = "版本 \(appVersion)"
        }
        
        displayUser()
        
        EventCenter.shared.bind(self).on(R.Event.user_saved) += {
            self.displayUser()
        }
        EventCenter.shared.bind(self).on(R.Event.sync_start) += {
            self.syncStatusText.alpha = 1
            self.syncStatusText.textColor = Color.lightGrayColor()
            self.syncStatusText.text = "正在同步..."
            self.syncBtn.enabled = false
        }
        
        EventCenter.shared.bind(self).on(R.Event.sync_finish) += {
            UIView.animateWithDuration(0.3, delay: 1.5, options: UIViewAnimationOptions.CurveEaseOut, animations: {
                    self.syncStatusText.textColor = MaterialColor.Green.primary.hue.color
                    self.syncStatusText.text = "\(FontIcon.check_circle.text) 同步完成"
                    self.syncBtn.enabled = true
                }) {
                    result in
                    UIView.animateWithDuration(1, delay: 3, options: UIViewAnimationOptions.CurveEaseOut, animations: {
                        self.syncStatusText.alpha = 0
                        }, completion: nil)
                }
        }
    }
    
    @IBAction func sendFeedback(sender: AnyObject) {
        var emailTitle = "禅道iOS客户端(v\(appVersion)问题反馈)"
        var messageBody = ""
        var toRecipents = ["sunhao@cnezsoft.com"]
        var mc: MFMailComposeViewController = MFMailComposeViewController()
        mc.mailComposeDelegate = self
        mc.setSubject(emailTitle)
        mc.setMessageBody(messageBody, isHTML: false)
        mc.setToRecipients(toRecipents)
        
        self.presentViewController(mc, animated: true, completion: nil)
    }
    
    func mailComposeController(controller:MFMailComposeViewController, didFinishWithResult result:MFMailComposeResult, error:NSError) {
        switch result.value {
        case MFMailComposeResultCancelled.value:
            Log.w("Mail cancelled")
        case MFMailComposeResultSaved.value:
            Log.v("Mail saved")
        case MFMailComposeResultSent.value:
            Log.v("Mail sent")
        case MFMailComposeResultFailed.value:
            Log.e("Mail sent failure: \(error.localizedDescription)")
            UIAlertView(title: "右键发送失败", message: "您可以手动发送邮件至sunhao@ezsoft.com来反馈您的问题。", delegate: nil, cancelButtonTitle: "确认")
                .show()
        default:
            break
        }
        self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    func displayUser() {
        if let user = app.getUser() {
            userAddressBtn.setTitle(user.address, forState: UIControlState.allZeros)
            userAccountText.text = user.displayAccount
            syncFreqBtn.setTitle("\(user.syncFrequencyText) \(FontIcon.angle_right.text)", forState: UIControlState.allZeros)
            lastSyncTimeText.text = "上次同步：\(user.lastSyncTimeText)"
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    deinit {
        EventCenter.shared.unbind(self)
    }

    // MARK: - Table view data source

    /*
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // #warning Potentially incomplete method implementation.
        // Return the number of sections.
        return 0
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete method implementation.
        // Return the number of rows in the section.
        return 0
    }
    */

    /*
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("reuseIdentifier", forIndexPath: indexPath) as UITableViewCell

        // Configure the cell...

        return cell
    }
    */

    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return NO if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            // Delete the row from the data source
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
        } else if editingStyle == .Insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(tableView: UITableView, moveRowAtIndexPath fromIndexPath: NSIndexPath, toIndexPath: NSIndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return NO if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using [segue destinationViewController].
        // Pass the selected object to the new view controller.
    }
    */

}
