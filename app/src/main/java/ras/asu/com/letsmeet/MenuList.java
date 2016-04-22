package ras.asu.com.letsmeet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.mikepenz.fastadapter.utils.RecyclerViewCacheUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MenuList extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {
    private static final int PROFILE_SETTING = 100000;
    LinkedList<User> mutualUsers;
    private DrawerBuilder s;
    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private Database db;
    private LinkedList<PrimaryDrawerItem> lst = new LinkedList<PrimaryDrawerItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_dark_toolbar);
        db = new Database(MenuList.this);

        db.open();

        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            // String type = intent.getType();
            try {
                if (action.equals("acceptReq")) {
                    db.addIsFriend(intent.getStringExtra("fbID"));
                    sendInvite(intent.getStringExtra("fbID"));
                    Toast.makeText(getApplicationContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        mutualUsers = db.getMutualUsers();

        db.close();

        new loadImage(savedInstanceState).execute();


    }

    private Bitmap loadImageFromStorage(String path) {

        try {
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {



        if (drawerItem != null) {
            Intent intent = null;
            if (drawerItem.getIdentifier() == 1) {
                PrimaryDrawerItem p = (PrimaryDrawerItem) drawerItem;
                intent = new Intent(MenuList.this, SelectPlaceOrPersonScreen.class);
                intent.putExtra("fbId", p.getDescription().getText());
                ProjCostants.OTHER_GUYS_FB_ID = p.getDescription().getText();
                intent.setAction("userNameClick");
               // startActivity(intent);
            } else if (drawerItem.getIdentifier() == 2000) {
                SecondaryDrawerItem p = (SecondaryDrawerItem) drawerItem;
                intent = new Intent(MenuList.this, SelectPlaceOrPersonScreen.class);
                intent.putExtra("place", p.getName().getText());
                intent.setAction("placeClick");
               // startActivity(intent);
            } else if (drawerItem.getIdentifier() == 3) {
            } else if (drawerItem.getIdentifier() == 13) {
            }
            if(intent!=null)
                MenuList.this.startActivity(intent);
        }

        return false;
    }

    public class loadImage extends AsyncTask<Void, Void, Void> {
        Bundle s2;
        Drawable self;

        public loadImage(Bundle s1) {
            super();
            s2 = s1;

        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mutualUsers != null) {
                //Create the drawer
                //lst PrimaryDrawerItem ih [] = new  PrimaryDrawerItem[mutualUsers.size()];
                for (int i = 0; i < mutualUsers.size(); i++) {
                    try {
                        // Bitmap bitmap = getFacebookProfilePicture(id.get(i));
                        lst.add(new PrimaryDrawerItem().withName(mutualUsers.get(i).getKEY_USERNAME()).withDescription(mutualUsers.get(i).getKEY_USERID()).withIcon(UrlHelper.getImageDrawable("https://graph.facebook.com/" + mutualUsers.get(i).getKEY_USERID() + "/picture?type=large")).withIdentifier(1).withSelectable(false));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                self = UrlHelper.getImageDrawable("https://graph.facebook.com/" + ProjCostants.FB_ID + "/picture?type=large");


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
            MenuList.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    //  String pUrl = "https://graph.facebook.com/" + ProjCostants.FB_ID + "/picture?type=large";
                    String pUrl = "https://graph.facebook.com/" + "1312614118754662" + "/picture?type=large";
                    //  pUrl="\"https://scontent.fphx1-2.fna.fbcdn.net/hphotos-xfp1/v/t1.0-9/11855739_10205587550940373_7974574911618547961_n.jpg?oh=007484cb080fa56479ab3a78c1e17936&oe=57788D59";
                    final IProfile profile = new ProfileDrawerItem().withName(ProjCostants.FB_ID).withEmail(ProjCostants.FB_ID).withIcon(self).withIdentifier(100);

                    headerResult = new AccountHeaderBuilder()
                            .withActivity(MenuList.this)
                            .withTranslucentStatusBar(true)
                            .withHeaderBackground(R.drawable.header)
                            .addProfiles(
                                    profile
                            )
                            .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                                @Override
                                public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                                    if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                                        int count = 100 + headerResult.getProfiles().size() + 1;
                                        IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count).withEmail("batman" + count + "@gmail.com").withIcon("https://graph.facebook.com/1312614118754662/picture?type=large").withIdentifier(count);
                                        if (headerResult.getProfiles() != null) {
                                            headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                                        } else {
                                            headerResult.addProfiles(newProfile);
                                        }
                                    }

                                    return false;
                                }
                            })
                            .withSavedInstance(s2)
                            .build();
                    s = new DrawerBuilder()
                            .withActivity(MenuList.this)
                            .withToolbar(toolbar)
                            .withHasStableIds(true)
                            .withAccountHeader(headerResult);
                    // add the items we want to use with our Drawer
                    for (PrimaryDrawerItem p : lst)
                        s.addDrawerItems(p);

                    s.addDrawerItems(

                            new ExpandableDrawerItem().withName("Meet Up Places").withIcon(R.drawable.backgroundpic).withIdentifier(19).withSelectable(false).withSubItems(
                                    new SecondaryDrawerItem().withName("Coffee").withLevel(2).withIcon(R.drawable.coffee_cup_icon_70002).withIdentifier(2000),
                                    new SecondaryDrawerItem().withName("Restaurant").withLevel(2).withIcon(R.drawable.city_restaurant_icon).withIdentifier(2000),
                                    new SecondaryDrawerItem().withName("icecream").withLevel(2).withIcon(R.drawable.cup_ice_cream).withIdentifier(2000),
                                    new SecondaryDrawerItem().withName("pizza").withLevel(2).withIcon(R.drawable.food_pizza_icon).withIdentifier(2000),
                                    new SecondaryDrawerItem().withName("fastfood").withLevel(2).withIcon(R.drawable.food_cooking_meal_5_512).withIdentifier(2000)
                                    ));

                    result = s.withOnDrawerItemClickListener(MenuList.this)
                            .withSavedInstance(s2)
                            .withShowDrawerOnFirstLaunch(true)
                            .build();
                    new RecyclerViewCacheUtil<IDrawerItem>().withCacheSize(2).apply(result.getRecyclerView(), result.getDrawerItems());

                    if (s2 == null) {
                        result.setSelection(21, false);

                        headerResult.setActiveProfile(profile);
                    }

                    result.updateBadge(4, new StringHolder(10 + ""));

                }//public void run() {
            });

        }
    }
    private void sendInvite(String fbId) {
        Map<String , String > jobj = new HashMap<String,String>();
        jobj.put("fbId1",fbId);
        jobj.put("action", "setFriends");
        jobj.put("fbId2", ProjCostants.FB_ID);
        StoreUserDataAsyncTask asyn = new StoreUserDataAsyncTask(jobj);
        asyn.execute();
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        if(result!=null)
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        if(headerResult!=null)
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
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

