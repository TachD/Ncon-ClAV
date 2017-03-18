package ncon.barsu.edu.client;

import android.os.Bundle;
import android.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class messenger extends Fragment {
    private ArrayAdapter<String> MsgAdapter;
    private ArrayList<String> MsgList;
    private String Nick;
    //
    private ListView lvMsg;
    private ImageView ivSend;
    private EditText etMsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messenger, container, false);

        Nick = getActivity().getIntent().getExtras().getString("Nickname");
        MsgList = new ArrayList<String>();
        MsgAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, MsgList);

        lvMsg = (ListView) view.findViewById(R.id.lvMesg);
        lvMsg.setAdapter(MsgAdapter);

        etMsg = (EditText) view.findViewById(R.id.etMsg);
        ivSend = (ImageView) view.findViewById(R.id.ivSend);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MsgList.add(TimeLog() + Nick + ": " + etMsg.getText().toString());

                MsgAdapter.notifyDataSetChanged();
                etMsg.setText("");
            }
        });
        return view;
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