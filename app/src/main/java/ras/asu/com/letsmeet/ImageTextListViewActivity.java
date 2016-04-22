package ras.asu.com.letsmeet;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImageTextListViewActivity extends Activity implements
        OnItemClickListener {
    @Override
    protected void onStart() {
        super.onStart();

    }

    /*public static final String[] titles = new String[] { "Strawberry",
            "Banana", "Orange", "Mixed" };

    public static final String[] descriptions = new String[] {
            "It is an aggregate accessory fruit",
            "It is the largest herbaceous flowering plant", "Citrus Fruit",
            "Mixed Fruits" };

    public static final Integer[] images = { R.drawable.straw,
            R.drawable.banana, R.drawable.orange, R.drawable.mixed };*/

    ListView listView;
    Button createdb;
    List<RowItem> rowItems;
    ArrayList<String> friends = new ArrayList<String>();
    ArrayList<String> id = new ArrayList<String>();
    List<List<Bitmap>> aList = new ArrayList<List<Bitmap>>();
    String ID;
    Database db;
    File mydir;



    ImageView raks;

    /** Called when the activity is first created. */




List<Bitmap> imagesB = new ArrayList<Bitmap>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = getIntent();

        String jsondata = intent.getStringExtra("jsondata");


         mydir = getApplicationContext().getDir("images", Context.MODE_PRIVATE); //Creating an internal dir;
        if(!mydir.exists())
        {
            mydir.mkdirs();
        }

        /*File sub = new File(getApplicationContext().getFilesDir(), "subdirectory");
        if (!sub.exists())
            sub.mkdirs();*/

        JSONArray friendslist = null;

        try {
            friendslist = new JSONArray(jsondata);
            for (int l = 0; l < friendslist.length(); l++) {
                friends.add(friendslist.getJSONObject(l).getString("name"));
                id.add(friendslist.getJSONObject(l).getString("id"));
                Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
                    imagesB.add(bmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rowItems = new ArrayList<RowItem>();
        for (int i = 0; i < friends.size(); i++) {
            try {
                // Bitmap bitmap = getFacebookProfilePicture(id.get(i));

                RowItem item = new RowItem(friends.get(i), id.get(i),imagesB.get(i));
                rowItems.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new loadImage().execute();
        listView = (ListView) findViewById(R.id.list);
        CustomListViewAdapter adapter = new CustomListViewAdapter(this,
                R.layout.list_item, rowItems);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        registerUser(ProjCostants.FB_ID);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        int id1 = view.getId();
        if(id1==R.id.button1) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Item " + (position + 1) + ": " + rowItems.get(position),
                    Toast.LENGTH_SHORT);
            sendInvite(rowItems.get(position).getImageId());
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

            toast.show();
        }
    }
public class loadImage extends AsyncTask<Void,Void,Void>{


    public loadImage() {
        super();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for(int i=0;i<friends.size();i++) {
            URL imageURL = null;
            try {
                imageURL = new URL("https://graph.facebook.com/" + id.get(i) + "/picture?type=large");
                Bitmap b = imagesB.get(i);
                 b = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                int p;
                //for(p=0;p<friends.size();p++)
                //{
                    File mypath = new File(mydir, id.get(i)+".jpg");
                    FileOutputStream fos = null;
                    try{
                        fos = new FileOutputStream(mypath);
                        b.compress(Bitmap.CompressFormat.PNG,100, fos);
                    }catch(Exception e){
                        e.printStackTrace();

                    }finally{
                        fos.close();
                    }
                    //return mydir.getPath();
                //}


                rowItems.get(i).setImage(b);
                imagesB.set(i, b);



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ImageTextListViewActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomListViewAdapter adapter = new CustomListViewAdapter(getApplicationContext(),
                        R.layout.list_item, rowItems);
                listView.setAdapter(adapter);
                db = new Database(ImageTextListViewActivity.this);
                db.open();
                for(int i=0;i<friends.size();i++)
                {
                    db.createEntry(id.get(i),friends.get(i),getApplicationContext().getDir("images", Context.MODE_PRIVATE )+"/"+friends.get(i)+".JPG",0);
                }



                db.close();
            }//public void run() {
        });

    }
}

    public static Bitmap getFacebookProfilePicture(String userID) throws IOException
    {


        return null;
    }

    private void registerUser(String fbId) {
        Map<String , String > jobj = new HashMap<String,String>();
        jobj.put("gcmId",ProjCostants.REG_ID);
        jobj.put("fbId",fbId);
        jobj.put("action", "addNewUser");
        StoreUserDataAsyncTask asyn = new StoreUserDataAsyncTask(jobj);
        asyn.execute();
    }

    private void sendInvite(String fbId) {
        Map<String , String > jobj = new HashMap<String,String>();
            jobj.put("message",ProjCostants.INVITE_TOKEN);
        jobj.put("fbId",fbId);
        jobj.put("action", "sendRequest");
        jobj.put("myFbId", ProjCostants.FB_ID);
        StoreUserDataAsyncTask asyn = new StoreUserDataAsyncTask(jobj);
        asyn.execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Map <String,String> paramsMap= new HashMap<String,String>();

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


            //return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

}

