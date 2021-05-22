package com.andbase.qrcode;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import com.andbase.library.utils.AbLogUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AbDecodeUtil {

    public static List<ResultPoint> pointList = new ArrayList<>();

    public static  String  decode(Bitmap bitmap) {
        try {
            Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);

            int[] bitmapData = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(bitmapData, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(),bitmapData);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader qrCodeReader = new QRCodeReader();
            Result result = qrCodeReader.decode(binaryBitmap, hints);
            String code = result.getText();
            AbLogUtil.e("DecodeThread", "扫描到了二维码：" + code);
            return  code;
        } catch (Exception e) {
            e.printStackTrace();
            AbLogUtil.e("DecodeThread", "扫描二维码异常：" + e.getMessage());
        }
        return  "";
    }

    public static String decode(byte[] yuvData, int width, int height, Rect frameRect,final AbResultPointCallback callback) {
        pointList.clear();
        Result result = null;
        try {
            Log.e("DecodeThread", "开始识别二维码...");
            Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
            //hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
            hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, new com.google.zxing.ResultPointCallback() {
                @Override
                public void foundPossibleResultPoint(ResultPoint point) {
                    pointList.add(point);
                    callback.foundResultPoints(pointList);
                    AbLogUtil.i("foundResultPoints","resultPoints:" + pointList.size());
                }
            });


            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(yuvData,
                    width,
                    height,
                    (width-frameRect.height())/2,(height - frameRect.width())/2
                    ,frameRect.height(),
                    frameRect.width(),
                    false);

            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader qrCodeReader = new QRCodeReader();
            try {
                result = qrCodeReader.decode(binaryBitmap, hints);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                qrCodeReader.reset();
            }

            if(result!=null){
                String code = result.getText();
                AbLogUtil.e("DecodeThread","扫描到了二维码：" + code);
                return  code;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;

    }

    /**
     * 生成简单二维码
     * createQRCodeBitmap(content, 800, 800,"UTF-8","H", "1", Color.BLACK, Color.WHITE);
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmap(String content, int width,int height,
                                            String character_set,String error_correction_level,
                                            String margin,int color_black, int color_white) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
