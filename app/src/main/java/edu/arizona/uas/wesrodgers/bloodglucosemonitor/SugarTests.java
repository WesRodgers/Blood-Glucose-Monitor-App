package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import edu.arizona.uas.wesrodgers.bloodglucosemonitor.database.SugarDbSchema;

public class SugarTests {
    private static SugarTests sSugarTests;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static SugarTests get(Context context){
        if(sSugarTests == null){
            sSugarTests = new SugarTests(context);
        }
        return sSugarTests;
    }

    private SugarTests(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new SugarBaseHelper(mContext).getWritableDatabase();
    }

    public ArrayList<Sugar> sort(ArrayList<Sugar> mSugars) {
        Collections.sort(mSugars, new SugarComparator());
        return mSugars;
    }

    private static ContentValues getContentValues(Sugar sugar){
        ContentValues values = new ContentValues();
        values.put(SugarDbSchema.SugarTable.Cols.UUID, sugar.getId().toString());
        values.put(SugarDbSchema.SugarTable.Cols.DATE, sugar.getDate().getTime());
        values.put(SugarDbSchema.SugarTable.Cols.FASTING, sugar.getFasting());
        values.put(SugarDbSchema.SugarTable.Cols.BREAKFAST, sugar.getBreakfast());
        values.put(SugarDbSchema.SugarTable.Cols.LUNCH, sugar.getLunch());
        values.put(SugarDbSchema.SugarTable.Cols.DINNER, sugar.getDinner());
        values.put(SugarDbSchema.SugarTable.Cols.NOTES, sugar.getNote());

        return values;
    }

    private static class SugarComparator implements Comparator<Sugar>{
        @Override
        public int compare(Sugar o1, Sugar o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }


    public List<Sugar> getSugars() {
        List<Sugar> mSugars = new ArrayList<>();
        SugarCursorWrapper cursor = querySugars(null, null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                mSugars.add(cursor.getSugar());
                cursor.moveToNext();
            }
        }finally{
            cursor.close();
        }
        mSugars = sort((ArrayList<Sugar>) mSugars);
        return mSugars;
    }

    public Sugar getSugar(UUID id){
        SugarCursorWrapper cursor = querySugars(
                SugarDbSchema.SugarTable.Cols.UUID + " = ?",
                new String[] { id.toString() });
        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getSugar();
        } finally{
            cursor.close();
        }
    }

    public void updateSugar(Sugar sugar){
        String uuidString = sugar.getId().toString();
        ContentValues values = getContentValues(sugar);
        mDatabase.update(SugarDbSchema.SugarTable.NAME, values,
                SugarDbSchema.SugarTable.Cols.UUID + " = ?",
                new String[] { uuidString});
    }

    public void add(Sugar sugar){
        ContentValues values = getContentValues(sugar);
        mDatabase.insert(SugarDbSchema.SugarTable.NAME, null, values);
    }

    public void delete(UUID id){
        mDatabase.delete(SugarDbSchema.SugarTable.NAME,
                SugarDbSchema.SugarTable.Cols.UUID + " = ?",
                new String[] { id.toString()});
    }

    private SugarCursorWrapper querySugars(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                SugarDbSchema.SugarTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new SugarCursorWrapper(cursor);
    }
}
