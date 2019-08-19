package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Fragment holding the recyclerView that lists historic Sugar data
 * @author Wes Rodgers
 */
public class DateListFragment extends VisibleFragment {

    private RecyclerView mSugarRecyclerView;
    private SugarAdapter mAdapter;

    public DateListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DateListFragment.
     */
    public static DateListFragment newInstance() {
        return new DateListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_date_menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        //when we click on the new entry button, open the date picker
        //dialog so we can pick a date.
        switch(item.getItemId()){
            case R.id.new_sugar:
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date());
                dialog.setTargetFragment(DateListFragment.this, 0);
                dialog.show(manager, "DialogDate");
                return true;
            case R.id.view_cloud:
                Intent i = WebDataActivity.newIntent(getActivity(),
                        Uri.parse("http://u.arizona.edu/~lxu/cscv381" +
                                "/myglucose.php?username=wesrod&password=a2801"));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Inflates the view and sets up the recyclerView for the historic data
     * @param inflater a LayoutInflater
     * @param container the fragment container
     * @param savedInstanceState the Bundle of saved info
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_date_list, container, false);

        mSugarRecyclerView = v.findViewById(R.id.sugarRecyclerView);
        mSugarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return v;
    }

    //we make sure to updateUI on resume and onStart
    //so that the adapter is attached.
    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    /**
     * grabs the SugarTests class data and sorts it, sets the adapter for the recycler view.
     */
    private void updateUI(){
        // creates and attaches the adapter with a List<Sugar> of entries,
        // or updates and attaches the adapter if it already exists.
        List<Sugar> sugars = SugarTests.get(getActivity()).getSugars();
        if(mAdapter == null){
            mAdapter = new SugarAdapter(sugars);
            mSugarRecyclerView.setAdapter(mAdapter);
        } else{
            if(mSugarRecyclerView.getAdapter() == null) mSugarRecyclerView.setAdapter(mAdapter);
            mAdapter.setSugars(sugars);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Private subclass to use as the fragment ViewHolder for the recyclerView's adapter
     */
    private class SugarHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Sugar mSugar;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mNormalCheck;

        /**
         * Constructor for the holder, finds the appropriate fields for the
         * text and checkbox from the .xml layout file
         * @param inflater the LayoutInflater
         * @param parent the passed in ViewGroup
         */
        private SugarHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_sugar, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.sugar_title);
            mDateTextView = itemView.findViewById(R.id.sugar_date);
            mNormalCheck = itemView.findViewById(R.id.sugar_check);
        }

        /**
         * Sets up the click listener for the historic data fragment
         * @param v passed in View
         */
        @Override
        public void onClick(View v) {

            //when we click an entry, we pass the id to a sugar pager fragment
            // in its arguments so it knows which entry to have as its initial
            // viewable object.
            Fragment historyFragment = new SugarPagerActivity();
            Bundle b = new Bundle();
            b.putSerializable("id", mSugar.getId());
            historyFragment.setArguments(b);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            //this is the hacky fix I had to throw in to get everything to work correctly.
            //remove all fragments from the backstack, then replace with a new one.
            for(Fragment g : getFragmentManager().getFragments()) transaction.remove(g);
            transaction.replace(R.id.fragmentContainer, historyFragment);
            transaction.commit();
        }

        /**
         * Sets up the fragment to be displayed in the historic data
         * @param sugar the Sugar item we are setting up
         */
        private void bind(Sugar sugar) {
            mSugar = sugar;
            String temp = String.valueOf(mSugar.getSugar()) + "   ";
            mTitleTextView.setText(temp);
            temp = "   " + new SimpleDateFormat("dd MMM yyyy")
                    .format(mSugar.getDate());
            mDateTextView.setText(temp);
            mNormalCheck.setChecked(mSugar.isNormal());
        }
    }

    /**
     * private subclass to act as an adapter for the recyclerView
     */
    private class SugarAdapter extends RecyclerView.Adapter<SugarHolder>{

        private List<Sugar> mSugars;
        public SugarAdapter(List<Sugar> sugars){ mSugars = sugars; }
        public void setSugars(List<Sugar> sugars){ mSugars = sugars; }


        @Override
        public SugarHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new SugarHolder(layoutInflater, parent);
        }

        /**
         * Sets up SugarTests fragments when they are bound
         * @param sugarHolder the viewHolder for our recyclerView
         * @param i the index int
         */
        @Override
        public void onBindViewHolder(@NonNull SugarHolder sugarHolder, int i) {
            Sugar sugar = mSugars.get(i);
            sugarHolder.bind(sugar);
        }

        /**
         * getter for size of the historic data list
         * @return the number of items in the list
         */
        @Override
        public int getItemCount() {
            return mSugars.size();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        //this handles activity results from things we start that send us a response
        //back, in our case the date picker dialog.
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        //this just identifies that it is the date picker dialog, as we have it sending
        //requestCode back as 0. Then we grab all of the info and open a new entry based
        //on the data that was sent back in the intent parameter.
        if(requestCode == 0){
            Date date = (Date) data.getSerializableExtra("extra_date");
            Sugar temp = null;

            for(Sugar s : SugarTests.get(getActivity()).getSugars()){
                if(DateSugar.sameDay(date, s.getDate())){
                    temp = s;
                }
            }
            SugarPagerActivity f = new SugarPagerActivity();
            if(temp == null){
                temp = new Sugar(DateSugar.newInstance());
                temp.setDate(date);
                SugarTests.get(getActivity()).add(temp);
            }

            Bundle b = new Bundle();
            b.putSerializable("id", temp.getId());
            f.setArguments(b);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for(Fragment g : getFragmentManager().getFragments()) transaction.remove(g);
            transaction.replace(R.id.fragmentContainer, f);
            transaction.commit();
        }
    }
}
