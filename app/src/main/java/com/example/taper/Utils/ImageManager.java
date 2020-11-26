package com.example.taper.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManager {
    public static Bitmap getBitmap(String imgUrl){
        File imageFile=new File(imgUrl);
        FileInputStream fis=null;
        Bitmap bitmap=null;
        try{
            fis=new FileInputStream(imageFile);
            bitmap= BitmapFactory.decodeStream(fis);

        }catch (FileNotFoundException e){

        }finally {
            try{
                fis.close();
            }catch (IOException e){

            }
        }
        return bitmap;
    }
    public static byte[] getByteFromBitmap(Bitmap bm,int quality){
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
    }
}
