package com.example.calculator2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private int counter;
    static final String CHANNEL_ID = "1";

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        Toast showToast = Toast.makeText(this, "Service is called when user exits the app", Toast.LENGTH_LONG);

        //Step 1 (CREATING THE CHANNEL) for showing notification
        createNotificationChannel();

        final ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        

        if(networkInfo!= null && networkInfo.isConnected()){

            final Timer timer = new Timer(true); //run in background

            timer.schedule(new TimerTask() {
                @Override
                public void run() {  //background run
                    counter++;
                    showToast.show();
                    String displayResult = "";

                    //downloading data from internet
                    URL url = null;
                    try {
                        url = new URL("https://quotes.rest/qod?language=en");
                        InputStream inputStream = url.openStream();
                        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A"); //read stream in one string


                        if(scanner.hasNext()){
                            displayResult = scanner.next();
                        }else {
                            displayResult = "";
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        String parsedDisplayResult = new JSONObject(displayResult)
                                .getJSONObject("contents").getJSONArray("quotes")
                                .getJSONObject(0).getString("quote");

                        //Step 2: Constructing Notification with icon, title and description...
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_baseline_add_reaction_24)
                                .setContentTitle("Test Notification")
                                .setContentText(displayResult)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        if(counter >= 3){
                            //Step 3: Showing the notification
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                            notificationManagerCompat.notify(0, builder.build());

                            //stop the service and cancel the timer
                            timer.cancel();
                            stopSelf();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                }
            }, 1000, 2000);



        }




        counter = 0;






        super.onCreate();
    }


    void createNotificationChannel(){
        //only for API 26+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Test Channel", NotificationManager.IMPORTANCE_DEFAULT);

            //Register our channel with system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }






}