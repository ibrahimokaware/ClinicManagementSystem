package com.example.clinicmanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinicmanagementsystem.models.Appointment;

import java.util.ArrayList;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private ArrayList<Appointment> appointments;
    private TextView textView;
    private AppointmentItemListener onClickListener;
    private AppointmentItemListener onLongClickListener;

    public AppointmentsAdapter(ArrayList<Appointment> appointments, TextView textView, AppointmentItemListener onClickListener,
                               AppointmentItemListener onLongClickListener) {
        this.appointments = appointments;
        this.textView = textView;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.patientTV.setText("Patient Name: " + appointment.getPatientName());
        holder.doctorTV.setText(appointment.getDoctorName());
        holder.dateTV.setText(appointment.getDate());
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView patientTV, doctorTV, dateTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv_appointment);
            patientTV = itemView.findViewById(R.id.tv_appointment_patient);
            doctorTV = itemView.findViewById(R.id.tv_appointment_doctor);
            dateTV = itemView.findViewById(R.id.tv_appointment_date);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onAction(getAdapterPosition());
                }
            });

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickListener.onAction(getAdapterPosition());
                    appointments.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    if (appointments.size() == 0)
                        textView.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }
    }
}
