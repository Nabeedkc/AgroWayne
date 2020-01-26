package agro.meteoro.wayanad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity
{
    TextView temp,humi,wind,rain,current_temp;
    ImageView weather_status;
    LineChart chart;

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

        chart = findViewById(R.id.chart);
        get_weather_now();
        start_chart();
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

    private void start_chart()
    {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 5));
        entries.add(new Entry(1, 45));
        entries.add(new Entry(2, 25));
        entries.add(new Entry(3, 75));
        entries.add(new Entry(4, 65));
        entries.add(new Entry(5, 15));
        entries.add(new Entry(6, 35));
        entries.add(new Entry(7, 55));
        entries.add(new Entry(8, 85));
        entries.add(new Entry(9, 115));


        LineDataSet dataSet = new LineDataSet(entries, "Customized values");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.chart_fill);

        dataSet.setColor(ContextCompat.getColor(this, R.color.chart_grad_top));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(drawable);



        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(2f);
        //xAxis.setEnabled(false);
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
        chart.setVisibleXRangeMaximum(4);
        chart.animateXY(1000,1000);
        chart.invalidate();
    }

}
