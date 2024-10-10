package com.example.proyectoticketstrabajo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CambiarContrasenaActivity extends AppCompatActivity {

    private EditText editTextOldPassword, editTextNewPassword, editTextConfirmNewPassword;
    private Button btnCambiarContrasena, btnCancelar;
    private DatabaseHelper dbHelper;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);

        // Obtener el ID del usuario pasado desde el LoginActivity
        userId = getIntent().getStringExtra("user_id");

        // Inicializar la base de datos
        dbHelper = new DatabaseHelper(this);

        // Inicializar los elementos de la interfaz
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword);
        btnCambiarContrasena = findViewById(R.id.btnCambiarContrasena);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Evento para cambiar la contraseña
        btnCambiarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarContrasena();
            }
        });

        // Evento para cancelar el cambio de contraseña
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar la actividad y volver al Login
                finish();
            }
        });
    }

    private void cambiarContrasena() {
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmNewPassword = editTextConfirmNewPassword.getText().toString().trim();

        // Verificar que la nueva contraseña coincide con la confirmación
        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "La nueva contraseña no coincide con la confirmación", Toast.LENGTH_SHORT).show();
            return;
        }



        // Cambiar la contraseña en la base de datos
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("contraseña", newPassword);  // Actualizar la contraseña con la nueva

        int rowsAffected = db.update("Usuarios", values, "id=?", new String[]{userId});
        if (rowsAffected > 0) {
            Toast.makeText(this, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
            finish();  // Cerrar la actividad después de cambiar la contraseña
        } else {
            Toast.makeText(this, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
        }
    }
}
