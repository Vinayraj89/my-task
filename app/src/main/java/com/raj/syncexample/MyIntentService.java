package com.raj.syncexample;

import android.Manifest;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.raj.syncexample.Datainterface.Api;
import com.raj.syncexample.model.ApiResponse;
import com.raj.syncexample.retrofit.APIClient;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ on 16/11/19.
 */

public class MyIntentService extends IntentService implements LocationListener {

    Api apiInterface;

    public static Timer timer;
    TimerTask timerTask;
    protected LocationManager locationManager;
    protected String latitude="", longitude="";
    String data;

    boolean status = false;
    final Handler handler = new Handler();

    public MyIntentService() {
        super(MyIntentService.class.getName());
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

//		Toast.makeText(ServerCallService.this,"Service Start",Toast.LENGTH_SHORT).show();

        System.out.println("ServerCallService onCrate");

    }
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        System.out.println("ServerCallService onStart");
        apiInterface = APIClient.getClient().create(Api.class);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        wifistatus();
        startTimer();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub



        System.out.println("ServerCallService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }



    public void startTimer() {


        System.out.println("Timer Started");

        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        timer.schedule(timerTask,  10000,  10000); //
//        timer.schedule(timerTask, 120 * 10 * 1000, 120 * 10 * 1000); //
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp

                        status = false;
                        wifistatus();


                    }
                });
            }
        };
    }


    public void wifistatus()
    {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {

            ApiCall();
        }
    }



    public void ApiCall()
    {
        System.out.println("ApiCall");
        if (latitude.equals(""))
        {
            status = false;
        }else{

            if (!status) {

                status = true;



                Call<ApiResponse> listCall = apiInterface.FetchData(latitude, longitude, "5ad7218f2e11df834b0eaf3a33a39d2a");

                listCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        ApiResponse apiResponse = response.body();

                        data = "Temperature : " +
                                apiResponse.getMain().getTemp() +
                                "\n" +
                                "Temperature(Min) : " +
                                apiResponse.getMain().getTempMin() +
                                "\n" +
                                "Temperature(Max) : " +
                                apiResponse.getMain().getTempMax() +
                                "\n" +
                                "Humidity : " +
                                apiResponse.getMain().getHumidity() +
                                "\n" +
                                "Pressure : " +
                                apiResponse.getMain().getPressure() +
                                "\n" +
                                "Wind Speed : " +
                                apiResponse.getWind().getSpeed() +
                                "\n" +
                                "City : " +
                                apiResponse.getName();

                        Intent intent1 = new Intent();
                        intent1.setAction("com.raj.syncexample");
                        intent1.putExtra("datatodisplay", data);
                        sendBroadcast(intent1);



                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        System.out.println("t : " + t.toString());

                        Toast.makeText(MyIntentService.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }

    }


    @Override
    public void onLocationChanged(Location location) {

//        System.out.println("location.getLatitude() : "+location.getLatitude());
//        System.out.println("location.getLongitude() : "+location.getLongitude());

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());



    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }


}

