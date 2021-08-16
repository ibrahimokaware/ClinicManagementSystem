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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPatientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPatientFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private EditText nameET, ageET, emailET, passwordET;
    private Button addBT;
    private ImageView passwordIV;
    private TextView nameHintTV, ageHintTV, emailHintTV, passwordHintTV, loginTV;

    private boolean isPasswordShow = false, isEmailValid = false, isPasswordValid = false;

    private MyDatabase database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewPatientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPatientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPatientFragment newInstance(String param1, String param2) {
        NewPatientFragment fragment = new NewPatientFragment();
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
        return inflater.inflate(R.layout.fragment_new_patient, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("login" , MODE_PRIVATE);
        editor = sharedPreferences.edit();

        database = new MyDatabase(getContext());

        nameET = view.findViewById(R.id.et_new_patient_name);
        ageET = view.findViewById(R.id.et_new_patient_age);
        emailET = view.findViewById(R.id.et_new_patient_email);
        passwordET = view.findViewById(R.id.et_new_patient_password);
        addBT = view.findViewById(R.id.bt_new_patient_add);
        passwordIV = view.findViewById(R.id.iv_new_patient_password);
        nameHintTV = view.findViewById(R.id.tv_new_patient_name_hint);
        ageHintTV = view.findViewById(R.id.tv_new_patient_age_hint);
        emailHintTV = view.findViewById(R.id.tv_new_patient_email_hint);
        passwordHintTV = view.findViewById(R.id.tv_new_patient_password_hint);
        loginTV = view.findViewById(R.id.tv_new_patient_login);

        // set watcher for edit text
        nameET.addTextChangedListener(textWatcher);
        ageET.addTextChangedListener(textWatcher);
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

        addBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameET.getText().toString();
                String age = ageET.getText().toString();
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                Patient patient = new Patient(name, age, email, password);   //  insert new user
                boolean result = database.addPatient(patient);   //  insert result
                if (result) {
                    editor.putString("email", email);  //  store email
                    editor.apply();
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), MainActivity.class));   //  success insertion
                }
                else Toast.makeText(getActivity(), getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
            }
        });

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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

        if (!isEmpty(nameET) && !isEmpty(ageET) && isEmailValid && isPasswordValid) {
            addBT.setEnabled(true);
            addBT.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            addBT.setEnabled(false);
            addBT.setTextColor(getResources().getColor(R.color.colorDisable));
        }
    }

    private boolean isEmpty(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString()))
            return true;
        return false;
    }
}