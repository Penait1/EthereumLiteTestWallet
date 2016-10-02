package com.example.calvin.nlcargochain.model;

import org.ethereum.geth.AccountManager;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Context;
import org.ethereum.geth.Enodes;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;

import java.io.File;

/**
 * Created by Calvin on 2-10-2016.
 */

public class Wallet {
    private Node node;
    private AccountManager accountManager;
    private EthereumClient ethereumClient;
    private Context context;

    /**
     * Ethereum has a netwerk id, ID 2 idicates that it is the test network.
     */
    private final static int TESTNET_NETWORK_ID = 2;

    /**
     * @param fileDirectory The root directory where the app is installed.
     */
    public Wallet(File fileDirectory) {
        try {
            context = new Context();
            node = Geth.newNode(fileDirectory + "/.ethereum", createTestnetConfiguration());
            node.start();
            ethereumClient = node.getEthereumClient();

            //Creating new private/public keypair: ONLY if there is no account yet,
            accountManager = new AccountManager(fileDirectory.toString(), Geth.LightScryptN, Geth.LightScryptP);
            if (accountManager.getAccounts().size() < 1) {
                accountManager.newAccount("test");
            }

            handleIncommingHeads();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an address at given position.
     *
     * @param position Which address nummer is it in the getAccounts array
     * @return The address
     */
    public String getAccountAddress(long position) {
        try {
            return this.accountManager.getAccounts().get(position).getAddress().getHex();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the balance for the account position that is provided.
     * @param position Which address nummer is it in the getAccounts array
     * @return The Balance for the given account position
     */
    public BigInt getBalanceForAccount(long position) {
        try {
            return ethereumClient.getBalanceAt(context, accountManager.getAccounts().get(position).getAddress(), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * To connect with the testnetwork instead of Homebrew we need to create a custom config.
     * @return NodeConfig
     */
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

    private void handleIncommingHeads() {
        NewHeadHandler handler = new NewHeadHandler() {
            @Override
            public void onError(String error) {
            }


            @Override
            public void onNewHead(final Header header) {
            }
        };

        try {
            ethereumClient.subscribeNewHead(context, handler, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
