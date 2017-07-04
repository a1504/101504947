var filters = angular.module('helloApp.filters', []);

filters.filter('langFilter', function (changeLanguageFactory) {
    return function (input) {
        return input === "en" ? 'English' : '\Español';
    };
});

var msgTemplate = {NAME: {en: "M2-English 1", es: "M2-Spanish 1"}};

var msgs = {
    //Home Msgs
    NO_MADE_POSTS: {en: "It has not made any publication in your channels", es: "No se ha realizado ninguna publicación en tus canales"},
    NO_MADE_POSTS_JOINS: {en: "It has not made any publication channel partners", es: "No se ha realizado ninguna publicación en canales asociados"},
    //Post Msg
    NO_POSTS: {en: "It has not made any publication in", es: "No se ha realizado ninguna publicación en"},
    NO_MSG_SECRETLY: {en: "No messages have been shared secretly with you", es: "No se han compartido mensajes secretamente contigo"},
    //Channels Msg
    NO_CHANNELS: {en: "You have not created any channel", es: "No has creado ningún canal"},
    NO_JOINS: {en: "You have not linked to any channel", es: "No te has vinculado a ningún canal"},
    NO_INVITATIONS: {en: "You have not received any invitation", es: "No has recibido ninguna invitación"},
    //create Channels
    CHANNEL_CREATED: {en: "Channel was created", es: "El canal fue creado"},
    CHANNEL_SIMILAR: {en: "Already have a channel with the same name", es: "Ya existe un canal con el mismo nombre"},
    CHANNEL_NOT_CREATED: {en: "Channel wasn't created", es: "No se creó el canal"}

};

filters.filter('msgFilter', function (changeLanguageFactory) {
    return function (msg) {
        var lang = changeLanguageFactory.getLanguage();
        return msgs[msg][lang];
    };
});

filters.filter('limitFilter', function () {
    return function (text, all) {
        if (text.length > 300 && all) {
            text = text.replace(/<br\/>/g, " ");
            text = text.replace(/<a target="_blank" href=/g, " ");
            text = text.replace(/<\/a>/g, " ");
            return text.slice(0, 300) + ".....";
        }
        return  text;
    };
});