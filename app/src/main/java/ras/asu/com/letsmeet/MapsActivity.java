package ras.asu.com.letsmeet;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity {


    private static final String GOOGLE_API_KEY = "AIzaSyDBdzgdiSUYtpWKKGRP4iwAUnvE1_mNVCI";
    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng MIDPOINT = new LatLng(40.7057, -73.9964);
    private double result[]= new double[2];
    private int PROXIMITY_RADIUS = 5000;
    EditText placetext;
    double latitude;
    double longitude;
    Button btnFind;

   private LatLng BROOKLYN_BRIDGE;
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

        result = midPoint(40.722543,-73.998585,40.7057,-73.9964);
        BROOKLYN_BRIDGE = new LatLng(result[0],result[1]);
        latitude = result[0];
        longitude = result[1];

        MarkerOptions options = new MarkerOptions();
        options.position(LOWER_MANHATTAN);
        options.position(BROOKLYN_BRIDGE);
        options.position(MIDPOINT);
        googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BROOKLYN_BRIDGE,
                13));
        addMarkers();


        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude + "|" + MIDPOINT.latitude + ","
                + MIDPOINT.longitude;

        String sensor = "sensor=false";
        String origin = "origin=" + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude;
        String destination = "destination=" + MIDPOINT.latitude + "," + MIDPOINT.longitude;
        String params = origin + "&" + destination + "&" + waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    private void addMarkers() {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                    .title("First Point"));
            googleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                    .title("Second Point"));
            googleMap.addMarker(new MarkerOptions().position(MIDPOINT)
                    .title("Mid Point"));
        }
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
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }
}