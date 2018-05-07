package me.dm7.barcodescanner.zbar;

public class Result {
    private BarcodeFormat mBarcodeFormat;
    private String mContents;

    public void setContents(String str) {
        this.mContents = str;
    }

    public void setBarcodeFormat(BarcodeFormat barcodeFormat) {
        this.mBarcodeFormat = barcodeFormat;
    }

    public BarcodeFormat getBarcodeFormat() {
        return this.mBarcodeFormat;
    }

    public String getContents() {
        return this.mContents;
    }
}
