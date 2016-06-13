package com.example.raazn.googleplaces;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {

    LocationManager lManager;
    Location location;
    boolean isGPSEnabled=false;
    String key,place_type,radius,url,latitude,longitude,test;
    Spinner sp1,sp2;


    // JSON Node names
    private static final String TAG_RESULTS = "results";
    private static final String TAG_NAME = "name";
    private static final String TAG_VICINITY = "vicinity";


    // contacts JSONArray
    JSONArray results = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> placesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp1=(Spinner)findViewById(R.id.spinner1);
        sp2=(Spinner)findViewById(R.id.spinner2);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                place_type=sp1.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                radius=sp2.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //Google Place Api Key
        key="AIzaSyArTyJfLxavyKQT5ROmUb6kTOwdYZ_iB4s";


    }
    public void go(View v){
        isGPSEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("GPS not enabled.");
            builder1.setMessage("Please enable GPS and Try Again. ");

            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "0k",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.out.println("*****************");
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        }
        else{
            if (lManager != null) {
                location = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = Double.toString(location.getLatitude());
                    longitude = Double.toString(location.getLongitude());
                }
            }
            if(latitude!=null&&longitude!=null) {

                url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + radius + "&type=" + place_type + "&key=" + key;
                Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                System.out.println(url);

                placesList = new ArrayList<HashMap<String, String>>();
                new GetPlaceInfo().execute();



            }
        }




    }

    private class GetPlaceInfo extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            test=jsonStr;

            if(jsonStr!=null){
                try {
                    JSONObject jsonObj=new JSONObject(jsonStr);
                    results=jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i=0;i<results.length();i++){
                        JSONObject c = results.getJSONObject(i);
                        String name=c.getString(TAG_NAME);
                        String visinity=c.getString(TAG_VICINITY);

                        System.out.println(name);
                        System.out.println(visinity);

                        HashMap<String, String> result = new HashMap<String, String>();
                        result.put(TAG_NAME,name);
                        result.put(TAG_VICINITY,visinity);

                        placesList.add(result);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println(test);
            super.onPostExecute(aVoid);

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, placesList,
                    R.layout.list_item, new String[] { TAG_NAME,
                    TAG_VICINITY }, new int[] { R.id.name,
                    R.id.vicinity });

            setListAdapter(adapter);
        }
    }



}
