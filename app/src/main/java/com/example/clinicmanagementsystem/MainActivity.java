package com.example.clinicmanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.clinicmanagementsystem.fragments.AppointmentsFragment;
import com.example.clinicmanagementsystem.fragments.ReservationFragment;
import com.example.clinicmanagementsystem.models.Doctor;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Toolbar toolbar;

    private MyDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);
        editor = sharedPreferences.edit();

        database = new MyDatabase(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (database.getDoctors().size() == 0) {
            ArrayList<Doctor> doctors = new ArrayList<>();
            doctors.add(new Doctor("Dr. Ahmed", "Pediatrics"));
            doctors.add(new Doctor("Dr. Mohamed", "Surgery"));
            doctors.add(new Doctor("Dr. Mahmud", "Orthodontics"));

            for (Doctor doctor : doctors)
                database.addDoctor(doctor);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ReservationFragment reservationFragment = new ReservationFragment();
        fragmentTransaction.replace(R.id.fragment_container, reservationFragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_appointments:
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    AppointmentsFragment appointmentsFragment = new AppointmentsFragment();
                    fragmentTransaction.replace(R.id.fragment_container, appointmentsFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.item_log_out:
                setDialog();
                break;
        }
        return true;
    }

    public void setDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Are you sure you want to log out?")
                .setTitle("Log Out")
                .setIcon(R.drawable.ic_exit_to_app);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                editor.remove("email");
                editor.apply();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
    }
}