package kamilkacprzak16185927.modelango;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoErrorException;

public class CreateModelActivity extends AppCompatActivity {
    private Camera mCamera;
    private Preview mPreview;

    private View.OnClickListener mCreateView;
    private Button mCaptureButton;

    private Tango mTango;
    private TangoConfig mTangoConfig;
    private boolean mIsTangoServiceConnected;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_model);
        mPreview = new Preview(this.getApplicationContext(),mCamera);

        mTango = new Tango(this.getApplicationContext());
        mTangoConfig = new TangoConfig();
        mTangoConfig = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
        mTangoConfig.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);

        mCaptureButton = (Button) findViewById(R.id.button_capture);
        mCaptureButton.setOnClickListener(mCreateView);
        mCreateView = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.button_capture:
                        break;
                    default:
                        break;
                }
            }
        };

        mIsTangoServiceConnected = false;

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

    @Override
    protected void onPause(){
        super.onPause();
        if(mTango != null){
            try{
                mTango.disconnect();
                mIsTangoServiceConnected = false;
            }catch(TangoErrorException e){
                Log.i("Tango error","Tango didn't disconnect properly");
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsTangoServiceConnected) {
            startActivityForResult(
                    Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_MOTION_TRACKING),
                    Tango.TANGO_INTENT_ACTIVITYCODE);
        }
    }


}
