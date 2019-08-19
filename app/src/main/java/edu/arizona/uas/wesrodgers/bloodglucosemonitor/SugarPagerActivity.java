package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class SugarPagerActivity extends VisibleFragment{

    private ViewPager mViewPager;
    private UUID sugarID;

    @Override
    public void onCreate(Bundle savedInstanceState){
        DailyNotification.setServiceAlarm(getActivity(), true);
        super.onCreate(savedInstanceState);
        for(Sugar sugar : SugarTests.get(getActivity()).getSugars()){
            if(DateSugar.sameDay(sugar.getDate(), new Date())){
                sugarID = sugar.getId();
            }
        }
        if(getArguments()!=null) sugarID = (UUID) getArguments().getSerializable("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle savedInstanceState){
        super.onCreateView(inflater, view, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_sugar_pager, view, false);

        mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        FragmentManager fragmentManager = getFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                Sugar sugar = SugarTests.get(getActivity()).getSugars().get(i);
                DateSugar f = new DateSugar();
                f.populateFromSugar(sugar);
                return f;
            }

            @Override
            public int getCount() {
                return SugarTests.get(getActivity()).getSugars().size();
            }

        });
        List<Sugar> l = SugarTests.get(getActivity()).getSugars();
        for(int i=0; i<l.size(); i++){
            if(l.get(i).getId().equals(sugarID)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
        return v;
    }

    @Override
    public void onResume() {
        mViewPager.getAdapter().notifyDataSetChanged();
        super.onResume();
    }

}
