package ets.transfersystem;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by Pierre-Luc on 2017-06-27.
 */

public class QrCode {

    private Activity activity;
    private Bitmap qrCode;
    private String deviceId;
    private String ipAddress;
    public Boolean isValide = false;

    public QrCode(Activity activity) {
        this.activity = activity;
        setMyDeviceId();
        setMyIpAddress();
        qrCode = encodeAsBitmap(deviceId+":"+ipAddress);
        Log.d("Qrcode", deviceId+":"+ipAddress);
    }

    public QrCode(String information) {
        setDeviceIdAndIpAddress(information);
        qrCode = encodeAsBitmap(information);
        Log.d("Qrcode", deviceId+":"+ipAddress);
    }

    private void setMyDeviceId() {
        deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void setMyIpAddress() {
        WifiManager wifiMgr = (WifiManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        ipAddress = Formatter.formatIpAddress(ip);
    }

    public void setDeviceIdAndIpAddress(String information) {
        String[] info = information.split(":");
        if (info.length == 2) {
            deviceId = info[0];
            ipAddress = info[1];
            isValide = true;
        }
    }

    public Bitmap getQrCode() {
        return qrCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    //Source: https://stackoverflow.com/questions/28232116/android-using-zxing-generate-qr-code
    //By Alexander Farber, May 29 '15 at 12:02
    private Bitmap encodeAsBitmap(String str)
    {
        try
        {
            BitMatrix result;
            final int WIDTH = 800;
            try
            {
                result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
            }
            catch (IllegalArgumentException ex){return null;}

            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for(int y = 0; y < h; y++)
            {
                int offset = y * w;
                for (int x = 0; x < w; x++)
                {
                    pixels[offset + x] = result.get(x,y) ? BLACK : WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
            return bitmap;
        }
        catch(WriterException ex){return null;}
    }
}
