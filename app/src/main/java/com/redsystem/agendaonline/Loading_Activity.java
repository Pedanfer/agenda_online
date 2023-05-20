package com.redsystem.agendaonline;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Loading_Activity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_de_carga);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseAuth = FirebaseAuth.getInstance();

        int Tiempo = 5000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*startActivity(new Intent(Pantalla_De_Carga.this, MainActivity.class));
                finish();*/
                VerificarUsuario();
            }
        },Tiempo);
    }

    private void VerificarUsuario(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){
            startActivity(new Intent(Loading_Activity.this, MainActivity.class));
            finish();

        }else {
            startActivity(new Intent(Loading_Activity.this, Main_Menu_Activity.class ));
            finish();
        }
    }
}