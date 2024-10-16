package com.example.proyectoticketstrabajo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;  // Importar para manejo de logs
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextID, editTextPassword;
    private Button btnLogin, btnCambiarContraseña;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar los elementos de la interfaz
        editTextID = findViewById(R.id.editTextID);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCambiarContraseña = findViewById(R.id.btnCambiarContraseña);

        // Inicializar la base de datos
        dbHelper = new DatabaseHelper(this);

        // Evento del botón de inicio de sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextID.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validación de campos vacíos
                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, complete ambos campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Verificar credenciales usando DatabaseHelper
                    if (dbHelper.authenticateUser(id, password)) {

                        // Verificar si la contraseña es igual al ID
                        if (password.equals(id)) {
                            // Mostrar mensaje específico
                            Toast.makeText(LoginActivity.this, "La contraseña no puede ser igual al ID. Cambie la contraseña.", Toast.LENGTH_SHORT).show();
                            return;  // Detener el flujo y pedir que cambie la contraseña
                        }

                        // Verificar si el usuario está bloqueado
                        if (dbHelper.isUserBlocked(id)) {
                            // Mostrar mensaje específico
                            Toast.makeText(LoginActivity.this, "El usuario está bloqueado. Contacte al administrador.", Toast.LENGTH_SHORT).show();
                            return;  // Detener el flujo ya que el usuario está bloqueado
                        }

                        // Redirigir según el tipo de usuario
                        Intent intent;
                        String tipoUsuario = dbHelper.getUserType(id);

                        switch (tipoUsuario) {
                            case "Administrador":
                                intent = new Intent(LoginActivity.this, AdminActivity.class);
                                break;
                            case "Técnico":
                                intent = new Intent(LoginActivity.this, TecnicoActivity.class);
                                intent.putExtra("tecnico_id", id); // Pasar ID del técnico
                                break;
                            case "Trabajador":
                                intent = new Intent(LoginActivity.this, TrabajadorActivity.class);
                                intent.putExtra("trabajador_id", id); // Pasar ID del trabajador
                                break;
                            default:
                                Toast.makeText(LoginActivity.this, "Tipo de usuario no válido", Toast.LENGTH_SHORT).show();
                                return;
                        }
                        startActivity(intent);
                        finish(); // Cerrar la actividad de login
                    } else {
                        // Mostrar mensaje cuando las credenciales no coinciden
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas. Verifique su ID o contraseña.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Manejo de errores
                    Log.e("LoginActivity", "Error al autenticar usuario", e);
                    Toast.makeText(LoginActivity.this, "Error al autenticar. Por favor, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Evento para cambiar la contraseña
        btnCambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextID.getText().toString().trim();  // Obtener el ID del usuario
                if (!id.isEmpty()) {
                    Intent intent = new Intent(LoginActivity.this, CambiarContrasenaActivity.class);
                    intent.putExtra("user_id", id);  // Pasar el ID del usuario logueado a CambiarContrasenaActivity
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Ingrese su ID primero", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
