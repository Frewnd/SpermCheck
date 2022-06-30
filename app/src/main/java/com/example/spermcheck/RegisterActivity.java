package com.example.spermcheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import static tools.RWResultAsTxt.saveVideoPath;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText informationName,informationAge,userName,userPassword;
    private Button conformRegister;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_register);

        informationName = findViewById(R.id.information_name);
        informationAge = findViewById(R.id.information_age);
        userName = findViewById(R.id.user_name);
        userPassword = findViewById(R.id.user_password);

        conformRegister = findViewById(R.id.confirm_register);
        conformRegister.setOnClickListener(this);



    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm_register:

                String information_name_path = getExternalFilesDir("Data")+ File.separator+"InformationName.txt";
                String information_age_path = getExternalFilesDir("Data")+ File.separator+"InformationAge.txt";
                String user_name_path = getExternalFilesDir("Data")+ File.separator+"UserName.txt";
                String user_password_path = getExternalFilesDir("Data")+ File.separator+"UserPassword.txt";
                //储存注册信息
                saveVideoPath(informationName.getText().toString(),information_name_path);
                saveVideoPath(informationAge.getText().toString(),information_age_path);
                saveVideoPath(userName.getText().toString(),user_name_path);
                saveVideoPath(userPassword.getText().toString(),user_password_path);

                Intent intent = new Intent();
                intent.setClass(this,LoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
