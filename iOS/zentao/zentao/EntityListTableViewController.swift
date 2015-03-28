//
//  EntityListTableViewController.swift
//  zentao
//
//  Created by Sun Hao on 15/3/26.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import UIKit

class EntityListTableViewController: UITableViewController {
    
    typealias CellViews = (id: UILabel, title: UILabel, icon: IconUILabel, iconBack: IconUILabel, status: UILabel, tag: UILabel, attr: UILabel, info: UILabel)
    
    let app: ZentaoApp = (UIApplication.sharedApplication().delegate as AppDelegate).app
    var tab: EntityQueryType? {
        didSet {
            if let thisTab = tab {
                if !thisTab.equalTo(oldValue) {
                    self.queryData()
                    Log.d("Set tab to \(thisTab.name) of \(entityTab.name)")
                }
            }
        }
    }
    
    var entityTab: EntityType {
        return tab!.entityType
    }
    var isBusy = false
    var dataSet: [Entity]?
    var user: User?
    let percentFormatter = NSNumberFormatter()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        percentFormatter.numberStyle = NSNumberFormatterStyle.PercentStyle
        
        self.tableView.separatorColor = MaterialColor.Grey.P300.hue.color
        self.tableView.tableFooterView = UIView(frame:CGRectZero)
        
        EventCenter.shared.bind(self).on(R.Event.data_stored) += {
            (sender, userInfo) in
            if let thisTab = self.tab {
                Log.d("Try reload data after data stored.")
                let changes = userInfo as [String: [String: Int]]
                if let tabChanges = changes[thisTab.entityType.name] {
                    if tabChanges["total"] > 0 {
                        Log.d("Ready reload data after data stored.")
                        self.queryData()
                    }
                }
            }
        }
    }
    
    func queryData() {
        if isBusy {
            Log.w("Data store is busy now, try again later.")
            return
        }
        
        isBusy = true
        
        let queue = TaskQueue()
        
        queue.tasks +=! {
            _, next in
            let store = self.app.dataStore
            self.user = self.app.getUser()
            let entities = store.query(self.tab!, user: self.user!)
            next(entities)
        }
        
        queue.tasks += {
            result, _ in
            self.dataSet = result as [Entity]?
            self.tableView.reloadData()
            self.isBusy = false
        }
        
        queue.run()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // #warning Potentially incomplete method implementation.
        // Return the number of sections.
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete method implementation.
        // Return the number of rows in the section.
        return dataSet?.count ?? 0
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("cell", forIndexPath: indexPath) as UITableViewCell
        
        if let data = dataSet {
            let entity = dataSet![indexPath.row]
            let contentView = cell.contentView
            
            displayCell(entity, views: (
                id: cell.contentView.viewWithTag(R.Tag.id) as UILabel,
                title: cell.contentView.viewWithTag(R.Tag.title) as UILabel,
                icon: cell.contentView.viewWithTag(R.Tag.icon) as IconUILabel,
                iconBack: cell.contentView.viewWithTag(R.Tag.icon_back) as IconUILabel,
                status: cell.contentView.viewWithTag(R.Tag.status) as UILabel,
                tag: cell.contentView.viewWithTag(R.Tag.tag) as UILabel,
                attr: cell.contentView.viewWithTag(R.Tag.attribute) as UILabel,
                info: cell.contentView.viewWithTag(R.Tag.information) as UILabel))
        }
        return cell
    }
    
    func displayCell(entity: Entity, views: CellViews) {
        switch tab!.entityType {
        case .Todo:
            displayCell(todo: entity as Todo, views: views)
        case .Task:
            displayCell(task: entity as Task, views: views)
        case .Bug:
            displayCell(bug: entity as Bug, views: views)
        case .Story:
            displayCell(story: entity as Story, views: views)
        case .Product:
            displayCell(product: entity as Product, views: views)
        case .Project:
            displayCell(project: entity as Project, views: views)
        default:
            break
        }
    }
    
    func displayCell(#todo: Todo, views: CellViews) {
        let status = todo.statusValue
        views.title.text = todo.name
        views.id.text = "#\(todo.id)"
        views.iconBack.text = FontIcon.circle_thin.text
        let color = MaterialColor.PriAccentSwatch[todo.pri.integerValue].P300.hue.color
        views.iconBack.textColor = color
        views.icon.textColor = color
        views.icon.text = status == .done ? FontIcon.check.text : ""
        if status == .doing {
            views.attr.text = "\(status.icon.text) \(status.displayName)"
            views.attr.textColor = status.swatch.primary.hue.color
            views.attr.hidden = false
        } else {
            views.attr.hidden = true
        }
        views.status.text = todo.begin.toString(formatStr: "M月d日 hh:mm")
        
        if todo.unread.boolValue {
            views.tag.hidden = false
            views.tag.text = "新\(EntityType.Todo.displayName)"
            views.tag.textColor = EntityType.Todo.swatch.primary.text.color
            views.tag.backgroundColor = EntityType.Todo.swatch.primary.hue.color
        } else if todo.type == "bug" {
            views.tag.hidden = false
            views.tag.text = EntityType.Bug.displayName
            views.tag.textColor = EntityType.Bug.swatch.primary.text.color
            views.tag.backgroundColor = EntityType.Bug.swatch.primary.hue.color
            if views.title.text == nil || views.title.text!.isEmpty {
                views.title.text = "\(EntityType.Bug.displayName) #\(todo.idvalue)"
                
                let relativeTask = TaskQueue()
                
                relativeTask.tasks +=! {
                    _, next in
                    let store = self.app.dataStore
                    let bug = store.query(EntityType.Bug, user: self.user!, id: todo.idvalue.integerValue)
                    next(bug)
                }
                
                relativeTask.tasks += {
                    result, _ in
                    if let bug = result as? Bug {
                        views.title.text = bug.title
                    }
                }
                
                relativeTask.run()
            }
        } else if todo.type == "task" {
            views.tag.hidden = false
            views.tag.text = EntityType.Task.displayName
            views.tag.textColor = EntityType.Task.swatch.primary.text.color
            views.tag.backgroundColor = EntityType.Task.swatch.primary.hue.color
            if views.title.text == nil || views.title.text!.isEmpty {
                views.title.text = "\(EntityType.Task.displayName) #\(todo.idvalue)"
                
                let relativeTask = TaskQueue()
                
                relativeTask.tasks +=! {
                    _, next in
                    let store = self.app.dataStore
                    let bug = store.query(EntityType.Task, user: self.user!, id: todo.idvalue.integerValue)
                    next(bug)
                }
                
                relativeTask.tasks += {
                    result, _ in
                    if let task = result as? Task {
                        views.title.text = task.name
                    }
                }
                
                relativeTask.run()
            }
        } else {
            views.tag.hidden = true
        }
    }
    
    func displayCell(task entity: Task, views: CellViews) {
        views.id.text = "#\(entity.id)"
        views.title.text = entity.name
        views.attr.hidden = entity.assignedTo.isEmpty
        if !views.attr.hidden {
            views.attr.text = "\(FontIcon.hand_o_right.text) \(entity.assignedTo)"
        }
        let status = entity.statusValue
        views.status.text = status.displayName
        views.status.textColor = status.swatch.primary.hue.color
        
        let swatch = MaterialColor.PriAccentSwatch[entity.pri.integerValue].P300
        views.iconBack.text = FontIcon.circle.text
        views.iconBack.textColor = swatch.hue.color
        views.icon.textColor = UIColor.whiteColor()
        views.icon.text = entity.pri.integerValue > 0 ? "\(entity.pri.integerValue)" : ""
        
        views.tag.hidden = !entity.unread.boolValue
        if !views.tag.hidden {
            let entityType = entity.entityType
            views.tag.text = "新\(entityType.displayName)"
            views.tag.textColor = entityType.swatch.primary.text.color
            views.tag.backgroundColor = entityType.swatch.primary.hue.color
        }
    }
    
    func displayCell(bug entity: Bug, views: CellViews) {
        views.id.text = "#\(entity.id)"
        views.title.text = entity.title
        views.attr.hidden = entity.assignedTo.isEmpty
        if !views.attr.hidden {
            views.attr.text = "\(FontIcon.hand_o_right.text) \(entity.assignedTo)"
        }
        let status = entity.statusValue
        views.status.text = status.displayName
        views.status.textColor = status.swatch.primary.hue.color
        
        let swatch = MaterialColor.PriAccentSwatch[entity.pri.integerValue].P300
        views.iconBack.text = FontIcon.circle.text
        views.iconBack.textColor = swatch.hue.color
        views.icon.textColor = UIColor.whiteColor()
        views.icon.text = entity.pri.integerValue > 0 ? "\(entity.pri.integerValue)" : ""
        
        views.info.hidden = entity.confirmed.boolValue
        if !views.info.hidden {
            views.info.text = "\(FontIcon.question_circle.text) 未确认"
        }
        
        views.tag.hidden = !entity.unread.boolValue
        if !views.tag.hidden {
            let entityType = entity.entityType
            views.tag.text = "新\(entityType.displayName)"
            views.tag.textColor = entityType.swatch.primary.text.color
            views.tag.backgroundColor = entityType.swatch.primary.hue.color
        }
    }
    
    func displayCell(story entity: Story, views: CellViews) {
        views.id.text = "#\(entity.id)"
        views.title.text = entity.title
        views.attr.hidden = entity.assignedTo.isEmpty
        if !views.attr.hidden {
            views.attr.text = "\(FontIcon.hand_o_right.text) \(entity.assignedTo)"
        }
        let status = entity.statusValue
        views.status.text = status.displayName
        views.status.textColor = status.swatch.primary.hue.color
        
        let swatch = MaterialColor.PriAccentSwatch[entity.pri.integerValue].P300
        views.iconBack.text = FontIcon.circle.text
        views.iconBack.textColor = swatch.hue.color
        views.icon.textColor = UIColor.whiteColor()
        views.icon.text = entity.pri.integerValue > 0 ? "\(entity.pri.integerValue)" : ""
        
        views.tag.hidden = !entity.unread.boolValue
        if !views.tag.hidden {
            let entityType = entity.entityType
            views.tag.text = "新\(entityType.displayName)"
            views.tag.textColor = entityType.swatch.primary.text.color
            views.tag.backgroundColor = entityType.swatch.primary.hue.color
        }
    }
    
    func displayCell(product entity: Product, views: CellViews) {
        views.id.text = "#\(entity.id)"
        views.title.text = entity.name
        
        views.attr.text = ""
        
        let entityType = entity.entityType
        let swatch = MaterialColor.specialFromId(entity.id.integerValue).P400
        views.iconBack.text = FontIcon.circle.text
        views.iconBack.textColor = swatch.hue.color
        views.icon.textColor = UIColor.whiteColor()
        views.icon.text = entityType.icon.text
        
        views.tag.hidden = !entity.unread.boolValue
        if !views.tag.hidden {
            views.tag.text = "新\(entityType.displayName)"
            views.tag.textColor = entityType.swatch.primary.text.color
            views.tag.backgroundColor = entityType.swatch.primary.hue.color
        }
        let status = entity.statusValue
        views.status.hidden = status != .normal
        if !views.status.hidden {
            views.status.text = status.displayName
            views.status.textColor = status.swatch.primary.hue.color
        }
        
        let relativeTask = TaskQueue()
        relativeTask.tasks +=! {
            _, next in
            let store = self.app.dataStore
            if entity.status == Product.Status.normal.name {
                entity.bugCount = store.getBugCountOfProduct(self.user!, productId: entity.id.integerValue)
            }
            entity.storyInfo = store.getStoryCountOfProduct(self.user!, productId: entity.id.integerValue)
            next(entity)
        }
        
        relativeTask.tasks += {
            result, _ in
            if let product = result as? Product {
                if product.bugCount > 0 {
                    views.status.hidden = false
                    views.status.text = "\(EntityType.Bug.icon.text) \(product.bugCount)"
                    views.status.textColor = EntityType.Bug.swatch.primary.hue.color
                }
                views.attr.text = product.storyInfoText
            }
        }
        
        relativeTask.run()
    }
    
    func displayCell(project entity: Project, views: CellViews) {
        views.id.text = "#\(entity.id)"
        views.title.text = entity.name
        
        views.attr.text = entity.friendlyTimeString
        
        let entityType = entity.entityType
        let swatch = MaterialColor.specialFromId(entity.id.integerValue + 10).P400
        views.iconBack.text = FontIcon.circle.text
        views.iconBack.textColor = swatch.hue.color
        views.icon.textColor = UIColor.whiteColor()
        views.icon.text = entityType.icon.text
        
        views.tag.hidden = !entity.unread.boolValue
        if !views.tag.hidden {
            views.tag.text = "新\(entityType.displayName)"
            views.tag.textColor = entityType.swatch.primary.text.color
            views.tag.backgroundColor = entityType.swatch.primary.hue.color
        }
        views.status.hidden = true
        
        let relativeTask = TaskQueue()
        relativeTask.tasks +=! {
            _, next in
            let store = self.app.dataStore
            entity.bugCount = store.getBugCountOfProduct(self.user!, productId: entity.id.integerValue)
            entity.hours = store.getProjectHours(self.user!, project: entity)
            next(entity)
        }
        
        relativeTask.tasks += {
            result, _ in
            if let project = result as? Project {
                if project.bugCount > 0 {
                    views.status.hidden = false
                    views.status.text = "\(EntityType.Bug.icon.text) \(project.bugCount)"
                    views.status.textColor = EntityType.Bug.swatch.primary.hue.color
                }
                if let hours = project.hours {
                    views.id.text = "\(self.percentFormatter.stringFromNumber(hours.progress)!)"
                    views.id.textColor = project.statusValue.swatch.primary.hue.color
                    if views.tag.hidden {
                        views.tag.hidden = false
                        views.tag.text = "\(hours.hour)h"
                        views.tag.textColor = UIColor.lightGrayColor()
                        views.tag.backgroundColor = UIColor.clearColor()
                    }
                }
            }
        }
        
        relativeTask.run()
    }

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
