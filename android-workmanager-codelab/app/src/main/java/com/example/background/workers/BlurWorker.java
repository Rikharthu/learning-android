package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.background.Constants;

import androidx.work.Data;
import androidx.work.Worker;

public class BlurWorker extends Worker {
    public static final String TAG = BlurWorker.class.getSimpleName();

    @NonNull
    @Override
    public WorkerResult doWork() {
        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI, null);
        Context appContext = getApplicationContext();

        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri");
            }

            ContentResolver resolver = appContext.getContentResolver();
            // Create a bitmap
            Bitmap picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));

            Bitmap blurryPicture = WorkerUtils.blurBitmap(picture, appContext);

            // Write bitmap to a temp file
            Uri outputUri = WorkerUtils.writeBitmapToFile(appContext, blurryPicture);

            WorkerUtils.makeStatusNotification("Output is " + outputUri.toString(), appContext);

            // Set output blurry picture
            setOutputData(new Data.Builder().putString(
                    Constants.KEY_IMAGE_URI, outputUri.toString()).build());

            return WorkerResult.SUCCESS;
        } catch (Throwable throwable) {
            return WorkerResult.FAILURE;
        }
    }
}
