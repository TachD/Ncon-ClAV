package ncon.barsu.edu.client;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class messenger extends Fragment {
    private ListView lvMsgList;
    private ImageView ivSend;
    private EditText etMsg;
    //
    private NcoNClient Client;
    private Handler MsgHandler;
    //
    private ArrayAdapter<String> MsgAdapter;
    private ArrayList<String> MsgList;
    private String Nick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messenger, container, false);

        Nick = getActivity().getIntent().getExtras().getString("Nickname");

        MsgList = new ArrayList<String>();
        MsgAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, MsgList);

        lvMsgList = (ListView) view.findViewById(R.id.lvMesg);
        lvMsgList.setAdapter(MsgAdapter);

        etMsg = (EditText) view.findViewById(R.id.etMsg);

        ivSend = (ImageView) view.findViewById(R.id.ivSend);
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Client != null)
                            if(Client.isConnected())
                                if (etMsg.getText().toString().length() != 0) {

                                    Client.send(Nick + ": " + etMsg.getText().toString());

                                    MsgList.add(TimeLog() + Nick + ": " + etMsg.getText().toString());
                                    MsgAdapter.notifyDataSetChanged();

                                    etMsg.setText("");
                            }
                } catch (Exception Ex) {
                    try {
                        Toast.makeText(getActivity(), Ex.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    } finally {
                        if (Client != null) {
                            Client.CloseStream();
                            Client = null;
                        }
                    }
                }
            }
        });

        MsgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                    MsgList.add(TimeLog() + msg.obj);
                    MsgAdapter.notifyDataSetChanged();
            }
        };

        return view;
    }
    // // TODO: 19.03.2017 use this method
    private void CreateSession(final int PORT) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketAddress SockAddr = new InetSocketAddress(InetAddress.getByName(MainActivity.Host), 10001);

                    Socket CSock = new Socket();

                    CSock.connect(SockAddr, 0);

                    ObjectOutputStream OS = new ObjectOutputStream(CSock.getOutputStream());

                    OS.writeObject(PORT);

                    Connect(PORT);

                    if (OS != null)
                        OS.close();

                    if (CSock != null)
                        CSock.close();

                } catch (Exception Ex) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), Ex.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();
    }

    private void Connect(int PORT) {
        try {
            Client = new NcoNClient(PORT);

            Client.OpenStream();

            while (true) {
                Message msg = new Message();
                msg.obj = Client.receive();
                MsgHandler.sendMessage(msg);
            }
        } catch (Exception Ex) {
            Looper.prepare();
            Toast.makeText(getActivity(), "Connection completed",
                    Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    private String TimeLog() {
        Time NowTime = new Time(Time.getCurrentTimezone());

        NowTime.setToNow();

        StringBuilder TimeLogMessage = new StringBuilder("[");

        TimeLogMessage.append((NowTime.hour < 10)
                ?"0" + NowTime.hour:
                NowTime.hour);

        TimeLogMessage.append(":");

        TimeLogMessage.append((NowTime.minute < 10)
                ?"0" + NowTime.minute:
                NowTime.minute);

        TimeLogMessage.append("] ");

        return TimeLogMessage.toString();
    }
}