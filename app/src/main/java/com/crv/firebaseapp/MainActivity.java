package com.crv.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    //Componentes de la interfaz y objeto de autenticación
    private TextInputEditText etEmail, etPassword;
    private Button btnRegistrar, btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1. Obtener la instancia de Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //2. Vincular vistas del XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLogin = findViewById(R.id.btnLogin);

        //3. Asignar listeners a los botones
        btnRegistrar.setOnClickListener(v -> registrarUsuario());
        btnLogin.setOnClickListener(v -> loginUsuario());

    }

    //Crea un nuevo usuario en Firebase si no existe previamente
    private void registrarUsuario() {
        ocultarTeclado();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validarCampos(email, password)) return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        irAHome();
                    } else {
                        // Mensaje genérico en español sin usar el de Firebase en inglés
                        Toast.makeText(MainActivity.this, "Error: El correo ya existe o es inválido", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //Inicia sesión con un usuario existente en Firebase
    private void loginUsuario() {
        ocultarTeclado();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validarCampos(email, password)) return;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
                        irAHome();
                    } else {
                        // Mensaje de fallo en español
                        Toast.makeText(MainActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //Validación de entrada para evitar consultas vacías o contraseñas cortas
    private boolean validarCampos(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Introduce un correo");
            return false;
        }
        // NUEVA VALIDACIÓN: Comprueba el formato correcto del correo
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Formato de correo no válido");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Introduce una contraseña");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        return true;
    }

    //Navega al Activity Home y destruye LoginActivity de la pila de actividades
    private void irAHome(){
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); //Impide volver al login con el botón "Atrás".
    }

    //Ocultamos el teclado
    private void ocultarTeclado() {
        android.view.View view = this.getCurrentFocus();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}