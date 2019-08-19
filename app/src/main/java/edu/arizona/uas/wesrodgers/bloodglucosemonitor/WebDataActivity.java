package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.arizona.uas.wesrodgers.bloodglucosemonitor.database.WebDataFragment;

public class WebDataActivity extends AppCompatActivity {

    public static Intent newIntent(Context context, Uri webDataUri){
        Intent i = new Intent(context, WebDataActivity.class);
        i.setData(webDataUri);
        return i;
    }

    protected Fragment createFragment(){
        return WebDataFragment.newInstance(getIntent().getData());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            Log.i("mine", "got inside the null frag check");
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    private int getLayoutResId() {
        return R.layout.activity_main;
    }
}
