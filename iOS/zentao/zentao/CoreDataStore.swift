//
//  CoreDataHelper.swift
//  zentao
//
//  Created by Sun Hao on 15/3/16.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import CoreData

class CoreDataStore {
    
    // Singleton
    private struct SingletonKeeper {
        static let instance = CoreDataStore()
    }
    
    class var sharedInstance: CoreDataStore {
        get {
            return SingletonKeeper.instance
        }
    }
    
    private init() {}
    
    
    let storeName = "zentao"
    let storeFileName = "zentao.sqlite"
    
    // MARK: - Core Data stack
    
    lazy var applicationDocumentsDirectory: NSURL = {
        // The directory the application uses to store the Core Data store file. This code uses a directory named "com.cnezsoft.zentao" in the application's documents Application Support directory.
        let urls = NSFileManager.defaultManager().URLsForDirectory(.DocumentDirectory, inDomains: .UserDomainMask)
        return urls[urls.count-1] as NSURL
        }()
    
    lazy var managedObjectModel: NSManagedObjectModel = {
        // The managed object model for the application. This property is not optional. It is a fatal error for the application not to be able to find and load its model.
        let modelURL = NSBundle.mainBundle().URLForResource(self.storeName, withExtension: "momd")!
        return NSManagedObjectModel(contentsOfURL: modelURL)!
        }()
    
    lazy var persistentStoreCoordinator: NSPersistentStoreCoordinator? = {
        // The persistent store coordinator for the application. This implementation creates and return a coordinator, having added the store for the application to it. This property is optional since there are legitimate error conditions that could cause the creation of the store to fail.
        // Create the coordinator and store
        var coordinator: NSPersistentStoreCoordinator? = NSPersistentStoreCoordinator(managedObjectModel: self.managedObjectModel)
        let url = self.applicationDocumentsDirectory.URLByAppendingPathComponent(self.storeFileName)
        var error: NSError? = nil
        var failureReason = "There was an error creating or loading the application's saved data."
        if coordinator!.addPersistentStoreWithType(NSSQLiteStoreType, configuration: nil, URL: url, options: [NSMigratePersistentStoresAutomaticallyOption: true, NSInferMappingModelAutomaticallyOption: true], error: &error) == nil {
            coordinator = nil
            // Report any error we got.
            let dict = NSMutableDictionary()
            dict[NSLocalizedDescriptionKey] = "Failed to initialize the application's saved data"
            dict[NSLocalizedFailureReasonErrorKey] = failureReason
            dict[NSUnderlyingErrorKey] = error
            error = NSError(domain: "YOUR_ERROR_DOMAIN", code: 9999, userInfo: dict)
            // Replace this with code to handle the error appropriately.
            // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
            NSLog("Unresolved error \(error), \(error!.userInfo)")
            abort()
        }
        
        return coordinator
        }()
    
    lazy var managedObjectContext: NSManagedObjectContext? = {
        // Returns the managed object context for the application (which is already bound to the persistent store coordinator for the application.) This property is optional since there are legitimate error conditions that could cause the creation of the context to fail.
        let coordinator = self.persistentStoreCoordinator
        if coordinator == nil {
            return nil
        }
        var managedObjectContext = NSManagedObjectContext(concurrencyType: .MainQueueConcurrencyType)
        managedObjectContext.persistentStoreCoordinator = coordinator
        return managedObjectContext
        }()
    
    // MARK: - Core Data Saving support
    
    func saveContext () -> Bool {
        if let moc = self.managedObjectContext {
            var error: NSError? = nil
            if moc.hasChanges {
                let result = moc.save(&error)
                if !result {
                    Log.e("ERROR on save context: \(error)")
                }
                return result
            }
        }
        return false
    }
    
    // MARK: dao methods
    
    func newEntityForInsert(type: EntityType, user: User) -> Entity? {
        if let context = self.managedObjectContext {
            let entity: Entity = NSEntityDescription.insertNewObjectForEntityForName(type.name,
                inManagedObjectContext: context) as Entity
            entity.zentao = user.zentao
            if type == .Todo {
                (entity as Todo).account = user.account
            }
            return entity
        }
        return nil
    }
    
