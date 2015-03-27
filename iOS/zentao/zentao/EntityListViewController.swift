//
//  EntityListViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/25.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class EntityListViewController: ZentaoViewController {
    
    @IBOutlet weak var menu: UISegmentedControl!
    
    var entityTab: EntityType = .Todo {
        didSet {
            switchTab(entityTab)
        }
    }
    
    func switchTab(type: EntityType) {
        let tabs = type.tabs
        let bundleName = "\(type.name).SelectedTab"
        var currentTab = type.defaultTab
        if let tabIndexInBundle = (app.shareBundle[bundleName] as? Int) {
            currentTab = tabs[tabIndexInBundle]
        }
        
        menu.removeAllSegments()
        for tab in tabs {
            menu.insertSegmentWithTitle(tab.name, atIndex: tab.index, animated: false)
        }
        selectTab(currentTab!)
        
        if let entityListPageViewController: EntityListPageViewController  = self.childViewControllers.first as? EntityListPageViewController {
            entityListPageViewController.tab = currentTab!
        }
    }
    
    func selectTab(tab: EntityQueryType) {
        let bundleName = "\(entityTab.name).SelectedTab"
        menu.selectedSegmentIndex = tab.index
        app.shareBundle[bundleName] = tab.index
    }
    
    @IBAction func onMenuChange(sender: UISegmentedControl) {
        
        let tab = entityTab.tabs[sender.selectedSegmentIndex]
        selectTab(tab)
        
        if let entityListPageViewController: EntityListPageViewController  = self.childViewControllers.first as? EntityListPageViewController {
            entityListPageViewController.tab = tab
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
