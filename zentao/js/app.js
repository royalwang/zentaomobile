(function(mui, $)
{
    var $content        = $('#content'),
        animateSpeed    = 200,
        windows         = {todo: "todos.html", task: "tasks.html", bug: "bugs.html", story: "stories.html"},
        mainView,
        currentTab,
        subTabs         = {todo: 1, task: 2, bug: 3, story: 4},
        defaultTab,
        firstBackbutton = null,
        loginWindow,
        settingWindow;

    mui.init(
    {
        swipeBack: false,
        back: function()
        {
            if(!firstBackbutton)
            {
                window.plus.nativeUI.toast('再按一次退出应用');
                firstBackbutton = new Date().getTime();
                setTimeout(function(){firstBackbutton = null}, 1000);
                return false;
            }
            else
            {
                return (new Date().getTime() - firstBackbutton) < 1000;
            }
        }
    });

    mui.plusReady(function()
    {
        mainView = plus.webview.currentWebview();
        document.getElementById('subpageNav').addDelegateListener('tap', '.open-subpage', function()
        {
            var tab = this.getAttribute('data-id');
            openSubWin(tab);
            this.classList.remove('unread');
            zentao.data[tab].getUnreadCount(true);
        });

        window.addEventListener('startSync', startSync);
        window.addEventListener('stopSync', stopSync);

        console.color('app plus ready', 'bgsuccess');
    });

    var $status     = $('#userStatus'),
        $statusName = $('#userStatusName'),
        $settingBtn = $('#settingBtn');
    zentao.on('logging', function()
    {
        $statusName.innerHTML = '登录中...';
        $status.setAttribute('data-status', 'logging');
    }).on('logged', function(result)
    {
        checkUserStatus();
    }).on('syncing', function()
    {
        startSync();
    }).on('sync', function(e)
    {
        if(e.result)
        {
            console.color('SYNC>>> ' + e.tab, 'h5|bginfo');
            var currentWin = windows[e.tab];
            if(typeof currentWin === 'object')
            {
                mui.fire(currentWin, 'reloadData', {offline: true});
            }

            if(e.tab != currentTab && e.unreadCount)
            {
                $('#tab-' + e.tab).classList.add('unread');
            }
        }
        stopSync();
    }).on('ready', function()
    {
        openSubWin(null, true);
        if(window.storage.get('autoSync', true))
        {
            zentao.startAutoSync();
        }
    });

    $status.on('tap', function()
    {
        var user = window.user;
        if(!user || user.status != 'online')
        {
            openLoginWindow();
        }
    });

    $('#settingBtn').on('tap', function()
    {
        settingWindow = plus.webview.create('setting.html', 'setting', 
        {
            top             : "0px",
            bottom          : "0px",
            bounce          : "vertical",
            scrollIndicator : "none"
        });
        settingWindow.addEventListener('close', checkUserStatus);
        settingWindow.show('slide-in-right', 200);
    });

    zentao.ready(function()
    {
        checkUserStatus('mild', true);
    });

    function checkUserStatus(mild, first)
    {
        var md     = mild === 'mild';
        var user   = md ? window.user : window.storage.getUser();
        var status = user.status;

        $status.classList.remove('hide-name');
        if(!user || user.status === 'logout')
        {
            $statusName.innerHTML = '请登录';
            status                = 'offline';
            $settingBtn.classList.add('mui-hidden');
            openLoginWindow();
        }
        else if(first)
        {
            user.status           = 'offline';
            status                = 'offline';
            $statusName.innerHTML = '离线';
            if(md) zentao.login();
            $settingBtn.classList.remove('mui-hidden');
        }
        else if(user.status === 'online')
        {
            $statusName.innerHTML = '在线';
            status                = 'online';
            setTimeout(function(){$status.classList.add('hide-name');}, 2000);
            openSubWin();
            $settingBtn.classList.remove('mui-hidden');
        }
        else
        {
            $statusName.innerHTML = '离线';
            status = 'offline';
            if(md) zentao.login();
            $settingBtn.classList.remove('mui-hidden');
        }
        $status.setAttribute('data-status', status);
    }

    function openLoginWindow()
    {
        loginWindow = plus.webview.create('login.html', 'login', 
        {
            top             : "0px",
            bottom          : "0px",
            bounce          : "vertical",
            scrollIndicator : "none"
        });
        loginWindow.addEventListener('close', checkUserStatus);
        loginWindow.show('zoom-in', 200);
    }

    function openSubWin(list, offline)
    {
        if(!defaultTab)
        {
            defaultTab = window.storage.get('lastTab', 'todo');
        }

        list = list || currentTab || defaultTab;
        var currentWin = windows[currentTab];

        if(currentWin && currentTab === list)
        {
            mui.fire(currentWin, 'reloadData', {offline: offline});
            return;
        }

        if(currentWin)
        {
            currentWin.hide(subTabs[list] < subTabs[currentTab] ? 'slide-out-right' : 'slide-out-left', animateSpeed);
            var openeds = currentWin.opened();
            openeds.forEach(function(opendedDialog)
            {
                if(opendedDialog.dialogOptions)
                {
                    opendedDialog.hide('zoom-out', 100);
                }
            });
        }
        if(typeof windows[list] === 'string')
        {
            windows[list] = plus.webview.create(windows[list], list, 
            {
                top             : "44px",
                bottom          : "0px",
                bounce          : "vertical",
                scrollIndicator : "none"
            });

            mainView.append(windows[list]);
        }
        else
        {
            windows[list].show(subTabs[list] > subTabs[currentTab] ? 'slide-in-right' : 'slide-in-left', animateSpeed);
        }

        $('.open-subpage').forEach(function(el)
        {
            el.classList[el.getAttribute('data-id') === list ? 'add' : 'remove']('mui-active');
        });

        currentTab = list;
        window.storage.set('lastTab', currentTab);
    }

    function startSync()
    {
        $('#userStatus').classList.add('syncing');
        console.log('startSync');
    }

    function stopSync()
    {
        $('#userStatus').classList.remove('syncing');
        console.log('stopSync');
    }
})(mui, $);
