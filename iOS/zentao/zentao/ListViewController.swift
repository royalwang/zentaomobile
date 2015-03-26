//
//  ListViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/25.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class ListViewController: ZentaoViewController, UITabBarDelegate {

    @IBOutlet weak var menuTabBar: UITabBar!
    @IBOutlet var menuTabBarCollection: [UITabBar]!
    @IBOutlet weak var titleBar: UINavigationItem!
    
    var entityTab: EntityType {
        get {
            return (app.shareBundle["SelectedTab"] as? EntityType) ?? .Todo
        }
        set {
            app.shareBundle["SelectedTab"] = newValue
        }
    }
    
    func switchTab(tab: EntityType) {
        self.accentSwatch = tab.swatch
        menuTabBar.selectedImageTintColor = tab.swatch.primary.hue.color
        titleBar.title = tab.displayName
        
        menuTabBar.selectedItem = menuTabBar.items![tab.index - 1] as? UITabBarItem
        if let entityListViewController: EntityListViewController  = self.childViewControllers.first as? EntityListViewController {
            entityListViewController.entityTab = tab
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        self.showNavigationBarShadow = false
        menuTabBar.delegate = self
        menuTabBar.selectedImageTintColor = MaterialColor.Red.primary.hue.color
        
        switchTab(entityTab)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func tabBar(tabBar: UITabBar, didSelectItem item: UITabBarItem!) {
        let entityType = EntityType.values[item.tag] as EntityType
        entityTab = entityType
        switchTab(entityType)
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
