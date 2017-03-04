package ncon.barsu.edu.client;

import android.content.Intent;
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName(MainActivity.Host), 10001);

                            Socket CSock = new Socket();

                            CSock.connect(SockAddr, 0);

                            ObjectOutputStream OS = new ObjectOutputStream(CSock.getOutputStream());

                            OS.writeObject(-3);

                            ObjectInputStream IS = new ObjectInputStream(CSock.getInputStream());

                            Registration(OS, IS);

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

    private void Registration(ObjectOutputStream OS, ObjectInputStream IS) {
        Bundle RegBundle = getIntent().getExtras();
        String validString = RegBundle.getString("ValidCode");

        try {
            if (!validString.equals(MainActivity.getEncryptedString(editValidCode.getText().toString()))) {
                Toast.makeText(getApplicationContext(), "Incorrect code!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NoSuchAlgorithmException NSAEx) {
            System.out.println("Validation error!");
            return;
        }

        try {
            String CryptPass;

            CryptPass = RegBundle.getString("Password");

            OS.writeObject(RegBundle.getString("Nickname"));
            OS.writeObject(CryptPass);
            OS.writeObject(RegBundle.getString("FName"));
            OS.writeObject(RegBundle.getString("LName"));
            OS.writeObject(RegBundle.getString("Email"));
            OS.writeObject(RegBundle.getString("DayOfBirth"));

            String LoginData;

            try {
                LoginData = IS.readObject().toString();
            } catch (Exception Ex) {
                return;
            }

            if (Integer.valueOf(LoginData) == 0)
                Toast.makeText(getApplicationContext(), "Account not created!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_SHORT).show();

        } catch (IOException IOEx) {
            Toast.makeText(getApplicationContext(), IOEx.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
