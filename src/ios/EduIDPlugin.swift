import UIKit
import MobileCoreServices

@objc(EduIDPlugin) class EduIDPlugin : CDVPlugin {
    
    var command : CDVInvokedUrlCommand?
    private static var nail: IdNativeAppIntegrationLayer?
    
    ///Main function which would be call to open the extension
    @objc(authorizeProtocols:)
    func authorizeProtocols(command: CDVInvokedUrlCommand){
        
        //set the protocols and and singleton for the extension
        let singleton = true
        self.command = command
        print("Args : " , command.arguments)
        print("IN PLUGIN SWIFT , singleton : \(singleton)")
        
        let arg = command.arguments.first as! [String : Any]
        var item = arg["protocols"] as! [String]
        item.append(singleton.description)
        
        let activityVC = UIActivityViewController(activityItems: item, applicationActivities: nil)
        DispatchQueue.main.async {
            self.viewController.present(activityVC, animated: true, completion: nil)
        }
        
        activityVC.completionWithItemsHandler = {
            activityType, completed, returnedItems, error in
            print("BACK FROM EXTENSION")
            if(returnedItems == nil || returnedItems!.count <= 0){
                print("No Item found from extension")
                let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR)
                self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
            }else {
                let item : NSExtensionItem = returnedItems?.first as! NSExtensionItem
                self.extractDataFromExtension(item: item)
            }
        }
    }
    
    @objc(authorizeProtocols2:)
    func authorizeProtocols2(command: CDVInvokedUrlCommand){
        
        // var x = self.getNail()
        //print("NAIL TEXT non Singleton : " , x.getText())
        //set the protocols and and singleton for the extension
        let singleton = false
        self.command = command
        print("Args : " , command.arguments)
        print("IN PLUGIN SWIFT , singleton : \(singleton)")
        
        let arg = command.arguments.first as! [String : Any]
        var item = arg["protocols"] as! [String]
        item.append(singleton.description)
        
        let activityVC = UIActivityViewController(activityItems: item, applicationActivities: nil)
        DispatchQueue.main.async {
            self.viewController.present(activityVC, animated: true, completion: nil)
        }
        
        activityVC.completionWithItemsHandler = {
            activityType, completed, returnedItems, error in
            print("BACK FROM EXTENSION")
            if(returnedItems == nil || returnedItems!.count <= 0){
                print("No Item found from extension")
                let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR)
                self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
            }else {
                let item : NSExtensionItem = returnedItems?.first as! NSExtensionItem
                self.extractDataFromExtension(item: item)
            }
        }
    }
    
    //Extract the data from the extension
    func extractDataFromExtension(item : NSExtensionItem){
        DispatchQueue.global(qos: .background).async {
            
            let itemProvider = item.attachments?.first as! NSItemProvider
            
            if itemProvider.hasItemConformingToTypeIdentifier(kUTTypeJSON as String){
                itemProvider.loadItem(forTypeIdentifier: kUTTypeJSON as String, options: nil, completionHandler: { (data, error) -> Void in
                    if error != nil {
                        print("error on extracting data from extension , \(error.localizedDescription)")
                        return
                    }
                    let jsonData = data as! Data
                    EduIDPlugin.nail = IdNativeAppIntegrationLayer(inputData: jsonData)
                    
                    //returned items is in json format
                    let response = self.getNail()!.serialize()
                    print("DATA SOURCE : \(response)")
                    
                    let x = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: response)
                    self.commandDelegate.send(x, callbackId: self.command?.callbackId)
                })
            }
        }
    }
    
    @objc(serviceNames:)
    func serviceNames(commandItem: CDVInvokedUrlCommand){
        DispatchQueue.global(qos: .background).async {
            guard let nailTmp = self.getNail() else {
                return
            }
            
            let names = nailTmp.getServiceNames()
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: names)
            self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
        }
    }
    
    @objc(getEndpointUrl:)
    func getEndpointUrl(commandItem: CDVInvokedUrlCommand){
        DispatchQueue.global(qos: .background).async {
            guard let nailTmp = self.getNail() else{
                return
            }
            
            let arg = commandItem.arguments.first as! [String: Any]
            
            guard let serviceName = arg["serviceName"] as? String else {
                print("getEndpointUrl: 'serviceName' Key is not found in Dictionary")
                return
            }
            guard let prtcl = arg["protocolName"] as? String else{
                print("getEndpointUrl: 'protocolName' Key is not found in Dictionary")
                return
                
            }
            
            let endpoint = nailTmp.getEndpointUrl(serviceName: serviceName, protocolName: prtcl)
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: endpoint)
            self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
        }
    }
    
    @objc(getDisplayName:)
    func getDisplayName(commandItem: CDVInvokedUrlCommand){
        guard let nailTmp = getNail() else {
            return
        }
        
        let arg = commandItem.arguments.first as! [String: Any]
        guard let serviceName = arg["serviceName"] as? String else {
            print("getDisplayName: 'serviceName' Key is not found in Dictionary")
            return
        }
        
        let displayname = nailTmp.getDisplayName(serviceName: serviceName)
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: displayname)
        self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
    }
    
    @objc(getServiceToken:)
    func getServiceToken(commandItem: CDVInvokedUrlCommand){
        DispatchQueue.global(qos: .background).async {
            guard let nailTmp = self.getNail() else {
                return
            }
            
            let arg = commandItem.arguments.first as! [String: Any]
            
            guard let serviceName = arg["serviceName"] as? String else{
                print("getServiceToken: 'serviceName' Key is not found in Dictionary")
                return
            }
            guard let prtcl = arg["protocolName"] as? String else{
                print("getServiceToken: 'protocolName' Key is not found in Dictionary")
                return
            }
            
            let token = nailTmp.getAccessToken(serviceName: serviceName, protocolName: prtcl)
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: token)
            self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
        }
    }
    
    @objc(getServiceUrl:)
    func getServiceUrl(commandItem: CDVInvokedUrlCommand){
        
        guard let nailTmp = getNail() else {
            let pluginRes  = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Login is required to use this function")
            self.commandDelegate.send(pluginRes, callbackId: commandItem.callbackId)
            return
        }
        
        let arg = commandItem.arguments.first as! [String : Any]
        guard let serviceName = arg["serviceName"] as? String else{
            print("getServiceUrl: 'serviceName' Key is not found in Dictionary")
            return
        }
        
        let serviceUrl = nailTmp.getServiceUrl(serviceName: serviceName)
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: serviceUrl)
        self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
        
    }
    
    @objc(removeService:)
    func removeService(commandItem: CDVInvokedUrlCommand){
        
        guard let nailTmp = getNail() else {
            let pluginRes  = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Login is required to use this function")
            self.commandDelegate.send(pluginRes, callbackId: commandItem.callbackId)
            return
        }
        
        let arg = commandItem.arguments.first as! [String : Any]
        guard let serviceName = arg["serviceName"] as? String else{
            print("getServiceUrl: 'serviceName' Key is not found in Dictionary")
            return
        }
        
        nailTmp.removeService(serviceName: serviceName)
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK)
        self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
        
    }
    
    @objc(clearAllServices:)
    func clearAllServices(commandItem: CDVInvokedUrlCommand){
        guard let nailTmp = getNail() else {
            return
        }
        nailTmp.clearAllService()
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK)
        self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
    }
    
    @objc(serialize:)
    func serialize(commandItem: CDVInvokedUrlCommand){
        guard let nailTmp = getNail() else {
            return
        }
        let serializedString = nailTmp.serialize()
        if serializedString.count == 0 {
            return
        }
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: serializedString)
        self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
    }
    
    @objc(parse:)
    func parse(commandItem: CDVInvokedUrlCommand){
        let arg = commandItem.arguments.first as! [String : Any]
        print(arg.keys)
        guard let serviceSpec = arg["serviceSpec"] as? String else{
            print("parse: 'serviceSpec' Key is not found in Dictionary")
            return
        }
        //check if the string is empty or not
        if serviceSpec.count == 0 {
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR)
            self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
        }
        
        let nailTmp = IdNativeAppIntegrationLayer(serializedStr: serviceSpec)
        if nailTmp.getServiceNames().count == 0 {
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR)
            self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
        }else {
            EduIDPlugin.nail = nailTmp
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK)
            self.commandDelegate.send(pluginResult, callbackId: commandItem.callbackId)
        }
    }
    
    func getNail()-> IdNativeAppIntegrationLayer? {
        if EduIDPlugin.nail == nil{
            return nil
        }
        return EduIDPlugin.nail!
        
    }
    
    
}

