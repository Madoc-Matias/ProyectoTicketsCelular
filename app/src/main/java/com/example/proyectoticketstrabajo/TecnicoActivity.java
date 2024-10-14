package com.example.proyectoticketstrabajo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class TecnicoActivity extends AppCompatActivity {

    private ListView listViewTickets;
    private Button btnTomarTicket, btnResolverTicket, btnLiberarTicket, btnAgregarComentario, btnVerDescripcion;
    private EditText editTextComentario;
    private TextView textViewDescripcion;  // Nueva declaración
    private DatabaseHelper dbHelper;
    private String tecnicoID;
    private TicketAdapter ticketAdapter;
    private List<Ticket> ticketsList;
    private int selectedTicketID = -1; // Variable para almacenar el ID del ticket seleccionado
    private Ticket selectedTicket;  // Nueva declaración para el ticket seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnico);

        // Obtener ID del técnico del intent
        tecnicoID = getIntent().getStringExtra("tecnico_id");

        // Inicializar elementos gráficos
        listViewTickets = findViewById(R.id.listViewTickets);
        btnTomarTicket = findViewById(R.id.btnTomarTicket);
        btnResolverTicket = findViewById(R.id.btnResolverTicket);
        btnLiberarTicket = findViewById(R.id.btnLiberarTicket);
        btnVerDescripcion = findViewById(R.id.buttonVerDescripcion);  // Usar el ID correcto
        // Inicialización
        textViewDescripcion = findViewById(R.id.textViewDescripcion);  // Inicialización
        textViewDescripcion.setVisibility(View.GONE);  // Ocultar por defecto

        // Inicializar DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Cargar los tickets (Atendidos, No atendidos y Reabiertos)
        cargarTickets();

        // Evento para tomar un ticket
        btnTomarTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTicketID != -1) {
                    // Verificar si el técnico puede tomar otro ticket
                    if (dbHelper.puedeTomarMasTickets(Integer.parseInt(tecnicoID))) {
                        dbHelper.tomarTicket(selectedTicketID, Integer.parseInt(tecnicoID));
                        cargarTickets();
                        Toast.makeText(TecnicoActivity.this, "Ticket tomado", Toast.LENGTH_SHORT).show();
                    } else {
                        // Mostrar mensaje si el técnico ya tiene 3 tickets asignados
                        Toast.makeText(TecnicoActivity.this, "No puedes tomar más de 3 tickets.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TecnicoActivity.this, "Selecciona un ticket", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Evento para ver la descripción del ticket seleccionado
        btnVerDescripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTicket != null) {
                    // Mostrar la descripción del ticket seleccionado
                    textViewDescripcion.setText(selectedTicket.getDescripcion());
                    textViewDescripcion.setVisibility(View.VISIBLE);  // Hacer visible el TextView
                } else {
                    Toast.makeText(TecnicoActivity.this, "Selecciona un ticket", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Evento para resolver un ticket
        btnResolverTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTicketID != -1) {
                    try {
                        dbHelper.resolverTicket(selectedTicketID);
                        cargarTickets();  // Recargar los tickets después de resolver
                        Toast.makeText(TecnicoActivity.this, "Ticket resuelto", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(TecnicoActivity.this, "Error al resolver el ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TecnicoActivity.this, "Seleccione un ticket", Toast.LENGTH_SHORT).show();
                }
            }
        });

// Evento para liberar un ticket
        btnLiberarTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTicketID != -1) {
                    dbHelper.liberarTicket(selectedTicketID);
                    cargarTickets();
                    Toast.makeText(TecnicoActivity.this, "Ticket liberado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TecnicoActivity.this, "Selecciona un ticket", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // Evento para seleccionar un ticket de la lista
        listViewTickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTicket = ticketsList.get(position);  // Guardamos el ticket seleccionado
                selectedTicketID = selectedTicket.getId();  // Guardamos el ID del ticket seleccionado
                Toast.makeText(TecnicoActivity.this, "Ticket seleccionado: " + selectedTicket.getTitulo(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para cargar tanto los tickets atendidos, no atendidos y reabiertos
    private void cargarTickets() {
        ticketsList = dbHelper.obtenerTicketsAtendidosNoAtendidosYReabiertos();  // Llamamos al método que obtiene ambos tipos de tickets
        ticketAdapter = new TicketAdapter(this, ticketsList);
        listViewTickets.setAdapter(ticketAdapter);
    }
}
