package com.example.proyectoticketstrabajo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Definir constantes para la base de datos
    private static final String DATABASE_NAME = "TicketSystemDB";
    private static final int DATABASE_VERSION = 5;

    // Tabla de usuarios
    private static final String TABLE_USUARIOS = "Usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_TIPO_USUARIO = "tipo_usuario";
    private static final String COLUMN_CONTRASEÑA = "contraseña";
    private static final String COLUMN_BLOQUEADO = "bloqueado";
    private static final String COLUMN_FALLAS = "fallas"; // Nueva columna para almacenar fallas

    // Tabla de tickets
    private static final String TABLE_TICKETS = "Tickets";
    private static final String COLUMN_TITULO = "titulo";
    private static final String COLUMN_DESCRIPCION = "descripcion";
    private static final String COLUMN_ESTADO = "estado";
    private static final String COLUMN_TRABAJADOR_ID = "trabajador_id";
    private static final String COLUMN_TECNICO_ID = "tecnico_id";


    // Tabla de comentarios
    private static final String TABLE_COMENTARIOS = "Comentarios";
    private static final String COLUMN_TICKET_ID = "ticket_id";
    private static final String COLUMN_COMENTARIO = "comentario";
    private static final String COLUMN_FECHA = "fecha";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de usuarios
        String createTableUsuarios = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT, " +
                COLUMN_TIPO_USUARIO + " TEXT, " +
                COLUMN_CONTRASEÑA + " TEXT, " +
                COLUMN_BLOQUEADO + " INTEGER DEFAULT 0, " +
                COLUMN_FALLAS + " INTEGER DEFAULT 0)";  // Nueva columna de fallas
        db.execSQL(createTableUsuarios);

        // Crear tabla de tickets
        String createTableTickets = "CREATE TABLE " + TABLE_TICKETS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITULO + " TEXT, " +
                COLUMN_DESCRIPCION + " TEXT, " +
                COLUMN_ESTADO + " TEXT DEFAULT 'No atendido', " +
                COLUMN_TRABAJADOR_ID + " INTEGER, " +
                COLUMN_TECNICO_ID + " INTEGER, " +

                "FOREIGN KEY(" + COLUMN_TRABAJADOR_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_TECNICO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";
        db.execSQL(createTableTickets);

        // Crear tabla de comentarios
        String createTableComentarios = "CREATE TABLE " + TABLE_COMENTARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TICKET_ID + " INTEGER, " +
                COLUMN_TECNICO_ID + " INTEGER, " +
                COLUMN_COMENTARIO + " TEXT, " +
                COLUMN_FECHA + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_TICKET_ID + ") REFERENCES " + TABLE_TICKETS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_TECNICO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";
        db.execSQL(createTableComentarios);

        // Insertar usuario administrador por defecto
        insertarUsuarioAdmin(db);
    }
    // Obtener el ID del técnico asignado a un ticket específico
    public int getTecnicoIdFromTicket(int ticketID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tecnico_id FROM Tickets WHERE id = ?", new String[]{String.valueOf(ticketID)});
        int tecnicoID = -1;
        if (cursor.moveToFirst()) {
            tecnicoID = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return tecnicoID;
    }

    private void insertarUsuarioAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, "admin");
        values.put(COLUMN_TIPO_USUARIO, "Administrador");
        values.put(COLUMN_CONTRASEÑA, "1234");
        values.put(COLUMN_BLOQUEADO, 0);
        db.insert(TABLE_USUARIOS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMENTARIOS);
        onCreate(db);
    }

    // Método para autenticar usuario con manejo de excepciones
    public boolean authenticateUser(String id, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Consulta para autenticar al usuario
            String query = "SELECT * FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_ID + "=? AND " + COLUMN_CONTRASEÑA + "=?";
            cursor = db.rawQuery(query, new String[]{id, password});

            // Verificar si la contraseña es igual al ID (no permitir login)
            if (password.equals(id)) {
                Log.e("DatabaseHelper", "La contraseña no puede ser igual al ID.");
                return false;
            }

            boolean isAuthenticated = cursor.getCount() > 0;
            cursor.close();
            return isAuthenticated;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error al autenticar usuario", e);
            return false; // En caso de error, no autenticar
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public String getUserType(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT " + COLUMN_TIPO_USUARIO + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_ID + "=?";
            cursor = db.rawQuery(query, new String[]{id});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return null;
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error al obtener el tipo de usuario", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean isUserBlocked(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT " + COLUMN_BLOQUEADO + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_ID + "=?";
            cursor = db.rawQuery(query, new String[]{id});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0) == 1;
            }
            return false;
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error al verificar si el usuario está bloqueado", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    // ========== MÉTODOS PARA LA GESTIÓN DE TICKETS ==========

    public void insertTicket(String titulo, String descripcion, int trabajadorID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_DESCRIPCION, descripcion);
        values.put(COLUMN_TRABAJADOR_ID, trabajadorID);
        db.insert(TABLE_TICKETS, null, values);
        db.close();
    }

    public ArrayList<Ticket> getTicketsByTrabajador(int trabajadorID) {
        ArrayList<Ticket> tickets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Asegurarse de obtener el técnico asignado en la consulta
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_TRABAJADOR_ID + "=? AND " + COLUMN_ESTADO + " != 'Finalizado'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(trabajadorID)});

        if (cursor.moveToFirst()) {
            do {
                Ticket ticket = new Ticket();
                ticket.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                ticket.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITULO)));
                ticket.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)));
                ticket.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)));

                ticket.setTecnicoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TECNICO_ID)));  // Asegúrate de obtener el tecnico_id
                tickets.add(ticket);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tickets;
    }


    public void updateTicketStatus(int ticketId, String estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESTADO, estado);
        db.update(TABLE_TICKETS, values, COLUMN_ID + "=?", new String[]{String.valueOf(ticketId)});
        db.close();
    }

    // ========== MÉTODOS ADICIONALES PARA GESTIÓN DE TICKETS ==========

    public void tomarTicket(int ticketID, int tecnicoID) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar cuántos tickets ya tiene asignados el técnico en estado 'No atendido' o 'Atendido'
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TICKETS + " WHERE " + COLUMN_TECNICO_ID + " = ? AND " + COLUMN_ESTADO + " IN ('No atendido', 'Atendido')", new String[]{String.valueOf(tecnicoID)});
        cursor.moveToFirst();
        int numTicketsAsignados = cursor.getInt(0);  // Obtener el número de tickets asignados en esos estados
        cursor.close();

        // Si el técnico tiene menos de 3 tickets en esos estados, permitir que tome otro
        if (numTicketsAsignados < 3) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ESTADO, "Atendido");  // Cambiar el estado a "Atendido"
            values.put(COLUMN_TECNICO_ID, tecnicoID);  // Asignar el ID del técnico
            db.update(TABLE_TICKETS, values, COLUMN_ID + "=?", new String[]{String.valueOf(ticketID)});
            Log.d("DatabaseHelper", "Ticket asignado al técnico con ID: " + tecnicoID);
        } else {
            // Si ya tiene 3 tickets en estado 'No atendido' o 'Atendido', no permitir tomar más
            Log.d("DatabaseHelper", "El técnico con ID " + tecnicoID + " ya tiene 3 tickets en estado 'No atendido' o 'Atendido'.");
        }

        db.close();
    }




    public void resolverTicket(int ticketID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESTADO, "Resuelto");
        db.update(TABLE_TICKETS, values, COLUMN_ID + "=?", new String[]{String.valueOf(ticketID)});
        db.close();
    }

    public void liberarTicket(int ticketID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESTADO, "No atendido");
        values.putNull(COLUMN_TECNICO_ID);
        db.update(TABLE_TICKETS, values, COLUMN_ID + "=?", new String[]{String.valueOf(ticketID)});
        db.close();
    }

    public void agregarComentario(int ticketID, int tecnicoID, String comentario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TICKET_ID, ticketID);
        values.put(COLUMN_TECNICO_ID, tecnicoID);
        values.put(COLUMN_COMENTARIO, comentario);
        db.insert(TABLE_COMENTARIOS, null, values);
        db.close();
    }

    // Obtener tickets en los estados 'Atendido', 'No atendido' y 'Reabierto'
    public ArrayList<Ticket> obtenerTicketsAtendidosNoAtendidosYReabiertos() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_ESTADO + " IN ('Atendido', 'No atendido', 'Reabierto')";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Ticket ticket = new Ticket();
                ticket.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                ticket.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITULO)));
                ticket.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)));
                ticket.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)));

                tickets.add(ticket);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tickets;
    }
    public boolean puedeTomarMasTickets(int tecnicoID) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Contar cuántos tickets en estado 'No atendido' o 'Atendido' tiene el técnico
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TICKETS + " WHERE " + COLUMN_TECNICO_ID + " = ? AND " + COLUMN_ESTADO + " IN ('No atendido', 'Atendido')", new String[]{String.valueOf(tecnicoID)});
        cursor.moveToFirst();
        int numTicketsAsignados = cursor.getInt(0);  // Obtener el número de tickets en esos estados
        cursor.close();
        db.close();

        // Retornar true si tiene menos de 3 tickets en esos estados
        return numTicketsAsignados < 3;
    }




    // ========== MÉTODOS PARA MANEJO DE FALLAS ==========

    public void incrementarFallaTecnico(int tecnicoId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Incrementar las fallas del técnico
        db.execSQL("UPDATE " + TABLE_USUARIOS + " SET " + COLUMN_FALLAS + " = " + COLUMN_FALLAS + " + 1 WHERE " + COLUMN_ID + " = ?", new Object[]{tecnicoId});

        // Obtener la cantidad actual de fallas después de incrementarlas
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_FALLAS + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(tecnicoId)});
        if (cursor.moveToFirst()) {
            int fallasActuales = cursor.getInt(0);

            // Si las fallas llegan a 6, bloquear al técnico
            if (fallasActuales >= 6) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_BLOQUEADO, 1);  // Bloquear al técnico
                db.update(TABLE_USUARIOS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(tecnicoId)});
                // Puedes agregar un mensaje de log para seguimiento
                Log.d("DatabaseHelper", "Técnico con ID " + tecnicoId + " bloqueado por alcanzar 6 fallas.");
            }
        }

        cursor.close();
        db.close();
    }

    public void descontarFallaTecnico(int tecnicoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_FALLAS + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(tecnicoId)});
        if (cursor.moveToFirst()) {
            int fallasActuales = cursor.getInt(0);
            if (fallasActuales > 0) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_FALLAS, fallasActuales - 1);
                db.update(TABLE_USUARIOS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(tecnicoId)});
            }
        }
        cursor.close();
        db.close();
    }

    public int obtenerFallasTecnico(int tecnicoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_FALLAS + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(tecnicoId)});
        int fallas = 0;
        if (cursor.moveToFirst()) {
            fallas = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return fallas;
    }
}
