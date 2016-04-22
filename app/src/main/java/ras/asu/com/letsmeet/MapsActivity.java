package ras.asu.com.letsmeet;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity {


    private static final String GOOGLE_API_KEY = "AIzaSyDY1KOswvVJpRcc6UaSejohbC1aJs3NyyM";
    private static  LatLng startPoint;
    private static  LatLng endPoint;
    private double result[]= new double[2];
    private int PROXIMITY_RADIUS = 100000;
    EditText placetext;
    double latitude;
    double longitude;
    Button btnFind;
    double mylat,mylong,yourlat,yourlong;
    private static HashMap <String, Place> mHMReference = new HashMap<String,Place> ();
    Spinner mSprPleaceType;
    private LatLng midPoint;
    GoogleMap googleMap;
    final String TAG = "MapsActivity";
   // private double result[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();
        placetext = (EditText) findViewById(R.id.placetext);
        btnFind = (Button) findViewById(R.id.find);


        Intent intent = getIntent();

        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if(action.equalsIgnoreCase("fromReceiver"))
            {
                mylat= Double.parseDouble(intent.getStringExtra("mylat"));
                mylong= Double.parseDouble(intent.getStringExtra("mylong"));
                // startPoint =  new LatLng(mylat,mylong);
                startPoint =  new LatLng(mylat,mylong);

                yourlat= Double.parseDouble(intent.getStringExtra("yourlat"));
                yourlong= Double.parseDouble(intent.getStringExtra("yourlong"));
//endPoint = new LatLng(yourlat,yourlong);
                endPoint = new LatLng(yourlat,yourlong);

            }

        }
        // startPoint =  new LatLng(mylat,mylong);
        startPoint =  new LatLng(45.722543,-73.998585);
        endPoint = new LatLng(40.7057,-73.9964);
        result = midPoint(45.722543,-73.998585,40.7057,-73.9964);

        midPoint = new LatLng(result[0],result[1]);
        latitude = result[0];
        longitude = result[1];
        MarkerOptions options = new MarkerOptions();
        options.position(startPoint);
        options.position(midPoint);
        options.position(endPoint);
        googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(midPoint,
                13));
        addMarkers();



        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


              if (!mHMReference.containsKey(marker.getId()))
              return false;
               Place place = mHMReference.get(marker.getId());
                DisplayMetrics dm = new DisplayMetrics();
               getWindowManager().getDefaultDisplay().getMetrics(dm);
                PlaceDialogFragment dialogFragment = new PlaceDialogFragment(place, dm ,startPoint,endPoint);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(dialogFragment, "TAG");
                ft.commit();
                return false;








            }

        });


        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                MarkerOptions options = new MarkerOptions();
                options.position(startPoint);
                options.position(midPoint);
                options.position(endPoint);
                googleMap.addMarker(options);
                String url = getMapsApiDirectionsUrl();
                ReadTask downloadTask = new ReadTask();
                downloadTask.execute(url);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(midPoint,
                        13));
                addMarkers();
                String type = placetext.getText().toString();
                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlacesUrl.append("location=" + latitude + "," + longitude);
                googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                googlePlacesUrl.append("&type=" + type);
                //googlePlacesUrl.append("&sensor=true");
                googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

                GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                Object[] toPass = new Object[2];
                toPass[0] = googleMap;
                toPass[1] = googlePlacesUrl.toString();
                googlePlacesReadTask.execute(toPass);
            }
        });

    }








    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true|"
                + startPoint.latitude + "," + startPoint.longitude
                + "|" + "|" + midPoint.latitude + ","
                + midPoint.longitude + "|" + endPoint.latitude + ","
                + endPoint.longitude;

        String sensor = "sensor=false";
        String origin = "origin=" + startPoint.latitude + "," + startPoint.longitude;
        String destination = "destination=" + endPoint.latitude + "," + endPoint.longitude;
        String params = origin + "&" + destination + "&" + waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    private void addMarkers() {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(midPoint)
                    .title("First Point").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.addMarker(new MarkerOptions().position(startPoint)
                    .title("Second Point").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.addMarker(new MarkerOptions().position(endPoint)
                    .title("Mid Point").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }

    public static void setMapDataMarkers( HashMap <String, Place> mPlacesList1){
        mHMReference = mPlacesList1;


    }
    public static double[] midPoint(double lat1,double lon1,double lat2,double lon2){


        double dLon = Math.toRadians(lon2 - lon1);

//convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

//print out in degrees
        double latfinal = Math.toDegrees(lat3);
        double lonfinal = Math.toDegrees(lon3);
        return new double[] {latfinal,lonfinal};
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(20);
                polyLineOptions.color(Color.RED);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }
}