package edu.arizona.uas.wesrodgers.bloodglucosemonitor.database;

public class SugarDbSchema {
    public static final class SugarTable{
        public static final String NAME="sugars";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String DATE = "date";
            public static final String FASTING = "fasting";
            public static final String BREAKFAST = "breakfast";
            public static final String LUNCH = "lunch";
            public static final String DINNER = "dinner";
            public static final String NOTES = "notes";
        }
    }
}
