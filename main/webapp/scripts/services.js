var services = angular.module('helloApp.services', ['ngResource', 'angular-md5', 'base64', 'ngCookies', 'pascalprecht.translate']);
var baseUrl ='https://eventos-app-swlozano.appspot.com/api';
//var baseUrl = 'http://localhost:8888/api';

//especificando la URL de los recursos a los cuales se quieres acceder, 
//la descripción de los recursos, los métodos que conforman los recursos, 
//los parámetros recibidos y los parámetros de retorno.

/*
 * canalFactory
 * Recurso provee informacion relacionadas a los 
 */

services.factory('canalFactory', function ($resource) {
    return $resource(baseUrl + '/ch/:userName/:idCanal/:ispublic/:page', {}, {
        get: {method: 'GET', params: {userName: '@userName', ispublic: '@ispublic', page: '@page'}, isArray: true},
        getCanal: {method: 'GET', params: {userName: '@userName', idCanal: '@idCanal'}, isArray: false},
        crear: {method: 'POST', params: {userName: '@userName'}, isArray: false},
        edit: {method: 'PUT', params: {userName: '@userName', idCanal: '@idCanal'}, isArray: false}
    });
});

services.factory('canalInvFactory', function ($resource) {
    return $resource(baseUrl + '/ch/:userName/get/inv/:page', {}, {
        get: {method: 'GET', params: {userName: '@userName', page: '@page'}, isArray: true}
    });
});

services.factory('canalDetalleFactory', function ($resource) {
    return $resource(baseUrl + '/ch/:userName/d/:idCanal', {}, {
        getCanal: {method: 'GET', params: {userName: '@userName', idCanal: '@idCanal'}, isArray: false}
    });
});

services.factory('canalCountFactory', function ($resource) {
    return $resource(baseUrl + '/ch/:userName/counts', {}, {
        get: {method: 'GET', params: {userName: '@userName'}, isArray: false}
    });
});

services.factory('usuarioFactory', function ($resource) {
    return $resource(baseUrl + '/usu', {}, {
        crear: {method: 'POST', isArray: false},
        update: {method: 'PUT', isArray: false}
    });
});

services.factory('userInfoFactory', function ($resource) {
    return $resource(baseUrl + '/usu/info', {}, {
        get: {method: 'GET', isArray: false}
    });
});

services.factory('loginFactory', function ($resource) {
    return $resource(baseUrl + '/login', {}, {
        login: {method: 'GET', isArray: false},
        logout: {method: 'DELETE', isArray: false}
    });
});

services.factory('loginCheckFactory', function ($resource) {
    return $resource(baseUrl + '/login/check', {}, {
        get: {method: 'GET', isArray: false}
    });
});

services.factory('canalCrearFactory', function ($resource) {
    return $resource(baseUrl + '/ch/:userName/:idCanal/:ispublic', {}, {
        crear: {method: 'POST', params: {userName: '@userName', ispublic: '@ispublic'}, isArray: false}
    });
});

services.factory('canalDeleteFactory', function ($resource) {
    return $resource(baseUrl + '/ch/:userName/:idCh', {}, {
        delet: {method: 'DELETE', params: {userName: '@userName', idCh: '@idCh'}, isArray: false}
    });
});

services.factory('canalCloseFactory', function ($resource) {
    return $resource(baseUrl + '/ch/:userName/close/:idCh', {}, {
        close: {method: 'PUT', params: {userName: '@userName', idCh: '@idCh'}, isArray: false}
    });
});

services.factory('comentarioFactory', function ($resource) {
    return $resource(baseUrl + '/comentario/:id_post', {}, {
        get: {method: 'GET', params: {id_post: '@id_post'}, isArray: true},
        post: {method: 'POST', params: {id_post: '@id_post'}, isArray: false},
    });
});

services.factory('comentarioGetFactory', function ($resource) {
    return $resource(baseUrl + '/comentario/:owner/:id_channel/:id_post/:page', {}, {
        get: {method: 'GET', params: {owner: '@owner', id_channel: '@id_channel', id_post: '@id_post', page: '@page'}, isArray: true}
    });
});

services.factory('commentCountFactory', function ($resource) {
    return $resource(baseUrl + '/comentario/count/:owner/:id_channel/:id_post', {}, {
        getCommentCount: {method: 'GET', params: {owner: '@owner', id_channel: '@id_channel', id_post: '@id_post'}, isArray: false}
    });
});

services.factory('comentarioDeleteFactory', function ($resource) {
    return $resource(baseUrl + '/comentario/:id_post/:id_comment', {}, {
        delete: {method: 'DELETE', params: {id_post: '@id_post', id_comment: '@id_comment'}, isArray: false}
    });
});

services.factory('postFactory', function ($resource) {
    return $resource(baseUrl + '/post/:idCanal/:owner/:page', {}, {
        get: {method: 'GET', params: {idCanal: '@idCanal', owner: '@owner', page: '@page'}, isArray: true},
        post: {method: 'POST', params: {idCanal: '@idCanal', owner: '@owner'}, isArray: false}
    });
});

