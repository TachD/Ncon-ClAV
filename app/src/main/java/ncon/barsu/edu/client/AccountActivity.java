package ncon.barsu.edu.client;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
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

public class AccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tvNick;
    private TextView tvLFName;
    //
    private TextView tvAccFName;
    private TextView tvAccLName;
    private TextView tvAccEmail;
    private TextView tvAccBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fbSend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvNick = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNick);
        tvLFName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLFName);
        /*
        TextView tvAccLName = (TextView) findViewById(R.id.tvAccLName);
        TextView tvAccFName = (TextView) findViewById(R.id.tvAccFName);
        TextView tvAccEmail = (TextView) findViewById(R.id.tvAccEmail);
        TextView tvAccBirth = (TextView) findViewById(R.id.tvAccBirthday);
        */
        Bundle AuthBundle = getIntent().getExtras();

        // getting bundle data... / auth data object
        tvNick.setText(AuthBundle.getString("Nickname"));
        tvLFName.setText(AuthBundle.getString("FName") + " " +AuthBundle.getString("LName"));
        /*
        tvAccFName.setText(AuthBundle.getString("FName"));
        tvAccLName.setText(AuthBundle.getString("LName"));
        tvAccEmail.setText(AuthBundle.getString("Email"));
        tvAccBirthday.setText(AuthBundle.getString("DayOfBirthday"));
        */
        //
    }


    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName("192.168.43.47"), 10001);

                    Socket CSock = new Socket();

                    CSock.connect(SockAddr, 0);

                    ObjectOutputStream OS = new ObjectOutputStream(CSock.getOutputStream());

                    OS.writeObject(-5);

                    OS.writeObject(tvNick.getText().toString());

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
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_messenger:
                System.out.println("Messenger");
                break;
            case R.id.nav_friendlist:
                System.out.println("Friend list");
                break;
            case R.id.nav_cloud:
                System.out.println("Cloud");
                break;
            case R.id.nav_settings:
                System.out.println("Settings");
                break;
            case R.id.nav_view:
                System.out.println("View");
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}