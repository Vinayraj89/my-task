package com.raj.syncexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.raj.syncexample.Datainterface.Api;
import com.raj.syncexample.retrofit.APIClient;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity  {

    Api apiInterface;

    public static final int PERMISSION_REQUEST_CODE = 200;

    TextView dataToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiInterface = APIClient.getClient().create(Api.class);

//hghjghg  ghkgjkb

        dataToDisplay = (TextView)findViewById(R.id.dataToDisplay);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission())
            {
                System.out.println("!checkPermission()");
                requestPermission();
            }else{

                System.out.println("else !checkPermission()");
                GoFurther();
            }

        }
        else {

            GoFurther();
        }


    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {

        System.out.println("in on requestPermission");
        System.out.println("requestPermission requestPermission ");
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                },
                PERMISSION_REQUEST_CODE);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        System.out.println("onRequestPermissionsResult "+grantResults);
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationfineAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean locationcoarseAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationfineAccepted && locationcoarseAccepted){
                        Snackbar.make(MainActivity.this.getWindow().getDecorView(), "Permission Granted", Snackbar.LENGTH_LONG).show();

                       GoFurther();



                    }
                    else {

                        Snackbar.make(MainActivity.this.getWindow().getDecorView(), "Permission Denied", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION,
                                                                    ACCESS_COARSE_LOCATION
                                                            },
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    private void GoFurther() {

        startService(new Intent(MainActivity.this, MyIntentService.class));

    }






    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.raj.syncexample");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s1 = intent.getStringExtra("datatodisplay");

            System.out.println("s11 : "+s1);
            dataToDisplay.setText(s1);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }
}
