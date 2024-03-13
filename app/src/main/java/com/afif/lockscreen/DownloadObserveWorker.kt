package com.afif.lockscreen

import android.content.Context
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters

class DownloadObserveWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun doWork(): Result {

        //etName.setOnTouchListener(exitSoftKeyBoard);
        //etName.setShowSoftInputOnFocus(false);
        //etName.getShowSoftInputOnFocus();

        //startService();
        Log.d("dwonload_det", "doWork executed")

        try {
                // Create a Handler on the background thread

                // Use the Handler to post messages/runnables to the background thread



            //CoroutineScope(Dispatchers.IO).launch {
                // Code to be executed on the background thread

            Handler(Looper.getMainLooper()).post {
                var handler = Handler();
                var downloadObserver: ContentObserver  =  DownloadContentObserver(handler, applicationContext)

                val downloadsUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)

                applicationContext.contentResolver.registerContentObserver(
                    downloadsUri,
                    true, downloadObserver as DownloadContentObserver)

            }



            return Result.success()

        }
        catch (throwable: Throwable) {
            Log.e("dwonload_det", "catch "+throwable.message.toString())
            Result.failure()
        }

        return Result.success()

    }
}