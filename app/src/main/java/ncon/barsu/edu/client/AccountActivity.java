package ncon.barsu.edu.client;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.Key;
import java.security.MessageDigest;

public class AccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment Acc_Data;
    private Fragment Messenger;
    //
    private TextView tvNick;
    private TextView tvLFName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Bundle getting...
        Bundle AuthBundle = getIntent().getExtras();

        Messenger = new messenger();
        Acc_Data = new acc_data();
            android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.content_frame, Acc_Data).commit();

        tvNick = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNick);
        tvLFName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLFName);
        tvNick.setText(AuthBundle.getString("Nickname"));
        tvLFName.setText(AuthBundle.getString("FName") + " " + AuthBundle.getString("LName"));


    }


    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName(MainActivity.Host), 10001);

                    Socket CSock = new Socket();

                    CSock.connect(SockAddr, 0);

                    ObjectOutputStream OS = new ObjectOutputStream(CSock.getOutputStream());

                    OS.writeObject(-5);

                    Crypto CryptoObj = new Crypto();
                    OS.writeObject(CryptoObj.genKey());

                    CryptoObj.sendEncryptString(OS, tvNick.getText().toString());

                    OS.close();
                    CSock.close();
                } catch (Exception Ex) {
                    System.out.println(Ex.getMessage());
                }
            }
        }).start();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            SharedPreferences AccountDataPref = getSharedPreferences("AccData", MODE_PRIVATE);

            String Log = AccountDataPref.getString("Login", "");

            String Pass = AccountDataPref.getString("Password", "");

            SharedPreferences.Editor editor = AccountDataPref.edit();

            editor.clear();
            editor.commit();

            Log = AccountDataPref.getString("Login", "");

            Pass = AccountDataPref.getString("Password", "");

            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_messenger:
                fragment = Messenger;
                break;
            case R.id.nav_profile:
                fragment = Acc_Data;
                break;
        }

        if (fragment != null) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
