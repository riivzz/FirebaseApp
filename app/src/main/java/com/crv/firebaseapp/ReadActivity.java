package com.crv.firebaseapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReadActivity extends AppCompatActivity {

    private TextInputEditText etBuscarDni;
    private Button btnBuscarDni;
    private TextView tvResDni, tvResNombre, tvResApellidos, tvResEdad, tvResEmail, tvResMascota;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        // Menú "up" para volver a la HomeActivity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar base de datos Firestore
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        tvResDni = findViewById(R.id.tvResDni);
        etBuscarDni = findViewById(R.id.etBuscarDni);
        btnBuscarDni = findViewById(R.id.btnBuscarDni);
        tvResNombre = findViewById(R.id.tvResNombre);
        tvResApellidos = findViewById(R.id.tvResApellidos);
        tvResEdad = findViewById(R.id.tvResEdad);
        tvResEmail = findViewById(R.id.tvResEmail);
        tvResMascota = findViewById(R.id.tvResMascota);

        // Listener del botón buscar
        btnBuscarDni.setOnClickListener(v -> buscarRegistro());
    }

    private void buscarRegistro() {
        ocultarTeclado();
        // Convertimos a mayúsculas para evitar problemas si el usuario escribe la letra en minúscula
        String dni = etBuscarDni.getText().toString().trim().toUpperCase();

        if (dni.isEmpty()) {
            etBuscarDni.setError("Introduce un DNI");
            return;
        }

        // Comprueba el formato de 8 números y 1 letra
        if (!dni.matches("^[0-9]{8}[A-Z]$")) {
            etBuscarDni.setError("Formato de DNI no válido. El formato es 00000000A");
            etBuscarDni.setText("");
            return;
        }

        // Consultar el documento en Firestore utilizando el DNI validado
        db.collection("usuarios").document(dni).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extraemos los datos del documento
                        String nombre = documentSnapshot.getString("nombre");
                        String apellidos = documentSnapshot.getString("apellidos");
                        String edad = documentSnapshot.getString("edad");
                        String email = documentSnapshot.getString("email");
                        String mascota = documentSnapshot.getString("mascotaPreferida");

                        // Mostrar los datos en formato de texto
                        tvResDni.setText("DNI: " + dni);
                        tvResNombre.setText("Nombre: " + (nombre != null ? nombre : ""));
                        tvResApellidos.setText("Apellidos: " + (apellidos != null ? apellidos : ""));
                        tvResEdad.setText("Edad: " + (edad != null ? edad : ""));
                        tvResEmail.setText("Email: " + (email != null ? email : ""));
                        tvResMascota.setText("Mascotas preferidas: " + (mascota != null ? mascota : "(ninguna)"));

                        Toast.makeText(this, "Registro encontrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No se encontró ningún registro con ese DNI", Toast.LENGTH_LONG).show();
                        limpiarResultados();
                    }
                    // Limpia la casilla del DNI tras completar la búsqueda correcta o fallida
                    etBuscarDni.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar en la base de datos", Toast.LENGTH_SHORT).show();
                    etBuscarDni.setText(""); // Limpia la casilla en caso de error de red o servidor
                });
    }

    private void limpiarResultados() {
        tvResDni.setText("DNI: ");
        tvResNombre.setText("Nombre: ");
        tvResApellidos.setText("Apellidos: ");
        tvResEdad.setText("Edad: ");
        tvResEmail.setText("Email: ");
        tvResMascota.setText("Mascotas preferidas: ");
    }

    // Gestionar el botón "up" para volver a la HomeActivity
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
