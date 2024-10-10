package com.example.proyectoticketstrabajo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private ListView listViewUsuarios;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        listViewUsuarios = findViewById(R.id.listViewUsuarios);
        dbHelper = new DatabaseHelper(this);

        // Cargar los usuarios desde la base de datos
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        ArrayList<String> usuarios = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Obtener todos los usuarios de la tabla Usuarios, incluyendo la contraseña
        Cursor cursor = db.rawQuery("SELECT id, nombre, tipo_usuario, contraseña FROM Usuarios", null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String nombre = cursor.getString(1);
                String tipoUsuario = cursor.getString(2);
                String password = cursor.getString(3);  // Obtener la contraseña

                // Añadir nombre, tipo de usuario y contraseña a la lista de usuarios
                usuarios.add("ID: " + id + "- Nombre: " + nombre + " - Tipo: " + tipoUsuario + " - Contraseña: " + password);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Mostrar los usuarios en el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usuarios);
        listViewUsuarios.setAdapter(adapter);
    }
}

