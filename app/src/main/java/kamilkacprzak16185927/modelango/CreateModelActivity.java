package kamilkacprzak16185927.modelango;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class CreateModelActivity extends AppCompatActivity {
    private Camera mCamera;
    private Preview mPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_model);
        mPreview = new Preview(this.getApplicationContext(),mCamera);
        new CameraOpenTask().execute();
    }

    private class CameraOpenTask extends AsyncTask<Void, Void, Camera> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Camera doInBackground(Void... params) {
            if (ContextCompat.checkSelfPermission(CreateModelActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return safeCameraOpen(0);
            }else {
                Log.d("Lack of permission","Camera is not open due lack of permission" );
                return null;
            }
        }

        @Override
        protected void onPostExecute(Camera camera) {
            mCamera = camera;
            if(mCamera != null){
                mPreview.setCamera(mCamera);
                FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
                preview.addView(mPreview);
            }


        }
    }

    private Camera safeCameraOpen(int id) {

        Camera camera;

        try {
            releaseCameraAndPreview();
            camera = Camera.open(id);
            if (camera != null) {
                return camera;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void releaseCameraAndPreview() {
        mPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
