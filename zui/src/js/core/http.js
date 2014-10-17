(function()
{
    'use strict';

    var xhr;
    var Http = function() {};

    /**
     * Get last xhr object.
     * @return {object}
     */
    Http.prototype.getXhr = function()
    {
        return xhr;
    };

    /**
     * Send an request to remote server
     * @param  {string}   method          "GET" or "POST"
     * @param  {string}   url            
     * @param  {function} successCallback
     * @param  {function} errorCallback  
     * @return {object}   
     */
    Http.prototype.send = function(method, url, successCallback, errorCallback)
    {
        if(this.debug) console.groupCollapsed('%cHTTP ' + method + ': ' + url, 'color: blue; border-left: 10px solid blue; padding-left: 5px; font-size: 16px; font-weight: bold; background-color: lightblue;');

        xhr = window.plus ? new window.plus.net.XMLHttpRequest() : new XMLHttpRequest();
        var that = this;
        var protocol = /^([\w-]+:)\/\//.test(url) ? RegExp.$1 : window.location.protocol;

        if(this.debug) console.log('XMLHttpRequest:', xhr);

        xhr.onreadystatechange = function()
        {
            if(that.debug) console.log('readyState:', xhr.readyState, ', status:', xhr.status);

            if (xhr.readyState === 4)
            {
                if ((xhr.status >= 200 && xhr.status < 300) || xhr.status === 304 || (xhr.status === 0 && protocol === 'file:'))
                {
                    if(that.debug)
                    {
                        console.group('responseText');
                        console.log("%c" + xhr.responseText, 'color:blue; margin: 3px 0; padding:2px 5px; background: #fafafa');
                        console.groupEnd();
                        console.groupCollapsed('ResponseHeaders');
                        console.log(xhr.getAllResponseHeaders());
                        console.groupEnd();

                        console.groupEnd();
                    }

                    successCallback && successCallback(xhr.responseText, xhr);
                }
                else
                {
                    if(that.debug) console.groupEnd();
                    errorCallback && errorCallback(xhr);
                }
            }
        };

        xhr.open(method, url, true);
        xhr.send();

        return xhr;
    };

    /**
     * Send request to remote server as "GET" method
     * @param  {string}   url
     * @param  {function} successCallback
     * @param  {function} errorCallback 
     * @return {object}      
     */
    Http.prototype.get = function(url, successCallback, errorCallback)
    {
        return this.send('GET', url, successCallback, errorCallback);
    };

    /**
     * Send request to remote server and get json data back
     * @param  {string}   url             
     * @param  {function} successCallback
     * @param  {function} errorCallback
     * @return {object}            
     */
    Http.prototype.getJSON = function(url, successCallback, errorCallback)
    {
        var debug = this.debug;
        return this.get(url, function(response, xhr)
        {
            try
            {
                successCallback(JSON.parse(response), response, xhr);
            }
            catch(e)
            {
                if(debug) console.log('%cWrong json string.', 'color: red; font-weight: bold;');
                errorCallback && errorCallback(response, xhr);
            }
        }, errorCallback);
    };

    /**
     * Send request to remote server as "POST" method
     * @param  {string}   url        
     * @param  {function} successCallback
     * @param  {function} errorCallback 
     * @return {object}             
     */
    Http.prototype.post = function(url, successCallback, errorCallback)
    {
        return this.send('POST', url, successCallback, errorCallback);
    };

    window.http = new Http();
    window.http.debug = true;
}());
