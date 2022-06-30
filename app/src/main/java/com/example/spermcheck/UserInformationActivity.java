package com.example.spermcheck;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import static tools.RWResultAsTxt.loadFromSDFile;

public class UserInformationActivity extends AppCompatActivity {

    private TextView userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);


        String user_name_path = getExternalFilesDir("Data")+ File.separator+"UserName.txt";
        String user_password_path = getExternalFilesDir("Data")+ File.separator+"UserPassword.txt";
        String ID = loadFromSDFile(user_name_path);
        String PASSWORD = loadFromSDFile(user_password_path);

        if (ID==null&&PASSWORD==null){
            ID = "admin";
            PASSWORD = "123456";
        }
        userInformation = findViewById(R.id.user_information);
        userInformation.setText("用户名："+ID+"\n\n密码："+PASSWORD);
    }

}
