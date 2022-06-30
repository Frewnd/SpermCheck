package com.example.spermcheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import static tools.RWResultAsTxt.loadFromSDFile;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputId,inputPassword;
    private TextView newUser,forgetPassword;
    private Button buttonLogin;

    private String id,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(this);

        newUser = findViewById(R.id.new_user);
        newUser.setOnClickListener(this);

        forgetPassword = findViewById(R.id.forget_password);
        forgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        inputId = findViewById(R.id.input_id);
        id = inputId.getText().toString();

        inputPassword  = findViewById(R.id.input_password);
        password = inputPassword.getText().toString();

//        String user_name_path = getExternalFilesDir("Data")+ File.separator+"UserName.txt";
//        String user_password_path = getExternalFilesDir("Data")+ File.separator+"UserPassword.txt";
//        String ID = loadFromSDFile(user_name_path);
//        String PASSWORD = loadFromSDFile(user_password_path);

        String ID = null;
        String PASSWORD = null;
        if (ID==null&&PASSWORD==null){
            ID = "admin";
            PASSWORD = "123456";
        }
        switch (v.getId()){
            case R.id.button_login:
                if (id.equals(ID)&&password.equals(PASSWORD)){
                    Intent intent1 = new Intent();
                    intent1.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent1);
                }else if (id.equals("")){
                    Toast.makeText(LoginActivity.this,"请输入用户名！",Toast.LENGTH_LONG).show();
                }else if (password.equals("")){
                    Toast.makeText(LoginActivity.this,"请输入密码！",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this,"用户名或密码错误！",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.new_user:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.forget_password:
                Intent intent2 = new Intent();
                intent2.setClass(LoginActivity.this, UserInformationActivity.class);
                startActivity(intent2);
            default:
                break;
        }
    }
}
