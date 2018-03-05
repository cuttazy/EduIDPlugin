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
     },

     serviceNames: function(successCallback){
        cordova.exec(
            successCallback, // success callback function
            null, // error callback function
            'EduIDPlugin', // Java Class
            'serviceNames', // action name
            []
        );
     },

    getEndpointURL: function(serviceName, protocolName, successCallback){
        cordova.exec(
            successCallback, // success callback function
            null, // error callback function
            'EduIDPlugin', // Java Class
            'getEndpointURL', // action name
            [{                  // args
                "serviceName": serviceName,
                "protocolName": protocolName
            }]
        );
     },

    getDisplayName: function(serviceName, successCallback){
        cordova.exec(
            successCallback, // success callback function
            null, // error callback function
            'EduIDPlugin', // Java Class
            'getDisplayName', // action name
            [{                  // args
                "serviceName": serviceName
            }]
        );
     },

     getServiceToken: function(serviceName, protocolName, successCallback){
        cordova.exec(
            successCallback, // success callback function
            null, // error callback function
            'EduIDPlugin', // Java Class
            'getServiceToken', // action name
            [{                  // args
                "serviceName": serviceName,
                "protocolName": protocolName
            }]
        );
     },

     getServiceUrl: function(serviceName, successCallback, errorCallback){
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'EduIDPlugin', // Java Class
            'getServiceUrl', // action name
            [{                  // args
                "serviceName": serviceName
            }]
        );
     },

     removeService: function(serviceName, successCallback){
        cordova.exec(
            successCallback, // success callback function
            null, // error callback function
            'EduIDPlugin', // Java Class
            'removeService', // action name
            [{                  // args
                "serviceName": serviceName
            }]
        );
     },

     clearAllServices: function(successCallback){
        cordova.exec(
            successCallback, // success callback function
            null, // error callback function
            'EduIDPlugin', // Java Class
            'clearAllServices', // action name
            []
        );
     },

     serialize: function(successCallback){
        cordova.exec(
            successCallback, // success callback function
            null, // error callback function
            'EduIDPlugin', // Java Class
            'serialize', // action name
            []
        );
     },

     parse: function(serviceSpec, successCallback, errorCallback){
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'EduIDPlugin', // Java Class
            'parse', // action name
            [{                  // args
                "serviceSpec": serviceSpec
            }]
        );
     }






}

module.exports = EduIDPlugin;