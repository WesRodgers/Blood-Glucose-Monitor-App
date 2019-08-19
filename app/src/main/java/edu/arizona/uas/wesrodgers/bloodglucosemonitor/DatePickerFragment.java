package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {
    private Date mDate;

    public DatePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Date date = (Date) getArguments().getSerializable("date");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        DatePicker mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) ->{
                            int year2 = mDatePicker.getYear();
                            int month2 = mDatePicker.getMonth();
                            int day2 = mDatePicker.getDayOfMonth();
                            Date date2 = new GregorianCalendar(year2, month2, day2).getTime();
                            sendResult(Activity.RESULT_OK, date2);
                        })
                .create();
    }

    private void sendResult(int resultOk, Date date2) {
        if(getTargetFragment() == null) return;
        Intent intent = new Intent();
        intent.putExtra("extra_date", date2);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultOk, intent);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param date a Date object
     * @return A new instance of fragment DatePickerFragment.
     */
    public static DatePickerFragment newInstance(Date date) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable("date", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }

}
