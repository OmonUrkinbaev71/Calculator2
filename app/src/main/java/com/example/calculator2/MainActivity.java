package com.example.calculator2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button btnDecrement;
    private Button btnIncrement;
    private Button btnReset;
    private Button btnCalculate;
    private EditText editRateValue;
    private EditText editPrinciple;
    private EditText editTotal;
    private SharedPreferences sharedPreferences;
    private static double DEFAULT_RATE = 10.0;
    private ImageView imageViewForSmileyFace;
    private Timer timerForSmileyFace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDecrement = findViewById(R.id.buttonDecrement);
        btnIncrement = findViewById(R.id.buttonIncrement);
        btnReset = findViewById(R.id.buttonReset);
        btnCalculate = findViewById(R.id.buttonCalculate);

        editPrinciple = findViewById(R.id.editPrinciple);
        editRateValue = findViewById(R.id.editRate);
        editTotal = findViewById(R.id.editTotal);

        imageViewForSmileyFace = findViewById(R.id.imageViewSmileyFace);

        //Toast.makeText(getApplicationContext(), "New Toast created from \" Main Activity \".", Toast.LENGTH_LONG).show();
        Snackbar.make(findViewById(R.id.MainLayout), "Snackbar created...", Snackbar.LENGTH_LONG).show();

        View.OnClickListener btnIncDecListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.buttonDecrement:
                        // minus 1 from edit text value "10"
                        //"10" --> 10 Integer.parseInt(stringValue)
                        //subtracted 1 from it
                        //converted that number to String using append with blank string
                        //setText back to editText
                        editRateValue.setText("" + (Double.parseDouble(editRateValue.getText().toString()) -1));
                        break;
                    case R.id.buttonIncrement:
                        // plus 1 from edit text value "10"
                        //"10" --> 10 Integer.parseInt(stringValue)
                        //added 1 from it
                        //converted that number to String using append with blank string
                        //setText back to editText
                        editRateValue.setText("" + (Double.parseDouble(editRateValue.getText().toString()) +1));
                        break;
                    default:
                        break;
                }
            }
        };
        btnIncrement.setOnClickListener(btnIncDecListener);
        btnDecrement.setOnClickListener(btnIncDecListener);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reset the values for principle, total and rate of interest
                performReset(false);
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calculate the Simple Interest
                //editTotal.setText(String.valueOf(performTotal()));

                //store our principal deposited
                double deposit = Double.parseDouble(editPrinciple.getText().toString());
//                ((TaxCalculatorApplication)getApplication()).addDeposit(deposit);
//
                double interest = deposit * Double.parseDouble(editRateValue.getText().toString())/100;
//                ((TaxCalculatorApplication)getApplication()).addInterest(interest);

                //TODO: add transaction using INSERT SQL Statement to get row in the table
                ((TaxCalculatorApplication)getApplication()).addTransaction(deposit, interest);

                //change the image from smiley face to check
                imageViewForSmileyFace.setImageResource(R.drawable.ic_baseline_check_24);
                //simple animation - fade in effect from 0f to 1f - here you don't have listener set up
                imageViewForSmileyFace.setAlpha(0f);
                //imageViewForSmileyFace.animate().alpha(1f).setDuration(2000);

                //setting up listener with animation end
                imageViewForSmileyFace.animate().alpha(1f).setDuration(2000).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        editTotal.setText(String.valueOf(performTotal()));
                    }
                });

                //TODO : perform refresh/reset after 5 seconds
                performReset(true);
            }
        });

        sharedPreferences = getSharedPreferences("LastInput", MODE_PRIVATE);
    }

    private void performReset(boolean doReset){
//        editPrinciple.setText("");
//        editTotal.setText("");
//        editRateValue.setText(String.valueOf(DEFAULT_RATE));
        // or editRateValue.setText(""+DEFAULT_RATE);

        if(doReset){
            //TODO: wait for 5 seconds before actually resetting the values/Image
            //if the user re-enters principal, rate after clicking on Calculate during the 5 second wait,
            // the earlier timer should get canceled
            if(timerForSmileyFace != null){
                timerForSmileyFace.cancel();
            }
            timerForSmileyFace = new Timer(true);
            timerForSmileyFace.schedule(new TimerTask() {
                @Override
                public void run() { //belongs to the background thread not main ui thread
                    //to run the timer once, once it runs we set it to null and cancel the transaction
                    timerForSmileyFace.cancel();
                    timerForSmileyFace = null;
                    //call the main UI thread
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() { //belongs to the main UI thread
                             editPrinciple.setText("");
                             editTotal.setText("");
                             editRateValue.setText(String.valueOf(DEFAULT_RATE));
                             imageViewForSmileyFace.setImageResource(R.drawable.ic_baseline_add_reaction_24);
                         }
                     });
                }
            }, 5000);

        }else {
            //normal scenario - don't wait for 5 seconds
            editPrinciple.setText("");
            editTotal.setText("");
            editRateValue.setText(String.valueOf(DEFAULT_RATE));
            imageViewForSmileyFace.setImageResource(R.drawable.ic_baseline_add_reaction_24);

        }

    }

    private double performTotal(){

        //Amount = Principal + Principal*Rate/100
        //amount = Principal * (1 + Rate/100)
        Double amount = Double.parseDouble(editPrinciple.getText().toString())*
                (1 + Double.parseDouble(editRateValue.getText().toString())/100);
        return amount;
    }

    @Override
    protected void onPause() {

        //Toast.makeText(this, "onPause is called", Toast.LENGTH_SHORT).show();

        //save preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("principle", editPrinciple.getText().toString());
        editor.putString("rate", editRateValue.getText().toString());
        editor.putString("total", editTotal.getText().toString());
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {

        //Toast.makeText(this, "onResume is called", Toast.LENGTH_SHORT).show();

        super.onResume();

        //get values from shared preferences
        editPrinciple.setText(sharedPreferences.getString("principle",""));
        editRateValue.setText(sharedPreferences.getString("rate", String.valueOf(DEFAULT_RATE)));
        editTotal.setText(sharedPreferences.getString("total",""));
    }

    @Override
    protected void onStop() {

        //this is called when user has deactivated the app or moved outside the app by pressing home button
        startService(new Intent(getApplicationContext(), NotificationService.class));
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu with the layout calculator_menu
        getMenuInflater().inflate(R.menu.calculator_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_reset:
                //reset the values
                performReset(false);
                break;

            case R.id.menu_settings:
                //call Settings Activity
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_stats:
                //call Settings Activity
                Intent i = new Intent(this, StatsActivity.class);
                startActivity(i);
            default:
                break;
        }

        return true;
    }
}