    func createFetchRequest(type: EntityType, user: User, var predicate: String = "", predicateArguments: [AnyObject]? = nil, var sortDescriptor: [NSSortDescriptor]? = nil) -> NSFetchRequest {
        let fetchRequest = NSFetchRequest(entityName: type.name)
        let requiredPredicate = type != .Todo ? "zentao == '\(user.zentao)'" : "zentao == '\(user.zentao)' AND account == '\(user.account)'"
        predicate = predicate.isEmpty ?
            requiredPredicate : "\(requiredPredicate) AND (\(predicate))";
        if sortDescriptor == nil {
            sortDescriptor = [NSSortDescriptor(key: "id", ascending: false, selector: Selector("localizedStandardCompare:"))]
        }
        
        fetchRequest.sortDescriptors = sortDescriptor!
        fetchRequest.predicate = NSPredicate(format: predicate, argumentArray: predicateArguments)
        return fetchRequest
    }
    
    func query(type: EntityType, user: User, var predicate: String, sortDescriptor: [NSSortDescriptor]?, complete: ((finalResult: [Entity]?) -> Void)) -> NSPersistentStoreResult? {
        if let context = self.managedObjectContext {
            let fetchRequest = createFetchRequest(type, user: user, predicate: predicate, predicateArguments: nil, sortDescriptor: sortDescriptor)
            let asyncFetchRequest = NSAsynchronousFetchRequest(fetchRequest: fetchRequest) {
                result in
                complete(finalResult: result.finalResult as [Entity]?)
            }
            
            var error: NSError? = nil
            return context.executeRequest(asyncFetchRequest, error: &error)
        }
        return nil
    }
    
    func query(type: EntityType, user: User, var predicate: String, complete: ((finalResult: [Entity]?) -> Void)) -> NSPersistentStoreResult? {
        return query(type, user: user, predicate: predicate, sortDescriptor: nil, complete)
    }
    
    func query(type: EntityType, user: User, complete: ((finalResult: [Entity]?) -> Void)) -> NSPersistentStoreResult? {
        return query(type, user: user, predicate: "", complete: complete)
    }
    
    func query(type: EntityType, user: User, predicate: String = "", predicateArguments: [AnyObject]? = nil, sortDescriptor: [NSSortDescriptor]? = nil) -> [Entity]? {
        if let context = self.managedObjectContext {
            let fetchRequest = createFetchRequest(type, user: user, predicate: predicate, predicateArguments: predicateArguments, sortDescriptor: sortDescriptor)
            var error: NSError? = nil
            return context.executeFetchRequest(fetchRequest, error: &error) as [Entity]?
        }
        return nil
    }
    
    func query(queryType: EntityQueryType, user: User, predicate: String = "",
        var sortDescriptor: [NSSortDescriptor]? = nil) -> [Entity]? {
        var pred: String = ""
        let entityType = queryType.entityType
        var predicateArguments: [AnyObject]?
        switch entityType {
        case .Todo:
            switch queryType as Todo.PageTab {
            case .Today:
                let now = NSDate().dateAtStartOfDay()
                pred = "begin >= %@"
                predicateArguments = [now]
            case .Undone:
                pred = "status != 'done'"
            case .Done:
                pred = "status == 'done'"
            }
        case .Task, .Bug, .Story:
            pred = "\(queryType.name) == '\(user.account)'"
        case .Product:
            switch queryType as Product.PageTab {
            case .Closed:
                pred = "status == 'closed'"
            case .Working:
                pred = "status != 'closed'"
            }
        case .Project:
            switch queryType as Project.PageTab {
            case .AssignedTo:
                pred = "pm == '\(user.account)'"
            case .Finished:
                pred = "status == 'done'"
            case .Going:
                pred = "status != 'done'"
            }
        default:
            return nil
        }
        pred = predicate.isEmpty ? pred : "\(pred) and (\(predicate))"
        return query(entityType, user: user, predicate: pred,
            predicateArguments: predicateArguments, sortDescriptor: sortDescriptor)
    }
    
    func query(type: EntityType, user: User, id: Int) -> Entity? {
        let result = query(type, user: user, predicate: "id == \(id)")
        if let r = result {
            if r.count > 0 {
                return r[0]
            }
        }
        return nil
    }
    
    func query(id: NSManagedObjectID) -> Entity? {
        if let context = self.managedObjectContext {
            return (context.objectWithID(id) as Entity)
        }
        return nil
    }
    
    func count(type: EntityType, user: User, predicate: String = "",
        predicateArguments: [AnyObject]? = nil,
        sortDescriptor: [NSSortDescriptor]? = nil) -> Int? {
        if let context = self.managedObjectContext {
            let fetchRequest = createFetchRequest(type, user: user,
                predicate: predicate, predicateArguments: predicateArguments,
                sortDescriptor: sortDescriptor)
            var error: NSError? = nil
            return context.countForFetchRequest(fetchRequest, error: &error)
        }
        return nil
    }
    
