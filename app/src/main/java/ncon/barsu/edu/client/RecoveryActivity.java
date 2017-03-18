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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.*;

public class RecoveryActivity extends AppCompatActivity implements OnClickListener{
    private EditText editRecEmail;
    private Button btnRecContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        editRecEmail = (EditText) findViewById(R.id.editRecEmail);
        btnRecContinue = (Button) findViewById(R.id.btnRecContinue);

        btnRecContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View V) {
        switch (V.getId()) {
            case R.id.btnRecContinue:

                Pattern EmailValidation =
                        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

                Matcher EmailMatcher = EmailValidation.matcher(editRecEmail.getText().toString());

                if (!EmailMatcher.find()) {
                    Toast.makeText(getApplicationContext(), "Enter the E-mail correctly", Toast.LENGTH_LONG).show();
                    return;
                }

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        int Code;

                        try {
                            SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName(MainActivity.Host), 10001);

                            Socket CSock = new Socket();

                            CSock.connect(SockAddr, 0);

                            ObjectOutputStream OS = new ObjectOutputStream(CSock.getOutputStream());

                            OS.writeObject(-4);


                            ObjectInputStream IS = new ObjectInputStream(CSock.getInputStream());

                            Code = Recovery(OS, IS);

                            if (OS != null)
                                OS.close();

                            if (IS != null)
                                IS.close();

                            if (CSock != null)
                                CSock.close();

                        } catch (Exception Ex) {
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), Ex.getMessage(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            return;
                        }

                        try {
                            Looper.prepare();
                            if (Code == 0)
                                Toast.makeText(getApplicationContext(), "Go check your e-mail post",
                                        Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getApplicationContext(), "Unknown E-mail",
                                        Toast.LENGTH_LONG).show();

                            Looper.loop();

                        } catch (Throwable ThEx) {
                            Toast.makeText(getApplicationContext(), "No internet connection or server unavailable",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
        }
    }

    private int Recovery(ObjectOutputStream OS, ObjectInputStream IS) {
        String RecoveryData;

        try {
            OS.writeObject(editRecEmail.getText().toString());

            try {
                RecoveryData = IS.readObject().toString();
            } catch (Exception Ex) {
                return -1;
            }

        } catch (IOException IOEx) {
            System.out.println("Server not found! " + IOEx);
            return -1;
        }

        return (Integer.valueOf(RecoveryData) != -1)?0:-1;
    }
}
