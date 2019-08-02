package com.proyecto1.william.proyecto1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email,contraseña;
    private Button btnLogin;
    private TextView txtRegistro,txtRecuperar;
    private FirebaseAuth firebaseAuth;
    private String login,password;
    private static final String NOMBRE_PREFERENCIA= "LoginActivityPreferencia";
    private static final String TAG = LoginActivity.class.getSimpleName();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.txtEmail_Login);
        contraseña = findViewById(R.id.txtPassword_Login);
        btnLogin =  findViewById(R.id.btnLogin_login);
        txtRegistro = findViewById(R.id.btnRegistro_login);
        txtRecuperar= findViewById(R.id.btnRecuperar_login);
        //preferences = getSharedPreferences(NOMBRE_PREFERENCIA,MODE_PRIVATE);

        txtRegistro.setOnClickListener(this);
        txtRecuperar.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        if ( firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    public boolean validarLogin(){

        boolean cancel = false;
        View focusView = null;

        login = email.getText().toString();
        password = contraseña.getText().toString();

        //Log.d(TAG,email.getText().toString());
        //Log.d(TAG,contraseña.getText().toString());

        if(TextUtils.isEmpty(login)){
            email.setError(getString(R.string.validaCorreo));
            focusView = email;
            //Toast.makeText(getApplicationContext(),getString(R.string.validaCorreo),Toast.LENGTH_SHORT).show();
            cancel=true;
        }
        if(TextUtils.isEmpty(password)){
            contraseña.setError(getString(R.string.validaPassword));
            focusView = contraseña;
            //Toast.makeText(getApplicationContext(),getString(R.string.validaPassword),Toast.LENGTH_SHORT).show();
            cancel=true;
        }else if (!isPasswordValid(password)){
            contraseña.setError(getString(R.string.error_invalid_email));
            focusView = contraseña;
            cancel=true;
        }

        if(cancel){
            focusView.requestFocus();
            return false;
        }else
        return true;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnRegistro_login:
                startActivity(new Intent(this,RegistroActivity.class));
                break;
            case R.id.btnRecuperar_login:
                startActivity(new Intent(this,OlvidaContraActivity.class));
                break;
            case R.id.btnLogin_login:
                Loguearse();
                break;

        }
    }

    public void Loguearse(){
        if(validarLogin()){
            //Log.d(TAG,login);
            //Log.d(TAG,password);
            firebaseAuth.signInWithEmailAndPassword(login,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                    if(!task.isSuccessful()){
                        //Log.e(TAG, "signInWithEmail:Fallo", task.getException());
                        //Log.e(TAG, "Email: "+login+" Password: "+password);
                        Toast.makeText(getApplicationContext(),getString(R.string.correoNoValido),Toast.LENGTH_SHORT).show();
                    }else {
                        //SharedPreferences sharedPreferences = getSharedPreferences(NOMBRE_PREFERENCIA,MODE_PRIVATE);
                        //SharedPreferences.Editor editor = sharedPreferences.edit();
                        //Log.d(TAG,usu);
                        //Log.d(TAG,task.getResult().getUser().getUid());
                        //editor.putString("Usuario",usu);
                        //editor.putString("UID",task.getResult().getUser().getUid());
                        //editor.commit();
                        callMainPrincipal();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(getApplicationContext(),"Error al Loguearse "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void callMainPrincipal(){
        //Log.d(TAG,"callMainPrincipal");
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
