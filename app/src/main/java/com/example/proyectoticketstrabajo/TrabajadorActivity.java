package com.example.proyectoticketstrabajo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class TrabajadorActivity extends AppCompatActivity {

    private EditText editTextTitulo, editTextDescripcion;
    private Button buttonCrearTicket, buttonConfirmarResuelto, buttonReabrirTicket;
    private ListView listViewTickets;
    private TicketAdapter ticketAdapter;
    private DatabaseHelper databaseHelper;

    private int trabajadorID = 1;  // ID del trabajador logueado
    private Ticket ticketSeleccionado;  // Para almacenar el ticket seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajador);

        // Inicializar vistas
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        buttonCrearTicket = findViewById(R.id.buttonCrearTicket);
        buttonConfirmarResuelto = findViewById(R.id.buttonConfirmarResuelto);
        buttonReabrirTicket = findViewById(R.id.buttonReabrirTicket);  // Nuevo botón para reabrir ticket
        listViewTickets = findViewById(R.id.listViewTickets);

        // Inicializar la base de datos y el adaptador
        databaseHelper = new DatabaseHelper(this);
        List<Ticket> tickets = databaseHelper.getTicketsByTrabajador(trabajadorID);
        ticketAdapter = new TicketAdapter(this, tickets);

        // Establecer el adaptador para el ListView
        listViewTickets.setAdapter(ticketAdapter);

        // Configurar el listener para la selección de items en el ListView
        listViewTickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Almacenar el ticket seleccionado
                ticketSeleccionado = (Ticket) parent.getItemAtPosition(position);

                // Mostrar mensaje para confirmar la selección
                Toast.makeText(TrabajadorActivity.this, "Seleccionado: " + ticketSeleccionado.getTitulo(), Toast.LENGTH_SHORT).show();
            }
        });

        // Acción del botón para crear un ticket
        buttonCrearTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = editTextTitulo.getText().toString();
                String descripcion = editTextDescripcion.getText().toString();

                if (!titulo.isEmpty() && !descripcion.isEmpty()) {
                    databaseHelper.insertTicket(titulo, descripcion, trabajadorID);
                    Toast.makeText(TrabajadorActivity.this, "Ticket creado", Toast.LENGTH_SHORT).show();
                    // Actualizar la lista de tickets
                    ticketAdapter.updateTickets(databaseHelper.getTicketsByTrabajador(trabajadorID));
                } else {
                    Toast.makeText(TrabajadorActivity.this, "Complete el título y la descripción", Toast.LENGTH_SHORT).show();
                }
            }
        });


// Acción del botón para confirmar que el ticket fue resuelto
        buttonConfirmarResuelto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ticketSeleccionado != null) {
                    // Verificar si el ticket tiene un técnico asignado
                    int tecnicoID = ticketSeleccionado.getTecnicoId();  // Obtener el técnico asignado al ticket

                    if (tecnicoID > 0) {  // Verificar que el técnico está asignado
                        // Confirmar el ticket como resuelto
                        databaseHelper.updateTicketStatus(ticketSeleccionado.getId(), "Finalizado");
                        Toast.makeText(TrabajadorActivity.this, "Ticket confirmado como resuelto", Toast.LENGTH_SHORT).show();

                        // Descontar una falla al técnico asignado
                        databaseHelper.descontarFallaTecnico(tecnicoID);
                        Toast.makeText(TrabajadorActivity.this, "Se descontó una falla al técnico", Toast.LENGTH_SHORT).show();

                        // Actualizar la lista de tickets
                        ticketAdapter.updateTickets(databaseHelper.getTicketsByTrabajador(trabajadorID));
                        ticketSeleccionado = null;  // Restablecer la selección
                    } else {
                        Toast.makeText(TrabajadorActivity.this, "Este ticket no tiene técnico asignado.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TrabajadorActivity.this, "Seleccione un ticket para confirmar como resuelto", Toast.LENGTH_SHORT).show();
                }
            }
        });



        buttonReabrirTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ticketSeleccionado != null) {
                    // Verificar si el ticket tiene un técnico asignado
                    int tecnicoID = ticketSeleccionado.getTecnicoId();  // Obtener el técnico asignado al ticket

                    if (tecnicoID > 0) {  // Verificar que el técnico está asignado
                        try {
                            // Reabrir el ticket
                            databaseHelper.updateTicketStatus(ticketSeleccionado.getId(), "Reabierto");
                            Toast.makeText(TrabajadorActivity.this, "Ticket reabierto", Toast.LENGTH_SHORT).show();

                            // Incrementar la cantidad de fallas del técnico asignado
                            databaseHelper.incrementarFallaTecnico(tecnicoID);
                            Toast.makeText(TrabajadorActivity.this, "Se incrementó una falla al técnico", Toast.LENGTH_SHORT).show();

                            // Actualizar la lista de tickets
                            ticketAdapter.updateTickets(databaseHelper.getTicketsByTrabajador(trabajadorID));
                            ticketSeleccionado = null;  // Restablecer la selección
                        } catch (Exception e) {
                            Toast.makeText(TrabajadorActivity.this, "Error al reabrir el ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TrabajadorActivity.this, "Este ticket no tiene técnico asignado.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TrabajadorActivity.this, "Seleccione un ticket para reabrir", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}


