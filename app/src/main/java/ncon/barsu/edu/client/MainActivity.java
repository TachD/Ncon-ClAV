package ncon.barsu.edu.client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
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
        btnSingin =   (Button) findViewById(R.id.btnSingin);
        btnRecovery = (Button) findViewById(R.id.btnRecovery);

        btnSingin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegActivity.class));
            }
        });

        btnRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RecoveryActivity.class));
            }
        });
    }
}
