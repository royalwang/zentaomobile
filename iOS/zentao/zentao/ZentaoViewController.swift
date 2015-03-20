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


        println("View did load in ZentaoViewController.")
        // Do any additional setup after loading the view.
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
    
    var accentSwatch: MaterialColorSwatch? {
        didSet {
            if let swatch = accentSwatch {
                
                if let nav = self.navigationController {
                    nav.navigationBar.barTintColor = swatch.primaryColor.uicolor
                    nav.navigationBar.titleTextAttributes = [NSForegroundColorAttributeName:UIColor.whiteColor()]
                    
                    UIApplication.sharedApplication().statusBarStyle = UIStatusBarStyle.LightContent
                    
                    println("Accent swatch changed to \(swatch.name)(#\(swatch.primaryColor.description))")
                }
            }
        }
    }

}
