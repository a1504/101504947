var helloApp = angular.module('helloApp', ['ngRoute', 'helloApp.controllers', 'helloApp.services', 'pascalprecht.translate', 'helloApp.filters']);
helloApp.config(function ($routeProvider, $translateProvider) {

    $routeProvider.when('/test', {templateUrl: 'templates/contact.html', controller: 'contactControl'});
    $routeProvider.when('/inicio', {templateUrl: 'templates/home.html', controller: 'inicioControl'});
//    $routeProvider.when('/best', {templateUrl: 'templates/best.html', controller: 'bestChsController'});
    $routeProvider.when('/new', {templateUrl: 'templates/new_channel.html', controller: 'crearCanalControl'});
    $routeProvider.when('/channels', {templateUrl: 'templates/channels.html', controller: 'canalController'});
    $routeProvider.when('/update', {templateUrl: 'templates/dir_user_update.html', controller: ''});
    $routeProvider.when('/contact', {templateUrl: 'templates/contact_1.html', controller: 'contactControl'});
    $routeProvider.when('/msg', {templateUrl: 'templates/msg.html', controller: ''});
    $routeProvider.when('/email/inv/:userName/:idCh', {templateUrl: 'templates/email.html', controller: 'emailInvController'});
    $routeProvider.when('/post/:userName/:idCh/:id', {templateUrl: 'templates/post_view.html', controller: 'postViewControl'});
    $routeProvider.when('/editch/:userName/:id', {templateUrl: 'templates/edit_ch_test.html', controller: 'editCanalontrol'});
    $routeProvider.when('/:userName', {templateUrl: 'templates/user_channels.html', controller: 'usuarioCanalControl'});
    $routeProvider.when('/:userName/:id', {templateUrl: 'templates/canal.html', controller: 'canalViewControl'});
    $routeProvider.when('/:userName/:id/:id_post', {templateUrl: 'templates/comentarios.html', controller: 'comentariosControl'});
    $routeProvider.otherwise({redirectTo: '/inicio'});
    $translateProvider.translations('en', {
        SIGN_IN: 'Sign in',
        SIGN_IN_INFO: 'To register please complete the application data in the form below',
        REGISTER_HERE: 'Register Here',
        USER_NAME: 'User Name',
        EMAIL: 'Email Address',
        NAME: 'Name',
        LAST_NAME: 'Last Name',
        PASSWORD: 'Password',
        INAVALID_EMAIL: 'This is not a valid email.',
        CANCEL: 'Cancel',
        SEND: 'Send',
        LOGIN: 'Login',
        REGISTER: 'Register',
        LOGIN_NOW: 'Login Now',
        LOGIN_NOW_INFO: 'To enter please complete the application data in the form below',
        LOGIN_HERE: 'Login Here',
        YOURS: 'Yours',
        JOINS: 'Joins',
        CHANNELS: 'Channels', //listo
        SING_OUT: 'Sign out',
        POST_BY: 'Post by',
        VIEW_POST: 'View Post',
        HOME: 'Home',
        NEW_CHANNEL: 'New Channel',
        INVITATIONS: 'Invitations',
        JOINERS: 'Joiners',
        EDIT: 'Edit',
        CLOSE: 'Close',
        VIEW_USER_PROFILE: 'View User Profile',
        USER_DETAILS: 'User Details', //<-
        DELETE: 'Delete',
        PUBLIC: 'Public',
        PRIVATE: 'Private',
        JOIN: 'Join',
        UNJOIN: 'UNJOIN',
        BY: 'by',
        JOINERS:'Joiners',
                MAKE_POST: 'Make Post',
        CHANNEL_DATAILS: 'Channel Details',
        WITH_ALL: 'With All',
        WITH_YOU_AND_OTHERS: 'With You + Others',
        POSTS: 'Posts',
        VIEW_POST:'View Post',
                POST_DETAILS: 'Post Details',
        MAKE_COMMENT: 'Make a Comment',
        COMMENT: 'Comment',
        CHANNEL_NAME: 'Channel Name',
        CHANNEL_DESCRIPTION: 'Channel Description',
        DESCRIPTION: 'Description',
        WHO_CAN_POST: 'Who can post on my channel?',
        JUST_ME: 'Just Me',
        INVITED: 'Invited',
        ANYONE: 'Anyone',
        EG: 'eg',
        NOTE: 'NOTE',
        MESSAGE: 'MESSAGE',
        POST: 'Post',
        UPLOAD: 'UPLOAD',
        ONLY_WITH: 'ONLY WITH',
        SHARED_ONLY_WITH: 'Shared only with',
        UPDATE_USER_PROFILE: 'Update User Profile',
        UPDATE_YOUR_INFORMATION: 'Update your Information',
        UPDATE_YOUR_PASSWORD: 'Update your Password',
        NEW_PASSWORD: 'New Password',
        PROFILE: 'Profile',
        UPDATE_INFO: 'Update Info',
        LANGUAGE: 'Language',
        ENGLISH: 'English',
        SPANISH: 'Español',
        LOADING: 'Loading…',
        LOGIN_NOT_OK: 'Please verify that your username and password are correct.',
        INVITE: 'Invite',
        //Register Msg
        USER_NAME_FORMAT_INAVLID: "The username must contain only numbers and letters",
        EMAIL_INVALID: "Invalid email",
        USER_NAME_EXISTS: "The user name already exists",
        EMAIL_EXISTS: "The email entered already exists",
        //UPDATE INFORMATION PROFILE
        USER_INFO_UPDATE: 'Updated information',
        USER_INFO_NOT_UPDATE: 'It failed to update information',
        EMAIL_NOT_MATCH: 'The current password does not match',
        //
        VIEW_MORE: 'View more',
        VIEW_LESS: 'View less',
        //
        CONTACTS: 'Contacts',
        USER_NAME_SEARCH: 'Search by User Name',
        ADD: 'Add',
        SEARCH: 'Search',
        NO_RESULTS: 'No results found',
        UPDATE_CHANNEL: 'Update Channel',
        UPDATE_CHANNEL_INFO: 'Update Channel Info',
        ACCOUNT_NOT_ACTIVATED: 'Your account has not been activated, yet'
        
    });
    $translateProvider.translations('es', {
        SIGN_IN: 'Registrarse',
        SIGN_IN_INFO: 'Para registrarse en la aplicacion por favor complete los datos en el formulario de abajo',
        REGISTER_HERE: 'Registrarse aquí',
        USER_NAME: 'Nombre de usuario',
        EMAIL: 'Email',
        NAME: 'Nombre',
        LAST_NAME: 'Apellidos',
        PASSWORD: 'Contraseña',
        INAVALID_EMAIL: 'El Email no tiene un formato valido',
        CANCEL: 'Cancelar',
        SEND: 'Enviar',
        LOGIN: 'Ingresar',
        REGISTER: 'Registrarse',
        LOGIN_NOW: 'Ingrese ahora',
        LOGIN_NOW_INFO: 'Para ingresar a la aplicacion por favor complete los datos en el formulario de abajo',
        LOGIN_HERE: 'Ingresar',
        YOURS: 'Yours',
        JOINS: 'Joins',
        CHANNELS: 'Canales',
        SING_OUT: 'Salir',
        POST_BY: 'Publicado Por',
        VIEW_POST: 'Ver publicación',
        HOME: 'Inicio',
        NEW_CHANNEL: 'Nuevo Canal',
        INVITATIONS: 'Invitaciones',
        JOINERS: 'Joiners',
        EDIT: 'Editar',
        CLOSE: 'Cerrar',
        VIEW_USER_PROFILE: 'Perfil de usuario',
        USER_DETAILS: 'Detalles del usuario',
        DELETE: 'Eliminar',
        PUBLIC: 'Publico',
        PRIVATE: 'Privado',
        JOIN: 'Join',
        UNJOIN: 'UNJOIN',
        BY: 'por',
        JOINERS:'Joiners',
                MAKE_POST: 'Nueva publicación',
        CHANNEL_DATAILS: 'Detalles de Canal',
        WITH_ALL: 'Con todos',
        WITH_YOU_AND_OTHERS: 'Contigo y otros',
        POSTS: 'Publicaciones',
        VIEW_POST:'Ver publicación',
                POST_DETAILS: 'Detalles de publicación',
        MAKE_COMMENT: 'Comentario',
        COMMENT: 'Comentar',
        CHANNEL_NAME: 'Nombre del Canal',
        CHANNEL_DESCRIPTION: 'Descripción del Canal',
        DESCRIPTION: 'Descripción',
        WHO_CAN_POST: 'Quien puede publicar',
        JUST_ME: 'Solo yo',
        INVITED: 'Invitados',
        ANYONE: 'Cualquiera',
        EG: 'Ejm',
        NOTE: 'NOTA',
        MESSAGE: 'MENSAJE',
        POST: 'Publicar',
        UPLOAD: 'ARCHIVOS',
        ONLY_WITH: 'SOLO CON',
        SHARED_ONLY_WITH: 'Compartir solo con',
        UPDATE_USER_PROFILE: 'Actualizar perfil de usuario',
        UPDATE_YOUR_INFORMATION: 'Actualiza tu información personal',
        UPDATE_YOUR_PASSWORD: 'Actualiza tu contraseña',
        NEW_PASSWORD: 'Nueva Contraseña',
        PROFILE: 'Perfil',
        UPDATE_INFO: 'Actualizar información',
        LANGUAGE: 'Lenguaje',
        ENGLISH: 'English',
        SPANISH: 'Español',
        LOADING: 'Cargando…',
        LOGIN_NOT_OK: 'Por favor, verifica que tu nombre de usuario y contraseña sean correctos.',
        INVITE: 'Invitados',
        //Register Msg
        USER_NAME_FORMAT_INAVLID: "El nombre de usuario debe contener solo números y letras",
        EMAIL_INVALID: "Email no válido",
        USER_NAME_EXISTS: "El nombre de usuario ya existe",
        EMAIL_EXISTS: "El Email ingresado ya existe",
        //UPDATE INFORMATION PROFILE
        USER_INFO_UPDATE: 'Información Actualizada',
        USER_INFO_NOT_UPDATE: 'No se pudo actualizar la información',
        EMAIL_NOT_MATCH: 'La contraseña actual no corresponde',
        //
        VIEW_MORE: 'Ver más',
        VIEW_LESS: 'Ver menos',
        //
        CONTACTS: 'Contactos',
        USER_NAME_SEARCH: 'Buscar por nombre de usuario',
        ADD: 'Agregar',
        SEARCH: 'Buscar',
        NO_RESULTS: 'No se encontraron resultados',
        UPDATE_CHANNEL: 'Actualizar Canal',
        UPDATE_CHANNEL_INFO: 'Actualizar Información del Canal',
        ACCOUNT_NOT_ACTIVATED: 'Tu cuenta aún no ha sido activada'
    });
    $translateProvider.preferredLanguage('es');
});
helloApp.run(function ($rootScope, authService, userCookFactory, $location, qkPostService, newChService, newPostService) {
    $rootScope.userName = "";
    $rootScope.channel = "";
    var setDataSlack = function () {
        qkPostService.setDataSlack($rootScope.userName, $rootScope.channel);
    };
    var notifyNewCh = function () {
        newChService.set($rootScope.newCh);
    };
    var notifyNewPost = function () {
        newPostService.set($rootScope.newCh);
    };
    $rootScope.$watch('userName', setDataSlack, true);
    $rootScope.$watch('channel', setDataSlack, true);
    $rootScope.$watch('newCh', notifyNewCh, true);
    $rootScope.$watch('newPost', notifyNewPost, true);
    authService.setAuthToken();
});

helloApp.controller('controlx', function ($scope) {
    $scope.fx = "Always Relax";
});