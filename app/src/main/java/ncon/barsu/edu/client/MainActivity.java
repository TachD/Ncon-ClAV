package ncon.barsu.edu.client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
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
                try {
                    SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName("192.168.1.3"), 10001);

                    Socket CSock = new Socket();

                    CSock.connect(SockAddr, 0);

                    ObjectOutputStream OS = new ObjectOutputStream(CSock.getOutputStream());

                    OS.writeObject(-1);
                    //
                    Intent AuthIntent = new Intent("ncon.barsu.edu.client.account");
                    //
                    ObjectInputStream IS = new ObjectInputStream(CSock.getInputStream());

                    Auth(OS, IS, AuthIntent);

                    startActivity(AuthIntent);
                } catch (Exception Ex) {
                    Toast.makeText(this, Ex.getMessage(), Toast.LENGTH_SHORT);
                }
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

    private void Auth(ObjectOutputStream OS, ObjectInputStream IS, Intent AuthIntent) {
        try {
            OS.writeObject(editNick.getText().toString());

            OS.writeObject(getEncryptedString(editPass.getText().toString()));
        } catch (Exception Ex) {
            Toast.makeText(this, Ex.getMessage(), Toast.LENGTH_SHORT);
            return;
        }

        String LoginData;

        try {
            LoginData = IS.readObject().toString();
        } catch (Exception Ex) {
            Toast.makeText(this, Ex.getMessage(), Toast.LENGTH_SHORT);
            return;
        }

            if (LoginData.equals("0"))
                Toast.makeText(this, "Uncorrected Password and/or Login!", Toast.LENGTH_SHORT);
            else
                try {
                    AuthIntent.putExtra("Nickname", LoginData);
                    AuthIntent.putExtra("Email", IS.readObject().toString());
                    AuthIntent.putExtra("FName", IS.readObject().toString());
                    AuthIntent.putExtra("DayOfBirthday", IS.readObject().toString());
                    AuthIntent.putExtra("LName", IS.readObject().toString());

                } catch (Exception Ex) {
                    Toast.makeText(this, Ex.getMessage(), Toast.LENGTH_SHORT);
                }
    }
}
