package kamilkacprzak16185927.modelango;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mCreate;
    private Button mGallery;
    private View.OnClickListener mMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch(v.getId()){
                    case R.id.mainCreateButton:
                        intent = new Intent(v.getContext(),CreateModelActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.mainGalleryButton:
                        intent = new Intent(v.getContext(),GalleryActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        };
        mCreate = (Button) findViewById(R.id.mainCreateButton);
        mCreate.setOnClickListener(mMainView);
        mGallery = (Button) findViewById(R.id.mainGalleryButton);
        mGallery.setOnClickListener(mMainView);

    }


}
