package agro.meteoro.wayanad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Dashboard extends AppCompatActivity
{
    TextView temp,humi,wind,rain,current_temp;
    ImageView weather_status;
    TextView cityText,advice_title;
    LinearLayout temp_click,humi_click,wind_click,rain_click;
    Intent gotoGraph;
    int counter = 1;
    JSONObject requested_data;
    SharedPreferences preferences;
    String crops;
    ArrayList<String> advice_text = new ArrayList<>();
    private RecyclerView advice_recy;
    private RecyclerView.Adapter advice_adapter;
    private RecyclerView.LayoutManager advices_lyt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        hide_sys_ui.hideui(getWindow().getDecorView());
        super.onCreate(savedInstanceState);
        //Set Language
        preferences = getSharedPreferences("Preferences",MODE_PRIVATE);
        String lang = preferences.getString("Language","");
        setLang(lang); //End of Set Language

        setContentView(R.layout.activity_dashboard);

        current_temp = findViewById(R.id.current_temp);
        temp = findViewById(R.id.temp_value);
        humi = findViewById(R.id.humi_value);
        wind = findViewById(R.id.wind_value);
        rain = findViewById(R.id.rain_value);

        weather_status = findViewById(R.id.weather_state_img);
        cityText = findViewById(R.id.city);

        temp_click = findViewById(R.id.start_temp_graph);
        humi_click = findViewById(R.id.start_humi_graph);
        wind_click = findViewById(R.id.start_wind_graph);
        rain_click = findViewById(R.id.start_rain_graph);

        gotoGraph = new Intent(Dashboard.this,Graph.class);

        setClickListener();
        get_city();
        get_weather_now();
        get_crop_advice();
    }

    private void get_weather_now()
    {
        String url = "http://neutralizer.ml/weather/get_weather_now.php";
        RequestQueue weather_q = Volley.newRequestQueue(this);
        StringRequest weather_req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject params = new JSONObject(response);
                    current_temp.setText(getString(R.string.temp_unit, params.getString("temp")));
                    temp.setText(getString(R.string.temp_unit, params.get("temp")));
                    humi.setText(getString(R.string.humi_unit, params.getString("humi")));
                    wind.setText(getString(R.string.wind_unit, params.getString("wind")));
                    rain.setText(getString(R.string.rain_unit, params.getString("rain")));
                }
                catch (Exception e){e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        weather_q.add(weather_req);
    }
    private void get_crop_advice()
    {
        String advice_url = "http://neutralizer.ml/weather/advice.php";
        RequestQueue advice_q = Volley.newRequestQueue(getApplicationContext());
        JSONObject req_data = new JSONObject();
        crops = preferences.getString("Crops","");

        try
        {
            req_data = new JSONObject(crops);
            requested_data = req_data;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        JsonObjectRequest advice_req = new JsonObjectRequest(Request.Method.POST, advice_url, req_data,
        new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    if(requested_data.getInt("Tea")==1)
                    {
                        advice_list(response.getJSONArray("tea_instruct"), R.id.tea_recycle, R.id.tea_advice_title);
                    }
                    if(requested_data.getInt("Rice")==1)
                    {
                        advice_list(response.getJSONArray("rice_instruct"), R.id.rice_recycle, R.id.rice_advice_title);
                    }
                    if(requested_data.getInt("Coffee")==1)
                    {
                        advice_list(response.getJSONArray("coffee_instruct"), R.id.coffee_recycle, R.id.coffee_advice_title);
                    }
                    if(requested_data.getInt("Bpepper")==1)
                    {
                        advice_list(response.getJSONArray("bpepper_instruct"), R.id.bpepper_recycle, R.id.bpepper_advice_title);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        advice_q.add(advice_req);
    }
    private void setClickListener()
    {
        temp_click.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                gotoGraph.putExtra("ChartId","temperature");
                startActivity(gotoGraph);
            }
        });


        humi_click.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                gotoGraph.putExtra("ChartId","humidity");
                startActivity(gotoGraph);
            }
        });


        wind_click.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                gotoGraph.putExtra("ChartId","wind");
                startActivity(gotoGraph);
            }
        });


        rain_click.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                gotoGraph.putExtra("ChartId","rain");
                startActivity(gotoGraph);
            }
        });
    }
    private void get_city()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getApplicationContext(),"Location Permission Denied",Toast.LENGTH_SHORT).show();
        }
        else
        {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            String city = get_city_name(location.getLatitude(),location.getLongitude());
            cityText.setText(city);
        }
    }
    private String get_city_name(double lat, double log)
    {
        String city = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try
        {
            addresses = geocoder.getFromLocation(lat,log,10);
            if(addresses.size() > 0)
            {
                for(Address adr: addresses)
                {
                    if(adr.getLocality() != null && adr.getLocality().length() > 0)
                    {
                        city = adr.getLocality() +", "+ adr.getAdminArea() +"\n"+ adr.getCountryName();
                        break;
                    }
                }
            }
        }
        catch (IOException e){e.printStackTrace();}
        return city;
    }
    private void setLang(String locale)
    {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            config.setLocale(new Locale(locale.toLowerCase()));
        }
        else
        {
            config.locale = new Locale(locale.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }

    private void advice_list(JSONArray data_instructions, int advice_id, int title_id)
    {
        try
        {
            advice_text.clear();
            for (int i = 0; i < data_instructions.length(); i++)
            {
                advice_text.add(data_instructions.getString(i));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        advice_recy = findViewById(advice_id);
        advice_recy.setVisibility(View.VISIBLE);
        findViewById(title_id).setVisibility(View.VISIBLE);
        advices_lyt = new LinearLayoutManager(getApplicationContext());
        advice_recy.setLayoutManager(advices_lyt);

        advice_adapter = new Advice_Recycler(getApplicationContext(), advice_text);
        advice_recy.setAdapter(advice_adapter);
        advice_recy = findViewById(R.id.tea_recycle);
        advices_lyt = new LinearLayoutManager(getApplicationContext());
        advice_recy.setLayoutManager(advices_lyt);

        advice_adapter = new Advice_Recycler(getApplicationContext(), advice_text);
        advice_recy.setAdapter(advice_adapter);
    }

    @Override
    public void onBackPressed()
    {
        if(counter>=1)
        {
            Toast.makeText(getApplicationContext(),"Press once again to Exit",Toast.LENGTH_SHORT).show();
        }
        if(counter<1)
        {
            super.finish();
        }
        counter--;
    }

}
