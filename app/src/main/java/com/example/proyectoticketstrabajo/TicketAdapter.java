package com.example.proyectoticketstrabajo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TicketAdapter extends ArrayAdapter<Ticket> {
    private Context context;
    private List<Ticket> ticketList;

    public TicketAdapter(Context context, List<Ticket> ticketList) {
        super(context, 0, ticketList);
        this.context = context;
        this.ticketList = ticketList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ticket_item, parent, false);
        }

        // Obtén los elementos visuales del layout
        TextView ticketTitle = convertView.findViewById(R.id.ticketTitle);
        TextView ticketStatus = convertView.findViewById(R.id.ticketStatus);
        TextView ticketFallos = convertView.findViewById(R.id.ticketFallos); // Campo para mostrar el número de fallos

        // Obtén el ticket actual
        Ticket ticket = ticketList.get(position);

        // Asigna los valores del ticket a los elementos visuales
        ticketTitle.setText(ticket.getTitulo());
        ticketStatus.setText(ticket.getEstado());

        // Mostrar el número de fallos si es mayor a 0
        if (ticket.getFallos() > 0) {
            ticketFallos.setVisibility(View.VISIBLE);  // Mostrar si hay fallos
            ticketFallos.setText("Fallos: " + ticket.getFallos());  // Asignar texto con el número de fallos
        } else {
            ticketFallos.setVisibility(View.GONE);  // Ocultar si no hay fallos
        }

        return convertView;
    }

    // Método para actualizar la lista de tickets
    public void updateTickets(List<Ticket> newTickets) {
        this.ticketList.clear();  // Limpia la lista actual
        this.ticketList.addAll(newTickets);  // Agrega los nuevos tickets
        notifyDataSetChanged();  // Notifica al adaptador que los datos han cambiado
    }


}

