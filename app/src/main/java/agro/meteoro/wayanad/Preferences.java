package agro.meteoro.wayanad;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

public class Preferences extends AppCompatActivity
{
    ImageButton next1,next2,next3;
    RadioGroup radios;
    RadioButton lang_selected;
    ConstraintLayout lay1,lay2,lay3;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        //Layers
        lay1 = findViewById(R.id.view1);
        lay2 = findViewById(R.id.view2);
        lay3 = findViewById(R.id.view3);

        //Next Buttons
        next1 = findViewById(R.id.next_1);
        next2 = findViewById(R.id.next_2);
        next3 = findViewById(R.id.next_3);


        setListeners();
    }

    private void setListeners()
    {
        next1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                radios = findViewById(R.id.lang_radios);
                int id = radios.getCheckedRadioButtonId();
                lang_selected = findViewById(id);
                Toast.makeText(getApplicationContext(),lang_selected.getText(),Toast.LENGTH_SHORT).show();
                lay1.setVisibility(View.GONE);
                lay2.setVisibility(View.VISIBLE);
            }
        });

        next2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
        next3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    private void change(String localeCode)
    {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            config.setLocale(new Locale(localeCode.toLowerCase()));
        }
        else
        {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
