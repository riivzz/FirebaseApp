package com.crv.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity  extends AppCompatActivity {

    //Declaramos los botones del menú principal
    private Button btnCrear, btnBuscar, btnModificar, btnBorrar, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //1. Vincular los botones con sus IDs del XML
        btnCrear = findViewById(R.id.btnCrear);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnModificar = findViewById(R.id.btnModificar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnLogout = findViewById(R.id.btnLogout);

        //2. Configurar la navegación hacia la pantalla de Creación
        btnCrear.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        //3. Configurar la navegación hacia la pantalla de Búsqueda
        btnBuscar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ReadActivity.class);
            startActivity(intent);
        });

        //4. Configurar la navegación hacia la pantalla de Modificación
        btnModificar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UpdateActivity.class);
            startActivity(intent);
        });

        //5. Configurar la navegación hacia la pantalla de Borrado
        btnBorrar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DeleteActivity.class);
            startActivity(intent);
        });

        //6. Cerrar sesión en Firebase y regresar al Login (MainActivity)
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); //Cierra sesión en Firebase
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); //Cierra HomeActivity para impedir volver atrás con el botón físico
        });

    }
}
