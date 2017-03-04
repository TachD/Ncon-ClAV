package ncon.barsu.edu.client;

import android.content.Intent;
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
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegActivity extends AppCompatActivity {
    private Button btnRegistration;
    private EditText editNick;
    private EditText editFName;
    private EditText editLName;
    private EditText editPass1;
    private EditText editPass2;
    private EditText editEmail;
    private EditText editDayOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        editNick = (EditText) findViewById(R.id.editNick);
        editFName = (EditText) findViewById(R.id.editFName);
        editLName = (EditText) findViewById(R.id.editLName);
        editPass1 = (EditText) findViewById(R.id.editPass1);
        editPass2 = (EditText) findViewById(R.id.editPass2);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editDayOfBirth = (EditText) findViewById(R.id.editDateofBirth);

        btnRegistration = (Button) findViewById(R.id.btnReg);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
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

                            OS.writeObject(-2);

                            ObjectInputStream IS = new ObjectInputStream(CSock.getInputStream());

                            Looper.prepare();
                            Bundle RegBundle = Validation(OS, IS); // if bundle is empty...
                            //Looper.loop();

                            if (RegBundle.isEmpty())
                                return;

                            if (OS != null)
                                OS.close();

                            if (IS != null)
                                IS.close();

                            if (CSock != null)
                                CSock.close();

                            Intent ValidIntent = new Intent(RegActivity.this, ValidationActivity.class);

                            ValidIntent.putExtras(RegBundle);

                            startActivity(ValidIntent);

                        } catch (Exception Ex) {
                            Toast.makeText(getApplicationContext(), Ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        });
    }

    private Bundle Validation(ObjectOutputStream OS, ObjectInputStream IS) {
        if (editNick.getText().toString().length() < 3) {
            Toast.makeText(getApplicationContext(), "Username is too short!", Toast.LENGTH_LONG).show();
            return Bundle.EMPTY;
        }

        if (!editPass1.getText().toString().equals(editPass2.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
            return Bundle.EMPTY;
        }

        if (editPass1.getText().toString().length() < 5) {
            Toast.makeText(getApplicationContext(), "Password is too short!", Toast.LENGTH_LONG).show();
            return Bundle.EMPTY;
        }

        Pattern EmailValiadtion =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher EmailMatcher = EmailValiadtion.matcher(editEmail.getText().toString());

        if (!EmailMatcher.find()){
            Toast.makeText(getApplicationContext(), "Uncorrected e-mail!", Toast.LENGTH_LONG).show();
            return Bundle.EMPTY;
        }

        if (editFName.getText().length() < 2) {
            Toast.makeText(getApplicationContext(), "Uncorrected first name!", Toast.LENGTH_LONG).show();
            return Bundle.EMPTY;
        }

        if (editLName.getText().length() < 2) {
            Toast.makeText(getApplicationContext(), "Uncorrected last name!", Toast.LENGTH_LONG).show();
            return Bundle.EMPTY;
        }

        //int Year;
        Pattern DataValiadtion =
                Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- ..](0[1-9]|1[012])[- ..](19|20)\\d\\d", Pattern.CASE_INSENSITIVE);

        Matcher DataMatcher = DataValiadtion.matcher(editDayOfBirth.getText().toString());

        if (!DataMatcher.find()){
            Toast.makeText(getApplicationContext(), "Uncorrected day of birthday! Correct format: DD.MM.YYYY!", Toast.LENGTH_LONG).show();
            return Bundle.EMPTY;
        }

        Bundle RegBundle = new Bundle();
        try {
            OS.writeObject(editNick.getText().toString());
            RegBundle.putString("Nickname", editNick.getText().toString());

            OS.writeObject(editFName.getText().toString());
            RegBundle.putString("FName", editFName.getText().toString());

            OS.writeObject(editLName.getText().toString());
            RegBundle.putString("LName", editLName.getText().toString());

            OS.writeObject(editEmail.getText().toString());
            RegBundle.putString("Email", editEmail.getText().toString());

            RegBundle.putString("DayOfBirth", editDayOfBirth.getText().toString());

            try {
                RegBundle.putString("Password", MainActivity.getEncryptedString(editPass1.getText().toString()));
            } catch (Exception Ex) {
                Toast.makeText(getApplicationContext(), "Save password error!", Toast.LENGTH_LONG).show();
                return Bundle.EMPTY;
            }

            String RegData;
            try {
                RegData = IS.readObject().toString();
            } catch (Exception Ex) {
                return Bundle.EMPTY;
            }

            try {
                if (Integer.valueOf(RegData) == -1)
                    Toast.makeText(getApplicationContext(), "Nickname is already in use!", Toast.LENGTH_LONG).show();
                else
                if (Integer.valueOf(RegData) == -2)
                    Toast.makeText(getApplicationContext(), "Email is already in use!", Toast.LENGTH_LONG).show();

            } catch (NumberFormatException NFEx) {
                RegBundle.putString("ValidCode", RegData);

                Toast.makeText(getApplicationContext(), "Check the mailbox!", Toast.LENGTH_LONG).show();

                return RegBundle;
            }

            return Bundle.EMPTY;

        } catch (IOException IOEx) {
            System.out.println("Server not found! " + IOEx);
            return Bundle.EMPTY;
        }
    }
}
