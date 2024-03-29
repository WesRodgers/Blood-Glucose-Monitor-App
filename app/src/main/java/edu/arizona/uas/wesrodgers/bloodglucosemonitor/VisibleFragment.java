package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class VisibleFragment extends Fragment {
    private static final String TAG = "VisibleFragment";

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            Log.i(TAG, "Received notification " + intent.getAction());
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(DailyNotification.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification, filter,
                DailyNotification.PERM_PRIVATE, null);
    }

    @Override
    public void onStop(){
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }
}
