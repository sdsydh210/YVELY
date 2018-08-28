package com.example.sdsyd.yvely.Token;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.simple.JSONObject;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.example.sdsyd.yvely.R;

public class MyTokenActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeLayout;

    WalletCreate wc = new WalletCreate();

    String url = "https://ropsten.infura.io/v3/b9a69556b82f4341b5d2a6ca24774632";

    Web3j web3 = Web3jFactory.build(new HttpService(url));

    String smartcontract = "0xb19d2702028b543a82f8923c7799a0e77a71c064";
    String passwordwallet = "1234";

    File DataDir;

//    TextView ethaddress, ethbalance, tokenname, tokensymbol, tokensupply, tokenaddress, tokenbalance, tokensymbolbalance;
    TextView ethaddress, ethbalance, tokenname, tokenbalance, tokensymbolbalance;
//    TextView tv_gas_limit, tv_gas_price, tv_fee;
    EditText sendtoaddress, sendtokenvalue, sendethervalue;

//    ImageView qr_small, qr_big;

    BigInteger GasPrice, GasLimit;

    final Context context = this;

//    IntentIntegrator qrScan;

//    @Override
//    public void onRefresh() {
//
//        wc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        swipeLayout.setRefreshing(false);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_token);

//        swipeLayout = findViewById(R.id.TokenSwipeRefresh);
//        swipeLayout.setOnRefreshListener(this);

        ethaddress =  findViewById(R.id.ethaddress); // Your Ether Address
        ethbalance =  findViewById(R.id.ethbalance); // Your Ether Balance

        tokenname =  findViewById(R.id.tokenname); // Token Name
//        tokensymbol =  findViewById(R.id.tokensymbol); // Token Symbol
////        tokensupply =  findViewById(R.id.tokensupply); // Token Supply
////        tokenaddress =  findViewById(R.id.tokenaddress); // Token Address
        tokenbalance =  findViewById(R.id.tokenbalance); // Token Balance
        tokensymbolbalance =  findViewById(R.id.tokensymbolbalance);

        sendtoaddress =  findViewById(R.id.sendtoaddress); // Address for sending ether or token

        sendtokenvalue =  findViewById(R.id.SendTokenValue); // Ammount token for sending
//        sendethervalue =  findViewById(R.id.SendEthValue); // Ammount ether for sending

//        qr_small = (ImageView)findViewById(R.id.qr_small);

//        qrScan = new IntentIntegrator(this);

//        tv_gas_limit =  findViewById(R.id.tv_gas_limit);
//        tv_gas_price =  findViewById(R.id.tv_gas_price);
//        tv_fee =  findViewById(R.id.tv_fee);

