package ras.asu.com.letsmeet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.model.LatLng;

// Defining DialogFragment class to show the place details with photo
public class PlaceDialogFragment extends DialogFragment{

    TextView lat = null;
    TextView longt = null;
    TextView slat = null;
    TextView slongt = null;
    TextView elat = null;
    TextView elongt = null;
    Button goThere=null;
    TextView mTVPhotosCount = null;
    TextView mTVVicinity = null;
    ViewFlipper mFlipper = null;
    Place mPlace = null;
    DisplayMetrics mMetrics = null;
LatLng start=null;
    LatLng end=null;
    public PlaceDialogFragment(){
        super();
    }

    public PlaceDialogFragment(Place place, DisplayMetrics dm,LatLng star , LatLng en){
        super();
        this.mPlace = place;
        this.mMetrics = dm;
        this.start =star;
        this.end = en;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // For retaining the fragment on screen rotation
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_layout, null);

        // Getting reference to ViewFlipper
        mFlipper = (ViewFlipper) v.findViewById(R.id.flipper);

        // Getting reference to TextView to display photo count
        mTVPhotosCount = (TextView) v.findViewById(R.id.tv_photos_count);
        lat = (TextView) v.findViewById(R.id.lat);
        longt = (TextView) v.findViewById(R.id.longt);
        slat = (TextView) v.findViewById(R.id.slat);
        slongt = (TextView) v.findViewById(R.id.slongt);
        elat = (TextView) v.findViewById(R.id.elat);
        elongt = (TextView) v.findViewById(R.id.elongt);
        goThere =(Button)v.findViewById(R.id.goThere);

        // Getting reference to TextView to display place vicinity
        mTVVicinity = (TextView) v.findViewById(R.id.tv_vicinity);

        if(mPlace!=null){

            // Setting the title for the Dialog Fragment
            getDialog().setTitle(mPlace.mPlaceName);

            // Array of references of the photos
            Photo[] photos = mPlace.mPhotos;

            // Setting Photos count
            mTVPhotosCount.setText("Photos available : " + photos.length);

            // Setting the vicinity of the place
            mTVVicinity.setText(mPlace.mVicinity);

            lat.setText(mPlace.mLat);
            longt.setText(mPlace.mLng);
            slat.setText(String.valueOf(start.latitude));
            slongt.setText(String.valueOf(start.longitude));
            elat.setText(String.valueOf(end.latitude));
            elongt.setText(String.valueOf(end.longitude));
            // Creating an array of ImageDownloadTask to download photos
            ImageDownloadTask[] imageDownloadTask = new ImageDownloadTask[photos.length];

            int width = (int)(mMetrics.widthPixels*3)/4;
            int height = (int)(mMetrics.heightPixels*1)/2;

            String url = "https://maps.googleapis.com/maps/api/place/photo?";
            String key = "key=AIzaSyCKVPTiiTfWr971nPRxUrX2xrsYWodp9KY";
            String sensor = "sensor=true";
            String maxWidth="maxwidth=" + width;
            String maxHeight = "maxheight=" + height;
            url = url + "&" + key + "&" + sensor + "&" + maxWidth + "&" + maxHeight;

            // Traversing through all the photoreferences
            for(int i=0;i<photos.length;i++){
                // Creating a task to download i-th photo
                imageDownloadTask[i] = new ImageDownloadTask();

                String photoReference = "photoreference="+photos[i].mPhotoReference;

                // URL for downloading the photo from Google Services
                url = url + "&" + photoReference;

                // Downloading i-th photo from the above url
                imageDownloadTask[i].execute(url);
            }
            goThere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(),MenuList.class);
                    i.putExtra("lat",lat.getText().toString());
                    i.putExtra("longt",longt.getText().toString());
                    i.putExtra("slongt",slongt.getText().toString());

                    i.putExtra("slat",slat.getText().toString());
                    i.putExtra("elongt", elongt.getText().toString());
                    i.putExtra("elat", elat.getText().toString());

                    startActivity(i);

                    Map<String , String > jobj = new HashMap<String,String>();
                    jobj.put("lat",lat.getText().toString() );
                    jobj.put("action","startNavigation" );
                    jobj.put("long",longt.getText().toString());
                    jobj.put("fbId2", ProjCostants.OTHER_GUYS_FB_ID);
                    jobj.put("fbId", ProjCostants.FB_ID);
                    StoreUserDataAsyncTask asyn = new StoreUserDataAsyncTask(jobj);
                    asyn.execute();



                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + lat.getText().toString() + "," + longt.getText().toString()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    private Bitmap downloadImage(String strUrl) throws IOException{
        Bitmap bitmap=null;
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);

            /** Creating an http connection to communcate with url */
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            /** Connecting to url */
            urlConnection.connect();

            /** Reading data from url */
            iStream = urlConnection.getInputStream();

            /** Creating a bitmap from the stream returned from the url */
            bitmap = BitmapFactory.decodeStream(iStream);

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
        }
        return bitmap;
    }

    private class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap>{
        Bitmap bitmap = null;
        @Override
        protected Bitmap doInBackground(String... url) {
            try{
                // Starting image download
                bitmap = downloadImage(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Creating an instance of ImageView to display the downloaded image
            ImageView iView = new ImageView(getActivity().getBaseContext());

            // Setting the downloaded image in ImageView
            iView.setImageBitmap(result);

            // Adding the ImageView to ViewFlipper
            mFlipper.addView(iView);

            // Showing download completion message
            Toast.makeText(getActivity().getBaseContext(), "Image downloaded successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Map<String,String> paramsMap= new HashMap<String,String>();

        public StoreUserDataAsyncTask(Map <String,String> p) {
            paramsMap = p;
            // this.userCallBack = userCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";

            URL serverUrl = null;
            try {
                serverUrl = new URL(ProjCostants.SERVER_ADDRESS);
                Log.d("URL PROBLEM", serverUrl.toString());
            } catch (MalformedURLException e) {
                Log.e("AppUtil", "URL Connection Error: "
                        + ProjCostants.SERVER_ADDRESS, e);
                result = ProjCostants.SERVER_ADDRESS;
            }
            StringBuilder postBody = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = paramsMap.entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();
            HttpURLConnection httpCon = null;
            try {
                httpCon = (HttpURLConnection) serverUrl.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setUseCaches(false);
                httpCon.setFixedLengthStreamingMode(bytes.length);
                httpCon.setRequestMethod("GET");
                httpCon.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                OutputStream out = httpCon.getOutputStream();
                out.write(bytes);
                out.close();

                int status = httpCon.getResponseCode();
                if (status == 200) {
                    result =  "Success";
                } else {
                    result = "Post Failure." + " Status: " + status;
                }
            }
            catch (Exception e){
                Log.d("SOME ERROR", e.toString());
                httpCon.disconnect();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

}