services.factory('postSlackFactory', function ($resource) {
    return $resource(baseUrl + '/post/:idCanal/:owner/:page/:slack', {}, {
        post: {method: 'POST', params: {idCanal: '@idCanal', owner: '@owner', slack: '@slack'}, isArray: false}
    });
});

services.factory('postGetFactory', function ($resource) {
    return $resource(baseUrl + '/post/:idCanal/:owner/:page/:haswith', {}, {
        get: {method: 'GET', params: {idCanal: '@idCanal', owner: '@owner', page: '@page', haswith: '@haswith'}, isArray: true}
    });
});

services.factory('postDeleteFactory', function ($resource) {
    return $resource(baseUrl + '/post/:idCanal/:idPost', {}, {
        deletePost: {method: 'DELETE', params: {idCanal: '@idCanal', idPost: '@idPost'}, isArray: false}
    })
});

services.factory('postDetailFactory', function ($resource) {
    return $resource(baseUrl + '/post/:idCanal/detail/:owner/:idPost', {}, {
        get: {method: 'GET', params: {idCanal: '@idCanal', owner: '@owner', idPost: '@idPost'}, isArray: false}
    })
});

services.factory('getFileFactory', function ($resource) {
    return $resource(baseUrl + '/file/:key', {}, {
        get: {method: 'GET', params: {key: '@key'}, isArray: false}
    })
});

services.factory('contactFactory', function ($resource) {
    return $resource(baseUrl + '/contact/:page', {}, {
        create: {method: 'POST', isArray: false},
        get: {method: 'GET', params: {page: '@page'}, isArray: true}
    });
});

services.factory('contactDeleteFactory', function ($resource) {
    return $resource(baseUrl + '/contact/:id', {}, {
        delete: {method: 'DELETE', params: {id: '@id'}, isArray: true}
    });
});

services.factory('searchContactFactory', function ($resource) {
    return $resource(baseUrl + '/contact/search/:contact', {}, {
        search: {method: 'GET', params: {contact: '@contact'}, isArray: false}
    });
});

services.factory('joinFactory', function ($resource) {
    return $resource(baseUrl + '/join/', {}, {
        create: {method: 'POST', isArray: false}
    });
});

services.factory('joinGetFactory', function ($resource) {
    return $resource(baseUrl + '/join/get/:page', {}, {
        get: {method: 'GET', params: {page: '@page'}, isArray: true}
    });
});

services.factory('unjoinFactory', function ($resource) {
    return $resource(baseUrl + '/join/:id', {}, {
        delete: {method: 'DELETE', params: {id: '@id'}, isArray: false}
    });
});

services.factory('notifyChannelsFactory', function ($resource) {
    return $resource(baseUrl + '/notify/:page', {}, {
        get: {method: 'GET', params: {page: '@page'}, isArray: true}
    });
});

services.factory('notifyJoinsFactory', function ($resource) {
    return $resource(baseUrl + '/notify/joins/:page', {}, {
        get: {method: 'GET', params: {page: '@page'}, isArray: true}
    });
});

services.factory('notifyCountFactory', function ($resource) {
    return $resource(baseUrl + '/notify/count/:joins', {}, {
        get: {method: 'GET', params: {joins: '@joins'}, isArray: false}
    });
});

services.factory('checkNewPostFactory', function ($resource) {
    return $resource(baseUrl + '/notify/joins/:idChannel/:idLastPost/:joins', {}, {
        get: {method: 'GET', params: {idChannel: '@idChannel', idLastPost: '@idLastPost', joins: '@joins'}, isArray: false}
    });
});

services.factory('bestChsFactory', function ($resource) {
    return $resource(baseUrl + '/ch/:userName/bestch', {}, {
        get: {method: 'GET', params: {userName: '@userName'}, isArray: true}
    });
});

// return $resource(baseUrl + '/ch/:userName/get/inv/:page', {}, {
//        get: {method: 'GET', params: {userName: '@userName', page: '@page'}, isArray: true}
//    });


services.factory('canalCacheFactory', function () {
    var canalCache = {};

    return {
        saveCanal: function (data) {
            canalCache = data;
        },
        getCanal: function () {
            return canalCache;
        }
    };
});
services.service('cookieFactory', function ($cookieStore) {
    this.put = function (cookie_name, cookie_value)
    {
        $cookieStore.put(cookie_name, cookie_value);
        return true;
    };

    this.get = function (cookie_name)
    {
        return $cookieStore.get(cookie_name);
    };

    this.remove = function (cookie_name)
    {
        return $cookieStore.remove(cookie_name);
    };
});

