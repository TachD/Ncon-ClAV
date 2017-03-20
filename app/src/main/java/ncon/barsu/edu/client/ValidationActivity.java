package ncon.barsu.edu.client;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class ValidationActivity extends AppCompatActivity {
    private EditText editValidCode;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        editValidCode = (EditText) findViewById(R.id.editValidCode);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(editValidCode.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter Valid-code", Toast.LENGTH_LONG).show();
                    return;
                }

                String validString = getIntent().getExtras().getString("ValidCode");
                try {
                    if (!validString.equals(MainActivity.getEncryptedString(editValidCode.getText().toString()))) {
                        Toast.makeText(getApplicationContext(), "Incorrect Valid-code!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NoSuchAlgorithmException NSAEx) {
                    System.out.println("Validation error!");
                    return;
                }


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName(MainActivity.Host), 10001);

                            Socket CSock = new Socket();

                            CSock.connect(SockAddr, 0);

                            ObjectOutputStream OS = new ObjectOutputStream(CSock.getOutputStream());

                            OS.writeObject(-3);

                            Crypto CryptoObj = new Crypto();
                            OS.writeObject(CryptoObj.genKey());

                            ObjectInputStream IS = new ObjectInputStream(CSock.getInputStream());

                            Registration(OS, IS, CryptoObj);

                            if (OS != null)
                                OS.close();

                            if (IS != null)
                                IS.close();

                            if (CSock != null)
                                CSock.close();

                        } catch (Exception Ex) {
                            Toast.makeText(getApplicationContext(), Ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        });
    }

    private void Registration(ObjectOutputStream OS, ObjectInputStream IS, Crypto CryptoObj) {
        Bundle RegBundle = getIntent().getExtras();

        try {
            String CryptPass;

            CryptPass = RegBundle.getString("Password");

            CryptoObj.sendEncryptString(OS, RegBundle.getString("Nickname"));
            CryptoObj.sendEncryptString(OS,CryptPass);
            CryptoObj.sendEncryptString(OS,RegBundle.getString("FName"));
            CryptoObj.sendEncryptString(OS,RegBundle.getString("LName"));
            CryptoObj.sendEncryptString(OS,RegBundle.getString("Email"));
            CryptoObj.sendEncryptString(OS,RegBundle.getString("DayOfBirth"));

            String LoginData;

            CryptoObj.setKey((Key) IS.readObject());

            try {
                LoginData = CryptoObj.readDecryptString(IS);
            } catch (Exception Ex) {
                return;
            }

            Looper.prepare();
                Toast.makeText(getApplicationContext(), (Integer.valueOf(LoginData) == 0)?
                        "Account not created!":"Account created!",
                        Toast.LENGTH_SHORT).show();
            Looper.loop();
        } catch (Exception Ex) {
            Toast.makeText(getApplicationContext(), Ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
