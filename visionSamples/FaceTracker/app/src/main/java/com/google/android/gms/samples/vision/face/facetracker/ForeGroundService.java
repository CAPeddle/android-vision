package com.google.android.gms.samples.vision.face.facetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.samples.vision.face.facetracker.patch.SafeFaceDetector;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.InputStream;

public class ForeGroundService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "ForegroundService";


    public ForeGroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(TAG, "Start Foreground Intent Camera Paused: "  );

            createNotificationChannel();

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
            android.util.Log.d(TAG, "onStartCommand: " + getApplicationContext().getPackageName());
            launchIntent.setAction(Constants.ACTION.MAIN_ACTION);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

            Intent playIntent = new Intent(this, ForeGroundService.class);
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

            Intent nextIntent = new Intent(this, ForeGroundService.class);
            nextIntent.setAction(Constants.ACTION.PAUSE_ACTION);
            PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Face Pay")
                    .setTicker("Face Pay")
                    .setContentText("Face Pay")
                    .setSmallIcon(R.drawable.noticon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
//                        .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent)
                    .addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
                    .addAction(android.R.drawable.ic_media_pause, "Pause", pnextIntent).build();

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        }
        else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i(TAG, "Clicked Play " + DetectFaces(null));
        } else if (intent.getAction().equals(Constants.ACTION.PAUSE_ACTION)) {
            Log.i(TAG, "Clicked Pause ");
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Stop Foreground Intent");
        }

        return START_NOT_STICKY;
    }

    public int DetectFaces (Bitmap bitmap) {

        if (bitmap == null) {
            InputStream stream = getResources().openRawResource(R.raw.faces);
            bitmap = BitmapFactory.decodeStream(stream);
        }

        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .build();

        Detector<Face> safeDetector = new SafeFaceDetector(detector);

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = safeDetector.detect(frame);

        int facesFound = 0;
        if (!safeDetector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        } else {
            facesFound = faces.size();
        }

        return facesFound;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service OnCreate ");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "createNotificationChannel: Create Notification");

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        } else {
            Log.d(TAG, "createNotificationChannel: Invalid SDK");
        }
    }
}
