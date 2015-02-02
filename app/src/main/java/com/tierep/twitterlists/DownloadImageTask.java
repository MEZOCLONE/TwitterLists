package com.tierep.twitterlists;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Class that is used for downloading images from an url and placing them in an ImageView.
 *
 * Created by pieter on 01/02/15.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;

    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        Bitmap bitmap = null;
        InputStream in = null;

        try {
            in = new URL(url).openStream();
            Log.d("DOWNLOAD", "Downloading image...");
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }
        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
