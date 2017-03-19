package ncon.barsu.edu.client;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class acc_data extends Fragment {
    private TextView tvAccFName;
    private TextView tvAccLName;
    private TextView tvAccEmail;
    private TextView tvAccBirthday;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.acc_data, container, false);

        Bundle AuthData = getActivity().getIntent().getExtras();

        tvAccFName = (TextView) view.findViewById(R.id.tvAccFName);
        tvAccFName.setText(AuthData.getString("FName"));

        tvAccLName = (TextView) view.findViewById(R.id.tvAccLName);
        tvAccLName.setText(AuthData.getString("LName"));

        tvAccEmail = (TextView) view.findViewById(R.id.tvAccEmail);
        tvAccEmail.setText(AuthData.getString("Email"));

        tvAccBirthday = (TextView) view.findViewById(R.id.tvAccBirthday);
        tvAccBirthday.setText(AuthData.getString("DayOfBirthday"));

        return view;
    }
}