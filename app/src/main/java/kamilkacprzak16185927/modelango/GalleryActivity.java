package kamilkacprzak16185927.modelango;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

public class GalleryActivity extends AppCompatActivity {

    private Button[] mButtons;
    private File mFile;
    private File[] mFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setting params of buttons in alyout
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,10,20,10);
        params.gravity= Gravity.CENTER;
        params.width = 900;

        //creating buttons for each file
        mFile = getFilesDir();
        mFiles = mFile.listFiles();
        mButtons = new Button[mFiles.length];

        for(int i = 0; i<mFiles.length; i++){
            Button but = new Button(this.getApplicationContext());
            but.setId(i+1);
            but.setText(mFiles[i].getName());
            but.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            but.setTextColor(getResources().getColor(R.color.colorAccent));
            but.setLayoutParams(params);
            layout.addView(but);
            mButtons[i] = but;
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
