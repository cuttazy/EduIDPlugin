# EduIDPlugin
Cordova plugin to use NAIL layer of the EduID Native App

##Installation
```
cordova plugin add https://github.com/cuttazy/EduIDPlugin

```


##Methods
###authorizeProtocols(protocols, success, error)
This method implements the protocol authorization.

The method expects a list of protocol names as the first parameter.

####Example:

```javascript
var protocols = [
    "org.ietf.oauth2"
];
var success = function(token) { 
    alert("Success!"); 
};
var error = function(message) { 
    alert("Error! " + message); 
};
EduIDPlugin.authorizeProtocols(protocols, success, error);
```