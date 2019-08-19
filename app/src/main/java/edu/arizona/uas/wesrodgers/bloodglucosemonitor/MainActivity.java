package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;

/**
 * Main activity for the blood glucose monitor app. Pulls up a DateSugar fragment
 * with the current date on launch.
 * @author Wes Rodgers
 */

public class MainActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        //checks to see if an entry exists for the current date
        boolean today = false;
        for(Sugar sugar : SugarTests.get(this).getSugars()){
            if(DateSugar.sameDay(sugar.getDate(), new Date())){
                today = true;
            }
        }

        //if this is the initial open, creates the view pager with the current date
        //as the initial viewable date entry.
        if(savedInstanceState == null) {

            //if no entry exists for the current date, creates one
            if (!today) SugarTests.get(this).add(new Sugar(DateSugar.newInstance()));

            fragment = new SugarPagerActivity();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "channel";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
