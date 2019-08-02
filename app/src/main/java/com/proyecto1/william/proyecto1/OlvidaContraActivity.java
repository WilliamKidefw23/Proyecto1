package com.proyecto1.william.proyecto1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class OlvidaContraActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private Button btnRecupar;
    private TextView btnRegresar;
    private RelativeLayout activity_olvido;
    private Snackbar snackBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvida_contra);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.txtEmail_Olvido);
        btnRecupar = findViewById(R.id.btnRecuperar_Olvido);
        btnRegresar = findViewById(R.id.btnRegresar_Olvido);
        activity_olvido = findViewById(R.id.rl_actividad_olvido_contra);

        btnRecupar.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnRecuperar_Olvido:
                recuperarContraseña(email.getText().toString());
                break;
            case R.id.btnRegresar_Olvido:
                finish();
                break;
        }
    }

    private void recuperarContraseña(final String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    snackBar = Snackbar.make(activity_olvido,getString(R.string.recuperarPasswordEmail)+email,Snackbar.LENGTH_SHORT);
                    snackBar.show();
                }
                else{
                    snackBar = Snackbar.make(activity_olvido,getString(R.string.recuperarErrorEmail),Snackbar.LENGTH_SHORT);
                    snackBar.show();
                }
            }
        });
    }
}
