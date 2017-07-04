var app = angular.module('helloApp.controllers', ['ngRoute', 'angularFileUpload', 'angular-md5', 'ui.bootstrap', 'ui.bootstrap.progressbar', 'ngSanitize', 'ngAnimate', 'pascalprecht.translate']);

var USER_NOT_LOGGED = 0;
var USER_LOGGED = 1;
var TRANSACTION_OK = 2;
var TRANSACTION_NOT_OK = 3;
var MSGS_SERVICES_CHANNEL = ["CHANNEL_CREATED", "CHANNEL_SIMILAR", "CHANNEL_NOT_CREATED"];

var RESPONSE_SERVICE = [
    {code: 2, msg: 'USER_INFO_UPDATE', style: "success"},
    {code: 3, msg: 'USER_INFO_NOT_UPDATE', style: "danger"},
    {code: 21, msg: 'EMAIL_INVALID', style: "danger"},
    {code: 23, msg: 'EMAIL_EXISTS', style: "danger"},
    {code: 24, msg: 'EMAIL_NOT_MATCH', style: "danger"}
];

var url = "https://eventos-app-swlozano.appspot.com/";
//var url = "http://localhost:8888/";
var urlFiles = url + 'api/file/';
var urlImages = url + 'api/file/img/';
var urlThImages = url + 'api/file/img/th/';
var urlApi = url + "api/";

app.controller('updateUserControl', function ($scope, userInfoFactory, md5, usuarioFactory) {
    $scope.userInfo = {};
    $scope.showAlertInfo = false;
    $scope.showAlertPwd = false;
    $scope.alertInfoStates = [{code: 24, msg: "ok", style: "success"}];
    $scope.alertMsg = "";
    $scope.alertStyle = "";
    var alertTrigger = {info: 1, pwd: 2};
    var valueAlertTrigger = 0;

    var updateUserInfo = function () {
        usuarioFactory.update($scope.userInfo, function (data) {
            $scope.userInfo.pwd = "";
            $scope.userInfo.newPwd = "";
            showAlertInfo(data);
        });
    };

    $scope.onClickBtnCloseAlert = function () {
        $scope.showAlertInfo = false;
    };

    var showAlertInfo = function (dataResponse) {

        if (valueAlertTrigger == alertTrigger['info']) {
            $scope.showAlertInfo = true;
        } else {
            $scope.showAlertPwd = true;
        }

        valueAlertTrigger = 0;
        var data;
        for (i = 0; i < RESPONSE_SERVICE.length; i++) {
            var responseService = RESPONSE_SERVICE[i];
            if (dataResponse.id == responseService.code) {
                data = responseService;
                $scope.alertMsg = responseService.msg;
                $scope.alertStyle = responseService.style;
                break;
            }
        }
    };

    userInfoFactory.get(function (data) {
        $scope.userInfo = data;
    });


    $scope.onClickUpdatePwd = function () {
        valueAlertTrigger = 2;
        resetAlertShow();
        $scope.userInfo.pwd = md5.createHash($scope.userInfo.pwd);
        $scope.userInfo.newPwd = md5.createHash($scope.userInfo.newPwd);
        updateUserInfo();
    };

    $scope.onClickUpdateInfo = function () {
        valueAlertTrigger = 1;
        resetAlertShow();
        updateUserInfo();
    };

    var resetAlertShow = function () {
        $scope.showAlertInfo = false;
        $scope.showAlertPwd = false;
    };

});

/*****inicioControl*****/
app.controller('inicioControl', function ($timeout, checkNewPostFactory, $scope, $scope, $location, userCookFactory, $window, notifyChannelsFactory, notifyJoinsFactory, chCacheService, utilsService, loginCheckFactory, userCookFactory, loginFactory, notifyCountFactory, unjoinFactory, arrayService, $anchorScroll) {
    if (userCookFactory.get().token === undefined) {
//        $window.location.href = url + "login.html";
        $window.location.href = url + "about.html";
    }
    $scope.urlFiles = urlFiles;
    $scope.urlImages = urlThImages;
    $scope.dataSet = {};
    var tabJoinClicked = false;

    var numPages = 10;
    $scope.totalItems = 0;
    $scope.currentPage = 1;
    $scope.comentarios = {};
    $scope.showWarningMsg = false;
    var ini = true;

    var toggleWarningMsg = function (length) {
        if (length <= 0 || length == undefined) {
            $scope.showWarningMsg = true;
        } else {
            $scope.showWarningMsg = false;
        }

    };

    var singOut = function () {
        userCookFactory.remove();
        loginFactory.logout(function (data) {
            $window.location.href = url;
        });
    };

    var getNotifyChannels = function () {
        var params = {};
        params.page = $scope.currentPage;
        $scope.dataSet = {};
        toggleWarningMsg(1);
        notifyChannelsFactory.get(params, function (data) {
            $scope.dataSet = data;
            toggleWarningMsg($scope.dataSet.length);
        });
    };

    var getNotifyJoins = function () {
        var params = {};
        params.page = $scope.currentPage;
        $scope.dataSet = {};
        toggleWarningMsg(1);
        notifyJoinsFactory.get(params, function (data) {
            if (ini === false) {
                $scope.dataSet = data;
            }
            ini = false;
            toggleWarningMsg($scope.dataSet.length);
        });
    };

    var getCountNotify = function () {
        var params = {};
        params.joins = tabJoinClicked;
        notifyCountFactory.get(params, function (data) {
            $scope.totalItems = data.id;
        });
    };

    var checkSession = function () {
        $scope.dataSet = {};
        loginCheckFactory.get(function (data) {
            if (data.id == USER_NOT_LOGGED) {
                singOut();
            }

        });
    };

    checkSession();

    $scope.showYours = true;
    $scope.showJoins = false;
    $scope.clickTabYours = function () {
        tabJoinClicked = false;
        getCountNotify();
        getNotifyChannels();
//        showTab(true);
        $scope.showYours = true;
        $scope.ds_index = 0;
    };

    $scope.clickTabJoins = function () {
        tabJoinClicked = true;
        getCountNotify();
        getNotifyJoins();
//        showTab(false);
        $scope.showYours = false;
        $scope.ds_index = 0;
    };

    $scope.go = function (index) {

        chCacheService.put($scope.dataSet[index].nombre, $scope.dataSet[index].idCanal, $scope.dataSet[index].ownerUser);
    };

    $scope.isImage = function (index) {
        var typeBlob = $scope.dataSet[index].lastPost.typeBlobs[0];
        return utilsService.isImage(typeBlob);
    };

    var getNotifies = function () {
        if (tabJoinClicked) {
            getNotifyJoins();
        } else {
            getNotifyChannels();
        }
    };

    $scope.pageChanged = function () {
        $scope.ds_index = 0;
        getNotifies();
    };

    $scope.newPage = function (is_next) {

        if (is_next) {
            if ($scope.currentPage < $scope.numPages) {
                $scope.currentPage += 1;
            }
        } else {
            if ($scope.currentPage > 1) {
                $scope.currentPage -= 1;
            }
        }
        $scope.pageChanged();
    };

    $scope.clickUnjoin = function (index) {

        var params = {};
        var idCanal = $scope.dataSet[index].idCanal;
        params.id = idCanal;
        unjoinFactory.delete(params, function (data) {
            if (data.id == TRANSACTION_OK) {

                arrayService.remove($scope.dataSet, index);
            }
        });
    };

    $scope.gotoAnchor = function (x) {
        var newHash = 'anchor' + x;
        if ($location.hash() !== newHash) {
            // set the $location.hash to `newHash` and
            // $anchorScroll will automatically scroll to it
            $location.hash('anchor' + x);
        } else {
            // call $anchorScroll() explicitly,
            // since $location.hash hasn't changed
            $anchorScroll();
        }
    };

    $scope.ds_index = 0;
    $scope.onClickCh = function (index) {
        $scope.ds_index = index;
    };

    getNotifyChannels();
    getNotifyJoins();
    getCountNotify();
});

