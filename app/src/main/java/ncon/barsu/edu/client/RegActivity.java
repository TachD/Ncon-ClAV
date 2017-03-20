package ncon.barsu.edu.client;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button btnRegistration;
    private Button btnDate;
    private EditText editNick;
    private EditText editFName;
    private EditText editLName;
    private EditText editPass1;
    private EditText editPass2;
    private EditText editEmail;
    private String Data;

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

        btnDate = (Button) findViewById(R.id.btnDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPicker();
            }
        });

        btnRegistration = (Button) findViewById(R.id.btnReg);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ValidRespone = ActivityDataValidation();

                Toast.makeText(getApplicationContext(), ValidRespone, Toast.LENGTH_SHORT).show();

                if (!"Registration request...".equals(ValidRespone))
                    return;

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName(MainActivity.Host), 10001);

                            Socket CSock = new Socket();

                            CSock.connect(SockAddr, 0);

                            ObjectOutputStream OS = new ObjectOutputStream(CSock.getOutputStream());

                            OS.writeObject(-2);

                            Crypto CryptoObj = new Crypto();
                            OS.writeObject(CryptoObj.genKey());

                            ObjectInputStream IS = new ObjectInputStream(CSock.getInputStream());

                            Bundle RegBundle = Validation(OS, IS, CryptoObj);

                            if (OS != null)
                                OS.close();

                            if (IS != null)
                                IS.close();

                            if (CSock != null)
                                CSock.close();

                            if (RegBundle.isEmpty())
                                return;

                            Intent ValidIntent = new Intent(RegActivity.this, ValidationActivity.class);

                            ValidIntent.putExtras(RegBundle);

                            startActivity(ValidIntent);

                        } catch (Exception Ex) {
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), "No internet connection or server unavailable",
                                    Toast.LENGTH_SHORT).show();
                            //Looper.loop();
                        }
                    }
                }).start();
            }
        });
    }

    private String ActivityDataValidation() {

        if (editNick.getText().toString().length() < 3)
            return "Username is too short!";

        if (editFName.getText().length() < 2)
            return "Uncorrected first name!";

        if (editLName.getText().length() < 2)
            return "Uncorrected last name!";

        if (!editPass1.getText().toString().equals(editPass2.getText().toString()))
            return "Passwords do not match!";

        if (editPass1.getText().toString().length() < 5)
            return "Password is too short!";

        Pattern EmailValidation =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher EmailMatcher = EmailValidation.matcher(editEmail.getText().toString());

        if (!EmailMatcher.find())
            return "Uncorrected e-mail!";

        if (Data == null)
            return "Date of birth not selected";


        return "Registration request...";
    }

    private Bundle Validation(ObjectOutputStream OS, ObjectInputStream IS, Crypto CryptoObj) {
        Bundle RegBundle = new Bundle();
        try {
            CryptoObj.sendEncryptString(OS, editNick.getText().toString());
            RegBundle.putString("Nickname", editNick.getText().toString());

            CryptoObj.sendEncryptString(OS, editFName.getText().toString());
            RegBundle.putString("FName", editFName.getText().toString());

            CryptoObj.sendEncryptString(OS,editLName.getText().toString());
            RegBundle.putString("LName", editLName.getText().toString());

            CryptoObj.sendEncryptString(OS,editEmail.getText().toString());
            RegBundle.putString("Email", editEmail.getText().toString());

            RegBundle.putString("DayOfBirth",Data);

            try {
                RegBundle.putString("Password", MainActivity.getEncryptedString(editPass1.getText().toString()));
            } catch (Exception Ex) {
                Toast.makeText(getApplicationContext(), "Save password error!", Toast.LENGTH_LONG).show();
                return Bundle.EMPTY;
            }


            String RegData;
            try {
                CryptoObj.setKey((Key) IS.readObject());
                RegData = CryptoObj.readDecryptString(IS);
            } catch (Exception Ex) {
                return Bundle.EMPTY;
            }

            Looper.prepare();

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

            Looper.prepare();

            return Bundle.EMPTY;

        } catch (Exception Ex) {
            System.out.println("Server not found! " + Ex.getMessage());
            return Bundle.EMPTY;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar Cal = new GregorianCalendar(year, month, day);
        setDate(Cal);
    }
    private void setDate(Calendar Cal) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        Data = dateFormat.format(Cal.getTime()).toString().replace("/", ".");

    }

    private void dataPicker() {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date");
    }

    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar Now = Calendar.getInstance();

            return new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener) getActivity(),
                    Now.get(Calendar.YEAR),
                    Now.get(Calendar.MONTH),
                    Now.get(Calendar.DAY_OF_MONTH));
        }
    }
}
