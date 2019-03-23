/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.sunshine.MainActivity;
import com.example.android.sunshine.data.WeatherContract;

import java.util.Currency;


public class SunshineSyncUtils {


private static boolean sInitialized;

synchronized public static void initialize(@NonNull final Context context) {
    if (sInitialized) return;

    sInitialized = true;

    new AsyncTask<Void,Void,Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
            Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
            String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
            String selectionStatement = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

            Cursor cursor = context.getContentResolver().query(forecastQueryUri, projectionColumns, selectionStatement, null, null);

            if (null == cursor || cursor.getCount() == 0) {
                startImmediateSync(context);
            }

            cursor.close();
            return null;
        }
    }.execute();
}


    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }


}