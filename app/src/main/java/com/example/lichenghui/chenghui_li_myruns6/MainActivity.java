package com.example.lichenghui.chenghui_li_myruns6;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.lichenghui.myapplication.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

//    public static String SERVER_ADDR = "https://strong-summer-159600.appspot.com";
    public static String SERVER_ADDR = "http://localhost:8889";
    public static String GCMid = null;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> MyFragments;
    private MyViewPagerAdapter myViewPagerAdapter;
    public static final int PERMISSION_REQUEST_CODE = 1;
    public String[] RequestString = new String[]{ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        MyFragments = new ArrayList<Fragment>();
        MyFragments.add(new StartFragment());
        MyFragments.add(new HistoryFragment());
        MyFragments.add(new SettingsFragment());

        //set view pager
        myViewPagerAdapter = new MyViewPagerAdapter(getFragmentManager(),MyFragments);
        viewPager.setAdapter(myViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        checkPermission();
        new GcmRegistrationAsyncTask(this).execute();
    }

    public void checkPermission(){
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, RequestString, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, RequestString, PERMISSION_REQUEST_CODE);
        }

    }

    private class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String>{

        private Registration regService = null;
        private GoogleCloudMessaging gcm;
        private Context context;

        // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
        private static final String SENDER_ID = "622696655656";

        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (regService == null) {
                Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl(SERVER_ADDR+"/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                regService = builder.build();
            }
            String message = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                // Register sender ID
                String regId = gcm.register(SENDER_ID);

                GCMid = regId;
                message = "Device registered, registration ID=" + regId;
                // send the registration ID to server over HTTP,
                regService.register(regId).execute();

            } catch (IOException ex) {
                ex.printStackTrace();
                message = "Error: " + ex.getMessage();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String msg) {
            //Registration finished
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        }


    }
}
