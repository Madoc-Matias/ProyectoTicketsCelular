package com.example.proyectoticketstrabajo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddUserActivity extends AppCompatActivity {

    private EditText editTextUserName;
    private Spinner spinnerTipoUsuario;
    private Button btnAgregarUsuario;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Inicializar los elementos
        editTextUserName = findViewById(R.id.editTextUserName);
        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUsuario);
        btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario);

        // Inicializar la base de datos
        dbHelper = new DatabaseHelper(this);

        // Evento para el botón de agregar usuario
        btnAgregarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarUsuario();
            }
        });
    }

    private void agregarUsuario() {
        String nombreUsuario = editTextUserName.getText().toString().trim();
        String tipoUsuario = spinnerTipoUsuario.getSelectedItem().toString();

        // Validación de campo vacío
        if (nombreUsuario.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insertar el usuario en la base de datos
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombreUsuario);
        values.put("tipo_usuario", tipoUsuario);
        values.put("contraseña", "");  // Contraseña vacía temporalmente

        long nuevoId = db.insert("Usuarios", null, values);  // Insertar y obtener el ID generado

        if (nuevoId != -1) {
            // Actualizar la contraseña para que sea igual al ID
            ContentValues updateValues = new ContentValues();
            updateValues.put("contraseña", String.valueOf(nuevoId));  // Contraseña igual al ID

            db.update("Usuarios", updateValues, "id=?", new String[]{String.valueOf(nuevoId)});

            Toast.makeText(this, "Usuario agregado correctamente con ID: " + nuevoId, Toast.LENGTH_SHORT).show();
            finish();  // Cerrar la actividad
        } else {
            Toast.makeText(this, "Error al agregar el usuario", Toast.LENGTH_SHORT).show();
        }

        db.close();  // Cerrar la base de datos
    }
}
