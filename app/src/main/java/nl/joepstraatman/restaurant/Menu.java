package nl.joepstraatman.restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Menu extends AppCompatActivity {
    ImageView foto;
    String food;
    String saved;
    JSONArray newArray;
    Button order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        loadPrefs();
        order = (Button) findViewById(R.id.order);
        if (newArray != null){
        order.setText("Your Order ("+String.valueOf(newArray.length() + ")"));}
        Bundle bundle = getIntent().getExtras();
        final String meal = bundle.getString("meal");
        final String category = bundle.getString("catt");
        TextView cat = (TextView) findViewById(R.id.cat);
        cat.setText(meal);
        Button backMeal = (Button) findViewById(R.id.backButton);
        backMeal.setText("< " +category);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://resto.mprog.nl/menu";

// Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest (Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    public JSONArray ja_data = null;
                    @Override

                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                          try {
                            JSONObject jsonObj = new JSONObject(response.toString());
                            ja_data = jsonObj.getJSONArray("items");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        //JSONArray goodArray = (JSONArray)null;
                        JSONArray jArray = (JSONArray)ja_data;
                        if (jArray != null) {
                            for (int i=0;i<jArray.length();i++){
                                try {
                                    JSONObject jsonArray2 = new JSONObject(jArray.getString(i));
                                    String catgor = jsonArray2.getString("name");
                                    if (catgor.equals(meal)) {
                                        TextView price = (TextView) findViewById(R.id.price);
                                        TextView description = (TextView) findViewById(R.id.description);
                                        price.setText("Price : €"+jsonArray2.getString("price"));
                                        food = jsonArray2.getString("id");
                                        description.setText(jsonArray2.getString("description"));
                                        String link = jsonArray2.getString("image_url").replace("https://", "");
                                        foto = (ImageView) findViewById(R.id.foto);

                                        Picasso.with(getApplicationContext())
                                                .load("https://" + link)
                                                .resize(500,500)
                                                .centerCrop().into(foto);
                                    }
                                }catch (JSONException e){
                                    throw  new RuntimeException(e);
                                }
                            }
                        }
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
    }
    public void goToIndex(View v){

        finish();
    }
    public void openOrder(View v){
        Intent intent = new Intent(this, Order.class);
        startActivity(intent);
    }
    public void saveToOrder(View v){
        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //JSONArray jArrays = new JSONArray();
        //jArrays.put(food);
        if (saved != null){
            try{
                Boolean already = false;
                if (newArray!= null){
                for (int i=0;i<newArray.length();i++){
                    if (newArray.getString(i).equals(food)){
                        already =true;
                    }
                }}
                if (already == true){
                    Toast toast = Toast.makeText(getApplicationContext(), "This is already on your order list!", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    newArray.put(food);
                    editor.putString("foodSaved", newArray.toString());
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            newArray = new JSONArray();
            newArray.put(food);
            editor.putString("foodSaved", newArray.toString());
        }
        //Toast toast = Toast.makeText(getApplicationContext(), newArray.toString(), Toast.LENGTH_SHORT);
        //toast.show();
        editor.commit();
        order.setText("Your Order ("+String.valueOf(newArray.length() + ")"));
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