//        final SeekBar sb_gas_limit = (SeekBar) findViewById(R.id.sb_gas_limit);
//        sb_gas_limit.setOnSeekBarChangeListener(seekBarChangeListenerGL);
//        final SeekBar sb_gas_price = (SeekBar) findViewById(R.id.sb_gas_price);
//        sb_gas_price.setOnSeekBarChangeListener(seekBarChangeListenerGP);

        GetFee();

        /**
         * Get the full path to the directory with the keys
         */
        DataDir = this.getExternalFilesDir("/keys/");
        File KeyDir = new File(this.DataDir.getAbsolutePath());

        /**
         * Check whether there are purses
         */
        File[] listfiles = KeyDir.listFiles();
        if (listfiles.length == 0 ) {
            /**
             * If the directory file of the wallet, add the wallet
             */
            try {
                String fileName = WalletUtils.generateNewWalletFile(passwordwallet, DataDir, false);

                System.out.println("FileName: " + DataDir.toString() + fileName);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        } else {
            /**
             * If the wallet is created, start the thread
             */
            wc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    ///////////////////// QR Generation //////////////////////
    /**
     * QR Generation Ether Address
     */
//    public Bitmap QRGen(String Value, int Width, int Heigth) {
//        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
//        Bitmap bitmap = null;
//        try {
//            BitMatrix bitMatrix = multiFormatWriter.encode(Value, BarcodeFormat.DATA_MATRIX.QR_CODE, Width, Heigth);
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            bitmap = barcodeEncoder.createBitmap(bitMatrix);
//            return bitmap;
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
    ////////////////// END QR Generation ////////////////////

    ///////////////////// QR SCAN ///////////////////////////
    /**
     * QR scan Ether Address
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                sendtoaddress.setText(result.getContents());
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    //////////////////// END QR SCAN ////////////////////////

    /////////////////// SeekBar Listener ////////////////////
    /**
     * SeekBar Слушатель
     * SeekBar Listener
     */
//    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerGL = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            GetGasLimit(String.valueOf(seekBar.getProgress()*1000+21000));
//        }
//        @Override public void onStartTrackingTouch(SeekBar seekBar) { }
//        @Override public void onStopTrackingTouch(SeekBar seekBar) { }
//    };
//    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerGP = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            GetGasPrice(String.valueOf(seekBar.getProgress()+4));
//        }
//        @Override public void onStartTrackingTouch(SeekBar seekBar) { }
//        @Override public void onStopTrackingTouch(SeekBar seekBar) { }
//    };
    ///////////////// END SeekBar Listener /////////////////

    ///////////////////// Gas View /////////////////////////

    /**
     * The value is assigned to the visual elements
     * @param value Value Gas Limit and Gas Price
     */
//    public void GetGasLimit(String value) {
//        tv_gas_limit.setText(value);
//        GetFee();
//    }
//    public void GetGasPrice(String value) {
//        tv_gas_price.setText(value);
//        GetFee();
//    }
    /////////////////////////////////////////////////////////////////

    /////////////////////////// Get Fee /////////////////////////////

    /**
     * The value GazLimit and GasPrice converteres in BigInteger and prizhivaetsya global variables
     *
     * calculate the fee for miners
     */

    public void GetFee(){
//        GasPrice = Convert.toWei(tv_gas_price.getText().toString(),Convert.Unit.GWEI).toBigInteger();
        GasPrice = Contract.GAS_PRICE;
//        GasLimit = BigInteger.valueOf(Integer.valueOf(String.valueOf(tv_gas_limit.getText())));
        GasLimit = Contract.GAS_LIMIT;

        // fee
        BigDecimal fee = BigDecimal.valueOf(GasPrice.doubleValue()*GasLimit.doubleValue());
        BigDecimal feeresult = Convert.fromWei(fee.toString(),Convert.Unit.ETHER);
//        tv_fee.setText(feeresult.toPlainString() + " ETH");
    }
    ///////////////////////// End Get Fee ///////////////////////////

    /////////////////////// On Click /////////////////////////
    /**
     * Start executing thread for sending Ether or sending Token
     */
    public void onClick(View view) {
        SendingToken st = new SendingToken();
//        SendingEther se = new SendingEther();
        switch (view.getId()) {
//            case R.id.SendEther:
//                se.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                break;
            case R.id.SendToken:
                st.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
//            case R.id.qr_small:
//                final Dialog dialog = new Dialog(context);
//                dialog.setContentView(R.layout.qr_view);
//                qr_big = (ImageView) dialog.findViewById(R.id.qr_big);
//                qr_big.setImageBitmap(QRGen(ethaddress.getText().toString(), 600, 600));
//                dialog.show();
//                break;
//            case R.id.qrScan:
//                qrScan.setOrientationLocked(false);
//                qrScan.setBarcodeImageEnabled(true);
//                qrScan.initiateScan();
//                break;
        }

    }
    /////////////////////// end on click /////////////////////

    ///////////////////// Create and Load Wallet /////////////////
    public class WalletCreate extends AsyncTask<Void, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            /**
             // Get list files in folder
             */
            File KeyDir = new File(DataDir.getAbsolutePath());
            File[] listfiles = KeyDir.listFiles();
            File file = new File(String.valueOf(listfiles[0]));
            try {
                /**
                 // Upload the wallet file and get the address
                 */
                Credentials credentials = WalletUtils.loadCredentials(passwordwallet, file);
                String address = credentials.getAddress();
                System.out.println("Eth Address: " + address);

                /**
                 // Get balance Ethereum
                 */
                EthGetBalance etherbalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
                String ethbalance = Convert.fromWei(String.valueOf(etherbalance.getBalance()), Convert.Unit.ETHER).toString();
                System.out.println("Eth Balance: " + ethbalance);

                /**
                 // Download Token
                 */
                TokenERC20 token = TokenERC20.load(smartcontract, web3, credentials, GasPrice, GasLimit);

                /**
                 // Get the name of the token
                 */
                String tokenname = token.name().send();
                System.out.println("Token Name: " + tokenname);

                /**
                 // Get Symbol marking token
                 */
                String tokensymbol = token.symbol().send();
                System.out.println("Symbol Token: " + tokensymbol);

                /**
                 // Получаем адрес Токена
                 // Get The Address Token
                 */
                String tokenaddress = token.getContractAddress();
                System.out.println("Address Token: " + tokenaddress);

                /**
                 // Get the total amount of issued tokens
                 */
                BigInteger totalSupply = token.totalSupply().send();
                System.out.println("Supply Token: "+totalSupply.toString());

                /**
                 // Receive the Balance of Tokens in the wallet
                 */
                BigInteger tokenbalance = token.balanceOf(address).send();
                System.out.println("Balance Token: "+ tokenbalance.toString());

                JSONObject result = new JSONObject();
                result.put("ethaddress",address);
                result.put("ethbalance", ethbalance);
                result.put("tokenbalance", tokenbalance.toString());
                result.put("tokenname", tokenname);
                result.put("tokensymbol", tokensymbol);
                result.put("tokenaddress",tokenaddress);
                result.put("tokensupply", totalSupply.toString());
                return result;
            } catch (Exception ex) {System.out.println("ERROR:" + ex);}

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (result != null ){
                ethaddress.setText(result.get("ethaddress").toString());
                ethbalance.setText(result.get("ethbalance").toString());
                tokenname.setText(result.get("tokenname").toString());
//                tokensymbol.setText(result.get("tokensymbol").toString());
//                tokensupply.setText(result.get("tokensupply").toString());
//                tokenaddress.setText(result.get("tokenaddress").toString());
                tokenbalance.setText(result.get("tokenbalance").toString());
                tokensymbolbalance.setText(" "+result.get("tokensymbol").toString());

//                qr_small.setImageBitmap(QRGen(result.get("ethaddress").toString(), 200, 200));
            }
            else{
                System.out.println("Error!!!");
            }

        }
    }
    ////////////////// End create and load wallet ////////////////

    ///////////////////////// Sending Tokens /////////////////////
    public class SendingToken extends AsyncTask<Void, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... param) {

            /**
             // Получаем список файлов в каталоге
             // Get list files in folder
             */
            File KeyDir = new File(DataDir.getAbsolutePath());
            File[] listfiles = KeyDir.listFiles();
            File file = new File(String.valueOf(listfiles[0]));

            try {
                /**
                 // Загружаем файл кошелька и получаем адрес
                 // Upload the wallet file and get the address
                 */
                Credentials credentials = WalletUtils.loadCredentials(passwordwallet, file);
                String address = credentials.getAddress();
                System.out.println("Eth Address: " + address);

                /**
                 * Загружаем Токен
                 * Load Token
                 */
                TokenERC20 token = TokenERC20.load(smartcontract, web3, credentials, GasPrice, GasLimit);

                String status = null;
                String balance = null;

                /**
                 * Конвертируем сумму токенов в BigInteger и отправляем на указанные адрес
                 * Convert the amount of tokens to BigInteger and send to the specified address
                 */
                BigInteger sendvalue = BigInteger.valueOf(Long.parseLong(String.valueOf(sendtokenvalue.getText())));
                status = token.transfer(String.valueOf(sendtoaddress.getText()), sendvalue).send().getTransactionHash();
                Log.e("please", status.toString());
                /**
                 * Renew Token balance
                 */
                BigInteger tokenbalance = token.balanceOf(address).send();
                System.out.println("Balance Token: "+ tokenbalance.toString());
                balance = tokenbalance.toString();

                /**
                 * Returned from thread, transaction Status and Token balance
                 */
                JSONObject result = new JSONObject();
                result.put("status",status);
                result.put("balance",balance);

                return result;
            } catch (Exception ex) {System.out.println("ERROR:" + ex);}

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (result != null) {
                tokenbalance.setText(result.get("balance").toString());
                Toast toast = Toast.makeText(getApplicationContext(),result.get("status").toString(), Toast.LENGTH_LONG);
                toast.show();
            } else {System.out.println();}
        }
    }
    /////////////////////// End Sending Tokens ///////////////////

    ///////////////////////// Sending Ether //////////////////////
    public class SendingEther  extends AsyncTask<Void, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(Void... param) {

            /**
             // Получаем список файлов в каталоге
             // Get list files in folder
             */
            File KeyDir = new File(DataDir.getAbsolutePath());
            File[] listfiles = KeyDir.listFiles();
            File file = new File(String.valueOf(listfiles[0]));

            try {
                /**
                 // Загружаем файл кошелька и получаем адрес
                 // Upload the wallet file and get the address
                 */
                Credentials credentials = WalletUtils.loadCredentials(passwordwallet, file);
                String address = credentials.getAddress();
                System.out.println("Eth Address: " + address);

                /**
                 * Получаем счетчик транзакций
                 * Get count transaction
                 */
                EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
                BigInteger nonce = ethGetTransactionCount.getTransactionCount();

                /**
                 * Convert ammount ether to BigInteger
                 */
                BigInteger value = Convert.toWei(String.valueOf(sendethervalue.getText()), Convert.Unit.ETHER).toBigInteger();

                /**
                 * Транзакция
                 * Transaction
                 */
                RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(nonce, GasPrice, GasLimit, String.valueOf(sendtoaddress.getText()), value);
                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                String hexValue = "0x"+ Hex.toHexString(signedMessage);
                EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue.toString()).sendAsync().get();

                /**
                 * Get Transaction Error and Hash
                 */
                System.out.println("Error: "+ ethSendTransaction.getError());
                System.out.println("Transaction: " + ethSendTransaction.getTransactionHash());

                /**
                 * Возвращаем из потока, Адрес и Хэш транзакции
                 * Returned from thread, Ether Address and transaction hash
                 */
                JSONObject JsonResult = new JSONObject();
                JsonResult.put("Address", address);
                JsonResult.put("TransactionHash", ethSendTransaction.getTransactionHash());

                return JsonResult;

            }catch (Exception ex) {ex.printStackTrace();}
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            try {
                /**
                 * Получаем баланс Ethereum
                 * Get balance Ethereum
                 */
                EthGetBalance etherbalance = web3.ethGetBalance(result.get("Address").toString(), DefaultBlockParameterName.LATEST).sendAsync().get();
                String ethbalanceafter = Convert.fromWei(String.valueOf(etherbalance.getBalance()), Convert.Unit.ETHER).toString();
                System.out.println("Eth Balance: " + ethbalanceafter);

                ethbalance.setText(ethbalanceafter);
            } catch(Exception ex) {System.out.println(ex);}

            Toast toast = Toast.makeText(getApplicationContext(),result.get("TransactionHash").toString(), Toast.LENGTH_LONG);
            toast.show();
        }

    }

    }

