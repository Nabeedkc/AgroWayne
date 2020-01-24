package agro.meteoro.wayanad;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Dashboard extends AppCompatActivity
{
    TextView temp,humi,wind,rain,current_temp;
    ImageView weather_status;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        hide_sys_ui.hideui(getWindow().getDecorView());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        current_temp = findViewById(R.id.current_temp);
        temp = findViewById(R.id.temp_value);
        humi = findViewById(R.id.humi_value);
        wind = findViewById(R.id.wind_value);
        rain = findViewById(R.id.rain_value);
        weather_status = findViewById(R.id.weather_state_img);
        get_weather_now();
    }

    private void get_weather_now()
    {
        String url = "http://gecdatahandler.000webhostapp.com/get_weather_now.php";
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
                    if(params.getInt("temp")<30)
                    {
                        weather_status.setBackground(getDrawable(R.drawable.ic_back_clear_sky));
                    }

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
}
