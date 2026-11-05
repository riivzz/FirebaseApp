package com.crv.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    // Declaración de componentes
    private TextInputEditText etDni, etNombre, etApellidos, etEdad, etEmail;
    private TextView tvMascotaElegida;
    private Button btnElegirMascota, btnGuardar;
    private String mascotasSeleccionadas = "(ninguna)";
    private FirebaseFirestore db;

    // Lanzador para abrir la pantalla de selección de mascotas y recibir la respuesta
    private final ActivityResultLauncher<Intent> mascotaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    mascotasSeleccionadas = result.getData().getStringExtra("mascotas_seleccionadas");
                    tvMascotaElegida.setText("Mascotas preferidas: " + mascotasSeleccionadas);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Habilitar la flecha "Up" (atrás) en la barra superior
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Vincular vistas XML con las variables de Java
        etDni = findViewById(R.id.etDni);
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etEdad = findViewById(R.id.etEdad);
        etEmail = findViewById(R.id.etEmail);
        tvMascotaElegida = findViewById(R.id.tvMascotaElegida);
        btnElegirMascota = findViewById(R.id.btnElegirMascota);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Evento para abrir la pantalla de mascotas
        btnElegirMascota.setOnClickListener(v -> {
            Intent intent = new Intent(CreateActivity.this, EleccionMascotaActivity.class);
            mascotaLauncher.launch(intent);
        });

        // Evento para procesar el guardado en Firestore
        btnGuardar.setOnClickListener(v -> guardarRegistro());
    }

    private void guardarRegistro() {
        ocultarTeclado();
        // Convertimos el DNI a mayúsculas para evitar problemas con letras minúsculas
        String dni = etDni.getText().toString().trim().toUpperCase();
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validación 1: DNI obligatorio
        if (dni.isEmpty()) {
            etDni.setError("El DNI es obligatorio");
            return;
        }

        // Validación 2: Formato estricto de DNI (8 números y 1 letra)
        if (!dni.matches("^[0-9]{8}[A-Z]$")) {
            etDni.setError("Formato de DNI no válido. El formato es 00000000A");
            return;
        }

        // Validación 3: Nombre obligatorio
        if (nombre.isEmpty()) {
            etNombre.setError("El Nombre es obligatorio");
            return;
        }

        // Validación 4: Formato correcto de correo electrónico (si el campo no está vacío)
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Formato de correo no válido (ejemplo@dominio.com)");
            return;
        }

        // Validación 5: Comprobar si el DNI ya existe en Firestore para evitar sobreescrituras accidentales
        db.collection("usuarios").document(dni).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(CreateActivity.this, "Error: Ya existe un registro con este DNI", Toast.LENGTH_LONG).show();
                    } else {
                        // Crear el objeto de datos si todo es correcto
                        Map<String, Object> usuario = new HashMap<>();
                        usuario.put("dni", dni);
                        usuario.put("nombre", nombre);
                        usuario.put("apellidos", apellidos);
                        usuario.put("edad", edad);
                        usuario.put("email", email);
                        usuario.put("mascotaPreferida", mascotasSeleccionadas);

                        // Guardar en Firestore usando el DNI como Clave de Acceso (ID de documento)
                        db.collection("usuarios").document(dni)
                                .set(usuario)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(CreateActivity.this, "Registro guardado correctamente", Toast.LENGTH_SHORT).show();
                                    finish(); // Regresar al menú Home
                                })
                                .addOnFailureListener(e -> Toast.makeText(CreateActivity.this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(CreateActivity.this, "Error al verificar el DNI", Toast.LENGTH_SHORT).show());
    }


    // Gestionar la pulsación de la flecha atrás en la barra superior
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
