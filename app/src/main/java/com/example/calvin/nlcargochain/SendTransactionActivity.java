package com.example.calvin.nlcargochain;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendTransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_transaction);

        final Button sendButton = (Button) findViewById(R.id.sendButton);
        final EditText addressTextField = (EditText) findViewById(R.id.addressTextField);
        final EditText amountTextField = (EditText) findViewById(R.id.amountTextField);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }
}
