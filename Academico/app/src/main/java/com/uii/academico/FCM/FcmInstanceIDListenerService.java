package com.uii.academico.FCM;

import android.util.Log;

//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.uii.academico.Utility.Utils;


public class FcmInstanceIDListenerService extends FirebaseInstanceIdService {


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("TOKEN NYA : ", "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        simpanRegistrasiToken(refreshedToken);
    }

    public void  simpanRegistrasiToken (String refreshedToken) {

        // Token disimpan di Utils sharedpreference kemudian di ambil LoginActivity untuk post ke server academico
        if (Utils.sharedPreferences.contains("FcmToken")) {

           // Gak melakukan apa-apa

        } else {

            Utils.editSP.putString("FcmToken",refreshedToken);
            Utils.editSP.commit();
        }


    }













//    // Jika berhasil terdaftar akantersimpan dalam variable statik ini :
//
//    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
//    public static final String REGISTRATION_ERROR = "RegistrationError";
//
//    public FcmInstanceIDListenerService() {
//        super("");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        registerGCM();
//    }
//
//    private void registerGCM() {
//        Intent registrationComplete = null;
//        String token = null;
//
//        try {
//            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
//            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//            Log.w("GCMRegIntentService", "token:" + token);
//            //notify to UI that registration complete success
//            registrationComplete = new Intent(REGISTRATION_SUCCESS);
//            registrationComplete.putExtra("token", token);
//
//           // Toast.makeText(FcmInstanceIDListenerService.this, "TOKENNYA " + token, Toast.LENGTH_LONG).show();
//            Utils.TOKEN = token;
//
//        } catch (Exception e) {
//            Log.w("GCMRegIntentService", "Registration error");
//            registrationComplete = new Intent(REGISTRATION_ERROR);
//        }
//
//        //Send broadcast
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
//    }

}
