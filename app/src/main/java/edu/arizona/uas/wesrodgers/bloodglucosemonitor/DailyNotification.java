package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DailyNotification extends IntentService {
    private static final String TAG = "DailyNotification";
    private static final long INTERVAL = TimeUnit.MINUTES.toMillis(1);

    public static final String ACTION_SHOW_NOTIFICATION =
            "edu.arizona.uas.wesrodgers.bloodglucosemonitor.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE =
            "edu.arizona.uas.wesrodgers.bloodglucosemonitor.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";


    public DailyNotification() {
        super("DailyNotification");
    }

    public static void setServiceAlarm(Context context, boolean isOn){
        Intent i = DailyNotification.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), INTERVAL, pi);
        } else{
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    private static Intent newIntent(Context context) {
        return new Intent(context, DailyNotification.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("mine", "Received an intent: " + intent);

        boolean todayComplete = false;
        for(Sugar sugar : SugarTests.get(getBaseContext()).getSugars()){
            Log.i("mine", sugar.getId().toString());
            if(DateSugar.sameDay(sugar.getDate(), new Date())){
                if(sugar.getFasting() != 0 && sugar.getBreakfast() != 0
                && sugar.getLunch() != 0 && sugar.getDinner() != 0){
                    Log.i("mine", "" + sugar.getFasting());
                    Log.i("mine", "" + sugar.getBreakfast());
                    Log.i("mine", "" + sugar.getLunch());
                    Log.i("mine", "" + sugar.getDinner());
                    todayComplete = true;
                }
            }
        }

        if(!todayComplete){
            Log.i("mine", "today not complete");
            Resources resources = getResources();
            Intent i = MainActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker("Daily glucose input missing")
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setContentTitle("Blood Glucose Monitor")
                    .setContentText("Daily glucose input missing")
                    .setContentIntent(pi)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setChannelId("channel")
                    .setAutoCancel(true)
                    .build();

            showBackgroundNotification(0, notification);
        }
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        Log.i("mine", "sending ordered broadcast");
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }
}
