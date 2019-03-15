package com.aliyun.alink.devicesdk.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TimeExampleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_time_example );
    }

    @Override
    public void next(View view) {
        Intent intent = new Intent(this, LightExampleActivity.class);
        startActivity(intent);
    }

    @Override
    public void pre(View view) {
        Intent intent = new Intent(this, LightExampleActivity.class);
        startActivity(intent);

    }
}
