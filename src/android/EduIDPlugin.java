package ch.eduid.provacordovaplugin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


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
    private Activity currentActivity = null;

    // NAIL instance
    private static IdNativeAppIntegrationLayer nail = null;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        currentActivity = this.cordova.getActivity();
        if(this.context == null)
            this.context = this.cordova.getActivity().getApplicationContext();

        try {
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
                        callbackContext.success("TOKEN");
                    }

                    @Override
                    public void onAuthorizeProtocolsError(String s) {
                        callbackContext.error(s);
                    }
                });

                return true;

            }else { // Action not found
                callbackContext.error("Invalid action");
                return false;
            }
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
