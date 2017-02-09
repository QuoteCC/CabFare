package io.github.quotecc.cabfare;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button b;
    String[] returned = new String[4];
    Spinner s;
    boolean error = false;
    EditText[][] t = new EditText[2][3];
    String [][] r = new String[2][3];
    double[] rates = {2.5,2.75,3.0,3.25};
    int indx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t[0][0] = (EditText) findViewById(R.id.oAddress);
        t[0][1] = (EditText) findViewById(R.id.oCity);
        t[0][2] = (EditText) findViewById(R.id.oState);
        t[1][0] = (EditText) findViewById(R.id.dAddress);
        t[1][1] = (EditText) findViewById(R.id.dCity);
        t[1][2] = (EditText) findViewById(R.id.dState);
        s = (Spinner)findViewById(R.id.spnr);
        b = (Button)findViewById(R.id.bttn);
        b.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.bttn:
                r = new String[2][3];
                for (int i = 0; i< 3; i++){
                    r[0][i] = t[0][i].getText().toString();
                }
                for (int i = 0; i< 3; i++){
                    r[1][i] = t[1][i].getText().toString();
                }

                indx = s.getSelectedItemPosition();
                new getFare(r,rates[indx]).execute();
                break;
        }
    }
    class getFare extends AsyncTask<Void, Void, String>{
        final static String API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial";
        final static String API_KEY = "YOUR_API_KEY";
        JSONObject response;
        double rat;
        String[][] rs;

        public getFare(String[][] rs, double rat){
            this.rat = rat;
            this.rs = rs;

        }


        @Override
        protected void onPreExecute(){

        }
        protected String doInBackground(Void... urls){
            //create the URL STRING
            error=false;

            String origins = "&origins="; //will contain the params pulled from the fields
            for(String s:rs[0]){
                s = s.trim().replace(' ','+');
                origins = origins+ '+' + s;
            }
            String destinations = "&destinations=";
            for(String s:rs[1]){
                s = s.trim().replace(' ','+');
                destinations = destinations+s;
            }
            try { //pass URL String to queryDistance
                URL sub = new URL(API_URL + origins + destinations + "&key="+ API_KEY);
                Log.d("debug", sub.toString());
                response = queryDistance(sub);
            }
            catch (Exception e){
                //Toast.makeText(MainActivity.this,"Invalid Characters used in address", Toast.LENGTH_SHORT).show();
                Log.d("prob", e.toString());
                error = true;
                return e.toString();
            }
            if (!error){
                try{
                    returned[0] = response.getJSONArray("origin_addresses").getString(0);
                    returned[1] = response.getJSONArray("destination_addresses").getString(0);
                    JSONObject ele = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                    returned[2] = ele.getJSONObject("distance").getString("text");
                    returned[3] = ele.getJSONObject("duration").getString("text");
                    Log.d("test", "Completed JSON conversion");
                    Bundle b = new Bundle(2);
                    b.putStringArray("resStrings",returned);
                    b.putDouble("rate", rat);
                    Log.d("test", "Got here");
                    Intent i = new Intent(MainActivity.this, CalcScrn.class );
                    i.putExtras(b);
                    startActivity(i);
                }
                catch (Exception e){
                    Log.d("prob", e.toString());
                    error = true;
                    return e.toString();
                }

            }

            //format comma delimited string, return this
            return "";
        }

        public void onPostExecute(){


        }
        JSONObject queryDistance(URL toQuery){
            try {
                HttpURLConnection urlConnect = (HttpURLConnection) toQuery.openConnection();
                BufferedReader buffRead = new BufferedReader(new InputStreamReader(urlConnect.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while((line = buffRead.readLine()) != null){
                    response.append(line).append("\n");
                }
                buffRead.close();
                return new JSONObject(response.toString());
            }
            catch (Exception e){
                Log.d("prob", e.toString());
                error = true;
                //Toast.makeText(MainActivity.this,"Connection Exception, Please Check your internet connection", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

    }
}
