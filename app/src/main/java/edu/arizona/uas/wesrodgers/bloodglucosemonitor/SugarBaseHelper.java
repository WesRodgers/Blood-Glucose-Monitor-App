package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.arizona.uas.wesrodgers.bloodglucosemonitor.database.SugarDbSchema;

public class SugarBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SugarBaseHelper";
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "sugarBase.db";

    public SugarBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + SugarDbSchema.SugarTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                SugarDbSchema.SugarTable.Cols.UUID + "," +
                SugarDbSchema.SugarTable.Cols.DATE + "," +
                SugarDbSchema.SugarTable.Cols.FASTING + "," +
                SugarDbSchema.SugarTable.Cols.BREAKFAST + "," +
                SugarDbSchema.SugarTable.Cols.LUNCH + "," +
                SugarDbSchema.SugarTable.Cols.DINNER + "," +
                SugarDbSchema.SugarTable.Cols.NOTES + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}
