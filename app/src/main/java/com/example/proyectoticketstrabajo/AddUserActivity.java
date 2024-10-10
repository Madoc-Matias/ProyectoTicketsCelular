package com.example.proyectoticketstrabajo;

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
        String password = "12345";  // Contraseña temporal

        // Validación de campo vacío
        if (nombreUsuario.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insertar el usuario en la base de datos
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO Usuarios (nombre, tipo_usuario, contraseña) VALUES (?, ?, ?)",
                new String[]{nombreUsuario, tipoUsuario, password});

        Toast.makeText(this, "Usuario agregado correctamente", Toast.LENGTH_SHORT).show();
        finish();  // Cerrar la actividad
    }
}