services.service('userCookFactory', function (cookieFactory) {
    var nombreCk = "nombreCk";
    var apellidoCk = "apellidoCk";
    var userNameCk = "userNameCk";
    var tokenCk = "tokenCk";
    this.put = function (nombre, apellido, userName, token)
    {
        cookieFactory.put(nombreCk, nombre);
        cookieFactory.put(apellidoCk, apellido);
        cookieFactory.put(userNameCk, userName);
        cookieFactory.put(tokenCk, token);
        return true;
    };

    this.get = function ()
    {
        var usuarioLog = {};
        usuarioLog.nombre = cookieFactory.get(nombreCk);
        usuarioLog.apellido = cookieFactory.get(apellidoCk);
        usuarioLog.userName = cookieFactory.get(userNameCk);
        usuarioLog.token = cookieFactory.get(tokenCk);
        return usuarioLog;
    };

    this.remove = function ()
    {
        cookieFactory.remove(nombreCk);
        cookieFactory.remove(apellidoCk);
        cookieFactory.remove(userNameCk);
        cookieFactory.remove(tokenCk);
        return true;
    };
});


services.service('languageCookFactory', function (cookieFactory) {
    var languageCk = "languageCk";

    this.put = function (language)
    {
        cookieFactory.put(languageCk, language);
        return true;
    };

    this.get = function ()
    {
        var language = cookieFactory.get(languageCk);
        return language;
    };

    this.remove = function ()
    {
        cookieFactory.remove(languageCk);
        return true;
    };
});


services.service('changeLanguageFactory', function (languageCookFactory, $translate) {

    this.changeLanguage = function (language)
    {
        languageCookFactory.put(language);
        $translate.use(language);
        return true;
    };

    this.cookLanguage = function ()
    {
        var language = languageCookFactory.get();
        if (language === undefined) {
            language = "en";
        }
        $translate.use(language);
        return true;
    };

    this.getLanguage = function ()
    {
        var language = languageCookFactory.get();
        if (language === undefined) {
            language = "en";
        }
        return language;
    };

});


services.service('emailInvFactory', function (cookieFactory) {
    var nombreCk = "ei_nombreCk";
    var idChCk = "ei_idChCk";
    this.put = function (nombre, idCh)
    {
        cookieFactory.put(nombreCk, nombre);
        cookieFactory.put(idChCk, idCh);
        return true;
    };

    this.get = function ()
    {
        var data = {};
        data.userName = cookieFactory.get(nombreCk);
        data.idCh = cookieFactory.get(idChCk);
        return data;
    };

    this.remove = function ()
    {
        cookieFactory.remove(nombreCk);
        cookieFactory.remove(idChCk);
        return true;
    };
});

services.service('authService', function ($http, md5, $base64, userCookFactory) {

    this.setAuth = function (auth_header)
    {
        $http.defaults.headers.common['Authorization'] = 'Basic ' + auth_header;
        return true;
    };

    this.encodeAuth = function (userName, pwd)
    {
        var pwdMd5 = md5.createHash(pwd);
        return $base64.encode(userName + ":" + pwdMd5);
    };

    this.encodeAndSetAuth = function (userName, pwd)
    {
        var user = userCookFactory.get();
        var base64Encode = this.encodeAuth(userName, pwd);
        return this.setAuth(base64Encode);
    };

    this.setAuthToken = function ()
    {
        var user = userCookFactory.get();
        var base64Encode = $base64.encode(user.userName + ":" + user.token);
        return this.setAuth(base64Encode);
    };

});

services.service('chCacheService', function () {
    var nameCh = "";
    var idCh = 0;
    var ownerCh = "";

    this.put = function (channel, idChannel, ownerChannel)
    {
        nameCh = channel;
        idCh = idChannel;
        ownerCh = ownerChannel;
        return true;
    };

    this.get = function ()
    {
        var channel = {};
        channel.nameCh = nameCh;
        channel.idCh = idCh;
        channel.ownerCh = ownerCh;
        return channel;
    };

});

services.service('arrayService', function () {

    this.remove = function (array, index)
    {
        array.splice(index, 1);
        return true;
    };

});

services.service('utilsService', function () {

    this.isImage = function (item)
    {
        if (item !== undefined) {
            var type = '|' + item.slice(item.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    };


    /* this.validateEmail = function(email) { 
     var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
     return re.test(email);
     } */

});

services.service('stringService', function ($location) {

    this.hasAt = function (str, path)
    {
        var n = str.indexOf("@");
        if (n !== 0) {
            $location.path(path + "/@" + str);
        }
    };

});

services.service('qkPostService', function () {
    var slack = {};
    this.setDataSlack = function (userName, channel)
    {
        slack.userName = userName;
        slack.channel = channel;
    };

    this.getDataSlack = function ()
    {
        return slack;
    };

});

services.service('newChService', function () {
    var newCh = false;
    this.set = function (val)
    {
        newCh = val;
    };

    this.get = function ()
    {
        return newCh;
    };

});

services.service('dataSlackService', function ($rootScope) {
    var slack = {};
    this.setDataSlack = function (userName, channel)
    {
        $rootScope.userName = userName;
        $rootScope.channel = channel;
    };

//    this.getDataSlack = function()
//    {
//       return slack;
//    };

});

services.service('newPostService', function () {
    var postData = {};
    this.set = function (owner, chName)
    {
        postData.owner = owner;
        postData.chName = chName;
    };

    this.get = function ()
    {
        return postData;
    };

});