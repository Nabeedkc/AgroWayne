package agro.meteoro.wayanad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

public class Initiate extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        hide_sys_ui.hideui(getWindow().getDecorView());     //hide navigation keys
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate);
        ConnectivityStatus();
    }

    public void ConnectivityStatus()
    {
        ConnectivityManager net = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(net.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                net.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            startActivity(new Intent(Initiate.this,Dashboard.class));
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Check your Internet Connectivity",Toast.LENGTH_SHORT).show();
        }
    }
}
