package com.example.background.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import com.example.background.KEY_IMAGE_URI
import com.example.background.R


class BlurWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", appContext)

        return try {

            if (TextUtils.isEmpty(resourceUri)) {
                Log.e("TAG", "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)))

            val output = blurBitmap(picture, appContext)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(appContext, output)

            outputData = Data.Builder().putString(KEY_IMAGE_URI, outputUri.toString()).build()

            Result.SUCCESS
        } catch (throwable: Throwable) {
            Log.e("TAG", "Error applying blur", throwable)
            Result.FAILURE
        }
    }
}