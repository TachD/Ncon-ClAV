package ncon.barsu.edu.client;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAuth:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName("192.168.43.47"), 10001);

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
                            Toast.makeText(MainActivity.this, Ex.getMessage(), Toast.LENGTH_SHORT).show();
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

    private String getEncryptedString(String SourceString) throws NoSuchAlgorithmException {
        final MessageDigest MD = MessageDigest.getInstance("SHA-256");

        MD.reset();
        MD.update(SourceString.getBytes(Charset.forName("UTF8")));

        return String.format("%064x", new BigInteger(1, MD.digest()));
    }

    private Bundle Auth(ObjectOutputStream OS, ObjectInputStream IS) {
        String LoginData;

        try {
            OS.writeObject(editNick.getText().toString());

            OS.writeObject(getEncryptedString(editPass.getText().toString()));

            LoginData = IS.readObject().toString();
        } catch (Exception Ex) {
            Toast.makeText(this, Ex.getMessage(), Toast.LENGTH_SHORT);
            return null;
        }

        Bundle AuthBundle = null;

            if (LoginData.equals("0"))
                Toast.makeText(this, "Uncorrected Password and/or Login!", Toast.LENGTH_SHORT);
            else {
                AuthBundle = new Bundle();

                AuthBundle.putString("Nickname", LoginData);
                try {
                    AuthBundle.putString("Email", IS.readObject().toString());
                    AuthBundle.putString("FName", IS.readObject().toString());
                    AuthBundle.putString("DayOfBirthday", IS.readObject().toString());
                    AuthBundle.putString("LName", IS.readObject().toString());
                } catch (Exception Ex) {
                    Toast.makeText(this, Ex.getMessage(), Toast.LENGTH_SHORT);
                    return  null;
                }
            }

            return AuthBundle;
    }
}
