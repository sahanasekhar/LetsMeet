package ras.asu.com.letsmeet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    Button INVITE;
    //Button Createdb;
    Database db;
    GoogleCloudMessaging gcm;
    Button btnShowLocation;
    //GPSTracker gps;

    @Override
    protected void onStart() {
        super.onStart();


       /* Createdb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {


                db = new Database(LoginActivity.this);
                db.open();






                db.close();

            }

        });*/
        //btnShowLocation = (Button) findViewById(R.id.getloc);

        // show location button click event
        /*btnShowLocation.setOnClickListener(new View.OnClickListener() {

           /* @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(LoginActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        }
        );*/

    }
    String regid;
    GraphResponse response;
    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            // GraphResponse response1 = response;
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(ProjCostants.PROJECT_NUMBER);
                    ProjCostants.REG_ID = regid;
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, ImageTextListViewActivity.class);
                try {
                    if(response!=null) {
                        JSONArray rawName = response.getJSONObject().getJSONArray("data");
                        intent.putExtra("jsondata", rawName.toString());
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute(null, null, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INVITE = (Button)findViewById(R.id.button1);
        Intent intent = new Intent(LoginActivity.this, Invitation.class) ;
        //Createdb = (Button)findViewById(R.id.button3);
        //  Intent intent1 =  new Intent(LoginActivity.this, Database.class);



        /*Facebook button and manager*/
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.content_login);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        loginResult.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response1) {
                                response = response1;
                                ProjCostants.FB_ID = loginResult.getAccessToken().getUserId();
                                getRegId();
                                info.setText("User ID: " + loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken());
                            }
                        }
                ).executeAsync();
                Intent intent1 = new Intent(LoginActivity.this, MenuList.class);
                startActivity(intent1);

            }

            @Override
            public void onCancel() {

                info.setText("Login attempt canceled.");
                Toast.makeText(getApplicationContext(), "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {

                info.setText("Login attempt failed.");
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

}
