package kamilkacprzak16185927.modelango;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class Preview extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Context mContext;
    private int width = -1,height = -1;

    Preview(Context context, Camera camera){
        super(context);
        this.mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setCamera(camera);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG,"Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        setCameraParametres(width,height);

        // Important: Call startPreview() to start updating the preview surface.
        // Preview must be started before you can take a picture.
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void setCameraParametres(int width, int height) {
        if(this.width == -1 && this.height == -1){
           this.width = width;
           this.height = height;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        Display display = ((WindowManager)mContext.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if(display.getRotation() == Surface.ROTATION_0) {
            parameters.setPreviewSize(this.height, this.width);
            mCamera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90) {
            parameters.setPreviewSize(width, height);
        }

        if(display.getRotation() == Surface.ROTATION_180) {
            parameters.setPreviewSize(height, width);
        }

        if(display.getRotation() == Surface.ROTATION_270) {
            parameters.setPreviewSize(this.width, this.height);
            mCamera.setDisplayOrientation(180);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    public void setSupportedPreviewSizes(List<Camera.Size> supportedPreviewSizes) {
        this.mSupportedPreviewSizes = supportedPreviewSizes;
    }

    public void setCamera(Camera camera) {
        if (mCamera == camera) { return; }

        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            this.setSupportedPreviewSizes(localSizes);
            requestLayout();
            setCameraParametres(-1,-1);
            try {
                mCamera.setPreviewDisplay(this.getSurfaceHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();
        }
    }

    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }
}