    func contains(type: EntityType, user: User, id: Int) -> Bool? {
        if let count = count(type, user: user, predicate: "id == \(id)") {
            return count > 0
        }
        return nil
    }
    
    func delete(entities: [Entity]) -> Int {
        if let context = self.managedObjectContext {
            if entities.count > 0 {
                for entity in entities {
                    context.deleteObject(entity)
                }
                if saveContext() {
                    return entities.count
                }
            }
        }
        return 0;
    }
    
    func delete(entity: Entity) -> Bool {
        return delete([entity]) > 0
    }
    
    func delete(type: EntityType, user: User, predicate: String) -> Int {
        if let context = self.managedObjectContext {
            let result = query(type, user: user, predicate: predicate)
            if let r = result {
                if r.count > 0 {
                    for entity in r {
                        context.deleteObject(entity)
                    }
                    if saveContext() {
                        return r.count
                    }
                }
            }
        }
        return 0
    }
    
    func entityForSave(type: EntityType, user: User, id: Int) -> Entity {
        var entity = query(type, user: user, id: id)
        if entity == nil {
            entity = newEntityForInsert(type, user: user)
        }
        return entity!
    }
    
    // MARK: Special query
    func getBugCountOfProduct(user: User, productId: Int) -> Int {
        return count(EntityType.Bug, user: user,
            predicate: "product = \(productId) AND status ='\(Bug.Status.active.name)'") ?? 0
    }
    
    func getBugCountOfProject(user: User, projectId: Int) -> Int {
        return count(EntityType.Bug, user: user,
            predicate: "project = \(projectId) AND status ='\(Bug.Status.active.name)'") ?? 0
    }
    
    func getStoryCountOfProduct(user: User, productId: Int, status: Story.Status) -> Int {
        return count(EntityType.Story, user: user,
            predicate: "product = \(productId) AND status ='\(status.name)'") ?? 0
    }
    
    func getStoryCountOfProduct(user: User, productId: Int) -> (active: Int, changed: Int, draft: Int) {
        return (active: getStoryCountOfProduct(user, productId: productId, status: .active),
            changed: getStoryCountOfProduct(user, productId: productId, status: .changed),
            draft: getStoryCountOfProduct(user, productId: productId, status: .draft))
    }
    
    func getProjectTasks(user: User, projectId: Int) -> [Task]? {
        return query(EntityType.Task, user: user,
            predicate: "project = \(projectId)") as [Task]?
    }
    
    func getProjectHours(user: User, project: Project)
        -> (estimate: Float, consumed: Float, left: Float, progress: Float, hour: Float) {
        var estimate: Float = 0.0, consumed: Float = 0.0, left: Float = 0.0
        var progress: Float = 0.0, hour: Float = 0.0
            
        if let tasks = getProjectTasks(user, projectId: project.id.integerValue) {
            let statusCancelName = Task.Status.cancel.name
            let closedReasonName = "cancel"
            for task in tasks {
                estimate += task.estimate.floatValue
                consumed += task.consumed.floatValue
                if task.status != statusCancelName
//                    && task.closeReason != closedReasonName
                {
                    left += task.left.floatValue
                }
            }
            
            let real = consumed + left
            progress = min(1, real > 0 ? (consumed / real) : 0)
            hour = project.status == Project.Status.done.name ? consumed : -left
        }
        
        return (estimate: estimate, consumed: consumed, left: left, progress: progress, hour: hour)
    }
    
    typealias EntitySummery = (title: String, subtitle: String, amount: Int, amountType: String)
    
    func getSummery(entityType: EntityType, user: User) -> EntitySummery? {
        if let defaultTab = entityType.defaultTab {
            var subtitle = ""
            var amount = 0, amountType = defaultTab.displayName
            
            if let entities = query(entityType.defaultTab!, user: user) {
                amount = entities.count
                if amount > 0 {
                    var unread = 0
                    let first = entities.first!
                    subtitle = "#\(first.id) \(first.displayName)"
                    for entity in entities {
                        if entity.unread.boolValue {
                            unread++
                        }
                    }
                    if unread > 0 {
                        amount = 0
                        amountType = "新\(entityType.displayName)"
                    }
                }
            }
            
            return (title: entityType.displayName, subtitle: subtitle, amount: amount, amountType: amountType)
        }
        return nil
    }
    
    func getSummery(user: User) -> [EntitySummery] {
        var summeries: [EntitySummery] = []
        for entityType in EntityType.values {
            if let summery = getSummery(entityType, user: user) {
                summeries.append(summery)
            }
        }
        return summeries
    }
}
