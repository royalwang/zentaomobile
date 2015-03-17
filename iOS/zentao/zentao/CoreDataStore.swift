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
        var managedObjectContext = NSManagedObjectContext()
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
                    println("Save context error: \(error)")
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
            return entity
        }
        return nil
    }
    
    func query(type: EntityType, user: User, var predicate: String = "", var sortDescriptor: [NSSortDescriptor]? = nil) -> [Entity]? {
        if let context = self.managedObjectContext {
            let fetchRequest = NSFetchRequest(entityName: type.name)
            predicate = predicate.isEmpty ? "zentao is '\(user.zentao)'"
                : "zentao is '\(user.zentao)' and (\(predicate))";
            if sortDescriptor == nil {
                sortDescriptor = [NSSortDescriptor(key: "id", ascending: true, selector: Selector("localizedStandardCompare:"))]
            }
            
            fetchRequest.sortDescriptors = sortDescriptor!
            fetchRequest.predicate = NSPredicate(format: predicate)
            
            var error: NSError? = nil
            return context.executeFetchRequest(fetchRequest, error: &error) as [Entity]?
        }
        return nil
    }
    
    func query(type: EntityType, user: User, id: Int) -> Entity? {
        let result = query(type, user: user, predicate: "id is \(id)")
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
    
    func save(entities: [Entity]) -> Int {
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
    
    func entityForSave(type: EntityType, user: User, id: Int) -> Entity {
        var entity = query(type, user: user, id: id)
        if entity == nil {
            entity = newEntityForInsert(type, user: user)
        }
        return entity!
    }
}