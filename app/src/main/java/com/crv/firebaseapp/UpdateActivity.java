package com.crv.firebaseapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateActivity extends AppCompatActivity {

    // Declaración de las variables de la interfaz
    private TextInputEditText etUpdDniBusqueda, etUpdNombre, etUpdApellidos, etUpdEdad, etUpdEmail;
    private TextView tvUpdMascota;
    private Button btnUpdBuscar, btnUpdGuardar;

    // Instancia de la base de datos Firestore y variable para guardar el DNI activo
    private FirebaseFirestore db;
    private String dniActual = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Habilitar el botón "Up" (flecha atrás) en la barra de acción superior
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar la conexión con Firestore
        db = FirebaseFirestore.getInstance();

        // Vincular los componentes de la interfaz XML con las variables Java
        etUpdDniBusqueda = findViewById(R.id.etUpdDniBusqueda);
        etUpdNombre = findViewById(R.id.etUpdNombre);
        etUpdApellidos = findViewById(R.id.etUpdApellidos);
        etUpdEdad = findViewById(R.id.etUpdEdad);
        etUpdEmail = findViewById(R.id.etUpdEmail);
        tvUpdMascota = findViewById(R.id.tvUpdMascota);
        btnUpdBuscar = findViewById(R.id.btnUpdBuscar);
        btnUpdGuardar = findViewById(R.id.btnUpdGuardar);

        // Listeners para los botones de Buscar y Actualizar
        btnUpdBuscar.setOnClickListener(v -> buscarParaEditar());
        btnUpdGuardar.setOnClickListener(v -> actualizarRegistro());
    }

    // Método para consultar Firestore y cargar los datos editables del usuario
    private void buscarParaEditar() {
        ocultarTeclado();
        // Convertir a mayúsculas para asegurar coincidencia
        String dni = etUpdDniBusqueda.getText().toString().trim().toUpperCase();

        // Validación: Verificar que el campo no esté vacío
        if (dni.isEmpty()) {
            etUpdDniBusqueda.setError("Introduce un DNI");
            return;
        }

        // Validación: Comprobar el formato (8 números y 1 letra)
        if (!dni.matches("^[0-9]{8}[A-Z]$")) {
            etUpdDniBusqueda.setError("Formato de DNI no válido. El formato es 00000000A");
            return;
        }

        // Consulta del documento en la colección "usuarios" usando el DNI como Clave de Acceso
        db.collection("usuarios").document(dni).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // Guardar el DNI actual tras confirmar que el registro existe
                        dniActual = dni;

                        // Cargar la información en las cajas de texto editables
                        etUpdNombre.setText(doc.getString("nombre"));
                        etUpdApellidos.setText(doc.getString("apellidos"));
                        etUpdEdad.setText(doc.getString("edad"));
                        etUpdEmail.setText(doc.getString("email"));

                        // Mostrar las mascotas únicamente como texto no editable
                        String mascota = doc.getString("mascotaPreferida");
                        tvUpdMascota.setText("Mascotas preferidas: " + (mascota != null ? mascota : "(ninguna)"));

                        Toast.makeText(this, "Registro cargado para edición", Toast.LENGTH_SHORT).show();
                    } else {
                        // Notificar si el registro no existe y limpiar la entrada
                        Toast.makeText(this, "No existe ningún registro con ese DNI", Toast.LENGTH_LONG).show();
                        etUpdDniBusqueda.setText("");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al buscar en la base de datos", Toast.LENGTH_SHORT).show());
    }

    // Método para sobreescribir los datos modificados en Firestore
    private void actualizarRegistro() {
        ocultarTeclado();
        // Se controla que el usuario haya realizado la búsqueda previa
        if (dniActual.isEmpty()) {
            Toast.makeText(this, "Busca un DNI válido primero", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevoNombre = etUpdNombre.getText().toString().trim();
        // El nombre sigue siendo un campo obligatorio
        if (nuevoNombre.isEmpty()) {
            etUpdNombre.setError("El nombre no puede quedar vacío");
            return;
        }

        String nuevoEmail = etUpdEmail.getText().toString().trim();
        // Formato correcto de correo electrónico (si el campo no está vacío)
        if (!nuevoEmail.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()) {
            etUpdEmail.setError("Formato de correo no válido (ejemplo@dominio.com)");
            return;
        }

        // Actualización de campos específicos del documento existente en Firestore
        db.collection("usuarios").document(dniActual)
                .update(
                        "nombre", nuevoNombre,
                        "apellidos", etUpdApellidos.getText().toString().trim(),
                        "edad", etUpdEdad.getText().toString().trim(),
                        "email", nuevoEmail
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar la pantalla y volver a la HomeActivity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar el registro", Toast.LENGTH_SHORT).show());
    }

    // Gestionar la pulsación de la flecha arriba para volver al menú
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
