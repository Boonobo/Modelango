package kamilkacprzak16185927.modelango;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class CreateModelActivity extends AppCompatActivity {
    private Camera mCamera;
    private Handler mCustomHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_model);
    }

    private class CameraOpenTask extends AsyncTask<Void, Void, Camera> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Camera doInBackground(Void... params) {
            if (ContextCompat.checkSelfPermission(CreateModelActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return safeCameraOpen(0);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Camera camera) {
            mCamera = camera;
            if(mCamera != null){

                //Set this camera instance in camera preview
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
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

//    private class Preview extends ViewGroup implements SurfaceHolder.Callback{
//
//        Preview(Context context){
//            super(context);
//        }
//    }
}
