package com.applicoders.msp_2017_project.eatogether.UtilityClasses;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by rafay on 4/16/2017.
 */

public class ImageToFile {
    public static File convertImage(Context context, Bitmap bitmap) throws IOException{

        //create a file to write bitmap data
        File imageFile = new File(context.getCacheDir(), "image" + System.currentTimeMillis() + ".jpeg");
        imageFile.createNewFile();

//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return imageFile;
    }
}
