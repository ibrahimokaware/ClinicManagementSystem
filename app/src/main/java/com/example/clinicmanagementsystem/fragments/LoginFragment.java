package com.example.clinicmanagementsystem.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinicmanagementsystem.MainActivity;
import com.example.clinicmanagementsystem.MyDatabase;
import com.example.clinicmanagementsystem.R;
import com.example.clinicmanagementsystem.models.Patient;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private EditText emailET, passwordET;
    private Button loginBT, newPatientBT;
    private ImageView passwordIV;
    private TextView emailHintTV, passwordHintTV;

    private boolean isPasswordShow = false, isEmailValid = false, isPasswordValid = false;

    private MyDatabase database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("login" , MODE_PRIVATE);
        editor = sharedPreferences.edit();

        database = new MyDatabase(getContext());

        emailET = view.findViewById(R.id.et_login_email);
        passwordET = view.findViewById(R.id.et_login_password);
        loginBT = view.findViewById(R.id.bt_login);
        newPatientBT = view.findViewById(R.id.bt_login_new_patient);
        passwordIV = view.findViewById(R.id.iv_login_password);
        emailHintTV = view.findViewById(R.id.tv_login_email_hint);
        passwordHintTV = view.findViewById(R.id.tv_login_password_hint);

        // set watcher for edit text
        emailET.addTextChangedListener(textWatcher);
        passwordET.addTextChangedListener(textWatcher);

        checkValidation();

        // set password toggle
        passwordIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordShow) {
                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordET.setSelection(passwordET.length());
                    passwordIV.setImageResource(R.drawable.ic_visibility);
                    isPasswordShow = false;
                } else {
                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordET.setSelection(passwordET.length());
                    passwordIV.setImageResource(R.drawable.ic_visibility_off);
                    isPasswordShow = true;
                }
            }
        });

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                ArrayList<Patient> patients = database.getPatients(email);   //  get users by email
                if (patients.size() == 0)   //  no users have this email
                    Toast.makeText(getActivity(), "Email Error!", Toast.LENGTH_SHORT).show();
                else {
                    Patient patient = patients.get(0);   //  get logged user
                    if (patient.getPassword().equals(password)) {   //  check password
                        editor.putString("email", email);  //  store email
                        editor.apply();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                    else Toast.makeText(getActivity(), "Password Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        newPatientBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                NewPatientFragment newPatientFragment = new NewPatientFragment();
                fragmentTransaction.replace(R.id.fragment_container, newPatientFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkValidation();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    void checkValidation() {
        if (isEmpty(emailET)) {
            isEmailValid = false;
        } else {
            // check email pattern
            if (!Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()) {
                emailHintTV.setText(getString(R.string.email_check));
                isEmailValid = false;
            } else {
                emailHintTV.setText("");
                isEmailValid = true;
            }
        }

        if (isEmpty(passwordET)) {
            isPasswordValid = false;
        } else {
            // check password length
            if (passwordET.getText().toString().length() < 6 || passwordET.getText().toString().length() > 20) {
                passwordHintTV.setText(getString(R.string.password_check));
                isPasswordValid = false;
            } else {
                passwordHintTV.setText("");
                isPasswordValid = true;
            }
        }

        if (isEmailValid && isPasswordValid) {
            loginBT.setEnabled(true);
            loginBT.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            loginBT.setEnabled(false);
            loginBT.setTextColor(getResources().getColor(R.color.colorDisable));
        }
    }

    private boolean isEmpty(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString()))
            return true;
        return false;
    }
}