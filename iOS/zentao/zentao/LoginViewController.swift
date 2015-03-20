//
//  LoginViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/19.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class LoginViewController: ZentaoViewController {

    @IBOutlet weak var addressText: UITextField!
    @IBOutlet weak var accountText: UITextField!
    @IBOutlet weak var passwordText: UITextField!
    @IBOutlet weak var zentaoLogoImg: UIImageView!
    @IBOutlet weak var loginForm: UIView!
    @IBOutlet weak var loginBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboardOnTouchesBegan = true
        
        if let user = app.getUser() {
            if user.hasLoginCredentials {
                addressText.text = user.address
                accountText.text = user.account
                passwordText.text = user.passwordMD5WithFlag
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func loginBtnClick(sender: AnyObject) {
        if addressText.text.isEmpty {
            UIAlertView(title: "登录信息不完整", message: "请填写禅道专业版地址，例如zentao.com", delegate: nil, cancelButtonTitle: "确认")
                .show()
            addressText.becomeFirstResponder()
        } else if accountText.text.isEmpty {
            UIAlertView(title: "登录信息不完整", message: "请填写用户名或邮箱", delegate: nil, cancelButtonTitle: "确认")
                .show()
            accountText.becomeFirstResponder()
        } else if passwordText.text.isEmpty {
            UIAlertView(title: "登录信息不完整", message: "请填写密码", delegate: nil, cancelButtonTitle: "确认")
                .show()
            passwordText.becomeFirstResponder()
        } else {
            app.switchUser(addressText.text, account: accountText.text, password: passwordText.text)
            loginBtn.enabled = false
            hideUserKeyboard()
            app.login() {
                (result, error, message) in
                self.loginBtn.enabled = true
                println("view login result: \(result), error: \(error), message: \(message)")
                if result {
                    self.dismissViewControllerAnimated(true, nil)
                } else {
                    UIAlertView(title: "登录失败", message: message, delegate: nil, cancelButtonTitle: "确认").show()
                    println("alert shoud showed")
                }
            }
        }
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        loginBtn.setTitle("正在登录...", forState: UIControlState.Disabled)
        
        loginForm.frame.origin = CGPointMake((self.view.bounds.size.width - loginForm.frame.width)/2, (self.view.bounds.size.height - loginForm.frame.height)/2 + 20)
        
        zentaoLogoImg.frame.origin = CGPointMake(
            (self.view.bounds.size.width - zentaoLogoImg.frame.width)/2,
            loginForm.frame.origin.y - 20 - zentaoLogoImg.frame.height)
    }
    
    override var distanceForSlideUpOnKeyboardShow: CGFloat {
        return min(userKeyboardHeight, max(0, userKeyboardHeight - (self.view.bounds.size.height - loginForm.frame.origin.y - loginForm.frame.height)))
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
