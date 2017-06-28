package ets.transfersystem;

import android.graphics.Bitmap;

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

    private Bitmap qrCode;
    private String deviceId;
    private String ipAddress;

    public QrCode() {
        setMyDeviceId();
        setMyIpAddress();
        qrCode = encodeAsBitmap(deviceId+":"+ipAddress);
    }

    private void setMyDeviceId() {
        deviceId = "DEVICE_ID";
    }

    private void setMyIpAddress() {
        ipAddress = "IP_ADDRESS";
    }

    public Bitmap getQrCode() {
        return qrCode;
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