/*****usuarioControl*****/
app.controller('usuarioCanalControl', function ($scope, dataSlackService, canalFactory, $routeParams, $log, $location, canalCacheFactory, canalDeleteFactory, unjoinFactory, joinFactory, arrayService, stringService) {
    $scope.ispublic = true;
    $scope.userName = $routeParams.userName;
    dataSlackService.setDataSlack($scope.userName, "");
    var hasAt = stringService.hasAt($scope.userName, "");
    $scope.dataSet = {};
    $scope.getChannels = function () {
        var params = {userName: $routeParams.userName, ispublic: $scope.ispublic};
        params.page = 1;
        $scope.dataSet = {};
        canalFactory.get(params,
                function (data) {
                    $scope.dataSet = data;
                }
        );
    }

    $scope.clickPublic = function () {
        $scope.ispublic = true;
        $scope.getChannels();
    };

    $scope.clickPrivate = function () {
        $scope.ispublic = false;
        $scope.getChannels();
    };

    $scope.clickDelete = function (index) {
        canalDeleteFactory.delet({userName: $routeParams.userName, idCh: $scope.dataSet[index].idCanal},
        function (data) {
            if (data.id == TRANSACTION_OK) {
                arrayService.remove($scope.dataSet, index);
            }
        }
        );
    };

    $scope.clickEdit = function (index) {
        var id = $scope.dataSet[index].idCanal;
        var uname = $scope.userName;
        var idc = $scope.dataSet[index].idCanal;
        $location.path("/editch/" + uname + "/" + idc);
    };

    $scope.clickUnjoin = function (index) {
        var params = {};
        var idCanal = $scope.dataSet[index].idCanal;
        params.id = idCanal;
        unjoinFactory.delete(params, function (data) {
            $scope.dataSet[index].join = false;
        });
    };

    $scope.clickJoin = function (index) {

        var join = {};
        var idCanal = $scope.dataSet[index].idCanal;
        var owner = $scope.dataSet[index].ownerUser;
        join.idChannel = idCanal;
        join.ownerChannel = owner;
        joinFactory.create(join, function (data) {
            if (data.id == TRANSACTION_OK) {
                $scope.dataSet[index].join = true;
            }
        });
    };

    $scope.getChannels();
});
/**comentariosControl**/
app.controller('comentariosControl', function ($scope, $routeParams, $log, $location, comentarioGetFactory) {

    $scope.comentarios = {};
    comentarioGetFactory.get
            (
                    {owner: $routeParams.userName, id_channel: $routeParams.id, id_post: $routeParams.id_post},
            function (data) {
                $scope.comentarios = data;

            }
            );
});

