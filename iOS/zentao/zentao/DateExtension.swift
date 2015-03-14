//
//  DateExtension.swift
//  zentao
//
//  Created by Sun Hao on 15/3/14.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

/*

https://gist.github.com/atljeremy/7681cbad00a2c71803b3

Examples:

var date: NSDate
date = 10.seconds.fromNow
date = 30.minutes.ago
date = 2.days.from(someDate)
date = NSDate() + 3.days

if dateOne < dateTwo {
// dateOne is older than dateTwo
}

if dateOne > dateTwo {
// dateOne is more recent than dateTwo
}

if dateOne <= dateTwo {
// dateOne is older than or equal to dateTwo
}

if dateOne >= dateTwo {
// dateOne is more recent or equal to dateTwo
}

if dateOne == dateTwo {
// dateOne is equal to dateTwo
}
*/

extension NSDate: Comparable, Equatable {
    var calendar: NSCalendar {
        return NSCalendar(identifier: NSGregorianCalendar)!
    }
    
    func components(unitFlags: NSCalendarUnit) -> NSDateComponents {
        return calendar.components(unitFlags, fromDate: self)
    }
    
    func after(value: Int, calendarUnit:NSCalendarUnit) -> NSDate{
        return calendar.dateByAddingUnit(calendarUnit, value: value, toDate: self, options: NSCalendarOptions(0))!
    }
    
    func equalsTo(date: NSDate) -> Bool {
        return self.compare(date) == NSComparisonResult.OrderedSame
    }
    
    func isAfter(date: NSDate) -> Bool {
        return self.compare(date) == NSComparisonResult.OrderedDescending
    }
    
    func isBefore(date: NSDate) -> Bool {
        return self.compare(date) == NSComparisonResult.OrderedAscending
    }
    
    class func parse(dateString: String, format: String = "yyyy-MM-dd HH:mm:ss") -> NSDate{
        var formatter = NSDateFormatter()
        formatter.dateFormat = format
        return formatter.dateFromString(dateString)!
    }
    
    func toString(format: String = "yyyy-MM-dd HH:mm:ss") -> String{
        var formatter = NSDateFormatter()
        formatter.dateFormat = format
        return formatter.stringFromDate(self)
    }
}

public func + (date: NSDate, timeInterval: NSTimeInterval) -> NSDate {
    return date.dateByAddingTimeInterval(timeInterval)
}

public func - (date: NSDate, timeInterval: NSTimeInterval) -> NSDate {
    return date.dateByAddingTimeInterval(-timeInterval)
}

public func += (inout date: NSDate, timeInterval: NSTimeInterval) {
    date = date + timeInterval
}

public func -= (inout date: NSDate, timeInterval: NSTimeInterval) {
    date = date - timeInterval
}

public func == (lhs: NSDate, rhs: NSDate) -> Bool {
    if lhs.compare(rhs) == .OrderedSame {
        return true
    }
    return false
}

public func != (lhs: NSDate, rhs: NSDate) -> Bool {
    return !(lhs == rhs)
}

public func < (lhs: NSDate, rhs: NSDate) -> Bool {
    if lhs.compare(rhs) == .OrderedAscending {
        return true
    }
    return false
}

public func > (lhs: NSDate, rhs: NSDate) -> Bool {
    return !(lhs < rhs)
}

extension NSTimeInterval {
    var second: NSTimeInterval {
        return self.seconds
    }
    
    var seconds: NSTimeInterval {
        return self
    }
    
    var minute: NSTimeInterval {
        return self.minutes
    }
    
    var minutes: NSTimeInterval {
        let secondsInAMinute = 60 as NSTimeInterval
        return self * secondsInAMinute
    }
    
    var day: NSTimeInterval {
        return self.days
    }
    
    var days: NSTimeInterval {
        let secondsInADay = 86_400 as NSTimeInterval
        return self * secondsInADay
    }
    
    var fromNow: NSDate {
        let timeInterval = self
        return NSDate().dateByAddingTimeInterval(timeInterval)
    }
    
    func from(date: NSDate) -> NSDate {
        let timeInterval = self
        return date.dateByAddingTimeInterval(timeInterval)
    }
    
    var ago: NSDate {
        let timeInterval = self
        return NSDate().dateByAddingTimeInterval(-timeInterval)
    }
}