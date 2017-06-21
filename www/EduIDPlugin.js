var EduIDPlugin = {
     authorizeProtocols: function(protocols, successCallback, errorCallback){
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'EduIDPlugin', // Java Class
            'authorizeProtocols', // action name
            [{                  // args
                "protocols": protocols
            }]
        );
     }
}

module.exports = EduIDPlugin;