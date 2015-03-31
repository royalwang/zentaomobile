//
//  Event.swift
//  zentao
//
//  Created by Sun Hao on 15/3/24.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation

extension R {
    
    struct Event {
        static let login_success = "login_success"
        static let login_fail = "login_fail"
        
        static let sync_start = "sync_start"
        static let sync_finish = "sync_finish"
        static let timer_started = "timer_started"
        static let timer_stoped = "timer_stoped"
        static let start_timer = "start_timer"
        static let stop_timer = "stop_timer"
        static let timer_tick = "timer_tick"
        static let data_stored = "data_stored"
        static let user_saved = "user_saved"
        static let try_sync = "try_sync"
    }
}
