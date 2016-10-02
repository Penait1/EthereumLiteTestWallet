package com.example.calvin.nlcargochain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ethereum.geth.Account;
import org.ethereum.geth.AccountManager;
import org.ethereum.geth.Accounts;
import org.ethereum.geth.ChainConfig;
import org.ethereum.geth.Context;
import org.ethereum.geth.Enode;
import org.ethereum.geth.Enodes;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;
import org.ethereum.geth.NodeInfo;


public class MainActivity extends AppCompatActivity {
    private Context context;
    private AccountManager accountManager;
    private EthereumClient ethereumClient;

    private final static int FIRST_ACCOUNT = 0;
    private final static int TESTNET_NETWORK_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Ethereum Lite Wallet");
        final TextView latestBlockInfoText = (TextView) findViewById(R.id.latestBlockInfoText);
        final TextView addressText = (TextView) findViewById(R.id.addressText);
        final TextView balanceText = (TextView) findViewById(R.id.balanceText);
        final Button refreshButton = (Button) findViewById(R.id.refreshButton);

        context = new Context();

        try {

            Node node = Geth.newNode(getFilesDir() + "/.ethereum", createTestnetConfiguration());
            node.start();

            ethereumClient = node.getEthereumClient();

            //Setting latest block to last block downloaded
            latestBlockInfoText.setText("Latest downloaded block: " + Long.toString(ethereumClient.getBlockByNumber(context, -1).getNumber()));

            //Creating new private/public keypair: ONLY if there is no account yet,
            accountManager = new AccountManager(this.getFilesDir().toString(), Geth.LightScryptN, Geth.LightScryptP);
            if (accountManager.getAccounts().size() < 1) {
                accountManager.newAccount("test");
            }

            addressText.append("Your address: " + accountManager.getAccounts().get(FIRST_ACCOUNT).getAddress().getHex());
            balanceText.setText("Balance:\n" + ethereumClient.getBalanceAt(context, accountManager.getAccounts().get(0).getAddress(), -1));
            System.out.println(accountManager.getAccounts().get(FIRST_ACCOUNT).getAddress().getHex());

            //Event handler for refresh button
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        balanceText.setText("Your balance is: " + (ethereumClient.getBalanceAt(context, accountManager.getAccounts().get(0).getAddress(), -1)));
                        Toast toast = Toast.makeText(getApplicationContext(), "Refreshed!", Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //Handler that handles incomming downloaded block headers
            NewHeadHandler handler = new NewHeadHandler() {
                @Override
                public void onError(String error) {
                }


                @Override
                public void onNewHead(final Header header) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            latestBlockInfoText.setText("Latest downloaded block: " + Long.toString(header.getNumber()));
                        }
                    });
                }
            };
            ethereumClient.subscribeNewHead(context, handler, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private NodeConfig createTestnetConfiguration() {
        Enodes nodes = new Enodes(1);
        NodeConfig config = new NodeConfig();

        try {
            nodes.set(0, Geth.newEnode("enode://e7544d59131271e89bcf6b14cd57323e15b0c9474edb463d4832e6fbc80722245e840997286a0d21a589b3e024def1e45095959a12d42219d1e419fe487fa767@50.112.52.169:30301"));
            config.setBootstrapNodes(nodes);
            config.setEthereumChainConfig(Geth.getTestnetChainConfig());
            config.setEthereumGenesis(Geth.getTestnetGenesis());
            config.setEthereumTestnetNonces(true);
            config.setEthereumNetworkID(TESTNET_NETWORK_ID);
            config.setMaxPeers(25);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }
}
