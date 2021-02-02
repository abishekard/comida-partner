package com.abishek.comidapartner.loginAndSignUp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.abishek.comidapartner.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeChooser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeChooser extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String TAG = "TimeChooser";

    private String type;
    private TextView title;
    private Button btnDone;
    private TimePicker timePicker;
    private int hour,min;
    private String amPm;
    private String completeTime;
    private TimePickerListener timePickerListener;
    private String hStr,mStr;

    public TimeChooser() {
        // Required empty public constructor
    }

    public TimeChooser(String type) {
        this.type = type;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeChooser.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeChooser newInstance(String param1, String param2) {
        TimeChooser fragment = new TimeChooser();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_picker, container, false);

        timePicker = view.findViewById(R.id.time_picker);
        title = view.findViewById(R.id.title);
        btnDone = view.findViewById(R.id.done);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hour = timePicker.getHour();
                min = timePicker.getMinute();


                if(hour >12)
                {
                    amPm="PM";
                    hour=hour-12;
                }
                else{
                    amPm ="AM";
                }
                Log.e(TAG,"hour: "+hour+" min: "+min+"  "+amPm+"  " +" comp: "+completeTime);
                mStr = min+"";
                hStr = hour+"";
                if(min<10)
                    mStr="0"+mStr;
                if(hour<10)
                    hStr="0"+hStr;
                completeTime=hStr+":"+mStr+":"+"00";
                timePickerListener.getTime(hStr+":"+mStr+" "+amPm,completeTime,type);
                dismiss();

            }
        });

        if(type.equals("open"))
        {
            title.setText("Opening Time");
        }
        if(type.equals("close"))
        {
            title.setText("Closing Time");
        }


        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        timePickerListener = (TimePickerListener) context;
    }

    public interface TimePickerListener{
        void getTime(String time,String completeTime,String type);
    }
}