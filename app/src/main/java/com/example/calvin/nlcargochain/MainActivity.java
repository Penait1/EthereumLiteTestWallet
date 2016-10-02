package com.example.calvin.nlcargochain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calvin.nlcargochain.model.Wallet;


public class MainActivity extends AppCompatActivity {
    private final static long FIRST_ACCOUNT = 0;
    private final static int TESTNET_NETWORK_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Ethereum Lite Wallet");
        final TextView addressText = (TextView) findViewById(R.id.addressText);
        final TextView balanceText = (TextView) findViewById(R.id.balanceText);
        final Button refreshButton = (Button) findViewById(R.id.refreshButton);
        final Button sendButton = (Button) findViewById(R.id.sendButton);

        //Event handler for the send transaction button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SendTransactionActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        final Wallet wallet = new Wallet(getFilesDir());
        try {
            addressText.append("Your address: " + wallet.getAccountAddress(FIRST_ACCOUNT));
            balanceText.setText("Balance:\n" + wallet.getBalanceForAccount(FIRST_ACCOUNT));

            //Event handler for refresh button
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        balanceText.setText("Balance:\n" + wallet.getBalanceForAccount(FIRST_ACCOUNT));
                        Toast toast = Toast.makeText(getApplicationContext(), "Refreshed!", Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
