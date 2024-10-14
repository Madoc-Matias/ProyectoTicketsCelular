package com.example.proyectoticketstrabajo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private Button btnAgregarUsuario, btnBloquearUsuario, btnDesbloquearUsuario, btnVerUsuarios, btnBlanquearContraseña, btnReabrirTicket;
    private DatabaseHelper dbHelper;
    private EditText editTextUserId, editTextTicketId;
    private ListView listTickets, listComentarios;
    private String selectedTicketId; // Variable para almacenar el ticket seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DatabaseHelper(this);

        // Inicializar los elementos de la interfaz
        btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario);
        btnBloquearUsuario = findViewById(R.id.btnBloquearUsuario);
        btnDesbloquearUsuario = findViewById(R.id.btnDesbloquearUsuario);
        btnVerUsuarios = findViewById(R.id.btnVerUsuarios);
        btnBlanquearContraseña = findViewById(R.id.btnBlanquearContraseña);
        btnReabrirTicket = findViewById(R.id.btnReabrirTicket);
        editTextUserId = findViewById(R.id.editTextUserId);
        listTickets = findViewById(R.id.listTickets);
        listComentarios = findViewById(R.id.listComentarios);

        // Configurar eventos para los botones
        btnAgregarUsuario.setOnClickListener(v -> agregarUsuario());
        btnBloquearUsuario.setOnClickListener(v -> bloquearUsuario());
        btnDesbloquearUsuario.setOnClickListener(v -> desbloquearUsuario());
        btnVerUsuarios.setOnClickListener(v -> verUsuarios());
        btnBlanquearContraseña.setOnClickListener(v -> blanquearContraseña());
        btnReabrirTicket.setOnClickListener(v -> reabrirTicket());

        // Cargar los tickets atendidos para reabrir
        cargarTickets();

        // Evento para seleccionar un ticket de la lista mediante "touch"
        listTickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el ticket seleccionado
                String selectedTicket = (String) parent.getItemAtPosition(position);
                selectedTicketId = selectedTicket.split(" - ")[0];  // Almacenar solo el ID del ticket
                Toast.makeText(AdminActivity.this, "Ticket seleccionado: " + selectedTicket, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarUsuario() {
        Intent intent = new Intent(AdminActivity.this, AddUserActivity.class);
        startActivity(intent);
    }

    private void bloquearUsuario() {
        String userId = editTextUserId.getText().toString();
        if (!userId.isEmpty()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("bloqueado", 1); // Marcar como bloqueado
            int rowsAffected = db.update("Usuarios", values, "id=?", new String[]{userId});
            if (rowsAffected > 0) {
                Toast.makeText(this, "Usuario bloqueado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al bloquear usuario", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingrese un ID de usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void desbloquearUsuario() {
        String userId = editTextUserId.getText().toString();

        if (!userId.isEmpty()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                // Desbloquear al usuario
                ContentValues values = new ContentValues();
                values.put("bloqueado", 0);  // Marcar como desbloqueado

                int rowsAffected = db.update("Usuarios", values, "id=?", new String[]{userId});
                if (rowsAffected > 0) {
                    // Descontar 2 fallas del usuario desbloqueado
                    dbHelper.descontarFallaTecnico(Integer.parseInt(userId));

                    Toast.makeText(this, "Usuario desbloqueado y 1 fallas descontadas exitosamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al desbloquear usuario", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error al procesar el desbloqueo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                db.close();
            }
        } else {
            Toast.makeText(this, "Ingrese un ID de usuario", Toast.LENGTH_SHORT).show();
        }
    }


    private void blanquearContraseña() {
        String userId = editTextUserId.getText().toString();
        if (!userId.isEmpty()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("contraseña", userId); // Restablecer la contraseña a su ID
            int rowsAffected = db.update("Usuarios", values, "id=?", new String[]{userId});
            if (rowsAffected > 0) {
                Toast.makeText(this, "Contraseña blanqueada exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al blanquear contraseña", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingrese un ID de usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void reabrirTicket() {
        if (selectedTicketId != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            try {
                // Obtener el técnico asignado al ticket
                String query = "SELECT tecnico_id FROM Tickets WHERE id = ?";
                Cursor cursor = db.rawQuery(query, new String[]{selectedTicketId});

                if (cursor.moveToFirst()) {
                    int tecnicoID = cursor.getInt(0);  // Obtener el ID del técnico

                    // Verificar si existe un técnico asignado
                    if (tecnicoID != 0) {
                        // Reabrir el ticket
                        ContentValues values = new ContentValues();
                        values.put("estado", "Reabierto");  // Cambiar el estado a "Reabierto"
                        int rowsAffected = db.update("Tickets", values, "id=?", new String[]{selectedTicketId});

                        if (rowsAffected > 0) {
                            Toast.makeText(this, "Ticket reabierto exitosamente", Toast.LENGTH_SHORT).show();
                            cargarTickets();  // Recargar la lista de tickets

                            // Incrementar 1 marca al técnico asignado
                            dbHelper.incrementarFallaTecnicoAdmin(tecnicoID);
                            Toast.makeText(this, "Se ha incrementado una marca al técnico.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error al reabrir el ticket", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "El ticket no tiene técnico asignado.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No se encontró el técnico asignado.", Toast.LENGTH_SHORT).show();
                }

                cursor.close();
            } catch (Exception e) {
                Toast.makeText(this, "Error al procesar el ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                db.close();
            }
        } else {
            Toast.makeText(this, "Seleccione un ticket", Toast.LENGTH_SHORT).show();
        }
    }






    // Cargar los tickets atendidos para reabrir
    private void cargarTickets() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, titulo FROM Tickets WHERE estado = 'Atendido'", null);

        List<String> ticketList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String ticket = cursor.getString(0) + " - " + cursor.getString(1);  // Formato: "ID - Título"
                ticketList.add(ticket);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ticketList);
        listTickets.setAdapter(adapter);
    }


    private void verUsuarios() {
        Intent intent = new Intent(AdminActivity.this, UserListActivity.class);
        startActivity(intent);
    }



}
