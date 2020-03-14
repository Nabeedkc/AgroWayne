package agro.meteoro.wayanad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Long.valueOf;

public class Graph extends AppCompatActivity
{
    String chartId;
    LineChart chart;
    AVLoadingIndicatorView chart_loader;
    TextView chartTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("Preferences",MODE_PRIVATE);
        String lang = preferences.getString("Language","");
        setLang(lang);

        setContentView(R.layout.activity_graph);

        chartId = getIntent().getStringExtra("ChartId");
        chart = findViewById(R.id.chart);
        chart_loader = findViewById(R.id.chartLoader);
        chartTitle = findViewById(R.id.chart_name);

        chart.setVisibility(View.INVISIBLE);
        chart_loader.show();

        setChartTitle();
        get_weather_data();
    }

    private void setChartTitle()
    {
        if(chartId.equals("temperature"))
            chartTitle.setText(R.string.temp);

        if(chartId.equals("humidity"))
            chartTitle.setText(R.string.humi);

        if(chartId.equals("wind"))
            chartTitle.setText(R.string.wind);

        if(chartId.equals("rain"))
            chartTitle.setText(R.string.rain);
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
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();}
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


}
