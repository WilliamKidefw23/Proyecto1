package com.proyecto1.william.proyecto1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyecto1.william.proyecto1.Entidad.Usuario;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email,contraseña;
    private Button btnRegistro;
    private TextView txtInicio,txtRecuperar;
    private String usuario,password,uid;
    //Firebase Auth
    private FirebaseAuth auth;
    //Firebase Database
    private DatabaseReference databaseReference;
    private static String TAG = RegistroActivity.class.getSimpleName();
    private Snackbar snackbar;
    private RelativeLayout actividad_registro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        email = (EditText) findViewById(R.id.txtEmail_Registro);
        contraseña = (EditText) findViewById(R.id.txtPassword_Registro);
        btnRegistro = (Button) findViewById(R.id.btnRegistro_Registro);
        txtInicio = findViewById(R.id.btnInicio_registro);
        txtRecuperar = findViewById(R.id.btnRecuperar_registro);
        actividad_registro = findViewById(R.id.rl_actividad_registro);

        btnRegistro.setOnClickListener(this);
        txtInicio.setOnClickListener(this);
        txtRecuperar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnRegistro_Registro:
                Registrarse();
                break;
            case R.id.btnInicio_registro:
                //Intent intent = new Intent(this,LoginActivity.class);
                //startActivity(intent);
                finish();
                break;
            case R.id.btnRecuperar_registro:
                startActivity(new Intent(this,OlvidaContraActivity.class));
                break;
        }
    }

    public void Registrarse(){
        if(validarRegistro()){
            auth.createUserWithEmailAndPassword(usuario,password)
                    .addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Toast.makeText(getApplicationContext(),"Se ha Creado el Usuario",Toast.LENGTH_SHORT).show();
                            if(!task.isSuccessful()){
                                //Log.e(TAG, "createUserWithEmailAndPassword:Fallo", task.getException());
                                //Log.e(TAG, "email: "+usuario+" password: "+password);
                                snackbar = Snackbar.make(actividad_registro,getString(R.string.registroFalse)+task.getException(),Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                /*Toast.makeText(getApplicationContext(),
                                        "Tenemos un Problema "+task.getException(),Toast.LENGTH_LONG).show();*/

                            }else{
                                //Guardar en Base de Datos
                                uid = task.getResult().getUser().getUid();
                                Usuario usu = new Usuario(uid,usuario);
                                databaseReference.child("usuarios").child(uid).setValue(usu);

                                snackbar = Snackbar.make(actividad_registro,getString(R.string.registroTrue),Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                //callMainLogin();
                            }
                        }
                    });
        }
    }

    public void callMainLogin(){
        //Log.i(TAG,"callMainPrincipal");
        /*Intent intent = new Intent(RegistroActivity.this,LoginActivity.class);
        startActivity(intent);*/
        finish();
    }

    public boolean validarRegistro(){

        usuario = email.getText().toString();
        password = contraseña.getText().toString();

        //Log.d(TAG,email.getText().toString());
        //Log.d(TAG,contraseña.getText().toString());

        if(TextUtils.isEmpty(usuario)){
            Toast.makeText(getApplicationContext(),getString(R.string.validaCorreo),Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),getString(R.string.validaPassword),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
