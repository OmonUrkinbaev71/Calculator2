package com.example.calculator2;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class TaxCalculatorApplication extends Application {
    private double totalPrincipalDeposited = 0.0;
    private double totalInterestReceived = 0.0;
    private static final String DB_NAME = "db_tax_stats";
    private static final int DB_VERSION = 1;
    private SQLiteOpenHelper helper;

    public void addDeposit(double deposit){
        totalPrincipalDeposited += deposit;
    }

    public void addInterest(double interest){
        totalInterestReceived += interest;
    }

    public void addTransaction(double deposit, double interest){
        //inserting row in the table for values (deposit, interest)
        SQLiteDatabase db = helper.getWritableDatabase();
        //SQL Query: INSERT INTO tablename (column1, column2) VALUES ( Value1, Value2)
        db.execSQL("INSERT INTO tbl_stats (principal_deposited, interest_received) VALUES ("
        + deposit + "," + interest + ")");


    }

    public double getDeposit(){
        //return totalPrincipalDeposited;
        SQLiteDatabase db = helper.getReadableDatabase();
        //to get the sum of 1st column
        Cursor cursor = db.rawQuery("SELECT SUM(principal_deposited) AS TotalPrincipal FROM tbl_stats", null);
        cursor.moveToFirst(); //move to 1st column => 0th index
        double returnDeposit = cursor.getDouble(0);

        return returnDeposit;

    }

    public double getInterest(){
        //return totalInterestReceived;
        //TODO: change this code as per getDeposit method
        SQLiteDatabase db = helper.getReadableDatabase();
        //to get the sum of 1st column
        Cursor cursor = db.rawQuery("SELECT SUM(interest_received) FROM tbl_stats", null);
        cursor.moveToFirst(); //move to 1st column => 0th index
        double returnInterest = cursor.getDouble(0);

        return returnInterest;
    }

    public void resetStatsTable(){
        Toast.makeText(getApplicationContext(), "Reset button clicked", Toast.LENGTH_LONG).show();
        SQLiteDatabase database = helper.getWritableDatabase();
        //clearing off the contents
        database.execSQL("DELETE FROM tbl_stats;");
        Toast.makeText(getApplicationContext(), "database table deleted", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCreate() {
        super.onCreate();

        helper = new SQLiteOpenHelper(this, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_stats (" +
                        "principal_deposited REAL, interest_received REAL)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                //No-op
            }
        };

    }
}
