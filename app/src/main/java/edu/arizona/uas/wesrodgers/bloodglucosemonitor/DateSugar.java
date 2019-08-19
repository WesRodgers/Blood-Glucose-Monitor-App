package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateSugar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateSugar extends VisibleFragment {
    //Don't forget to write getters/setters and change these back to private!!
    private int mDay, mMonth, mYear;
    public UUID id;
    public Date mDate;
    private TextView fastingNote, breakfastNote, lunchNote, dinnerNote, notes;
    private Button clearButton, historyButton;
    private CheckBox normalCheck;
    private TextView fastingNumber, breakfastNumber, lunchNumber, dinnerNumber;
    public int fasting, breakfast, lunch, dinner;
    public boolean fastingNormal, breakfastNormal, lunchNormal, dinnerNormal;
    public String noteString;
    private boolean populated = false;
    private boolean isNew = false;
    private boolean mDeleted = false;

    public DateSugar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment DateSugar.
     */
    public static DateSugar newInstance(Date date) {
        DateSugar fragment = new DateSugar();
        Bundle b = new Bundle();
        b.putSerializable("date", date);
        fragment.setArguments(b);
        return fragment;
    }

    //factory method for getting a new date sugar, creates and returns
    //a date sugar object with today's date.
    public static DateSugar newInstance(){
        DateSugar f = new DateSugar();
        f.mDate = new Date();
        String tempdate = new SimpleDateFormat("dd MM yyyy").format(f.mDate);
        String[] dateArray = tempdate.split(" ");
        f.mDay = Integer.valueOf(dateArray[0]);
        f.mMonth = Integer.valueOf(dateArray[1]);
        f.mYear = Integer.valueOf(dateArray[2]);
        f.fasting = 0;
        f.breakfast = 0;
        f.lunch = 0;
        f.dinner = 0;
        f.fastingNormal = false;
        f.breakfastNormal = false;
        f.lunchNormal = false;
        f.dinnerNormal = false;
        f.id = UUID.randomUUID();
        f.isNew = true;

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //if a save state was passed in, populate from that
        if(savedInstanceState != null){
            mDay = savedInstanceState.getInt("day");
            mMonth = savedInstanceState.getInt("month");
            mYear = savedInstanceState.getInt("year");
            mDate = new Date(mYear-1900, mMonth-1, mDay-0);
            noteString = savedInstanceState.getString("notes");
            fasting = savedInstanceState.getInt("fasting");
            breakfast = savedInstanceState.getInt("breakfast");
            lunch = savedInstanceState.getInt("lunch");
            dinner = savedInstanceState.getInt("dinner");
            id = (UUID) savedInstanceState.getSerializable("id");
            populated = true;
        }

        //otherwise, if populated then use that
        else if(!populated) {
            if(getArguments() != null){
                mDate = (Date) getArguments().getSerializable("date");
            }
            else mDate = new Date();
            String tempdate = new SimpleDateFormat("dd MM yyyy").format(mDate);
            String[] dateArray = tempdate.split(" ");
            mDay = Integer.valueOf(dateArray[0]);
            mMonth = Integer.valueOf(dateArray[1]);
            mYear = Integer.valueOf(dateArray[2]);
            fasting = 0;
            breakfast = 0;
            lunch = 0;
            dinner = 0;
            fastingNormal = false;
            breakfastNormal = false;
            lunchNormal = false;
            dinnerNormal = false;
            id = UUID.randomUUID();
            isNew = true;
        }
    }

    /**
     * Extracts the fields from a Sugar item and populates this object's fields
     * with the correct info
     * @param sugar
     */
    public void populateFromSugar(Sugar sugar){
        mDate = sugar.getDate();
        String tempDate = new SimpleDateFormat("dd MM yyyy").format(mDate);
        String[] dateArray = tempDate.split(" ");
        mDay = Integer.valueOf(dateArray[0]);
        mMonth = Integer.valueOf(dateArray[1]);
        mYear = Integer.valueOf(dateArray[2]);
        fasting = sugar.getFasting();
        breakfast = sugar.getBreakfast();
        lunch = sugar.getLunch();
        dinner = sugar.getDinner();
        noteString = sugar.getNote();
        populated = true;
        id = sugar.getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().invalidateOptionsMenu();
        View v = inflater.inflate(R.layout.fragment_date_sugar, container, false);

        Button dateField = (Button) v.findViewById(R.id.date);
        String month = new DateFormatSymbols().getMonths()[mMonth-1];
        dateField.setText(String.format("%d %s %d", mDay, month, mYear));

        fastingNote = v.findViewById(R.id.fastingNote);
        breakfastNote = v.findViewById(R.id.breakfastNote);
        lunchNote = v.findViewById(R.id.lunchNote);
        dinnerNote = v.findViewById(R.id.dinnerNote);
        clearButton = v.findViewById(R.id.clearButton);
        historyButton = v.findViewById(R.id.historyButton);
        normalCheck = v.findViewById(R.id.normalCheck);
        fastingNumber = v.findViewById(R.id.fastingLevel);
        breakfastNumber = v.findViewById(R.id.breakfastLevel);
        lunchNumber = v.findViewById(R.id.lunchLevel);
        dinnerNumber = v.findViewById(R.id.dinnerLevel);
        notes = v.findViewById(R.id.notes);

        //if this was populated already, set the notes.
        if(populated){
            String sugarStatus = sugarStatus(fasting, true);
            fastingNormal = sugarStatus.equals("Normal") ? true : false;
            fastingNote.setText(String.format("[Fasting: %s]", sugarStatus));

            sugarStatus = sugarStatus(breakfast, false);
            breakfastNormal = sugarStatus.equals("Normal") ? true : false;
            breakfastNote.setText(String.format("[Breakfast: %s]", sugarStatus));

            sugarStatus = sugarStatus(lunch, false);
            lunchNormal = sugarStatus.equals("Normal") ? true : false;
            lunchNote.setText(String.format("[Lunch: %s]", sugarStatus));

            sugarStatus = sugarStatus(dinner, false);
            dinnerNormal = sugarStatus.equals("Normal") ? true : false;
            dinnerNote.setText(String.format("[Dinner: %s]", sugarStatus));

            fastingNumber.setText(String.valueOf(fasting));
            breakfastNumber.setText(String.valueOf(breakfast));
            lunchNumber.setText(String.valueOf(lunch));
            dinnerNumber.setText(String.valueOf(dinner));

            if(fasting == 0){
                fastingNote.setText("");
                fastingNumber.setText("");
                fastingNumber.setHint("mg/dL");
            }
            if(breakfast == 0){
                breakfastNote.setText("");
                breakfastNumber.setText("");
                breakfastNumber.setHint("mg/dL");
            }
            if(lunch == 0){
                lunchNote.setText("");
                lunchNumber.setText("");
                lunchNumber.setHint("mg/dL");
            }
            if(dinner == 0){
                dinnerNote.setText("");
                dinnerNumber.setText("");
                dinnerNumber.setHint("mg/dL");
            }

            setNormal();
            notes.setText(noteString);
        }
        populated = true;

        fastingNumber.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    fasting = 0;
                    return;
                }
                else fasting = Integer.parseInt(s.toString());
                String sugarStatus = sugarStatus(fasting, true);
                fastingNormal = sugarStatus.equals("Normal") ? true : false;
                fastingNote.setText(String.format("[Fasting: %s]", sugarStatus));
                setNormal();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        breakfastNumber.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    breakfast = 0;
                    return;
                }
                else breakfast = Integer.parseInt(s.toString());
                String sugarStatus = sugarStatus(breakfast,false);
                breakfastNormal = sugarStatus.equals("Normal") ? true : false;
                breakfastNote.setText(String.format("[Breakfast: %s]", sugarStatus));
                setNormal();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lunchNumber.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    lunch = 0;
                    return;
                }
                else lunch = Integer.parseInt(s.toString());
                String sugarStatus = sugarStatus(lunch,false);
                lunchNormal = sugarStatus.equals("Normal") ? true : false;
                lunchNote.setText(String.format("[Lunch: %s]", sugarStatus));
                setNormal();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dinnerNumber.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    dinner = 0;
                    return;
                }
                else dinner = Integer.parseInt(s.toString());
                String sugarStatus = sugarStatus(dinner,false);
                dinnerNormal = sugarStatus.equals("Normal") ? true : false;
                dinnerNote.setText(String.format("[Dinner: %s]", sugarStatus));
                setNormal();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearButton.setOnClickListener((e)->{
            fastingNormal = false;
            fasting = 0;
            breakfastNormal = false;
            breakfast = 0;
            lunchNormal = false;
            lunch = 0;
            dinnerNormal = false;
            dinner = 0;
            fastingNote.setText("");
            fastingNumber.setText("");
            fastingNumber.setHint("mg/dL");
            breakfastNote.setText("");
            breakfastNumber.setText("");
            breakfastNumber.setHint("mg/dL");
            lunchNote.setText("");
            lunchNumber.setText("");
            lunchNumber.setHint("mg/dL");
            dinnerNote.setText("");
            dinnerNumber.setText("");
            dinnerNumber.setHint("mg/dL");
            notes.setText("");
            setNormal();

        });

        notes.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                noteString = String.valueOf(notes.getText());
            }
        });


        //if we are already on the current date, do nothing. Otherwise, search for a Sugar with
        //the current date and make a DateSugar fragment from it. If one doesn't exist, create a
        //completely new one.
        dateField.setOnClickListener((e)->{
            FragmentManager manager = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(mDate);
            dialog.setTargetFragment(DateSugar.this, 0);
            dialog.show(manager, "DialogDate");
        });

        historyButton.setOnClickListener((e)->{
            Fragment historyFragment = new DateListFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for(Fragment g : getFragmentManager().getFragments()) transaction.remove(g);
            transaction.add(R.id.fragmentContainer, historyFragment);
            transaction.commit();
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_datesugar_menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.delete_sugar:
                AlertDialog askOption = new AlertDialog.Builder(getActivity())
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SugarTests.get(getActivity()).delete(id);
                                historyButton.performClick();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                askOption.show();
                return true;

            case R.id.upload_sugar:
                AlertDialog askOption2 = new AlertDialog.Builder(getActivity())
                        .setTitle("Upload")
                        .setMessage("This date's data will be uploaded")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                UploadDataTask task = new UploadDataTask();
                                task.execute();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dialog.dismiss();
                            }
                        })
                        .create();
                askOption2.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setNormal() {
        normalCheck.setChecked(fastingNormal && breakfastNormal && lunchNormal && dinnerNormal);
    }

    public String sugarStatus(int i, boolean fasted){
        if(i < 70) return "Hypoglycemic";
        if(fasted && i <= 99) return "Normal";
        if(fasted && i > 99) return "Abnormal";
        if(i < 140) return "Normal";
        return "Abnormal";
    }

    public boolean isToday(Date d){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d);
        cal2.setTime(new Date());
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean sameDay(Date d, Date d2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d);
        cal2.setTime(d2);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    //this makes sure to save the info into the sugarTests list when it is stopped
    @Override
    public void onPause(){
        super.onPause();
        SugarTests.get(getActivity()).updateSugar(new Sugar(this));
    }

    @Override
    public void onStop(){
        super.onStop();
        SugarTests.get(getActivity()).updateSugar(new Sugar(this));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt("fasting", fasting);
        savedInstanceState.putInt("breakfast", breakfast);
        savedInstanceState.putInt("lunch", lunch);
        savedInstanceState.putInt("dinner", dinner);
        savedInstanceState.putString("notes", noteString);
        savedInstanceState.putSerializable("id", id);
        savedInstanceState.putInt("day", mDay);
        savedInstanceState.putInt("month", mMonth);
        savedInstanceState.putInt("year", mYear);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == 0){
            Date date = (Date) data.getSerializableExtra("extra_date");
            Sugar temp = null;

            for(Sugar s : SugarTests.get(getActivity()).getSugars()){
                if(sameDay(date, s.getDate())){
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
            transaction.add(R.id.fragmentContainer, f);
            transaction.commit();
        }
    }

    private class UploadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                uploadData();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void uploadData() throws UnsupportedEncodingException, IOException, JSONException {
        SugarTests.get(getActivity()).updateSugar(new Sugar(this));
        JSONObject jo = getJSON();

        URL url = new URL("http://u.arizona.edu/~lxu/cscv381/local_glucose.php");
        Map<String, Object> params = new LinkedHashMap<>();
         params.put("username", "wesrod");
         params.put("password", "a2801");
         params.put("data", jo.toString());

         StringBuilder postData = new StringBuilder();
         for(Map.Entry<String, Object> param : params.entrySet()){
             if(postData.length() != 0) postData.append('&');
             postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
             postData.append("=");
             postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
         }
         byte[] postDataBytes = postData.toString().getBytes("UTF-8");

         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
         conn.setDoOutput(true);
         conn.getOutputStream().write(postDataBytes);

         if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
             System.out.println("It was OK");
             Looper.prepare();
             Toast toast = Toast.makeText(getContext(), "Upload Successful!", Toast.LENGTH_SHORT);
             View tView = toast.getView();
             tView.setBackgroundColor(Color.GRAY);
             toast.setView(tView);
             toast.show();
             Looper.loop();
             System.out.println("It was OK");
         }

         Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("uuid", id.toString());
        jo.put("date", mDate.toString());
        jo.put("fasting", fasting);
        jo.put("breakfast", breakfast);
        jo.put("lunch", lunch);
        jo.put("dinner", dinner);
        jo.put("notes", noteString == null ? "" : noteString);

        return jo;
    }
}
