package com.esprit.goga;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by khmai on 02/05/2018.
 */

public class InstanceIdService extends FirebaseInstanceIdService {

    public InstanceIdService() {
        super();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();

        //sends this token to the server
        sendToServer(token);
    }

    private void sendToServer(String token) {


        System.out.println("token "+token);

       /* try {
            URL url = new URL("https://www.whatsthatlambda.com/store");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod("POST");

            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

            dos.writeBytes("token=" + token);

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Do whatever you want after the
                // token is successfully stored on the server
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
