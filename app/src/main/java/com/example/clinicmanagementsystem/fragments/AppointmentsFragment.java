package com.example.clinicmanagementsystem.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinicmanagementsystem.AppointmentItemListener;
import com.example.clinicmanagementsystem.AppointmentsAdapter;
import com.example.clinicmanagementsystem.MainActivity;
import com.example.clinicmanagementsystem.MyDatabase;
import com.example.clinicmanagementsystem.R;
import com.example.clinicmanagementsystem.models.Appointment;
import com.example.clinicmanagementsystem.models.Doctor;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppointmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppointmentsFragment extends Fragment {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView noAppointmentsTV;

    private ArrayList<Appointment> appointments;
    AppointmentsAdapter appointmentsAdapter;

    private MyDatabase database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppointmentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppointmentsFragment newInstance(String param1, String param2) {
        AppointmentsFragment fragment = new AppointmentsFragment();
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
        return inflater.inflate(R.layout.fragment_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = new MyDatabase(getContext());

        toolbar = getActivity().findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.rv_appointments);
        noAppointmentsTV = view.findViewById(R.id.tv_no_appointments);

        appointments = database.getFullAppointments();

        toolbar.setTitle(getString(R.string.appointments));

        if (appointments.size() == 0)
            noAppointmentsTV.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        appointmentsAdapter = new AppointmentsAdapter(appointments, noAppointmentsTV, new AppointmentItemListener() {
            @Override
            public void onAction(int position) {
                updateDialog(position);
            }
        }, new AppointmentItemListener() {
            @Override
            public void onAction(int position) {
                boolean result = database.deleteAppointment(appointments.get(position).getId());
                if (result)
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(appointmentsAdapter);
    }

    public void updateDialog(final int position){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_update_appointment);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final DatePicker datePicker = dialog.findViewById(R.id.dp_update_appointment);
        TextView updateTV = dialog.findViewById(R.id.tv_update_appointment);

        String[] strings = appointments.get(position).getDate().split("/");
        int day = Integer.parseInt(strings[0]);
        int month = Integer.parseInt(strings[1]);
        int year = Integer.parseInt(strings[2]);
        datePicker.updateDate(year, month-1, day);

        updateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedDate = datePicker.getDayOfMonth()+"/"+ (datePicker.getMonth() + 1)+"/"+datePicker.getYear();
                boolean result = database.updateAppointment(appointments.get(position).getId(), updatedDate);

                appointments.get(position).setDate(updatedDate);
                appointmentsAdapter.notifyItemChanged(position);

                if (result)
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        dialog.show();
    }
}