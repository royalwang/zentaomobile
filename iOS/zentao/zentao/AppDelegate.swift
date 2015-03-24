//
//  AppDelegate.swift
//  zentao
//
//  Created by Sun Hao on 15/3/11.
//  Copyright (c) 2015年 SunHao@cnezsoft.com. All rights reserved.
//

import UIKit
import CoreData

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate/*, UISplitViewControllerDelegate*/ {
    
    var timer: NSTimer?

    var window: UIWindow?
    var app: ZentaoApp {
        return ZentaoApp.sharedInstance
    }
    
    // MARK: Timer
    
    func startTimer() -> Bool {
        if let user = app.getUser() {
            let interval = NSTimeInterval(user.syncFrequency)
            if timer != nil && timer!.timeInterval != interval {
                stopTimer()
            }
            if timer == nil {
                timer = NSTimer.scheduledTimerWithTimeInterval(interval,
                    target: self, selector: Selector("onTimerTick:"), userInfo: nil, repeats: true)
                NSRunLoop.mainRunLoop().addTimer(timer!, forMode: NSRunLoopCommonModes)
                Log.s("Timer started and running, repeat every \(interval) seconds.")
            } else {
                Log.i("Timer is already running, repeat every \(interval) seconds.")
            }
        }
        return timer != nil
    }
    
    func stopTimer() {
        timer?.invalidate()
        timer = nil
        Log.i("Timer is stoped.")
    }
    
    func restartTimer() {
        stopTimer()
        startTimer()
        Log.i("Timer restarted.")
    }
    
    func onTimerTick(timer: NSTimer) {
        Log.i("Timer tick.")
        EventCenter.shared.trigger(R.Event.timer_tick, sender: self)
    }

    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        
        // init timer
        EventCenter.shared.bind(self).on(R.Event.start_timer) += {
            self.startTimer()
            return
        }
        EventCenter.shared.on(R.Event.stop_timer) += {self.stopTimer()}
        EventCenter.shared.on(R.Event.login_success) >> (R.Event.start_timer, self)
        return true
    }
    

    func applicationWillResignActive(application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(application: UIApplication) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        // Saves changes in the application's managed object context before the application terminates.
//        self.app.dataStore.saveContext()
    }
}

