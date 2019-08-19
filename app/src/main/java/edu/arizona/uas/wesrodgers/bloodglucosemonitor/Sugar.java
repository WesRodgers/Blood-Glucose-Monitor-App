package edu.arizona.uas.wesrodgers.bloodglucosemonitor;

import java.util.Date;
import java.util.UUID;

/**
 * Serializable holding information about a specific date's sugar readings and any notes
 * @author Wes Rodgers
 */

class Sugar {

    private UUID mId;
    private int mSugar;
    private int fasting, breakfast, lunch, dinner;
    private String note;
    private Date mDate;
    private boolean normal;

    /**
     * Creates a new Sugar object with just a UUID field populated
     */
    public Sugar(){
        mId = UUID.randomUUID();
    }

    //this creates a new sugar based on the passed-in UUID
    public Sugar(UUID id){ mId = id; }

    /**
     * Creates a Sugar object based on information filled out by
     * the user in a DateSugar object
     * @param s a DateSugar object filled out by the user
     */
    public Sugar(DateSugar s){
        mId = s.id;
        fasting = s.fasting;
        breakfast = s.breakfast;
        lunch = s.lunch;
        dinner = s.dinner;
        int count = 0;
        if(s.fasting > 0) count++;
        if(s.breakfast > 0) count++;
        if(s.lunch > 0) count++;
        if(s.dinner > 0) count++;
        if(count > 0) mSugar = (fasting + breakfast + lunch + dinner)/count;
        else mSugar = 0;
        normal = s.fastingNormal && s.breakfastNormal && s.lunchNormal && s.dinnerNormal;
        mDate = s.mDate;
        note = s.noteString;
    }


    //getters and setters for Sugar fields
    public int getSugar() {
        return mSugar;
    }
    public void setFasting(int i){this.fasting = i;}
    public void setBreakfast(int i){this.breakfast = i;}
    public void setLunch(int i){this.lunch = i;}
    public void setDinner(int i){this.dinner = i;}
    public void setNote(String s){this.note = s;}
    public int getFasting(){return fasting;}
    public int getLunch(){return lunch;}
    public int getBreakfast(){return breakfast;}
    public int getDinner(){return dinner;}
    public String getNote(){return note;}
    public Date getDate() {
        return mDate;
    }
    public boolean isNormal() {
        return normal;
    }
    public void setDate(Date date){
        mDate = date;
    }
    public void setSugar(int sugar){
        mSugar = sugar;
    }
    public void setNormal(boolean normal){
        this.normal = normal;
    }
    public UUID getId() {
        return mId;
    }
}
