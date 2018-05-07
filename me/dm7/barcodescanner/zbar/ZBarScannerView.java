package me.dm7.barcodescanner.zbar;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import me.dm7.barcodescanner.core.BarcodeScannerView;
import me.dm7.barcodescanner.core.DisplayUtils;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;

public class ZBarScannerView extends BarcodeScannerView {
    private static final String TAG = "ZBarScannerView";
    private List<BarcodeFormat> mFormats;
    private ResultHandler mResultHandler;
    private ImageScanner mScanner;

    public interface ResultHandler {
        void handleResult(Result result);
    }

    static {
        System.loadLibrary("iconv");
    }

    public ZBarScannerView(Context context) {
        super(context);
        setupScanner();
    }

    public ZBarScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupScanner();
    }

    public void setFormats(List<BarcodeFormat> list) {
        this.mFormats = list;
        setupScanner();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this.mResultHandler = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        if (this.mFormats == null) {
            return BarcodeFormat.ALL_FORMATS;
        }
        return this.mFormats;
    }

    public void setupScanner() {
        this.mScanner = new ImageScanner();
        this.mScanner.setConfig(0, 256, 3);
        this.mScanner.setConfig(0, 257, 3);
        this.mScanner.setConfig(0, 0, 0);
        for (BarcodeFormat id : getFormats()) {
            this.mScanner.setConfig(id.getId(), 0, 1);
        }
    }

    public void onPreviewFrame(byte[] bArr, Camera camera) {
        if (this.mResultHandler != null) {
            try {
                Size previewSize = camera.getParameters().getPreviewSize();
                int i = previewSize.width;
                int i2 = previewSize.height;
                if (DisplayUtils.getScreenOrientation(getContext()) == 1) {
                    int rotationCount = getRotationCount();
                    if (rotationCount == 1 || rotationCount == 3) {
                        int i3 = i;
                        i = i2;
                        i2 = i3;
                    }
                    bArr = getRotatedData(bArr, camera);
                }
                Rect framingRectInPreview = getFramingRectInPreview(i, i2);
                Image image = new Image(i, i2, "Y800");
                image.setData(bArr);
                image.setCrop(framingRectInPreview.left, framingRectInPreview.top, framingRectInPreview.width(), framingRectInPreview.height());
                if (this.mScanner.scanImage(image) != null) {
                    bArr = this.mScanner.getResults();
                    camera = new Result();
                    bArr = bArr.iterator();
                    while (bArr.hasNext()) {
                        Object str;
                        Symbol symbol = (Symbol) bArr.next();
                        if (VERSION.SDK_INT >= 19) {
                            str = new String(symbol.getDataBytes(), StandardCharsets.UTF_8);
                        } else {
                            str = symbol.getData();
                        }
                        if (!TextUtils.isEmpty(str)) {
                            camera.setContents(str);
                            camera.setBarcodeFormat(BarcodeFormat.getFormatById(symbol.getType()));
                            break;
                        }
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            ResultHandler access$000 = ZBarScannerView.this.mResultHandler;
                            ZBarScannerView.this.mResultHandler = null;
                            ZBarScannerView.this.stopCameraPreview();
                            if (access$000 != null) {
                                access$000.handleResult(camera);
                            }
                        }
                    });
                } else {
                    camera.setOneShotPreviewCallback(this);
                }
            } catch (byte[] bArr2) {
                Log.e(TAG, bArr2.toString(), bArr2);
            }
        }
    }

    public void resumeCameraPreview(ResultHandler resultHandler) {
        this.mResultHandler = resultHandler;
        super.resumeCameraPreview();
    }
}
