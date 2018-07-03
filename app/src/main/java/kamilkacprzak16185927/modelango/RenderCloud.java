package kamilkacprzak16185927.modelango;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RenderCloud extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_cloud);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String s = getIntent().getStringExtra("fileName");
        getSupportActionBar().setTitle(s);
    }

}
