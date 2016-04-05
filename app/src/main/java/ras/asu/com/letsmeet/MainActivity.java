/*package ras.asu.com.letsmeet;

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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {




    Button btnRegId;
    Button sendMsg;
    Button sendData;

    EditText message;
    EditText fbId;
    EditText etRegId;

    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "499603359352";
//String SERVER_ADDRESS = "http://192.168.0.103:8080/LetsMeetWeb/";
    String SERVER_ADDRESS = "http://10.0.2.2:8080/LetsMeetWeb/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        btnRegId = (Button) findViewById(R.id.btnGetRegId);
        sendData = (Button) findViewById(R.id.sendData);
        sendMsg = (Button) findViewById(R.id.sendMsg);
        etRegId = (EditText) findViewById(R.id.etRegId);
        fbId = (EditText) findViewById(R.id.fbId);
        message = (EditText) findViewById(R.id.message);
        etRegId = (EditText) findViewById(R.id.etRegId);
        btnRegId.setOnClickListener(this);
        sendData.setOnClickListener(this);
        sendMsg.setOnClickListener(this);

    }
    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM",  msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);
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
                serverUrl = new URL(SERVER_ADDRESS);
                Log.d("URL PROBLEM", serverUrl.toString());
            } catch (MalformedURLException e) {
                Log.e("AppUtil", "URL Connection Error: "
                        + SERVER_ADDRESS, e);
                result = SERVER_ADDRESS;
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


class AsyncT extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
              URL url = new URL(SERVER_ADDRESS);
                //URL url = new URL("http://10.143.36.131:8080/LetsMeetWeb/");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();
//response data
                Log.i("Response",JsonResponse);
                //send to post execute
                // return JsonResponse;
                return null;



            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetRegId:
                getRegId();
                break;
            case R.id.sendData:
            registerUser();
                break;
            case R.id.sendMsg:
                sendMsg();
                break;
            default:break;
        }
    }

    private void registerUser() {
        Map<String , String > jobj = new HashMap<String,String>();
        jobj.put("gcmId",regid);
        jobj.put("fbId",fbId.getText().toString());
        jobj.put("action", "addNewUser");
        StoreUserDataAsyncTask asyn = new StoreUserDataAsyncTask(jobj);
        asyn.execute();
    }
    private void sendMsg() {
        Map<String , String > jobj = new HashMap<String,String>();
        jobj.put("message",message.getText().toString());
        jobj.put("fbId",fbId.getText().toString());
        jobj.put("action", "sendRequest");
        StoreUserDataAsyncTask asyn = new StoreUserDataAsyncTask(jobj);
        asyn.execute();
    }


}*/
