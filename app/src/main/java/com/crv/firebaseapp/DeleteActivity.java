package com.crv.firebaseapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteActivity extends AppCompatActivity {

    // Declaración de variables
    private TextInputEditText etDelDni;
    private Button btnDelBuscar, btnDelBorrar;
    private TextView tvDelNombre, tvDelApellidos, tvDelMascota;

    // Instancia de la base de datos Firestore y variable para almacenar el DNI a eliminar
    private FirebaseFirestore db;
    private String dniAEliminar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        // Habilitar la flecha "Up" (atrás) en la barra superior de navegación
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Conectar con la instancia de Firestore
        db = FirebaseFirestore.getInstance();

        // Vincular los componentes del layout XML con Java
        etDelDni = findViewById(R.id.etDelDni);
        btnDelBuscar = findViewById(R.id.btnDelBuscar);
        btnDelBorrar = findViewById(R.id.btnDelBorrar);
        tvDelNombre = findViewById(R.id.tvDelNombre);
        tvDelApellidos = findViewById(R.id.tvDelApellidos);
        tvDelMascota = findViewById(R.id.tvDelMascota);

        // Configurar los eventos para los botones
        btnDelBuscar.setOnClickListener(v -> buscarParaBorrar());
        btnDelBorrar.setOnClickListener(v -> borrarRegistro());
    }

    // Método para buscar el DNI en Firestore antes de confirmar la eliminación
    private void buscarParaBorrar() {
        ocultarTeclado();
        // Convertir la entrada a mayúsculas
        String dni = etDelDni.getText().toString().trim().toUpperCase();

        // El campo DNI no debe estar vacío
        if (dni.isEmpty()) {
            etDelDni.setError("Introduce un DNI");
            return;
        }

        // Comprobar el formato correcto de DNI (8 números y 1 letra)
        if (!dni.matches("^[0-9]{8}[A-Z]$")) {
            etDelDni.setError("Formato de DNI no válido. El formato es 00000000A");
            return;
        }

        // Consultar la colección "usuarios" en Firestore
        db.collection("usuarios").document(dni).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // Guardar la clave primaria una vez confirmada su existencia
                        dniAEliminar = dni;

                        // Cargar los datos visualmente como texto descriptivo
                        tvDelNombre.setText("Nombre: " + doc.getString("nombre"));
                        tvDelApellidos.setText("Apellidos: " + doc.getString("apellidos"));

                        String mascota = doc.getString("mascotaPreferida");
                        tvDelMascota.setText("Mascotas preferidas: " + (mascota != null ? mascota : "(ninguna)"));

                        Toast.makeText(this, "Registro localizado", Toast.LENGTH_SHORT).show();
                    } else {
                        // Notificar si no existe el DNI y vaciar el campo de texto y la vista
                        Toast.makeText(this, "No existe ningún registro con ese DNI", Toast.LENGTH_LONG).show();
                        limpiarVistas();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al buscar en la base de datos", Toast.LENGTH_SHORT).show());
    }

    // Método para eliminar el documento de Firestore
    private void borrarRegistro() {
        ocultarTeclado();
        // Controla que se haya buscado y seleccionado un usuario válido previamente
        if (dniAEliminar.isEmpty()) {
            Toast.makeText(this, "Busca un registro válido primero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ejecutar el borrado definitivo en Firestore usando el DNI
        db.collection("usuarios").document(dniAEliminar).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar la pantalla y volver a HomeActivity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar el registro", Toast.LENGTH_SHORT).show());
    }

    // Método para resetear los componentes de texto y la caja de búsqueda
    private void limpiarVistas() {
        etDelDni.setText("");
        dniAEliminar = "";
        tvDelNombre.setText("Nombre: ");
        tvDelApellidos.setText("Apellidos: ");
        tvDelMascota.setText("Mascotas preferidas: ");
    }

    // Gestor de la acción de la flecha arriba para volver al menú
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
