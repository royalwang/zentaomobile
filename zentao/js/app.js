(function()
{
    var isLoging        = false,
        network         = 'wifi',
        $status         = document.getElementById('userStatus'),
        $statusName     = document.getElementById('userStatusName'),
        $settingBtn     = document.getElementById('settingBtn'),
        animateSpeed    = 200,
        listViewsOrder  = {todo: 1, task: 2, bug: 3, story: 4},
        listViews       = {todo: "todos.html", task: "tasks.html", bug: "bugs.html", story: "stories.html"},
        loginWindow,
        settingWindow,
        mainView,
        currentListView,
        defaultListView;

    var openLoginWindow = function()
    {
        loginWindow = plus.webview.create('login.html', 'login', 
        {
            top             : "0px",
            bottom          : "0px",
            bounce          : "vertical",
            scrollIndicator : "none"
        });
        loginWindow.addEventListener('close', function()
        {
            loginWindow = null;
        });
        loginWindow.show('zoom-in', 200);
    };

    var openListView = function(options)
    {
        if(typeof options === 'string'){options = {name: options};}
        else if(!options) options = {};

        if(!defaultListView) defaultListView = window.userStore.get('lastListView', 'todo');
        options.name = options.name || currentListView || defaultListView;

        var lastListView = listViews[currentListView];
        if(lastListView && currentListView === options.name)
        {
            window.fire(lastListView, 'reloadData', options);
            return;
        }

        var aniType = 'none';
        if(lastListView)
        {
            // aniType = listViewsOrders.name] < listViewsOrdertListView] ? 'slide-out-right' : 'slide-out-left';
            lastListView.hide(aniType, animateSpeed);
            var openeds = lastListView.opened();
            openeds.forEach(function(opendedDialog)
            {
                if(opendedDialog.dialogOptions)
                {
                    opendedDialog.hide('zoom-out', 100);
                }
            });
        }

        if(typeof listViews[options.name] === 'string')
        {
            listViews[options.name] = plus.webview.create(listViews[options.name], options.name, 
            {
                top             : "44px",
                bottom          : "51px",
                bounce          : "vertical",
                scrollIndicator : "none"
            });

            mainView.append(listViews[options.name]);
        }
        else
        {
            // aniType = listViewsOrders.name] > listViewsOrdertListView] ? 'slide-in-right' : 'slide-in-left';
            listViews[options.name].show(aniType, animateSpeed);
        }

        document.getElementsByClassName('open-listview').forEach(function(el)
        {
            el.classList[el.getAttribute('data-id') === options.name ? 'add' : 'remove']('active');
        });

        currentListView = options.name;
        window.userStore.set('lastTab', currentListView);

        document.getElementById('tab-' + options.name).classList.remove('unread');
        // zentao.data[currentListView].getUnreadCount(true);
    };

    var tryLogin = function(key)
    {
        var withUi = key && key.ui && loginWindow;

        if(isLoging && zentao.isReady)
        {
            if(withUi)
            {
                window.fire(loginWindow, 'logged', {result: false, message: '系统正忙，稍后再试。'});
            }
            return false;
        }

        if(withUi)
        {
            key.pwdMd5 = window.md5(key.pwdMd5);
        }
        zentao.login(key, function()
        {

            if(withUi)
            {
                window.fire(loginWindow, 'logged', {result: true});
            }
            checkStatus();
        }, function(e)
        {
            if(withUi)
            {
                window.fire(loginWindow, 'logged', {result: false, message: e.message || '登录失败'});
            }
            checkStatus();
        });
    };

    var checkStatus = function()
    {
        var status = window.user ? window.user.status : 'logout';

        $settingBtn.classList.remove('hidden');
        if(status === 'logout')
        {
            $statusName.innerHTML = '请登录';
            $status.setAttribute('data-status', 'offline');

            $settingBtn.classList.add('hidden');
            openLoginWindow();
        }
        else if(status === 'online')
        {
            $statusName.innerHTML = '在线';
            $status.setAttribute('data-status', 'online');

            setTimeout(function()
            {
                if(user.status === 'online') $statusName.classList.add('hide-name');
            }, 2000);
            openListView({checkStatus: true});
        }
        else if(status === 'disconnect')
        {
            $statusName.innerHTML = '没有网络';
            $status.setAttribute('data-status', 'disconnect');

            setTimeout(function()
            {
                if(status === 'disconnect')
                {
                    $statusName.innerHTML = '离线';
                    $status.setAttribute('data-status', 'offline');
                }
            }, 10000);
        }
        else
        {
            $statusName.innerHTML = '离线';
            $status.setAttribute('data-status', 'offline');
        }
    };

    var startSync = function()
    {
        $status.classList.add('syncing');
    };

    var stopSync = function()
    {
        $status.classList.remove('syncing');
    };

    var restartSync = function()
    {
        zentao.restartAutoSync();
    };

    var onResume = function()
    {
        zentao.runningInBackground = false;
        plus.runtime.setBadgeNumber(0);
        plus.push.clear();
        console.color('RUNNING IN FRONT', 'bgsuccess');
    };

    var onPause = function()
    {
        zentao.runningInBackground = true;
        console.color('RUNNING IN BACKGROUND', 'bgdanger');
    };

    var onNetChange = function()
    {
        var nt = plus.networkinfo.getCurrentType();
        switch ( nt ) 
        {
            case plus.networkinfo.CONNECTION_ETHERNET:
            case plus.networkinfo.CONNECTION_WIFI:
            network = 'wifi';
            break; 
            case plus.networkinfo.CONNECTION_CELL2G:
            case plus.networkinfo.CONNECTION_CELL3G:
            case plus.networkinfo.CONNECTION_CELL4G:
            network = 'mobile';
            break; 
            default:
            network = 'disconnect';
            break;
        }
        zentao.network = network;
        console.color('NET CHANGE:' + network, "h3|bgdanger");

        var user   = window.user;
        if(user.status === 'online' && network === 'disconnect')
        {
            user.status = 'disconnect';
            checkUserStatus('mild');
        }
        else if(user.status !== 'online' && network != 'disconnect')
        {
            tryLogin();
        }
    };

    $status.on('tap', function()
    {
        var user = window.user;
        if(!user || user.status != 'online')
        {
            openLoginWindow();
        }
    });

    $settingBtn.on('tap', function()
    {
        settingWindow = plus.webview.create('setting/index.html', 'setting', 
        {
            top             : "0px",
            bottom          : "0px",
            bounce          : "vertical",
            scrollIndicator : "none"
        });
        settingWindow.addEventListener('close', checkUserStatus);
        settingWindow.show('slide-in-right', 200);
    });

    window.on('login', function(e){tryLogin(e.detail);})
          .on('openLogin', openLoginWindow)
          .on('checkStatus', checkStatus)
          .on('openListView', function(e){openListView(e.detail);})
          .on('startSync', startSync)
          .on('stopSync', stopSync)
          .on('restartSync', restartSync);

    zentao.on('logging', function()
    {
        isLoging = true;
        $statusName.innerHTML = '登录中...';
        $status.setAttribute('data-status', 'logging');
    }).on('logged', function(result)
    {
        console.color('logged: ' + result, 'h4|bg' + (result ? 'success' : 'danger'));

        isLoging = false;
        checkStatus();
    }).on('syncing', function()
    {
        startSync();
    }).on('sync', function(e)
    {
        if(e.result)
        {
            console.color('SYNC>>> ' + e.tab, 'h5|bginfo');
            var currentWin = listViews[e.tab];
            if(typeof currentWin === 'object')
            {
                window.fire(currentWin, 'reloadData', {offline: true});
            }

            if(e.tab != currentListView && e.unreadCount)
            {
                document.getElementById('tab-' + e.tab).classList.add('unread');
            }

            if(window.userStore.get('receiveNotify', true) && zentao.runningInBackground)
            {
                var unreadCount = e.unreadCount;
                plus.runtime.setBadgeNumber(unreadCount);

                if(unreadCount)
                {
                    var message;
                    lastPush = e;
                    if(unreadCount > 1)
                    {
                        message = '收到' + unreadCount + '个新的' + zentao.dataTabs[e.tab].name;
                    }
                    else
                    {
                        message = '新的' + zentao.dataTabs[e.tab].name + ": " + (e.latestItem.name || e.latestItem.title);
                    }
                    plus.push.createMessage(message, "LocalMSG", {cover: true, test: 'testtest4343'});
                    console.color('消息已推送：' + message, 'h3|info');
                }
            }

            lastSyncTime = new Date().getTime();
        }
        stopSync();
    }).on('ready', function()
    {
        openListView({offline: true});
        if(window.userStore.get('autoSync', true))
        {
            zentao.startAutoSync();
        }
    });

    window.plusReady(function()
    {
        window.plus.navigator.setStatusBarBackground( "#FAFAFA" );

        setTimeout(plus.navigator.closeSplashscreen, 200);
        
        mainView = plus.webview.currentWebview();
        
        checkStatus();
        tryLogin(); // if(window.user == 'offline') tryLogin();

        document.addEventListener('netchange', onNetChange, false);
        document.addEventListener('pause', onPause, false);
        document.addEventListener('resume', onResume, false);

        plus.push.addEventListener('click', function(msg)
        {
            if(lastPush)
            {
                openListView({name: lastPush.tab, offline: true});
                if(lastPush.unreadCount === 1 && lastPush.latestItem)
                {
                    window.fire(windows[currentListView], 'showItem', lastPush.latestItem.id);
                }
            }
        }, false);

        console.color('app plus ready', 'bgsuccess');
    });

    document.getElementById('subpageNav').on('tap', '.open-listview', function()
    {
        openListView(this.getAttribute('data-id'));
        this.classList.remove('unread');
    });
}());
