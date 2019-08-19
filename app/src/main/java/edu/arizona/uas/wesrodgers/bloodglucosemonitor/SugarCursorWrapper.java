package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import edu.arizona.uas.wesrodgers.bloodglucosemonitor.database.SugarDbSchema;

class SugarCursorWrapper extends CursorWrapper {
    public SugarCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Sugar getSugar(){
        String uuidString = getString(getColumnIndex(SugarDbSchema.SugarTable.Cols.UUID));
        long date = getLong(getColumnIndex(SugarDbSchema.SugarTable.Cols.DATE));
        int fasting = getInt(getColumnIndex(SugarDbSchema.SugarTable.Cols.FASTING));
        int breakfast = getInt(getColumnIndex(SugarDbSchema.SugarTable.Cols.BREAKFAST));
        int lunch = getInt(getColumnIndex(SugarDbSchema.SugarTable.Cols.LUNCH));
        int dinner = getInt(getColumnIndex(SugarDbSchema.SugarTable.Cols.DINNER));
        String notes = getString(getColumnIndex(SugarDbSchema.SugarTable.Cols.NOTES));

        Sugar sugar = new Sugar(UUID.fromString(uuidString));
        sugar.setDate(new Date(date));
        sugar.setFasting(fasting);
        sugar.setBreakfast(breakfast);
        sugar.setLunch(lunch);
        sugar.setDinner(dinner);
        sugar.setNote(notes);
        boolean norm = sugarStatus(fasting, true) && sugarStatus(breakfast, false)
                && sugarStatus(lunch, false) && sugarStatus(dinner, false);
        int count = 0;
        if(fasting > 0) count++;
        if(breakfast > 0) count++;
        if(lunch > 0) count++;
        if(dinner > 0) count++;
        int mSugar;
        if(count > 0) mSugar = (fasting + breakfast + lunch + dinner)/count;
        else mSugar = 0;
        sugar.setSugar(mSugar);
        sugar.setNormal(norm);

        return sugar;
    }

    private boolean sugarStatus(int i, boolean fasted){
        if(i < 70) return false;
        if(fasted && i <= 99) return true;
        if(fasted && i > 99) return false;
        if(i < 140) return true;
        return false;
    }
}
