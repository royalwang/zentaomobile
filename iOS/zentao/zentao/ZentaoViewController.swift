//
//  ZentaoViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/19.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class ZentaoViewController: UIViewController {
    
    let app: ZentaoApp = (UIApplication.sharedApplication().delegate as AppDelegate).app

    override func viewDidLoad() {
        super.viewDidLoad()
        
        NSNotificationCenter.defaultCenter().addObserver(self, selector: Selector("keyboardWillShow:"), name:UIKeyboardWillShowNotification, object: nil);
        NSNotificationCenter.defaultCenter().addObserver(self, selector: Selector("keyboardWillHide:"), name:UIKeyboardWillHideNotification, object: nil);

        Log.i("View did load in ZentaoViewController.")
        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(animated: Bool) {
        if checkUserStatusOnDidAppear {
            app.checkLogin() {
                result in
                Log.v("Check user login result: \(result)")
                if !result {
                    self.openLoginView()
                }
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    deinit {
        NSNotificationCenter.defaultCenter().removeObserver(self)
    }
    
    func keyboardWillShow(sender: NSNotification) {
        println("keyboardWillShow")
        let info: NSDictionary = sender.userInfo!
        let value: NSValue = info.valueForKey(UIKeyboardFrameBeginUserInfoKey) as NSValue
        let keyboardSize: CGSize = value.CGRectValue().size
        userKeyboardHeight = keyboardSize.height
        self.view.frame.origin.y = -distanceForSlideUpOnKeyboardShow
    }
    
    func keyboardWillHide(sender: NSNotification) {
        println("keyboardWillHide")
        self.view.frame.origin.y = 0
    }
    
    override func touchesBegan(touches: NSSet, withEvent event: UIEvent) {
        if hideKeyboardOnTouchesBegan {
            hideUserKeyboard()
        }
    }
    
    func hideUserKeyboard() {
        self.view.endEditing(true);
    }
    
    var distanceForSlideUpOnKeyboardShow: CGFloat {
        return userKeyboardHeight
    }
    
    var hideKeyboardOnTouchesBegan = false
    
    var userKeyboardHeight: CGFloat = 150
    
    var checkUserStatusOnDidAppear = true
    
    var showNavigationBarShadow: Bool = true {
        didSet {
            self.navigationController?.navigationBar.clipsToBounds = !showNavigationBarShadow
        }
    }
    
    var accentSwatch: MaterialColor.Swatch? {
        didSet {
            if let swatch = accentSwatch {
                var navbar: UINavigationBar? = self.navigationController?.navigationBar
                if navbar == nil {
                    navbar = self.view.viewWithTag(R.Tag.custom_navbar) as? UINavigationBar
                }
                
                if let nav = navbar {
                    nav.titleTextAttributes = [NSForegroundColorAttributeName: UIColor.whiteColor()]
                    nav.setBackgroundImage(UIImage(), forBarMetrics: UIBarMetrics.Default)
                    nav.translucent = true
                }
                
                UIApplication.sharedApplication().statusBarStyle = UIStatusBarStyle.LightContent
                
                let color = swatch.primary.hue.color
                
                UIView.animateWithDuration(0.3) {
                    self.view.viewWithTag(R.Tag.accent_header)?.layer.backgroundColor = color.CGColor
                    return
                }
                
                let hex = NSString(format:"%2X", swatch.primary.hue) as String
                Log.v("Accent swatch changed to \(swatch.name)(#\(hex))")
            }
        }
    }
    
    func openLoginView() {
        let storyboard = self.storyboard
        let loginVC: LoginViewController = storyboard?.instantiateViewControllerWithIdentifier("LoginViewController") as LoginViewController
        loginVC.modalPresentationStyle = UIModalPresentationStyle.Popover
        self.presentViewController(loginVC, animated: true, completion: nil)
    }

}
