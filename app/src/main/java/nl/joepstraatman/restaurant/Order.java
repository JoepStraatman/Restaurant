package nl.joepstraatman.restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Order extends AppCompatActivity {
    String saved;
    String picked;
    JSONArray newArray;
    String wrong;
    Button order;
    private String removed;
    private double totalPrice = 0.0d;
    TextView total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        loadPrefs();

        if( getIntent().getExtras() != null)
        {
            Bundle bundle = getIntent().getExtras();
            removed = bundle.getString("remove");
            Toast toast2 = Toast.makeText(getApplicationContext(), "removed: " + removed, Toast.LENGTH_SHORT);
            toast2.show();
        }

        order = (Button) findViewById(R.id.order);
        if (newArray != null){
            order.setText("Your Order ("+String.valueOf(newArray.length() + ")"));}

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://resto.mprog.nl/menu";

// Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest (Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    public JSONArray ja_data = null;
                    @Override

                    public void onResponse(JSONObject response) {
                        //Toast toast = Toast.makeText(getApplicationContext(), removed, Toast.LENGTH_SHORT);
                        //toast.show();
                        // Display the first 500 characters of the response string.
                        ArrayList<String> listdata = new ArrayList<String>();  // load data from file

                        try {
                            JSONObject jsonObj = new JSONObject(response.toString());
                            ja_data = jsonObj.getJSONArray("items");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        //JSONArray goodArray = (JSONArray)null;
                        JSONArray jArray = (JSONArray)ja_data;
                        if (newArray != null){
                            if (jArray != null) {
                                for (int z=0;z<newArray.length();z++){
                                    for (int i = 0; i < jArray.length(); i++) {
                                        try {
                                            JSONObject jsonArray2 = new JSONObject(jArray.getString(i));
                                            String catgor = jsonArray2.getString("id");
                                            //Toast toast = Toast.makeText(getApplicationContext(), categorie + catgor, Toast.LENGTH_SHORT);
                                            //toast.show();
                                            if (catgor.equals(newArray.getString(z))) {
                                                if (jsonArray2.getString("name").equals(removed)) {
                                                    wrong = (String.valueOf(z));
                                                    TextView foutje = (TextView) findViewById(R.id.foutje);
                                                    foutje.setText(wrong);
                                                } else {
                                                    //for (int x=0;x<jsonArray2.length();x++)
                                                    //listdata.add(jArray.getString(x));
                                                    listdata.add(jsonArray2.getString("name") + "   €" + jsonArray2.getString("price"));
                                                    //.getJSONArray("name").toString()
                                                    totalPrice += Double.parseDouble(jsonArray2.getString("price"));
                                                    total = (TextView) findViewById(R.id.total);
                                                    total.setText("Total price is: €" + totalPrice);
                                                }
                                            }

                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }

                            }
                        }
                        TextView foutje = (TextView) findViewById(R.id.foutje);
                        if (!foutje.getText().toString().equals("")) {
                            newArray.remove(Integer.parseInt(foutje.getText().toString()));
                            foutje.setText("");
                            item();
                        }
                        ArrayList<String> myArray = listdata;
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(
                                        Order.this,
                                        R.layout.main_layout,
                                        myArray);
                        ListView list = (ListView) findViewById(R.id.idorder);
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
        final ListView list = (ListView) findViewById(R.id.idorder);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                picked = ("" +(adapterView.getItemAtPosition(position)));
                picked = picked.substring(0,picked.indexOf("   €"));
                removeItem();
            }
        });

    }

    public void closeOrder(View v){
        finish();
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
    public void removeItem(){
        Intent intent = new Intent(this, Order.class);
        intent.putExtra("remove",picked);
        startActivity(intent);
        finish();
    }
    public void item(){

        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("foodSaved", newArray.toString());
        editor.commit();
        if (newArray != null){
            order.setText("Your Order ("+String.valueOf(newArray.length() + ")"));}
    }

    public void submit(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, "https://resto.mprog.nl/order", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (newArray.length() != 0){
                Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
                toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "{\"preperation time\" : 0}", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap<String, String> params2 = new HashMap<String, String>();
                for (int i = 0; i < newArray.length(); i++) {
                    try {
                        params2.put(newArray.getString(i), "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return new JSONObject(params2).toString().getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(sr);
    }
}
