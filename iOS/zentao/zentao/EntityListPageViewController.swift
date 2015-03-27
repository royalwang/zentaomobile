//
//  EntityListPageViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/26.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class EntityListPageViewController: UIPageViewController, UIPageViewControllerDataSource, UIPageViewControllerDelegate {

    let app: ZentaoApp = (UIApplication.sharedApplication().delegate as AppDelegate).app
    var visibleTab: EntityQueryType?
    var tab: EntityQueryType? {
        didSet {
            if let t = tab {
                if !t.equalTo(oldValue) {
                    if let controller = self.parentViewController as? EntityListViewController {
                        controller.selectTab(t)
                    }
                    
                    if !t.equalTo(visibleTab) {
                        setViewControllers([self.getController(t)!], direction: .Forward, animated: true, completion: nil)
                        visibleTab = t
                    }
                }
                if !t.equalTypeTo(oldValue) {
                    dataSource = nil
                    dataSource = self
                    controllerCache.removeAll(keepCapacity: true)
                    setViewControllers([self.getController(t)!], direction: .Forward, animated: true, completion: nil)
                    visibleTab = t
                }
            } else {
                Log.e("Tab set to nil.")
            }
        }
    }
    
    var entityTab: EntityType {
        return tab!.entityType
    }
    
    var pendingController: EntityListTableViewController?
    
    var controllerCache: [Int:UIViewController] = [:]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.delegate = self
        self.dataSource = self
        let type = (app.shareBundle["SelectedTab"] as? EntityType) ?? .Todo
        if let tabIndex = app.shareBundle["\(type.name).SelectedTab"] as? Int {
            tab = type.tabs[min(type.tabs.count, tabIndex)]
        } else {
            tab = type.defaultTab!
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // delegate
    func pageViewController(pageViewController: UIPageViewController, willTransitionToViewControllers pendingViewControllers: [AnyObject]) {
        pendingController = pendingViewControllers.first as? EntityListTableViewController
    }
    
    func pageViewController(pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [AnyObject], transitionCompleted completed: Bool) {
        if completed && pendingController != nil {
            visibleTab = pendingController!.tab
            tab = visibleTab
        }
    }
    
    // UIPageViewControllerDataSource
    func presentationCountForPageViewController(pageViewController: UIPageViewController) -> Int {
        return entityTab.tabs.count - 1
    }
    
    func presentationIndexForPageViewController(pageViewController: UIPageViewController) -> Int {
        return tab!.index
    }
    
    func pageViewController(pageViewController: UIPageViewController, viewControllerBeforeViewController viewController: UIViewController) -> UIViewController? {
        let oldTab = (viewController as EntityListTableViewController).tab!
        return getController(oldTab.prev)
    }
    
    func pageViewController(pageViewController: UIPageViewController, viewControllerAfterViewController viewController: UIViewController) -> UIViewController? {
        let oldTab = (viewController as EntityListTableViewController).tab!
        return getController(oldTab.next)
    }
    
    func getController(thisTab: EntityQueryType?) -> UIViewController? {
        if let t = thisTab {
            if let controller = controllerCache[t.index] {
                return controller
            }
            
            let controller = self.storyboard?.instantiateViewControllerWithIdentifier("EntityListTableViewController") as EntityListTableViewController
            controller.tab = t
            controllerCache[t.index] = controller
            return controller
        } else {
            return nil
        }
    }
}
