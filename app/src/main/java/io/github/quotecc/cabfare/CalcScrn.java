package io.github.quotecc.cabfare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CalcScrn extends AppCompatActivity {

    TextView[] tv = new TextView[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_scrn);
        tv[0] = (TextView)findViewById(R.id.orig);
        tv[1] = (TextView)findViewById(R.id.dest);
        tv[2] = (TextView)findViewById(R.id.dist);
        tv[3] = (TextView)findViewById(R.id.tim);
        tv[4] = (TextView)findViewById(R.id.cost);
        String[] str = getIntent().getExtras().getStringArray("resStrings");
        double d = getIntent().getExtras().getDouble("rate");

        tv[0].setText("Starting Point: " + str[0]);
        tv[1].setText("Destination: "+ str[1]);
        tv[2].setText("Distance: " + str[2]);
        tv[3].setText("Time Required: "+ str[3]);
        str[2] = str[2].split(" ")[0];
        double e = (Integer.parseInt(str[2])*d)+3.00;
        tv[4].setText("Cost of Trip: $" + d+"/mile\nTotalling: $" + e );





    }
}
