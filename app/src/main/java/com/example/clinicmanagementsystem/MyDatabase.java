package com.example.clinicmanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.clinicmanagementsystem.models.Appointment;
import com.example.clinicmanagementsystem.models.Doctor;
import com.example.clinicmanagementsystem.models.Patient;

import java.util.ArrayList;

public class MyDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "clinic_db";
    public static final int DB_VERSION = 1;

    public static final String PATIENT_TB_NAME = "patient";
    public static final String PATIENT_CLN_ID = "id";
    public static final String PATIENT_CLN_NAME = "name";
    public static final String PATIENT_CLN_AGE = "age";
    public static final String PATIENT_CLN_EMAIL = "email";
    public static final String PATIENT_CLN_PASSWORD = "password";

    public static final String DOCTOR_TB_NAME = "doctor";
    public static final String DOCTOR_CLN_ID = "id";
    public static final String DOCTOR_CLN_NAME = "name";
    public static final String DOCTOR_CLN_SPECIALIZATION = "specialization";

    public static final String APPOINTMENT_TB_NAME = "appointment";
    public static final String APPOINTMENT_CLN_ID = "id";
    public static final String APPOINTMENT_CLN_PATIENT_ID = "patientId";
    public static final String APPOINTMENT_CLN_DOCTOR_ID = "doctorId";
    public static final String APPOINTMENT_CLN_DATE = "date";

    public MyDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+PATIENT_TB_NAME+" ("+PATIENT_CLN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                PATIENT_CLN_NAME +" TEXT NOT NULL," + PATIENT_CLN_AGE +" TEXT NOT NULL," +
                PATIENT_CLN_EMAIL +" TEXT UNIQUE NOT NULL," + PATIENT_CLN_PASSWORD+" TEXT NOT NULL)");
        sqLiteDatabase.execSQL("CREATE TABLE "+DOCTOR_TB_NAME+" ("+DOCTOR_CLN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                DOCTOR_CLN_NAME +" TEXT NOT NULL," + DOCTOR_CLN_SPECIALIZATION +" TEXT NOT NULL)");
        sqLiteDatabase.execSQL("CREATE TABLE "+APPOINTMENT_TB_NAME+" ("+APPOINTMENT_CLN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                APPOINTMENT_CLN_PATIENT_ID +" INTEGER NOT NULL," + APPOINTMENT_CLN_DOCTOR_ID +" INTEGER NOT NULL," +
                APPOINTMENT_CLN_DATE +" TEXT NOT NULL," +
                " FOREIGN KEY("+ APPOINTMENT_CLN_PATIENT_ID+") REFERENCES " + PATIENT_TB_NAME +"("+ PATIENT_CLN_ID +")," +
                " FOREIGN KEY("+ APPOINTMENT_CLN_DOCTOR_ID+") REFERENCES " + DOCTOR_TB_NAME +"("+ DOCTOR_CLN_ID +"))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ PATIENT_TB_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DOCTOR_TB_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ APPOINTMENT_TB_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addPatient(Patient patient) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PATIENT_CLN_NAME, patient.getName());
        contentValues.put(PATIENT_CLN_AGE, patient.getAge());
        contentValues.put(PATIENT_CLN_EMAIL, patient.getEmail());
        contentValues.put(PATIENT_CLN_PASSWORD, patient.getPassword());
        long result= db.insert(PATIENT_TB_NAME, null, contentValues);
        return result != -1;
    }

    public boolean addDoctor(Doctor doctor) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOCTOR_CLN_NAME, doctor.getName());
        contentValues.put(DOCTOR_CLN_SPECIALIZATION, doctor.getSpecialization());
        long result= db.insert(DOCTOR_TB_NAME, null, contentValues);
        return result != -1;
    }

    public boolean addAppointment(Appointment appointment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APPOINTMENT_CLN_PATIENT_ID, appointment.getPatientId());
        contentValues.put(APPOINTMENT_CLN_DOCTOR_ID, appointment.getDoctorId());
        contentValues.put(APPOINTMENT_CLN_DATE, appointment.getDate());
        long result= db.insert(APPOINTMENT_TB_NAME, null, contentValues);
        return result != -1;
    }

    public boolean updatePatient(Patient patient) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PATIENT_CLN_NAME, patient.getName());
        contentValues.put(PATIENT_CLN_AGE, patient.getAge());
        contentValues.put(PATIENT_CLN_EMAIL, patient.getEmail());
        contentValues.put(PATIENT_CLN_PASSWORD, patient.getPassword());
        String args [] = {String.valueOf(patient.getId()), patient.getName()}; // or myUniversityInDB.getId()+""
        int result= db.update(PATIENT_TB_NAME, contentValues, "id=? AND name=?", args);
        return result != 0; // >0
    }

    public long getPatientsCount() {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, PATIENT_TB_NAME); // (db,table_university_name,"city=?",args)يمكن استخدام args معها لو كان هناك شرط معين - نريد عدد الجانعات التي موقعها غزة مثلا
    }

    public boolean deletePatient(Patient patient) {
        SQLiteDatabase db = getReadableDatabase();
        String args [] = {String.valueOf(patient.getId())};
        int result= db.delete(PATIENT_TB_NAME, "id=?", args);
        return result > 0; // != 0
    }

    public ArrayList<Patient> getPatients() {
        ArrayList<Patient> patients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+PATIENT_TB_NAME, null); // يمكن استخدام ال args
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //int id = cursor.getInt(0);
                // String name = cursor.getString(1);
                int id = cursor.getInt(cursor.getColumnIndex(PATIENT_CLN_ID));
                String name = cursor.getString(cursor.getColumnIndex(PATIENT_CLN_NAME));
                String age = cursor.getString(cursor.getColumnIndex(PATIENT_CLN_AGE));
                String email = cursor.getString(cursor.getColumnIndex(PATIENT_CLN_EMAIL));
                String password = cursor.getString(cursor.getColumnIndex(PATIENT_CLN_PASSWORD));
                Patient patient = new Patient(id, name, age, email, password);
                patients.add(patient);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return patients;
    }

    public ArrayList<Doctor> getDoctors() {
        ArrayList<Doctor> doctors = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+DOCTOR_TB_NAME, null); // يمكن استخدام ال args
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //int id = cursor.getInt(0);
                // String name = cursor.getString(1);
                int id = cursor.getInt(cursor.getColumnIndex(DOCTOR_CLN_ID));
                String name = cursor.getString(cursor.getColumnIndex(DOCTOR_CLN_NAME));
                String specialization = cursor.getString(cursor.getColumnIndex(DOCTOR_CLN_SPECIALIZATION));
                Doctor doctor = new Doctor(id, name, specialization);
                doctors.add(doctor);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return doctors;
    }

    public ArrayList<Appointment> getAppointments() {
        ArrayList<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+APPOINTMENT_TB_NAME, null); // يمكن استخدام ال args
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //int id = cursor.getInt(0);
                // String name = cursor.getString(1);
                int id = cursor.getInt(cursor.getColumnIndex(APPOINTMENT_CLN_ID));
                int patientId = cursor.getInt(cursor.getColumnIndex(APPOINTMENT_CLN_PATIENT_ID));
                int doctorId = cursor.getInt(cursor.getColumnIndex(APPOINTMENT_CLN_DOCTOR_ID));
                String date = cursor.getString(cursor.getColumnIndex(APPOINTMENT_CLN_DATE));
                Appointment appointment = new Appointment(id, patientId, doctorId, date);
                appointments.add(appointment);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return appointments;
    }

    public ArrayList<Appointment> getFullAppointments() {
        ArrayList<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + "a." + APPOINTMENT_CLN_ID + ", " + "a." + APPOINTMENT_CLN_PATIENT_ID + ", " +
                "a." + APPOINTMENT_CLN_DOCTOR_ID + ", "  + "a." + APPOINTMENT_CLN_DATE + ", " +
                "p." + PATIENT_CLN_NAME + ", " + "d." + DOCTOR_CLN_NAME +
                " FROM " + APPOINTMENT_TB_NAME + " a " +
                " INNER JOIN " + PATIENT_TB_NAME + " P " +
                " ON a." + APPOINTMENT_CLN_PATIENT_ID + " = p." + PATIENT_CLN_ID +
                " INNER JOIN " + DOCTOR_TB_NAME + " d " +
                " ON a." + APPOINTMENT_CLN_DOCTOR_ID + " = d." + DOCTOR_CLN_ID, null); // يمكن استخدام ال args
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //int id = cursor.getInt(0);
                // String name = cursor.getString(1);
                int id = cursor.getInt(0);
                int patientId = cursor.getInt(1);
                int doctorId = cursor.getInt(2);
                String date = cursor.getString(3);
                String patientName = cursor.getString(4);
                String doctorName = cursor.getString(5);
                Appointment appointment = new Appointment(id, patientId, doctorId, date, patientName, doctorName);
                appointments.add(appointment);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return appointments;
    }

    public ArrayList<Patient> getPatients(String emailSearch) {
        ArrayList<Patient> patients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+PATIENT_TB_NAME+" WHERE " + PATIENT_CLN_EMAIL+"=?", new String[] {emailSearch});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //int id = cursor.getInt(0);
                // String name = cursor.getString(1);
                int id = cursor.getInt(cursor.getColumnIndex(PATIENT_CLN_ID));
                String name = cursor.getString(cursor.getColumnIndex(PATIENT_CLN_NAME));
                String age = cursor.getString(cursor.getColumnIndex(PATIENT_CLN_AGE));
                String email = cursor.getString(cursor.getColumnIndex(PATIENT_CLN_EMAIL));
                String password = cursor.getString(cursor.getColumnIndex(PATIENT_CLN_PASSWORD));
                Patient patient = new Patient(id, name, age, email, password);
                patients.add(patient);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return patients;
    }

    public ArrayList<Doctor> getDoctors(int idSearch) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+DOCTOR_TB_NAME+" WHERE " + DOCTOR_CLN_ID+"=?", new String[] {""+idSearch});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //int id = cursor.getInt(0);
                // String name = cursor.getString(1);
                int id = cursor.getInt(cursor.getColumnIndex(DOCTOR_CLN_ID));
                String name = cursor.getString(cursor.getColumnIndex(DOCTOR_CLN_NAME));
                String specialization = cursor.getString(cursor.getColumnIndex(DOCTOR_CLN_SPECIALIZATION));
                Doctor doctor = new Doctor(id, name, specialization);
                doctors.add(doctor);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return doctors;
    }

    public ArrayList<Appointment> getAppointments(int patientIdSearch, int doctorIdSearch, String dateSearch) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+APPOINTMENT_TB_NAME+" WHERE " + APPOINTMENT_CLN_PATIENT_ID+"=? AND " +
                APPOINTMENT_CLN_DOCTOR_ID+"=? AND " + APPOINTMENT_CLN_DATE+"=?", new String[] {patientIdSearch+"", doctorIdSearch+"", dateSearch});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //int id = cursor.getInt(0);
                // String name = cursor.getString(1);
                int id = cursor.getInt(cursor.getColumnIndex(APPOINTMENT_CLN_ID));
                int patientId = cursor.getInt(cursor.getColumnIndex(APPOINTMENT_CLN_PATIENT_ID));
                int doctorId = cursor.getInt(cursor.getColumnIndex(APPOINTMENT_CLN_DOCTOR_ID));
                String date = cursor.getString(cursor.getColumnIndex(APPOINTMENT_CLN_DATE));
                Appointment appointment = new Appointment(id, patientId, doctorId, date);
                appointments.add(appointment);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return appointments;
    }

    public boolean updateAppointment(int appointmentId, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APPOINTMENT_CLN_DATE, date);
        String args [] = {String.valueOf(appointmentId)}; // or myUniversityInDB.getId()+""
        int result= db.update(APPOINTMENT_TB_NAME, contentValues, "id=?", args);
        return result != 0; // >0
    }

    public boolean deleteAppointment(int appointmentId) {
        SQLiteDatabase db = getReadableDatabase();
        String args [] = {String.valueOf(appointmentId)};
        int result= db.delete(APPOINTMENT_TB_NAME, "id=?", args);
        return result > 0; // != 0
    }

    public boolean resetPassword(String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PATIENT_CLN_PASSWORD, password);
        String args [] = {String.valueOf(email)};
        int result= db.update(PATIENT_TB_NAME, contentValues, "email=?", args);
        return result != 0; // >0
    }
}
