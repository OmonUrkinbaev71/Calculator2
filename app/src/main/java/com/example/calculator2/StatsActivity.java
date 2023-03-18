package com.example.calculator2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {

    private TextView textViewPrincipalDeposited;
    private TextView textViewInterestReceived;
    private Button buttonResetStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        textViewPrincipalDeposited = findViewById(R.id.textViewPrincipal);
        textViewInterestReceived = findViewById(R.id.textViewInterest);
        buttonResetStats = findViewById(R.id.buttonResetStats);


        double deposit = ((TaxCalculatorApplication)getApplication()).getDeposit();
        textViewPrincipalDeposited.setText(""+deposit);

        textViewInterestReceived.setText(""+ ((TaxCalculatorApplication)getApplication()).getInterest());

        buttonResetStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //we have to call the resetStatsTable method from Application Class
                ((TaxCalculatorApplication)getApplication()).resetStatsTable();
                //to update the content of the textviews on the layout
                textViewPrincipalDeposited.setText(""+((TaxCalculatorApplication)getApplication()).getDeposit());
                textViewInterestReceived.setText(""+ ((TaxCalculatorApplication)getApplication()).getInterest());

            }
        });

    }
}