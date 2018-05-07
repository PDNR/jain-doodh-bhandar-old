package me.dm7.barcodescanner.core;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public abstract class BarcodeScannerView extends FrameLayout implements PreviewCallback {
    private float mAspectTolerance = 0.1f;
    private boolean mAutofocusState = true;
    private float mBorderAlpha = 1.0f;
    @ColorInt
    private int mBorderColor = getResources().getColor(R.color.viewfinder_border);
    private int mBorderLength = getResources().getInteger(R.integer.viewfinder_border_length);
    private int mBorderWidth = getResources().getInteger(R.integer.viewfinder_border_width);
    private CameraHandlerThread mCameraHandlerThread;
    private CameraWrapper mCameraWrapper;
    private int mCornerRadius = 0;
    private Boolean mFlashState;
    private Rect mFramingRectInPreview;
    private boolean mIsLaserEnabled = true;
    @ColorInt
    private int mLaserColor = getResources().getColor(R.color.viewfinder_laser);
    private int mMaskColor = getResources().getColor(R.color.viewfinder_mask);
    private CameraPreview mPreview;
    private boolean mRoundedCorner = false;
    private boolean mShouldScaleToFill = true;
    private boolean mSquaredFinder = false;
    private int mViewFinderOffset = 0;
    private IViewFinder mViewFinderView;

    public BarcodeScannerView(Context context) {
        super(context);
        init();
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        context = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.BarcodeScannerView, 0, 0);
        try {
            setShouldScaleToFill(context.getBoolean(R.styleable.BarcodeScannerView_shouldScaleToFill, true));
            this.mIsLaserEnabled = context.getBoolean(R.styleable.BarcodeScannerView_laserEnabled, this.mIsLaserEnabled);
            this.mLaserColor = context.getColor(R.styleable.BarcodeScannerView_laserColor, this.mLaserColor);
            this.mBorderColor = context.getColor(R.styleable.BarcodeScannerView_borderColor, this.mBorderColor);
            this.mMaskColor = context.getColor(R.styleable.BarcodeScannerView_maskColor, this.mMaskColor);
            this.mBorderWidth = context.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderWidth, this.mBorderWidth);
            this.mBorderLength = context.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderLength, this.mBorderLength);
            this.mRoundedCorner = context.getBoolean(R.styleable.BarcodeScannerView_roundedCorner, this.mRoundedCorner);
            this.mCornerRadius = context.getDimensionPixelSize(R.styleable.BarcodeScannerView_cornerRadius, this.mCornerRadius);
            this.mSquaredFinder = context.getBoolean(R.styleable.BarcodeScannerView_squaredFinder, this.mSquaredFinder);
            this.mBorderAlpha = context.getFloat(R.styleable.BarcodeScannerView_borderAlpha, this.mBorderAlpha);
            this.mViewFinderOffset = context.getDimensionPixelSize(R.styleable.BarcodeScannerView_finderOffset, this.mViewFinderOffset);
            init();
        } finally {
            context.recycle();
        }
    }

    private void init() {
        this.mViewFinderView = createViewFinderView(getContext());
    }

    public final void setupLayout(CameraWrapper cameraWrapper) {
        removeAllViews();
        this.mPreview = new CameraPreview(getContext(), cameraWrapper, this);
        this.mPreview.setAspectTolerance(this.mAspectTolerance);
        this.mPreview.setShouldScaleToFill(this.mShouldScaleToFill);
        if (this.mShouldScaleToFill == null) {
            cameraWrapper = new RelativeLayout(getContext());
            cameraWrapper.setGravity(17);
            cameraWrapper.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
            cameraWrapper.addView(this.mPreview);
            addView(cameraWrapper);
        } else {
            addView(this.mPreview);
        }
        if ((this.mViewFinderView instanceof View) != null) {
            addView((View) this.mViewFinderView);
            return;
        }
        throw new IllegalArgumentException("IViewFinder object returned by 'createViewFinderView()' should be instance of android.view.View");
    }

    protected IViewFinder createViewFinderView(Context context) {
        IViewFinder viewFinderView = new ViewFinderView(context);
        viewFinderView.setBorderColor(this.mBorderColor);
        viewFinderView.setLaserColor(this.mLaserColor);
        viewFinderView.setLaserEnabled(this.mIsLaserEnabled);
        viewFinderView.setBorderStrokeWidth(this.mBorderWidth);
        viewFinderView.setBorderLineLength(this.mBorderLength);
        viewFinderView.setMaskColor(this.mMaskColor);
        viewFinderView.setBorderCornerRounded(this.mRoundedCorner);
        viewFinderView.setBorderCornerRadius(this.mCornerRadius);
        viewFinderView.setSquareViewFinder(this.mSquaredFinder);
        viewFinderView.setViewFinderOffset(this.mViewFinderOffset);
        return viewFinderView;
    }

    public void setLaserColor(int i) {
        this.mLaserColor = i;
        this.mViewFinderView.setLaserColor(this.mLaserColor);
        this.mViewFinderView.setupViewFinder();
    }

    public void setMaskColor(int i) {
        this.mMaskColor = i;
        this.mViewFinderView.setMaskColor(this.mMaskColor);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderColor(int i) {
        this.mBorderColor = i;
        this.mViewFinderView.setBorderColor(this.mBorderColor);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderStrokeWidth(int i) {
        this.mBorderWidth = i;
        this.mViewFinderView.setBorderStrokeWidth(this.mBorderWidth);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderLineLength(int i) {
        this.mBorderLength = i;
        this.mViewFinderView.setBorderLineLength(this.mBorderLength);
        this.mViewFinderView.setupViewFinder();
    }

    public void setLaserEnabled(boolean z) {
        this.mIsLaserEnabled = z;
        this.mViewFinderView.setLaserEnabled(this.mIsLaserEnabled);
        this.mViewFinderView.setupViewFinder();
    }

    public void setIsBorderCornerRounded(boolean z) {
        this.mRoundedCorner = z;
        this.mViewFinderView.setBorderCornerRounded(this.mRoundedCorner);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderCornerRadius(int i) {
        this.mCornerRadius = i;
        this.mViewFinderView.setBorderCornerRadius(this.mCornerRadius);
        this.mViewFinderView.setupViewFinder();
    }

    public void setSquareViewFinder(boolean z) {
        this.mSquaredFinder = z;
        this.mViewFinderView.setSquareViewFinder(this.mSquaredFinder);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderAlpha(float f) {
        this.mBorderAlpha = f;
        this.mViewFinderView.setBorderAlpha(this.mBorderAlpha);
        this.mViewFinderView.setupViewFinder();
    }

    public void startCamera(int i) {
        if (this.mCameraHandlerThread == null) {
            this.mCameraHandlerThread = new CameraHandlerThread(this);
        }
        this.mCameraHandlerThread.startCamera(i);
    }

    public void setupCameraPreview(CameraWrapper cameraWrapper) {
        this.mCameraWrapper = cameraWrapper;
        if (this.mCameraWrapper != null) {
            setupLayout(this.mCameraWrapper);
            this.mViewFinderView.setupViewFinder();
            if (this.mFlashState != null) {
                setFlash(this.mFlashState.booleanValue());
            }
            setAutoFocus(this.mAutofocusState);
        }
    }

    public void startCamera() {
        startCamera(CameraUtils.getDefaultCameraId());
    }

    public void stopCamera() {
        if (this.mCameraWrapper != null) {
            this.mPreview.stopCameraPreview();
            this.mPreview.setCamera(null, null);
            this.mCameraWrapper.mCamera.release();
            this.mCameraWrapper = null;
        }
        if (this.mCameraHandlerThread != null) {
            this.mCameraHandlerThread.quit();
            this.mCameraHandlerThread = null;
        }
    }

    public void stopCameraPreview() {
        if (this.mPreview != null) {
            this.mPreview.stopCameraPreview();
        }
    }

    protected void resumeCameraPreview() {
        if (this.mPreview != null) {
            this.mPreview.showCameraPreview();
        }
    }

    public synchronized Rect getFramingRectInPreview(int i, int i2) {
        if (this.mFramingRectInPreview == null) {
            Rect framingRect = this.mViewFinderView.getFramingRect();
            int width = this.mViewFinderView.getWidth();
            int height = this.mViewFinderView.getHeight();
            if (!(framingRect == null || width == 0)) {
                if (height != 0) {
                    Rect rect = new Rect(framingRect);
                    if (i < width) {
                        rect.left = (rect.left * i) / width;
                        rect.right = (rect.right * i) / width;
                    }
                    if (i2 < height) {
                        rect.top = (rect.top * i2) / height;
                        rect.bottom = (rect.bottom * i2) / height;
                    }
                    this.mFramingRectInPreview = rect;
                }
            }
            return 0;
        }
        return this.mFramingRectInPreview;
    }

    public void setFlash(boolean z) {
        this.mFlashState = Boolean.valueOf(z);
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
            if (z) {
                if (!parameters.getFlashMode().equals("torch")) {
                    parameters.setFlashMode("torch");
                } else {
                    return;
                }
            } else if (!parameters.getFlashMode().equals("off")) {
                parameters.setFlashMode("off");
            } else {
                return;
            }
            this.mCameraWrapper.mCamera.setParameters(parameters);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean getFlash() {
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera) && this.mCameraWrapper.mCamera.getParameters().getFlashMode().equals("torch")) {
            return true;
        }
        return false;
    }

    public void toggleFlash() {
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
            if (parameters.getFlashMode().equals("torch")) {
                parameters.setFlashMode("off");
            } else {
                parameters.setFlashMode("torch");
            }
            this.mCameraWrapper.mCamera.setParameters(parameters);
        }
    }

    public void setAutoFocus(boolean z) {
        this.mAutofocusState = z;
        if (this.mPreview != null) {
            this.mPreview.setAutoFocus(z);
        }
    }

    public void setShouldScaleToFill(boolean z) {
        this.mShouldScaleToFill = z;
    }

    public void setAspectTolerance(float f) {
        this.mAspectTolerance = f;
    }

    public byte[] getRotatedData(byte[] bArr, Camera camera) {
        camera = camera.getParameters().getPreviewSize();
        int i = camera.width;
        camera = camera.height;
        int rotationCount = getRotationCount();
        if (rotationCount != 1 && rotationCount != 3) {
            return bArr;
        }
        int i2 = camera;
        camera = bArr;
        bArr = null;
        while (bArr < rotationCount) {
            byte[] bArr2 = new byte[camera.length];
            for (int i3 = 0; i3 < i2; i3++) {
                for (int i4 = 0; i4 < i; i4++) {
                    bArr2[(((i4 * i2) + i2) - i3) - 1] = camera[(i3 * i) + i4];
                }
            }
            bArr++;
            camera = bArr2;
            int i5 = i2;
            i2 = i;
            i = i5;
        }
        return camera;
    }

    public int getRotationCount() {
        return this.mPreview.getDisplayOrientation() / 90;
    }
}
