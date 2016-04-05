package ras.asu.com.letsmeet;

/**
 * Created by SahanaSekhar on 3/31/16.
 */

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
import java.util.List;

public class ImageTextListViewActivity extends Activity implements
        OnItemClickListener {

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
       // createdb = (Button)findViewById(R.id.button3);
         //raks = (ImageView)findViewById(R.id.raks);
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
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + rowItems.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
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
                    File mypath = new File(mydir, id.get(i));
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
                    db.createEntry(id.get(i),friends.get(i),getApplicationContext().getDir("images", Context.MODE_PRIVATE )+"/"+friends.get(i)+".PNG");
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

}

