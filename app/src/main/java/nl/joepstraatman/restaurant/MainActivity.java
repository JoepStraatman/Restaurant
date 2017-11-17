package nl.joepstraatman.restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {
    String saved;
    JSONArray newArray;
    Button order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadPrefs();
        order = (Button) findViewById(R.id.order);
        if (newArray != null){
            order.setText("Your Order ("+String.valueOf(newArray.length() + ")"));}
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://resto.mprog.nl/categories";

// Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest (Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    public JSONArray ja_data = null;
                    @Override

                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        ArrayList<String> listdata = new ArrayList<String>();  // load data from file

                        try {
                            JSONObject jsonObj = new JSONObject(response.toString());
                            ja_data = jsonObj.getJSONArray("categories");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        JSONArray jArray = (JSONArray)ja_data;
                        if (jArray != null) {
                            for (int i=0;i<jArray.length();i++){
                                try {
                                    listdata.add(jArray.getString(i));
                                }catch (JSONException e){
                                    throw  new RuntimeException(e);
                            }
                        }
                    }
                        ArrayList<String> myArray = listdata;
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(
                                        MainActivity.this,
                                        R.layout.main_layout,
                                        myArray);
                        ListView list = (ListView) findViewById(R.id.home);
                        list.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(, "Error at sign in : " + error.getMessage());
                System.out.println(error);
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        final ListView list = (ListView) findViewById(R.id.home);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                String picked = ("" +(adapterView.getItemAtPosition(position)));
                Intent intent = new Intent(MainActivity.this, Category2.class);
                intent.putExtra("catt",picked);
                startActivity(intent);
                finish();

            }
        });
    }
    public void openOrder(View v){
        Intent intent = new Intent(this, Order.class);
        startActivity(intent);
    }

    public void loadPrefs(){
        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        saved = prefs.getString("foodSaved",null);
        try{
            if (saved != null){
                newArray = new JSONArray(saved);}
        }catch (JSONException e) {
            e.printStackTrace();
        }
        //saved = null; //test

    }
}
