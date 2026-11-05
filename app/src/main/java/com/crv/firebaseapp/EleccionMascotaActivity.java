package com.crv.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EleccionMascotaActivity extends AppCompatActivity {

    private CheckBox cbPerro, cbGato, cbPajaro, cbPez;
    private Button btnConfirmarMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eleccion_mascota);

        //1. Vincular los 4 CheckBoxes de la interfaz
        cbPerro = findViewById(R.id.cbPerro);
        cbGato = findViewById(R.id.cbGato);
        cbPajaro = findViewById(R.id.cbPajaro);
        cbPez = findViewById(R.id.cbPez);
        btnConfirmarMascota = findViewById(R.id.btnConfirmarMascota);

        //2. Lógica para procesar la selección
        btnConfirmarMascota.setOnClickListener(v -> {
            // Usamos minúsculas inicialmente
            ArrayList<String> elegidas = new ArrayList<>();
            if (cbPerro.isChecked()) elegidas.add("perro");
            if (cbGato.isChecked()) elegidas.add("gato");
            if (cbPajaro.isChecked()) elegidas.add("pájaro");
            if (cbPez.isChecked()) elegidas.add("pez");

            String resultadoFinal;

            if (elegidas.isEmpty()) {
                resultadoFinal = "(ninguna)";
            } else if (elegidas.size() == 1) {
                // Si solo hay una, simplemente le ponemos la primera letra mayúscula
                String m = elegidas.get(0);
                resultadoFinal = m.substring(0, 1).toUpperCase() + m.substring(1);
            } else {
                // Extraemos la última para ponerle el " y "
                String ultima = elegidas.remove(elegidas.size() - 1);
                // Unimos el resto con comas
                String unidas = String.join(", ", elegidas) + " y " + ultima;
                // Ponemos la primera letra de toda la frase en mayúscula
                resultadoFinal = unidas.substring(0, 1).toUpperCase() + unidas.substring(1);
            }

            //3. Devolver los datos a la Activity que nos llamó (CreateActivity)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("mascotas_seleccionadas", resultadoFinal);
            setResult(RESULT_OK, resultIntent);

            finish(); //Se cierra esta pantalla

        });

    }

}
