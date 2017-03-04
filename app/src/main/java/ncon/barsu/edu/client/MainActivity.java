package ncon.barsu.edu.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.math.BigInteger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import java.nio.charset.Charset;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    public static String Host = "192.168.1.3";
    private EditText editPass;
    private EditText editNick;

    private Button btnAuth;
    private Button btnSingin;
    private Button btnRecovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNick = (EditText) findViewById(R.id.editNick);
        editPass = (EditText) findViewById(R.id.editPass);

        btnAuth =     (Button) findViewById(R.id.btnAuth);
        btnAuth.setOnClickListener(this);

        btnSingin =   (Button) findViewById(R.id.btnSingin);
        btnSingin.setOnClickListener(this);

        btnRecovery = (Button) findViewById(R.id.btnRecovery);
        btnRecovery.setOnClickListener(this);
    }

    public void onStart() {
        super.onStart();

        String[] AccData = LoadAccountData();

        if (!"".equals(AccData[0]))
            btnAuth.callOnClick();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAuth:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName(Host), 10001);

                            Socket CSock = new Socket();

                            CSock.connect(SockAddr, 0);

                            ObjectOutputStream OS = new ObjectOutputStream(CSock.getOutputStream());

                            OS.writeObject(-1);


                            ObjectInputStream IS = new ObjectInputStream(CSock.getInputStream());

                            Bundle AuthBundle = Auth(OS, IS);

                            if (OS != null)
                                OS.close();

                            if (IS != null)
                            IS.close();

                            if (CSock != null)
                                CSock.close();

                            Intent AuthIntent = new Intent(MainActivity.this, AccountActivity.class);

                            AuthIntent.putExtras(AuthBundle);

                            startActivity(AuthIntent);

                        } catch (Exception Ex) {
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), Ex.getMessage(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();

                break;
            case R.id.btnSingin:
                startActivity(new Intent(this, RegActivity.class));
                break;
            case R.id.btnRecovery:
                startActivity(new Intent(this, RecoveryActivity.class));;
                break;
        }
    }

    public static String getEncryptedString(String SourceString) throws NoSuchAlgorithmException {
        final MessageDigest MD = MessageDigest.getInstance("SHA-256");

        MD.reset();
        MD.update(SourceString.getBytes(Charset.forName("UTF8")));

        return String.format("%064x", new BigInteger(1, MD.digest()));
    }

    private Bundle Auth(ObjectOutputStream OS, ObjectInputStream IS) {
        String LoginData;
        String PassData;

        String[] AccData = LoadAccountData();

        try {
            OS.writeObject(("".equals(AccData[0]))
                    ?editNick.getText().toString():
                    AccData[0]);

            PassData = ("".equals(AccData[1]))?
                    getEncryptedString(editPass.getText().toString()):
                    AccData[1];

            OS.writeObject(PassData);

            LoginData = IS.readObject().toString();
        } catch (Exception Ex) {
            Toast.makeText(getApplicationContext(), Ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }

        Bundle AuthBundle = null;

            if ("0".equals(LoginData)) {
                Looper.prepare();

                Toast.makeText(getApplicationContext(), "Uncorrected Password and/or Login!", Toast.LENGTH_SHORT).show();

                if (!"".equals(AccData[0])) {
                    SharedPreferences AccountDataPref = getSharedPreferences("AccData", MODE_PRIVATE);

                    AccountDataPref.edit().clear();
                }

                Looper.loop();
            }
            else {

                if ("".equals(AccData[0]))
                    SaveAccountData(LoginData, PassData);

                AuthBundle = new Bundle();

                AuthBundle.putString("Nickname", LoginData);
                try {
                    AuthBundle.putString("Email", IS.readObject().toString());
                    AuthBundle.putString("FName", IS.readObject().toString());
                    AuthBundle.putString("DayOfBirthday", IS.readObject().toString());
                    AuthBundle.putString("LName", IS.readObject().toString());
                } catch (Exception Ex) {
                    Toast.makeText(getApplicationContext(), Ex.getMessage(), Toast.LENGTH_SHORT).show();
                    return  null;
                }
            }

            return AuthBundle;
    }

    private void SaveAccountData(String Login, String Password) {
        SharedPreferences AccountDataPref = getSharedPreferences("AccData", MODE_PRIVATE);

        SharedPreferences.Editor editor = AccountDataPref.edit();

        editor.putString("Login", Login);
        editor.putString("Password", Password);
        editor.commit();
    }

    private String[] LoadAccountData() {
        SharedPreferences AccountDataPref = getSharedPreferences("AccData", MODE_PRIVATE);

        String[] AccData = {AccountDataPref.getString("Login", ""),
                            AccountDataPref.getString("Password", "")};

        return AccData;
    }
}
