(function(mui, $)
{
    // 空函数
    function shield()
    {
        return false;
    }

    // 取消浏览器的所有事件，使得active的样式在手机上正常生效
    document.addEventListener('touchstart', shield, false); 
    // document.oncontextmenu = shield;//屏蔽选择函数

    // 全局配置(通常所有页面引用该配置，特殊页面使用mui.init({})来覆盖全局配置)
    mui.initGlobal(
    {
        optimize: true,
        swipeBack: false,
        showAfterLoad: true,
        titleBar: false,
        show:
        {
            aniShow  : 'slide-in-right',
            duration : 400
        }
    });

    mui.init();

    mui.plusReady(function()
    {
        var url = plus.webview.currentWebview().getURL();
        console.color('WEBVIEW 准备好了！ [' + url.substring(url.lastIndexOf('/')) + ']', 'h1|bgmuted');

        // bind events
        $('body').on('tap', 'a', function(e)
        {
            var id = this.getAttribute('href');
            if (id && ~id.indexOf('.html'))
            {
                if (window.plus)
                {
                    $.openWindow(
                    {
                        id      : id,
                        url     : this.href,
                        preload : true //TODO 等show，hide事件，动画都完成后放开预加载
                    });
                }
                else
                {
                    document.location.href = this.href;
                }
            }
        });
    });

    /**
     * hyperlink
     */
    mui.ready(function()
    {
        mui('body').on('tap', 'a', function(e)
        {
            var id = this.getAttribute('href');
            if (id)
            {
                if (~id.indexOf('.html'))
                {
                    if (window.plus)
                    {
                        mui.openWindow(
                        {
                            id      : id,
                            url     : this.href,
                            preload : $.os.ios ? false : true //TODO 暂时屏蔽IOS的预加载
                        });
                    }
                    else
                    {
                        document.location.href = this.href;
                    }
                }
                else
                {
                    if (typeof plus !== 'undefined')
                    {
                        plus.runtime.openURL(id);
                    }
                }
            }

        });
    });
})(mui, $);