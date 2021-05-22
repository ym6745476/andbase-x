package com.andbase.library.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.andbase.library.R;
import com.andbase.library.app.base.AbBaseActivity;

public class AbAnalogClockActivity extends AbBaseActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ab_activity_analog_clock);
        Button closeButton = (Button)this.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
}


