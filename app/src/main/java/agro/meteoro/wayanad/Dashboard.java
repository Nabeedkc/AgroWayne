package agro.meteoro.wayanad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Integer.valueOf;

public class Dashboard extends AppCompatActivity
{
    TextView temp,humi,wind,rain,current_temp;
    ImageView weather_status;
    LineChart chart;
    AVLoadingIndicatorView chart_loader;
    TextView cityText;

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
        chart_loader = findViewById(R.id.chartLoader);
        chart = findViewById(R.id.chart);
        cityText = findViewById(R.id.city);

        chart.setVisibility(View.INVISIBLE);
        chart_loader.show();

        get_city();
        get_weather_now();
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
        get_weather_data();
    }

    private void get_weather_data()
    {
        String url = "http://neutralizer.ml/weather/wd.php";
        RequestQueue weather_q = Volley.newRequestQueue(this);
        StringRequest weather_req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONArray params = new JSONArray(response);
                    JSONArray hours = params.getJSONArray(0);
                    JSONArray values = params.getJSONArray(1);
                    start_chart(hours,values);
                }
                catch (Exception e){Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();}
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

    private void start_chart(JSONArray hour, JSONArray value)
    {
        ArrayList<Entry> entries = new ArrayList<>();
        final String[] hrs = new String[hour.length()];
            try
            {
                for (int i=0; i<hour.length(); i++)
                {
                    hrs[i]=hour.getString(i);
                }

                for (int i=0; i<value.length(); i++)
                {
                    entries.add(new Entry(i, valueOf(value.getInt(i))));
                }

            }
            catch (Exception e){e.printStackTrace();}




        LineDataSet dataSet = new LineDataSet(entries, "Customized values");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.chart_fill);

        dataSet.setColor(ContextCompat.getColor(this, R.color.chart_grad_top));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(drawable);



        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        IAxisValueFormatter hours = new IAxisValueFormatter()
        {
            @Override
            public String getFormattedValue(float value, AxisBase axis)
            {
                return hrs[(int) value];
            }
        };
        xAxis.setGranularity(1f);
        //xAxis.setEnabled(false);
        xAxis.setValueFormatter(hours);
        xAxis.setDrawGridLines(false);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisRight.setDrawGridLines(false);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(2f);
        //yAxisLeft.setEnabled(false);
        yAxisLeft.setDrawGridLines(false);

        LineData data = new LineData(dataSet);

        chart.setData(data);
        chart.setScaleEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setVisibleXRangeMaximum(6);
        chart.animateXY(1000,1000);
        chart.invalidate();

        chart.setVisibility(View.VISIBLE);
        chart_loader.hide();
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

}
