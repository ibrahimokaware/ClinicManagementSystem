package com.example.clinicmanagementsystem.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinicmanagementsystem.MainActivity;
import com.example.clinicmanagementsystem.MyDatabase;
import com.example.clinicmanagementsystem.R;
import com.example.clinicmanagementsystem.models.Appointment;
import com.example.clinicmanagementsystem.models.Doctor;
import com.example.clinicmanagementsystem.models.Patient;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Toolbar toolbar;
    private TextView nameTV, ageTV;
    private Spinner doctorsSP;
    private DatePicker appointmentDP;
    private Button reserveBT;

    private MyDatabase database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReservationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReservationFragment newInstance(String param1, String param2) {
        ReservationFragment fragment = new ReservationFragment();
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
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("login" , MODE_PRIVATE);
        editor = sharedPreferences.edit();

        database = new MyDatabase(getContext());

        toolbar = getActivity().findViewById(R.id.toolbar);
        nameTV = view.findViewById(R.id.tv_reservation_patient_name);
        ageTV = view.findViewById(R.id.tv_reservation_patient_age);
        doctorsSP = view.findViewById(R.id.sp_reservation_doctors);
        appointmentDP = view.findViewById(R.id.dp_reservation_appointment);
        reserveBT = view.findViewById(R.id.bt_reservation_reserve);

        toolbar.setTitle(getString(R.string.app_name));

        ArrayList<Patient> patients = database.getPatients(sharedPreferences.getString("email", ""));   //  get users by email
        final Patient patient = patients.get(0);   //  get logged user
        nameTV.setText(patient.getName());
        ageTV.setText(patient.getAge());

        final ArrayList<Doctor> doctors = database.getDoctors();
        if (doctors.size() != 0) {
            String[] doctorsArray = new String[doctors.size()];
            for (int i = 0; i < doctors.size(); i++)
                doctorsArray[i] = doctors.get(i).getName();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(), android.R.layout.simple_spinner_item, doctorsArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            doctorsSP.setAdapter(adapter);
        }

        reserveBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = appointmentDP.getDayOfMonth()+"/"+ (appointmentDP.getMonth() + 1)+"/"+appointmentDP.getYear();

                int position = doctorsSP.getSelectedItemPosition();
                int doctorId = doctors.get(position).getId();

                ArrayList<Appointment> appointments = database.getAppointments();
                if (appointments.size() == 0) {
                    boolean result = database.addAppointment(new Appointment(patient.getId(), doctorId, date));
                    if (result)
                        Toast.makeText(getContext(), "Booked", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Appointment> appointmentArrayList = database.getAppointments(patient.getId(), doctorId, date);
                    if (appointmentArrayList.size() == 0) {
                        boolean result = database.addAppointment(new Appointment(patient.getId(), doctorId, date));
                        if (result)
                            Toast.makeText(getContext(), "Booked", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    } else  Toast.makeText(getContext(), "Already Booked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}