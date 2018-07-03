package kamilkacprzak16185927.modelango;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcel;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoInvalidException;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;
import com.projecttango.tangosupport.TangoPointCloudManager;
import com.projecttango.tangosupport.TangoSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class CreateModelActivity extends AppCompatActivity {
    private static final String TAG = CreateModelActivity.class.getSimpleName();

    private static final int SECS_TO_MILLISECS = 1000;
    private static final double UPDATE_INTERVAL_MS = 300.0;
    final Handler handler = new Handler();

    private Camera mCamera;
    private Preview mPreview;
    private boolean first = true;

    private View.OnClickListener mCreateView;
    private Button mCaptureButton;

    private Tango mTango;
    private TangoConfig mTangoConfig;

    private TangoPointCloudManager mPointCloudManager;

    private double mPointCloudPreviousTimeStamp;

    private boolean mIsTangoServiceConnected = false;

    private double mPointCloudTimeToNextUpdate = UPDATE_INTERVAL_MS;
    private TangoPointCloudData mPointCloudData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_model);

        mPointCloudManager = new TangoPointCloudManager();

        mPreview = new Preview(this.getApplicationContext(),mCamera);

        mCreateView = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_capture:
                        v.setEnabled(false);
                        releaseCameraAndPreview();
                        bindTangoService();
                        final View vTmp = v;

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mPointCloudData = mPointCloudManager.getLatestPointCloud();
                                            saveData(mPointCloudData);
                                        } catch (Exception e) {
                                        }
                                        mTango.disconnect();
                                        mIsTangoServiceConnected = false;
                                        new CameraOpenTask().execute();
                                        vTmp.setEnabled(true);
                                    }
                                }, 5000);
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        };
        mCaptureButton = (Button) findViewById(R.id.button_capture);
        mCaptureButton.setOnClickListener(mCreateView);

        new CameraOpenTask().execute();
    }

    private void saveData(TangoPointCloudData mPointCloudData) {
        RenderCloud.TPCD = mPointCloudData;
        String stamp = "ML_"+String.valueOf(mPointCloudData.timestamp);
        File file = new File(this.getApplicationContext().getFilesDir(),stamp);
        FileOutputStream fos;
        try{
            fos = openFileOutput(stamp, Context.MODE_PRIVATE);
            final Parcel p1 = Parcel.obtain();
            final byte[] bytes;
            p1.writeValue(mPointCloudData);
            bytes = p1.marshall();
            assertEquals(4,bytes[0]);
            fos.write(bytes);
            p1.recycle();
            //fos.write(mPointCloudData.toString().tochar);
            //fos.write(mPointCloudData.describeContents());
        }catch(Exception e){

        }
    }

    private void bindTangoService() {
        // Initialize Tango Service as a normal Android Service. Since we call mTango.disconnect()
        // in onPause, this will unbind Tango Service, so every time onResume gets called we
        // should create a new Tango object.
        mTango = new Tango(CreateModelActivity.this, new Runnable() {
            // Pass in a Runnable to be called from UI thread when Tango is ready; this Runnable
            // will be running on a new thread.
            // When Tango is ready, we can call Tango functions safely here only when there are no
            // UI thread changes involved.
            @Override
            public void run() {
                    try {
                        mTangoConfig = setupTangoConfig(mTango);
                        mTango.connect(mTangoConfig);
                        startupTango();
                        TangoSupport.initialize(mTango);
                        mIsTangoServiceConnected = true;
                    } catch (TangoOutOfDateException e) {
                        Log.e(TAG, getString(R.string.exception_out_of_date), e);
                    } catch (TangoErrorException e) {
                        Log.e(TAG, getString(R.string.exception_tango_error), e);
                        showsToastAndFinishOnUiThread(R.string.exception_tango_error);
                    } catch (TangoInvalidException e) {
                        Log.e(TAG, getString(R.string.exception_tango_invalid), e);
                        showsToastAndFinishOnUiThread(R.string.exception_tango_invalid);
                    }
                }

        });
    }

    /**
     * Sets up the Tango configuration object. Make sure mTango object is initialized before
     * making this call.
     */
    private TangoConfig setupTangoConfig(Tango tango) {
        // Use the default configuration plus add depth sensing.
        TangoConfig config = tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        config.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);
        config.putInt(TangoConfig.KEY_INT_DEPTH_MODE, TangoConfig.TANGO_DEPTH_MODE_POINT_CLOUD);
        return config;
    }

    private void startupTango() {

        ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<>();

        framePairs.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                TangoPoseData.COORDINATE_FRAME_DEVICE));

        mTango.connectListener(framePairs, new Tango.TangoUpdateCallback() {
            @Override
            public void onPoseAvailable(TangoPoseData pose) {
            }

            @Override
            public void onPointCloudAvailable(TangoPointCloudData pointCloud) {
                mPointCloudManager.updatePointCloud(pointCloud);

                final double currentTimeStamp = pointCloud.timestamp;
                final double pointCloudFrameDelta =
                        (currentTimeStamp - mPointCloudPreviousTimeStamp) * SECS_TO_MILLISECS;
                mPointCloudPreviousTimeStamp = currentTimeStamp;
                final double averageDepth = getAveragedDepth(pointCloud.points,
                        pointCloud.numPoints);

                mPointCloudTimeToNextUpdate -= pointCloudFrameDelta;

                if (mPointCloudTimeToNextUpdate < 0.0) {
                    mPointCloudTimeToNextUpdate = UPDATE_INTERVAL_MS;
                    final String pointCountString = Integer.toString(pointCloud.numPoints);
                }
            }

            @Override
            public void onTangoEvent(TangoEvent event) {
            }
        });
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
                if(first){
                    preview.addView(mPreview);
                    first = false;
                }
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
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
            try {
                if(mIsTangoServiceConnected) mTango.disconnect();
                mIsTangoServiceConnected = false;
            } catch (TangoErrorException e) {
                Log.e(TAG, getString(R.string.exception_tango_error), e);
            }
    }

    private void showsToastAndFinishOnUiThread(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateModelActivity.this,
                        getString(resId), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private float getAveragedDepth(FloatBuffer pointCloudBuffer, int numPoints) {
        float totalZ = 0;
        float averageZ = 0;
        if (numPoints != 0) {
            int numFloats = 4 * numPoints;
            for (int i = 2; i < numFloats; i = i + 4) {
                totalZ = totalZ + pointCloudBuffer.get(i);
            }
            averageZ = totalZ / numPoints;
        }
        return averageZ;
    }
}