/*****canalViewControl*****/
app.controller('canalViewControl', function ($location, $scope, newPostService, dataSlackService, postFactory, postGetFactory, $routeParams, canalDetalleFactory, comentarioFactory, FileUploader, $modal, $log, postDeleteFactory, chCacheService, joinFactory, unjoinFactory, stringService, utilsService) {
    //$scope.canal = canalCacheFactory.getCanal();
    var hasWith = false;
    $scope.urlFiles = urlFiles;
    $scope.urlImages = urlThImages;
    $scope.dynamic = 0;
    $scope.showProgress = false;
    var lstBlobs = new Array();
    $scope.userName = $routeParams.userName;
    $scope.idCanal = $routeParams.id;
    $scope.posts = {};
    $scope.canal = {};
    $scope.postData = {};
    $scope.postData.remitente = "@";
    $scope.comentarios = [];
    var files = [];
    stringService.hasAt($routeParams.userName + "/" + $routeParams.id, "");
    $scope.showElementFrm = [true, false, false];
    $scope.showQckPost = false;

    $scope.clickMakePost = function () {
        $scope.showQckPost = !$scope.showQckPost;
    };

    $scope.showWarningMsg = false;

    var toggleWarningMsg = function (length) {
        if (length <= 0) {
            $scope.showWarningMsg = true;
        } else {
            $scope.showWarningMsg = false;
        }

    };

    $scope.clickIcon = function (id_btn) {
        if (id_btn == 0) {

            $scope.showElementFrm = [true, false, false];
        } else if (id_btn == 1) {

            $scope.showElementFrm = [false, true, false];
        } else {
            $scope.showElementFrm = [false, false, true];

        }
    };

    $scope.showElement = function (id_btn) {
        if (id_btn == 0) {
            return $scope.showElementFrm[0];
        } else if (id_btn == 1) {
            return $scope.showElementFrm[1];
        } else {
            return $scope.showElementFrm[2]
        }
    };

    $scope.clickUnjoin = function (index) {
        var params = {};
        var idCanal = $scope.idCanal;
        params.id = idCanal;
        unjoinFactory.delete(params, function (data) {
            $scope.canal.join = false;
        });
    };

    $scope.clickJoin = function (index) {
        var join = {};
        var idCanal = $scope.idCanal;
        var owner = $scope.userName;
        join.idChannel = idCanal;
        join.ownerChannel = owner;
        joinFactory.create(join, function (data) {
            if (data.id == TRANSACTION_OK) {
                $scope.canal.join = true;
            }
        });
    };

    $scope.go = function (index) {
        chCacheService.put($scope.canal.nombre, $scope.canal.idCanal, $scope.canal.ownerUser);
    };


    $scope.progress = function (val) {
        $scope.dynamic = val;
        if (val === 0 || val === 100)
            $scope.showProgress = false;
        else
            $scope.showProgress = true;
    };

    /******MODAL CONTROLLER********/
    $scope.open = function (urlImg, nameFile) {
        var modalInstance = $modal.open({
            templateUrl: 'myModalContent.html',
            controller: ModalInstanceCtrl,
            urlImg: urlImg,
            nameFile: nameFile,
            resolve: {
                file_name: function () {
                    return nameFile;
                },
                urlImage: function () {

                    return urlImg;
                }

            }
        });

        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    /******MODAL CONTROLLER COMMENTS********/
    $scope.openModalComments = function (userName, idCanal, idPost, commentCount) {
        var modalInstance = $modal.open({
            templateUrl: 'modalComment.html',
            controller: ModalControlComments,
            userName: userName,
            idCanal: idCanal,
            idPost: idPost,
            commentCount: commentCount,
            resolve: {
                data: function () {
                    var obj = {};
                    obj.userName = userName;
                    obj.idCanal = idCanal;
                    obj.idPost = idPost;
                    obj.commentCount = commentCount;
                    return obj;
                }
            }
        });

    };

    /***************************/

    $scope.clickImg = function (blob) {
        $scope.open(urlImages + blob.key, blob.name_file);
    };

    $scope.clickComment = function (userName, idCanal, idPost, commentCount) {
        $scope.openModalComments(userName, idCanal, idPost, commentCount);
    };

    $scope.clickDeletePost = function (idPost) {
        var idCanal = $routeParams.id;
        postDeleteFactory.deletePost({idCanal: idCanal, idPost: idPost},
        function (data) {
            getDetailChannel();
            $scope.getPots(hasWith);

        });
    }

    /**********IS_IMAGE***********/

    $scope.isImage = function (item) {
        var type = '|' + item.slice(item.lastIndexOf('/') + 1) + '|';
        return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
    };


    /*********PAGINATION**************/
    $scope.maxSize = 5;
    $scope.bigTotalItems = 0;
    $scope.bigCurrentPage = 1;

    $scope.pageChanged = function () {
        $scope.getPots(hasWith);
    };

    /*********GET DETALLE CANAL**************/

    var getDetailChannel = function () {
        canalDetalleFactory.getCanal({userName: $routeParams.userName, idCanal: $routeParams.id},
        function (data) {
            $scope.canal = data;
            dataSlackService.setDataSlack($scope.userName, $scope.canal.nombre);
            if ($scope.canal.delete == "true") {
                $location.path("/msg");
            }
            setTotalItems();
        }
        );
    };

    var setTotalItems = function () {
        if (hasWith) {
            $scope.bigTotalItems = $scope.canal.postWithCount;
        } else {
            $scope.bigTotalItems = $scope.canal.postCount;
        }
    };

    getDetailChannel();
    $scope.hasWith = false;
    /*********GET POTS**************/
    $scope.getPots = function (haswith) {
        $scope.hasWith = haswith;
        $scope.posts_index = 0;
        if (hasWith !== haswith) {
            $scope.bigCurrentPage = 1;
        }

        hasWith = haswith;
        var params = {idCanal: $routeParams.id, owner: $routeParams.userName, page: $scope.bigCurrentPage};
        params.haswith = haswith;
        toggleWarningMsg(1);
        postGetFactory.get(params,
                function (data) {
                    $scope.posts = data;
                    toggleWarningMsg($scope.posts.length);
                }
        );

        setTotalItems();
    };

    $scope.getPots(hasWith);

    var orderFiles = function () {
        var subfiles = {};
        for (i = 0; i < $scope.posts.length; i++) {
            files = [];
            var cont = 0;
            for (var j = 0; j < $scope.posts[i].blobs.length; j++) {
                if (cont === 0) {
                    subfiles.s1 = {};
                    subfiles.s1.key = $scope.posts[i].blobs[j];
                    subfiles.s1.blob_type = $scope.posts[i].typeBlobs[j];
                    subfiles.s1.name_file = $scope.posts[i].nameBlobs[j];
                }
                else if (cont === 1) {
                    subfiles.s2 = {};
                    subfiles.s2.key = $scope.posts[i].blobs[j];
                    subfiles.s2.blob_type = $scope.posts[i].typeBlobs[j];
                    subfiles.s2.name_file = $scope.posts[i].nameBlobs[j];
                }
                else if (cont === 2) {
                    subfiles.s3 = {};
                    subfiles.s3.key = $scope.posts[i].blobs[j];
                    subfiles.s3.blob_type = $scope.posts[i].typeBlobs[j];
                    subfiles.s3.name_file = $scope.posts[i].nameBlobs[j];
                }
                if (j === ($scope.posts[i].blobs.length - 1) || cont === 2) {
                    files.push(subfiles);
                    subfiles = {};
                    cont = 0;
                } else {
                    cont++;
                }
            }
            $scope.posts[i].blobs = files;
        }
    };

    $scope.clickComentario = function (index) {
        $scope.comentarioData = {};
        $scope.comentarioData.remitente = "@comentario";
        $scope.comentarioData.comentario = $scope.posts[index].comentario;
        $scope.posts[index].comentario = "";
        comentarioFactory.post({id_post: $scope.posts[index].idPost}, $scope.comentarioData,
                function (data) {

                }
        );
    };

    var watchFx = function () {
        var postData = newPostService.get();
        if (postData.chName === $scope.canal.nombre && postData.owner === $scope.canal.ownerUser) {
            $scope.getPots();
            newPostService.set("", "");
        }
    };
    $scope.$watch(newPostService.get, watchFx, true);

    $scope.posts_index = 0;
    $scope.onClickPost = function (index) {
        $scope.posts_index = index;
    };

    $scope.newPage = function (is_next) {
        if (is_next) {
            if ($scope.bigCurrentPage < $scope.numPages) {
                $scope.bigCurrentPage += 1;
            }
        } else {
            if ($scope.bigCurrentPage > 1) {
                $scope.bigCurrentPage -= 1;
            }
        }
        $scope.pageChanged();
    };


    /****UPLOAD_FILE*****/
    var uploader = $scope.uploader = new FileUploader(
            {
                url: urlFiles
            }
    );

    // FILTERS

    uploader.filters.push({
        name: 'customFilter',
        fn: function (item /*{File|FileLikeObject}*/, options) {
            return this.queue.length < 10;
        }
    });

    // CALLBACKS

    uploader.onWhenAddingFileFailed = function (item /*{File|FileLikeObject}*/, filter, options) {
        /*console.info('onWhenAddingFileFailed', item, filter, options);*/
    };
    uploader.onAfterAddingFile = function (fileItem) {
        /*console.log("@@@@@onAfterAddingFile");
         console.info('onAfterAddingFile', fileItem);*/
    };
    uploader.onAfterAddingAll = function (addedFileItems) {
        /*console.info('onAfterAddingAll', addedFileItems);*/
    };
    uploader.onBeforeUploadItem = function (item) {
        /*console.info('onBeforeUploadItem', item);*/
    };
    uploader.onProgressItem = function (fileItem, progress) {
        /*console.info('onProgressItem', fileItem, progress);*/
    };
    uploader.onProgressAll = function (progress) {
        console.info('onProgressAll', progress);
        $scope.progress(progress);
    };
    uploader.onSuccessItem = function (fileItem, response, status, headers) {
        lstBlobs.push(response);
        /*console.info('onSuccessItem', fileItem, response, status, headers);*/
    };
    uploader.onErrorItem = function (fileItem, response, status, headers) {
        /*console.info('onErrorItem', fileItem, response, status, headers);*/
    };
    uploader.onCancelItem = function (fileItem, response, status, headers) {
        /*console.info('onCancelItem', fileItem, response, status, headers);*/
    };
    uploader.onCompleteItem = function (fileItem, response, status, headers) {
        /*console.info('onCompleteItem', fileItem, response, status, headers);*/
    };
    uploader.onCompleteAll = function () {
        $scope.makePost();
        /*console.info('onCompleteAll');*/
    };
    console.info('uploader', uploader);
    // -------------------------------
    var controller = $scope.controller = {
        isImage: function (item) {
            var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    };

    $scope.enableBtnPost = function () {
        var enableBtn = false;
        if ($scope.postData.msg !== undefined) {
            var msg = $scope.postData.msg;
            msg = msg.trim();
            if (msg !== "") {
                enableBtn = true;
            }
        }
        return !($scope.uploader.getNotUploadedItems().length || enableBtn);
    };

    $scope.makePost = function () {
        $scope.postData.blobs = lstBlobs;
        postFactory.post({idCanal: $routeParams.id, owner: $routeParams.userName}, $scope.postData,
                function (data) {
                    lstBlobs = new Array();
                    $scope.uploader.clearQueue();
                    $scope.postData.msg = "";
                    $scope.postData.with = "";
                    getDetailChannel();
                    $scope.getPots(hasWith);
                }
        );
    };

    $scope.clickPost = function () {
        if ($scope.uploader.getNotUploadedItems().length > 0) {
            $scope.uploader.uploadAll();
        } else {
            $scope.makePost();
        }
    };



});

/*****Crear Canal*******/
app.controller('crearCanalControl', function ($scope, $routeParams, canalFactory, newChService, $rootScope) {
    $scope.canal = {};
    $scope.canal.whoPost = 1;
    $scope.alertMsg = "";
    $scope.alertShow = [false, false, false];
    $scope.showElementForm;
    $scope.showElementFrm = [true, false, false, false];
    $scope.showBtnPost = false;
    $scope.classAccess = 'fa fa-unlock fa-lg';
    $scope.accessPublic = true;
    $scope.canal.public = true;
    $rootScope.newCh = false;

    $scope.clickNewCanal = function () {
        $scope.closeAlert();
        canalFactory.crear({userName: '@'}, $scope.canal,
                function (data) {
                    console.info("@@@@@crearCanalControl", $scope.canal, data);
                    alertManage(data.id);
                });
    };

    $scope.onClickBtnAccess = function () {
        if (!$scope.accessPublic)
        {
            $scope.classAccess = 'fa fa-unlock fa-lg';
        } else {
            $scope.classAccess = 'fa fa-lock fa-lg';
        }
        $scope.accessPublic = !$scope.accessPublic;
        $scope.canal.public = $scope.accessPublic;

    };

    var alertManage = function (cod_id) {
        if (cod_id == TRANSACTION_OK) {
            $scope.alertMsg = MSGS_SERVICES_CHANNEL[0];
            $scope.alertShow = [true, false, false];
            newChannel();
        } else if (cod_id == 30) {
            $scope.alertMsg = MSGS_SERVICES_CHANNEL[1];
            $scope.alertShow = [false, true, false];
        } else {
            $scope.alertMsg = MSGS_SERVICES_CHANNEL[2];
            $scope.alertShow = [false, false, true];
        }
    };

    var newChannel = function () {
        $rootScope.newCh = true;
    };

    $scope.closeAlert = function () {
        $scope.alertShow = [false, false, false];
    };

    $scope.showElementForm = function (index) {
        $scope.showElementFrm[index] = !$scope.showElementFrm[index];
        $scope.showBtnPost = $scope.showElementFrm[index];
        for (var i = 0; i < $scope.showElementFrm.length; i++) {
            if (i !== index)
                $scope.showElementFrm[i] = false;
        }
    };
});

/*****crearUsuarioControl*******/
app.controller('crearUsuarioControl', function (changeLanguageFactory, $scope, usuarioFactory, md5, userCookFactory, $window) {

    var msgs = ['USER_NAME_FORMAT_INAVLID', 'EMAIL_INVALID', 'USER_NAME_EXISTS', 'EMAIL_EXISTS'];

    var msgsId = [20, 21, 22, 23];
    $scope.showAlert = false;
    $scope.registerOk = false;
    $scope.alertMsg = "";
    $scope.url = url;
    $scope.usuario = {};
    $scope.resp = "HolaApp";

    if (userCookFactory.get().token !== undefined) {
        $window.location.href = url;
    }

    $scope.clickNewUser = function () {
        $scope.showAlert = false;
        $scope.usuario.pwd = md5.createHash($scope.usuario.pwd);
        usuarioFactory.crear({userName: '@'}, $scope.usuario,
                function (data) {
                    $scope.resp = data;
                    if (data.id == TRANSACTION_OK) {
                        $scope.registerOk = true;
                        $window.location.href = url + "login.html";
                    } else {
                        $scope.getShowAlertMsg(data.id);
                    }
                });
    };

    $scope.getShowAlertMsg = function (id) {
        for (i = 0; i < msgsId.length; i++) {
            if (id == msgsId[i]) {
                $scope.alertMsg = msgs[i];
                break;
            }
        }
        $scope.showAlert = true;
    };

    $scope.closeAlert = function (id) {
        $scope.showAlert = false;
    };

    changeLanguageFactory.cookLanguage();
    $scope.currentLang = changeLanguageFactory.getLanguage();

    $scope.changeLanguage = function (langKey) {
        $scope.currentLang = langKey;
        changeLanguageFactory.changeLanguage(langKey);
    };
});

/*****loginControl*******/
app.controller('loginControl', function (changeLanguageFactory, $scope, authService, loginFactory, userCookFactory, $window) {

    $scope.url = url;
    $scope.loginAlertMsg = "";
    if (userCookFactory.get().token !== undefined) {
        $window.location.href = url;
    }
    $scope.ingreso = {};
    $scope.clickLogin = function () {
        var encodeAuth = authService.encodeAndSetAuth($scope.ingreso.userName, $scope.ingreso.pwd);
        loginFactory.login({},
                function (data) {
                    if (data.pwd !== undefined) {
                        userCookFactory.put(data.nombre, data.apellido, data.userName, data.pwd);
                        authService.setAuthToken();
                        $window.location.href = url;
                    } else {
                        $scope.loginAlertMsg = 'LOGIN_NOT_OK';
                        if (data.userName !== undefined)
                        {
                            $scope.loginAlertMsg = 'ACCOUNT_NOT_ACTIVATED';
                        }
                        $scope.showAlert = true;
                    }

                });
    };
    $scope.showAlert = false;

    $scope.closeAlert = function () {
        $scope.showAlert = false;
    };

    changeLanguageFactory.cookLanguage();
    $scope.currentLang = changeLanguageFactory.getLanguage();

    $scope.changeLanguage = function (langKey) {
        $scope.currentLang = langKey;
        changeLanguageFactory.changeLanguage(langKey);
    };

});

/*****loginControl*******/
app.controller('iniControl', function (checkNewPostFactory, $timeout, changeLanguageFactory, $scope, userCookFactory, loginFactory, userCookFactory, $window, emailInvFactory, $location) {

    $scope.mybind = "<h1>Hello how are you?</h1>"
    $scope.user = userCookFactory.get();
    $scope.show_log = false;
    $scope.show_user = false;
    if ($scope.user.token !== undefined) {
        $scope.show_log = false;
        $scope.show_user = true;
        $scope.userName = $scope.user.userName;
        var data = emailInvFactory.get();
        if (data.idCh !== undefined) {
            emailInvFactory.remove();
            $location.path(data.userName + "/" + data.idCh);
        }
    } else {
        $scope.show_log = true;
        $scope.show_user = false;
    }

    $scope.clickSalir = function () {
        userCookFactory.remove();
        loginFactory.logout(function (data) {
            $window.location.href = url;
        });
    };

    changeLanguageFactory.cookLanguage();
    $scope.currentLang = changeLanguageFactory.getLanguage();

    $scope.changeLanguage = function (langKey) {
        $scope.currentLang = langKey;
        changeLanguageFactory.changeLanguage(langKey);
    };

    var paramsNotifyCh = {};
    var paramsNotifyJoins = {};
    var stackNotifyIndex = {};
    var stackNotifyIndexCh = {};
    $scope.stackNotifyJoins = [];
    $scope.stackNotifyChs = [];


    var addNewNotify = function (join, idCanal, sender, idPost, owner, chName) {

        if (($scope.stackNotifyJoins.length <= 100 && join) || ($scope.stackNotifyChs.length <= 100 && !join)) {
            var dataIndex;
            dataIndex = join ? stackNotifyIndex[idCanal + sender] : stackNotifyIndexCh[idCanal + sender];
            if (dataIndex == undefined) {
                var item = {};
                item = {owner: owner, chName: chName, sender: sender, idCanal: idCanal, idPost: idPost};
                join ? $scope.stackNotifyJoins.push(item) : $scope.stackNotifyChs.push(item);
//                $scope.stackNotifyJoins.push(item);
                var index;
                index = join ? $scope.stackNotifyJoins.length - 1 : $scope.stackNotifyChs.length - 1;
                var item = {index: index};
                join ? stackNotifyIndex[idCanal + sender] = item : stackNotifyIndexCh[idCanal + sender] = item;
//                stackNotifyIndex[idCanal + sender] = {index: ($scope.stackNotifyJoins.length - 1)};
            } else {
                var item;
                item = join ? $scope.stackNotifyJoins[dataIndex.index] : $scope.stackNotifyChs[dataIndex.index];
                item = {owner: owner, chName: chName, sender: sender, idCanal: idCanal, idPost: idPost};
                join ? $scope.stackNotifyJoins[dataIndex.index] = item : $scope.stackNotifyChs[dataIndex.index] = item;
//                $scope.stackNotifyJoins[dataIndex.index] = item;
            }
        }
    };

    $scope.onClickNotifyJoins = function (index) {
        var item = $scope.stackNotifyJoins.splice(index, 1);
        delete stackNotifyIndex[item[0].idCanal + item[0].sender];
        orderStackNotifyIndex($scope.stackNotifyJoins, stackNotifyIndex);
    };

    $scope.onClickNotifyChs = function (index) {
        var item = $scope.stackNotifyChs.splice(index, 1);
        delete stackNotifyIndexCh[item[0].idCanal + item[0].sender];
        orderStackNotifyIndex($scope.stackNotifyChs, stackNotifyIndexCh);
    };

    var orderStackNotifyIndex = function (stackNotify, stackNotifyIndx) {
        var item;
        var dataIndex;
        for (var i = 0; i < stackNotify.length; i++) {
            item = stackNotify[i];
            dataIndex = stackNotifyIndx[item.idCanal + item.sender];
            dataIndex.index = i;
        }
    };

//    var orderStackNotifyIndex = function () {
//        var item;
//        var dataIndex;
//        for (var i = 0; i < $scope.stackNotifyJoins.length; i++) {
//            item = $scope.stackNotifyJoins[i];
//            dataIndex = stackNotifyIndex[item.idCanal + item.sender];
//            dataIndex.index = i;
//        }
//    };

    var setParamsNotify = function (joins, idCh, idLstPost) {
        if (joins) {
            paramsNotifyJoins.idChannel = idCh;
            paramsNotifyJoins.joins = true;
            paramsNotifyJoins.idLastPost = idLstPost;
        } else {
            paramsNotifyCh.idChannel = idCh;
            paramsNotifyCh.joins = false;
            paramsNotifyCh.idLastPost = idLstPost;
        }
    };
    setParamsNotify(true, 0, 0);
    setParamsNotify(false, 0, 0);

    var tickInterval = 73000;
    var tickIntervalCh = 62000;

    var tickch = function () {
        checkNewPostFactory.get(paramsNotifyCh, function (data) {
            if (data.idCanal != 0) {
                addNewNotify(false, data.idCanal, data.lastPost.remitente, data.lastPost.idPost, data.ownerUser, data.nombre);
                setParamsNotify(false, data.idCanal, data.lastPost.idPost);
            }
        });
        $timeout(tickch, tickIntervalCh);
    };
    $timeout(tickch, tickIntervalCh);

    var tickjoins = function () {
        checkNewPostFactory.get(paramsNotifyJoins, function (data) {
            if (data.idCanal != 0 && $scope.user.userName != data.lastPost.remitente) {
                addNewNotify(true, data.idCanal, data.lastPost.remitente, data.lastPost.idPost, data.ownerUser, data.nombre);
                setParamsNotify(true, data.idCanal, data.lastPost.idPost);
            }
        });
        $timeout(tickjoins, tickInterval);
    };
    $timeout(tickjoins, tickInterval);


});

app.controller('editCanalontrol', function (canalDetalleFactory, $scope, $routeParams, canalFactory) {
    $scope.canal = {};
    canalDetalleFactory.getCanal({userName: $routeParams.userName, idCanal: $routeParams.id},
    function (data) {
        $scope.canal = data;
    }
    );

    $scope.showAlert1 = false;
    $scope.showAlert2 = false;
    $scope.imageLockTitle = "";
    $scope.clickEditCanal = function () {
        $scope.closeAlert();
        canalFactory.edit({userName: $routeParams.userName, idCanal: $routeParams.id}, $scope.canal,
                function (data) {
                    if (data.id == TRANSACTION_OK) {
                        $scope.showAlert1 = true;
                        $scope.showAlert2 = false;
                    } else {
                        $scope.showAlert1 = false;
                        $scope.showAlert2 = true;
                    }
                }
        );
    }

    $scope.closeAlert = function () {
        $scope.showAlert1 = false;
        $scope.showAlert2 = false;
    }

    $scope.imageLock = function (public) {
        if (public == "true") {
            $scope.imageLockTitle = "This channel is public"
            return "img/unlock.png";
        }
        $scope.imageLockTitle = "This channel is private"
        return "img/lock.png"
    }
});

app.controller('canalController', function ($rootScope, $scope, $location, newChService, canalFactory, userCookFactory, canalDeleteFactory, canalInvFactory, joinFactory, joinGetFactory, unjoinFactory, arrayService, canalCountFactory, canalCloseFactory) {
    var numPages = 10;
    var counts = {};
    $scope.tabPressed = 0;
    $scope.totalItems = 0;
    $scope.currentPage = 1;
    var usuarioLog = userCookFactory.get();
    $scope.dataSet = {};
    $scope.showNewCh = false;
    $scope.countInvs = 0;
    $scope.showWarningMsg = false;

    var toggleWarningMsg = function (length) {
        if (length <= 0) {
            $scope.showWarningMsg = true;
        } else {
            $scope.showWarningMsg = false;
        }

    };


    var getCounts = function () {
        $scope.currentPage = 1;
        $scope.totalItems = 0;
        var params = {};
        params.userName = "@";
        canalCountFactory.get(params, function (data) {
            counts = data;
            setTotalItems();
        });
    };

    $scope.pageChanged = function () {
        if ($scope.tabPressed == 0) {
            getChannels();
        } else if ($scope.tabPressed == 1) {
            getJoins();
        } else if ($scope.tabPressed == 2) {
            getInvitations();
        }
    };

    var getChannels = function () {
        var params = {userName: usuarioLog.userName, ispublic: true};
        params.page = $scope.currentPage;
        $scope.dataSet = {};
        toggleWarningMsg(1);
        canalFactory.get(params,
                function (data) {
                    $scope.dataSet = data;
                    toggleWarningMsg($scope.dataSet.length);
                }
        );
    };

    $scope.clickNewCanal = function () {
        $location.path("/new");
    };

    $scope.clickDelete = function (index) {
        canalDeleteFactory.delet({userName: usuarioLog.userName, idCh: $scope.dataSet[index].idCanal},
        function (data) {
            if (data.id == TRANSACTION_OK) {
                arrayService.remove($scope.dataSet, index);
            }
        }
        );
    };

    $scope.clickClose = function (index) {
        canalCloseFactory.close({userName: usuarioLog.userName, idCh: $scope.dataSet[index].idCanal},
        function (data) {
            if (data.id == TRANSACTION_OK) {
                $scope.dataSet[index].close = true;
            }
        }
        );
    };

    $scope.clickEdit = function (index) {
        var uname = usuarioLog.userName;
        var idc = $scope.dataSet[index].idCanal;
        $location.path("/editch/" + uname + "/" + idc);
    };

    var getInvitations = function () {
        var params = {userName: usuarioLog.userName};
        params.page = $scope.currentPage;
        $scope.dataSet = {};
        toggleWarningMsg(1);
        canalInvFactory.get(params,
                function (data) {
                    $scope.dataSet = data;
                    $scope.countInvs = $scope.dataSet.length;
                    toggleWarningMsg($scope.dataSet.length);
                }
        );
    };

    var getJoins = function () {
        $scope.dataSet = {};
        var params = {};
        params.page = $scope.currentPage;
        toggleWarningMsg(1);
        joinGetFactory.get(params, function (data) {
            $scope.dataSet = data;
            toggleWarningMsg($scope.dataSet.length);
        });
    };

    $scope.clickTabYours = function () {
        $scope.tabPressed = 0;
        getCounts();
        getChannels();
    };

    $scope.clickTabJoins = function () {
        $scope.tabPressed = 1;
        getCounts();
        getJoins();
    };

    $scope.clickInvs = function () {
        $scope.tabPressed = 2;
        getCounts();
        getInvitations();
    };

    $scope.clickUnjoin = function (index) {
        var params = {};
        var idCanal = $scope.dataSet[index].idCanal;
        params.id = idCanal;
        unjoinFactory.delete(params, function (data) {
            $scope.dataSet.splice(index, 1);
            //getJoins();
        });
    };

    $scope.clickJoin = function (index) {

        var join = {};
        var idCanal = $scope.dataSet[index].idCanal;
        var owner = $scope.dataSet[index].ownerUser;
        join.idChannel = idCanal;
        join.ownerChannel = owner;
        joinFactory.create(join, function (data) {
            var resp = data.id;
            if (resp == TRANSACTION_OK)
            {
                $scope.dataSet.splice(index, 1);
            }
        });
    };
    $scope.totalChs = 0;
    var setTotalItems = function () {
        var c = 0;
        if ($scope.tabPressed === 0) {
            $scope.totalChs = counts.countCh;
            c = counts.countCh;
        } else if ($scope.tabPressed === 1) {
            c = counts.countJoin;
        } else if ($scope.tabPressed === 2) {
            c = counts.countInv;
        }
        $scope.totalItems = c;
    };

    var watchFx = function () {

        if ($rootScope.newCh === true) {
            getCounts();
            getChannels();
        }
        ;
        $rootScope.newCh = false;
    };
    $scope.$watch(newChService.get, watchFx, true);

    getCounts();
    getInvitations();
    getChannels();
});

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

app.controller('ModalInstanceCtrl', function ($scope, $modalInstance, urlImage, file_name) {


    $scope.urlImage = urlImage;
    $scope.file_name = file_name;

    /*$scope.selected = {
     item: $scope.items[0]
     };*/

    $scope.ok = function () {
        /*$modalInstance.close($scope.selected.item);*/
        $modalInstance.dismiss('cancel');
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

app.controller('ModalControlComments', function ($scope, $modalInstance, data, $routeParams, $log, $location, comentarioGetFactory, comentarioDeleteFactory, commentCountFactory) {

    var ccount = 0;
    var numPages = 0;
    var page = 1;

    var params = {};
    params.id_post = data.idPost;
    params.owner = $routeParams.userName;
    params.id_channel = $routeParams.id;
    $scope.comentarios = {};

    var paramsC = {};
    paramsC.id_post = data.idPost;
    paramsC.owner = $routeParams.userName;
    paramsC.id_channel = $routeParams.id;


    var getComments = function () {
        params.page = page;
        comentarioGetFactory.get
                (
                        params,
                        function (data) {
                            $scope.comentarios = data;
                        }
                );
    }

    var getNumPages = function () {
        numPages = Math.round((ccount / 10) + 0.4);
    }

    var getCommentCount = function () {
        params.page = 0;
        commentCountFactory.getCommentCount
                (
                        paramsC,
                        function (data) {
                            ccount = data.id;
                            getNumPages();
                        }
                );
    }

    getCommentCount();
    getComments();


    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.delete = function (id_comment) {
        var paramsDlt = {};
        paramsDlt.id_post = data.idPost;
        paramsDlt.id_comment = id_comment;
        comentarioDeleteFactory.delete(paramsDlt, function (data) {
            getCommentCount();
            getComments();
        });

    };
    /*********PAGINATION COMMENTS**************/

    $scope.nextPage = function () {
        if (page < numPages) {
            page += 1;
            getComments();
        }
    };

    $scope.prevPage = function () {
        if (page > 1) {
            page -= 1;
            getComments();
        }
    };
});

app.controller('contactControl', function ($scope, contactFactory, contactDeleteFactory, searchContactFactory) {

    $scope.contact = {};
    $scope.contacts = {};
    $scope.contactParams = {};
    $scope.searchResult = {};
    $scope.showMsgNoResults = false;

    var getContacts = function () {
        $scope.contactParams = {};
        $scope.contactParams.page = 1;
        contactFactory.get($scope.contactParams, function (data) {
            $scope.contacts = data;
            console.info("@@@@@$scope.contacts", $scope.contacts);
        });
    };

    $scope.clickAdd = function () {
        contactFactory.create($scope.searchResult, function (data) {
            $scope.searchResult = {};
            $scope.contact = {};
            getContacts();
        });
    };

    $scope.clickSearch = function () {
        $scope.showMsgNoResults = false;
        var paramSearch = {};
        paramSearch.contact = $scope.contact.contact;
        searchContactFactory.search(paramSearch, function (data) {
            $scope.searchResult = data;
            if (data.contact == undefined) {
                $scope.showMsgNoResults = true;
            }
        });
    };

    $scope.clickDelete = function (index) {
        $scope.contactParams = {};
        $scope.contactParams.id = $scope.contacts[index].id;
        contactDeleteFactory.delete($scope.contactParams, function (data) {
            getContacts();
        });
    };

    getContacts();
});


app.controller('postViewControl', function ($scope, $http, $routeParams, postDetailFactory, chCacheService, comentarioFactory, comentarioGetFactory, commentCountFactory, comentarioDeleteFactory, utilsService, stringService, canalDetalleFactory) {

    $scope.urlFiles = urlFiles;
    var numPages = 10;
    $scope.totalItems = 0;
    $scope.currentPage = 1;
    $scope.comentarios = {};

    var paramsC = {};
    paramsC.owner = $routeParams.userName;
    paramsC.id_channel = $routeParams.idCh;
    paramsC.id_post = $routeParams.id;

    stringService.hasAt($routeParams.userName + "/" + $routeParams.idCh + "/" + $routeParams.id, "post");

    var getCommentCount = function () {
        paramsC.page = $scope.currentPage;
        commentCountFactory.getCommentCount(paramsC, function (data) {
            $scope.totalItems = data.id;
        });
    };

    var getComments = function () {
        var dataResult = {};
        paramsC.page = $scope.currentPage;
        comentarioGetFactory.get(paramsC, function (data) {
            $scope.comentarios = data;
        });
    };

    $scope.channel = {};
    var getDetailChannel = function () {
        canalDetalleFactory.getCanal({userName: $routeParams.userName, idCanal: $routeParams.idCh},
        function (data) {
            $scope.channel = data;
//            if ($scope.canal.delete == "true") {
//                $location.path("/msg");
//            }
//            setTotalItems();
        }
        );
    };

    getCommentCount();
    getComments();
    getDetailChannel();
//    $scope.channel = chCacheService.get();
    $scope.comment = "";
    $scope.urlImages = urlImages;
    $scope.dataSet = {};
    var params = {};
    params.owner = $routeParams.userName;
    params.idCanal = $routeParams.idCh;
    params.idPost = $routeParams.id;

    postDetailFactory.get(params, function (data) {
        $scope.dataSet = data;
    });

    $scope.clickComentario = function (index) {
        var comentarioData = {};
        comentarioData.remitente = "@comentario";
        comentarioData.comentario = $scope.comment;
        $scope.comment = "";
        comentarioFactory.post({id_post: $routeParams.id}, comentarioData,
                function (data) {
                    console.log("comentarioFactory.post response " + data);
                    getCommentCount();
                    getComments();
                }
        );
    };

    $scope.pageChanged = function () {
        getComments();
    };

    $scope.delete = function (id_comment) {
        var paramsDlt = {};
        paramsDlt.id_post = $routeParams.id;
        paramsDlt.id_comment = id_comment;
        comentarioDeleteFactory.delete(paramsDlt, function (data) {
            getCommentCount();
            getComments();
        });
    };

    $scope.isImage = function (index) {
        var typeBlob = $scope.dataSet.typeBlobs[index];
        return utilsService.isImage(typeBlob);
    };

    $scope.index_blob = 0;
    $scope.changeBlob = function (isNext) {
        console.info("@@@@@$scope.changeBlob ", isNext, $scope.dataSet);
        if (isNext === true) {
            if ($scope.index_blob + 1 < $scope.dataSet.blobs.length) {
                $scope.index_blob += 1;
            } else {
                $scope.index_blob = 0;
            }
        } else {
            if ($scope.index_blob - 1 >= 0) {
                $scope.index_blob -= 1;
            } else {
                $scope.index_blob = $scope.dataSet.blobs.length - 1;
            }

        }
    };

});

app.controller('bestChsController', function ($scope, bestChsFactory, utilsService) {
    $scope.urlFiles = urlFiles;
    $scope.urlImages = urlThImages;
    $scope.url = url;
    $scope.dataSet = {};
    var params = {};
    params.userName = "@";
    bestChsFactory.get(params, function (data) {
        $scope.dataSet = data;
    });

    $scope.isImage = function (index) {
        var typeBlob = $scope.dataSet[index].lastPost.typeBlobs[0];
        return utilsService.isImage(typeBlob);
    };
});

app.controller('bestRedirectController', function ($window) {
    $window.location.href = url + "#/best";
});

app.controller('emailInvController', function ($scope, userCookFactory, $routeParams, $location, emailInvFactory) {

    var userName = $routeParams.userName;
    var idCh = $routeParams.idCh;

    if (userCookFactory.get().token === undefined) {
        //$window.location.href = url + "login.html";
        //console.log("@@@@@emailInvController logeo o registro");
        emailInvFactory.put(userName, idCh);
        var data = emailInvFactory.get();
        console.info("@@@@@data ", data);
    } else {
        $location.path(userName + "/" + idCh);
    }
});


app.directive('dirQckpost', function () {
    return {
        restrict: 'E',
        scope: {
            textoVariable: '=texto'
        },
        templateUrl: 'templates/quick_post.html'
    };
});

app.directive('dirNewChannel', function () {
    return {
        restrict: 'E',
        scope: {
            textoVariable: '=texto'
        },
        templateUrl: 'templates/dir_new_channel.html'
    };
});

app.directive('dirUserUpdate', function () {
    return {
        restrict: 'E',
        scope: {
            textoVariable: '=texto'
        },
        templateUrl: 'templates/dir_user_update.html'
    };
});

app.controller('AppController', function ($scope, FileUploader) {
    var uploader = $scope.uploader = new FileUploader({
        url: 'upload.php'
    });

    // FILTERS

    uploader.filters.push({
        name: 'imageFilter',
        fn: function (item /*{File|FileLikeObject}*/, options) {
            var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    });

    // CALLBACKS

    uploader.onWhenAddingFileFailed = function (item /*{File|FileLikeObject}*/, filter, options) {
        console.info('onWhenAddingFileFailed', item, filter, options);
    };
    uploader.onAfterAddingFile = function (fileItem) {
        console.info('onAfterAddingFile', fileItem);
    };
    uploader.onAfterAddingAll = function (addedFileItems) {
        console.info('onAfterAddingAll', addedFileItems);
    };
    uploader.onBeforeUploadItem = function (item) {
        console.info('onBeforeUploadItem', item);
    };
    uploader.onProgressItem = function (fileItem, progress) {
        console.info('onProgressItem', fileItem, progress);
    };
    uploader.onProgressAll = function (progress) {
        console.info('onProgressAll', progress);
    };
    uploader.onSuccessItem = function (fileItem, response, status, headers) {
        console.info('onSuccessItem', fileItem, response, status, headers);
    };
    uploader.onErrorItem = function (fileItem, response, status, headers) {
        console.info('onErrorItem', fileItem, response, status, headers);
    };
    uploader.onCancelItem = function (fileItem, response, status, headers) {
        console.info('onCancelItem', fileItem, response, status, headers);
    };
    uploader.onCompleteItem = function (fileItem, response, status, headers) {
        console.info('onCompleteItem', fileItem, response, status, headers);
    };
    uploader.onCompleteAll = function () {
        console.info('onCompleteAll');
    };

    console.info('uploader', uploader);
});




app.controller('quickPostController', function ($location, qkPostService, $scope, $rootScope, postFactory, postSlackFactory, postGetFactory, $routeParams, canalDetalleFactory, comentarioFactory, FileUploader, $modal, $log, postDeleteFactory, chCacheService, joinFactory, unjoinFactory, stringService, utilsService, newPostService) {
    //$scope.canal = canalCacheFactory.getCanal();
    var hasWith = false;
    $scope.urlFiles = urlFiles;
    $scope.urlImages = urlThImages;
    $scope.dynamic = 0;
    $scope.showProgress = false;
    var lstBlobs = new Array();
    $scope.userName = $routeParams.userName;
    $scope.idCanal = $routeParams.id;
    $scope.posts = {};
    $scope.canal = {};
    $scope.postData = {};
    $scope.postData.remitente = "@";
    $scope.comentarios = [];
    var files = [];
    $scope.showElementFrm = [false, true, false, false];
    $scope.showBtnPost = false;
    $scope.postData.slack = $rootScope.userName;
//    $scope.isImage = function(index) {
//        var typeBlob = $scope.dataSet[index].lastPost.typeBlobs[0];
//        return utilsService.isImage(typeBlob);
//    };

    $scope.showElementForm = function (index) {
        $scope.showElementFrm[index] = !$scope.showElementFrm[index];
        $scope.showBtnPost = $scope.showElementFrm[index];
        for (var i = 0; i < $scope.showElementFrm.length; i++) {
            if (i !== index)
                $scope.showElementFrm[i] = false;
        }
    };

    $scope.progress = function (val) {
        $scope.dynamic = val;
        if (val === 0 || val === 100)
            $scope.showProgress = false;
        else
            $scope.showProgress = true;
    };


    /**********IS_IMAGE***********/
    $scope.isImage = function (item) {
        var type = '|' + item.slice(item.lastIndexOf('/') + 1) + '|';
        return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
    };

    /****UPLOAD_FILE*****/
    var uploader = $scope.uploader = new FileUploader(
            {
                url: urlFiles
            }
    );

    // FILTERS

    uploader.filters.push({
        name: 'customFilter',
        fn: function (item /*{File|FileLikeObject}*/, options) {
            return this.queue.length < 10;
        }
    });

    // CALLBACKS

    uploader.onWhenAddingFileFailed = function (item /*{File|FileLikeObject}*/, filter, options) {
        /*console.info('onWhenAddingFileFailed', item, filter, options);*/
    };
    uploader.onAfterAddingFile = function (fileItem) {
        /*console.log("@@@@@onAfterAddingFile");
         console.info('onAfterAddingFile', fileItem);*/
    };
    uploader.onAfterAddingAll = function (addedFileItems) {
        /*console.info('onAfterAddingAll', addedFileItems);*/
    };
    uploader.onBeforeUploadItem = function (item) {
        /*console.info('onBeforeUploadItem', item);*/
    };
    uploader.onProgressItem = function (fileItem, progress) {
        /*console.info('onProgressItem', fileItem, progress);*/
    };
    uploader.onProgressAll = function (progress) {
        console.info('onProgressAll', progress);
        $scope.progress(progress);
    };
    uploader.onSuccessItem = function (fileItem, response, status, headers) {
        lstBlobs.push(response);
        /*console.info('onSuccessItem', fileItem, response, status, headers);*/
    };
    uploader.onErrorItem = function (fileItem, response, status, headers) {
        /*console.info('onErrorItem', fileItem, response, status, headers);*/
    };
    uploader.onCancelItem = function (fileItem, response, status, headers) {
        /*console.info('onCancelItem', fileItem, response, status, headers);*/
    };
    uploader.onCompleteItem = function (fileItem, response, status, headers) {
        /*console.info('onCompleteItem', fileItem, response, status, headers);*/
    };
    uploader.onCompleteAll = function () {
        $scope.makePost();
        /*console.info('onCompleteAll');*/
    };
//    console.info('uploader', uploader);
    // -------------------------------
    var controller = $scope.controller = {
        isImage: function (item) {
            var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    };

    $scope.enableBtnPost = function () {
        var enableBtn = false;
        if ($scope.postData.msg !== undefined) {
            var msg = $scope.postData.msg;
            msg = msg.trim();
            if (msg !== "") {
                enableBtn = true;
            }
        }
        return !($scope.uploader.getNotUploadedItems().length || enableBtn);
    };

    $scope.makePost = function () {

        $scope.postData.blobs = lstBlobs;
        $scope.postData.slack = $scope.postData.owner + $scope.postData.channel;
//        postFactory.post({idCanal: $routeParams.id, owner: $routeParams.userName}, $scope.postData,
        postSlackFactory.post({idCanal: 0, owner: "@", slack: $scope.postData.slack}, $scope.postData,
                function (data) {
                    newPostService.set($scope.postData.owner, $scope.postData.channel);
                    lstBlobs = new Array();
                    $scope.uploader.clearQueue();
                    $scope.postData.msg = "";
                    $scope.postData.with = "";
                    getDetailChannel();
                    $scope.getPots(hasWith);
                }
        );
    };

    $scope.clickPost = function () {
        if ($scope.uploader.getNotUploadedItems().length > 0) {
            $scope.uploader.uploadAll();
        } else {
            $scope.makePost();
        }
    };

    var watchFx = function () {
        $scope.postData.owner = qkPostService.getDataSlack().userName;
        $scope.postData.channel = qkPostService.getDataSlack().channel;
        $scope.postData.slack = qkPostService.getDataSlack().userName + qkPostService.getDataSlack().channel;
    };
    $scope.$watch(qkPostService.getDataSlack, watchFx, true);
});


app.controller('startController', function ($scope, $window) {
    $scope.onClickOk = function () {
        $window.location.href = url + "startok.html";
    };
    
    $scope.onClickNot = function () {
        $window.location.href = url + "startnot.html";
    };
    
    $scope.onClickWelcome = function () {
        $window.location.href = url + "welcome.html";
    };

});
