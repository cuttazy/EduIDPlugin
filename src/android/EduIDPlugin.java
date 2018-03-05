package ch.eduid.provacordovaplugin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.eduid.Library.DataAccess.EduIDLibraryException;
import com.eduid.Library.DataAccess.SwissEduIDCommunication;
import com.eduid.Library.IdNativeAppIntegrationLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * TEST
 */
//import org.apache.cordova.api.CallbackContext;
//import org.apache.cordova.api.CordovaPlugin;

/**
 * PRODUCTION
 */
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

/**
 * Created by Yann Cuttaz on 14.06.17.
 */
public class EduIDPlugin extends CordovaPlugin {

    private Context context = null;
    public static final String ACTION_AUTHORIZE_PROTOCOLS = "authorizeProtocols";
    public static final String ACTION_SERVICES_NAMES = "serviceNames";
    public static final String ACTION_GET_ENDPOINT_URL = "getEndpointURL";
    public static final String ACTION_GET_DISPLAY_NAME = "getDisplayName";
    public static final String ACTION_GET_SERVICE_TOKEN = "getServiceToken";
    public static final String ACTION_GET_SERVICE_URL = "getServiceUrl";
    public static final String ACTION_REMOVE_SERVICE = "removeService";
    public static final String ACTION_CLEAR_ALL_SERVICES = "clearAllServices";
    public static final String ACTION_SERIALIZE = "serialize";
    public static final String ACTION_PARSE = "parse";
    private Activity currentActivity = null;

    // NAIL instance
    private static IdNativeAppIntegrationLayer nail = null;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        currentActivity = this.cordova.getActivity();
        if(this.context == null)
            this.context = this.cordova.getActivity().getApplicationContext();

        try {
            /**
             * authorizeProtocols
             */
            if (ACTION_AUTHORIZE_PROTOCOLS.equals(action)) {

                /**
                 * Get args
                 */
                JSONObject args_obj = args.getJSONObject(0);

                /**
                 * Map protocols in array
                 */
                String[] protocols = jsonArrayToStringArray(args_obj.getJSONArray("protocols"));

                /**
                 * Authorize protocols
                 */
                getNAIL().authorizeProtocols(protocols, new SwissEduIDCommunication.AuthorizeProtocolsCallback() {
                    @Override
                    public void onAuthorizeProtocolsFinish() {
                        Intent openCurrentActivity = new Intent(EduIDPlugin.this.currentActivity, EduIDPlugin.this.currentActivity.getClass());
                        openCurrentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                        EduIDPlugin.this.context.startActivity(openCurrentActivity);
                        callbackContext.success();
                    }

                    @Override
                    public void onAuthorizeProtocolsError(String s) {
                        callbackContext.error(s);
                    }
                });

            }

            /**
             * serviceNames
             */
            else if(ACTION_SERVICES_NAMES.equals(action)){

                String[] serviceNames = getNAIL().serviceNames();

                // String ArrayList to JSONArray
                JSONArray ret = new JSONArray();
                for (String serviceName :
                        serviceNames) {
                    ret.put(serviceName);
                }

                callbackContext.success(ret);
            }


            /**
             * getEndpointURL
             */
            else if(ACTION_GET_ENDPOINT_URL.equals(action)){


                /**
                 * Get args
                 */
                JSONObject args_obj = args.getJSONObject(0);

                /**
                 * Get request attributes
                 */
                String serviceName = args_obj.getString("serviceName");
                String protocolName = args_obj.getString("protocolName");


                callbackContext.success(getNAIL().getEndpointURL(serviceName, protocolName));


            }


            /**
             * getDisplayName
             */
            else if(ACTION_GET_DISPLAY_NAME.equals(action)){
                /**
                 * Get args
                 */
                JSONObject args_obj = args.getJSONObject(0);

                /**
                 * Get request attributes
                 */
                String serviceName = args_obj.getString("serviceName");


                callbackContext.success(getNAIL().getDisplayName(serviceName));
            }


            /**
             * getServiceToken
             */
            else if(ACTION_GET_SERVICE_TOKEN.equals(action)){
                /**
                 * Get args
                 */
                JSONObject args_obj = args.getJSONObject(0);

                /**
                 * Get request attributes
                 */
                String serviceName = args_obj.getString("serviceName");
                String protocolName = args_obj.getString("protocolName");

                callbackContext.success(getNAIL().getServiceToken(serviceName, protocolName));
            }


            /**
             * getServiceURL
             */
            else if(ACTION_GET_SERVICE_URL.equals(action)){
                /**
                 * Get args
                 */
                JSONObject args_obj = args.getJSONObject(0);

                /**
                 * Get request attributes
                 */
                String serviceName = args_obj.getString("serviceName");
                try {
                    callbackContext.success(getNAIL().getServiceUrl(serviceName));
                } catch(EduIDLibraryException exception){
                    callbackContext.error(exception.getMessage());
                }
            }


            /**
             * removeService
             */
            else if(ACTION_REMOVE_SERVICE.equals(action)){
                /**
                 * Get args
                 */
                JSONObject args_obj = args.getJSONObject(0);

                /**
                 * Get request attributes
                 */
                String serviceName = args_obj.getString("serviceName");

                getNAIL().removeService(serviceName);
                callbackContext.success();
            }


            /**
             * clearAllServices
             */
            else if(ACTION_CLEAR_ALL_SERVICES.equals(action)){
                getNAIL().clearAllServices();
                callbackContext.success();
            }


            /**
             * serialize
             */
            else if(ACTION_SERIALIZE.equals(action)){
                callbackContext.success(getNAIL().serialize());
            }


            /**
             * parse
             */
            else if(ACTION_PARSE.equals(action)){
                /**
                 * Get args
                 */
                JSONObject args_obj = args.getJSONObject(0);

                /**
                 * Get request attributes
                 */
                String serviceSpec = args_obj.getString("serviceSpec");

                try {
                    getNAIL().parse(serviceSpec);
                    callbackContext.success();
                } catch(JSONException jsonException){
                    callbackContext.error(jsonException.getMessage());
                }
                catch(EduIDLibraryException exception){
                    callbackContext.error(exception.getMessage());
                }
            }




            else { // Action not found
                callbackContext.error("Invalid action");
                return false;
            }

            return true;


        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }

    }


    /**
     * Build a String array from a JSON array
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    private String[] jsonArrayToStringArray(JSONArray jsonArray) throws JSONException {
        String[] ret = new String[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++){
            ret[i] = jsonArray.getString(i);
        }
        return ret;
    }

    /**
     * Get the NAIL instance
     * @return
     */
    private IdNativeAppIntegrationLayer getNAIL(){
        if(nail == null) nail = new IdNativeAppIntegrationLayer(this.context);
        return nail;
    }

}
