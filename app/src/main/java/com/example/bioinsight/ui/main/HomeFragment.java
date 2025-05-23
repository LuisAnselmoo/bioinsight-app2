package com.example.bioinsight.ui.main;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bioinsight.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private ImageView avatarUsuario;
    private TextView textoSaludo, textViewDatos;
    private Button btnActualizar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final int[] avatarImages = {
            R.drawable.user1,
            R.drawable.user2,
            R.drawable.user3,
            R.drawable.user4,
            R.drawable.user5,
            R.drawable.user6,
            R.drawable.user7
    };

    private final String[] avatarNames = {
            "Avatar 1",
            "Avatar 2",
            "Avatar 3",
            "Avatar 4",
            "Avatar 5",
            "Avatar 6",
            "Avatar 7"
    };

    public HomeFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        avatarUsuario = view.findViewById(R.id.avatar_usuario);
        avatarUsuario.setImageResource(R.drawable.user1); // Avatar por defecto

        textoSaludo = view.findViewById(R.id.texto_saludo);
        textViewDatos = view.findViewById(R.id.datos_usuario);
        btnActualizar = view.findViewById(R.id.btn_actualizar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        cargarDatosUsuario();

        btnActualizar.setOnClickListener(v -> mostrarDialogoEditarPerfil());
        // Listener para seleccionar avatar al tocar la imagen
        avatarUsuario.setOnClickListener(v -> mostrarDialogoSeleccionAvatar());

        return view;
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            String correo = user.getEmail();

            db.collection("usuarios").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre");
                            String apellidos = documentSnapshot.getString("apellidos");
                            String username = documentSnapshot.getString("username");
                            Timestamp creation = documentSnapshot.getTimestamp("creationdate");

                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"));
                            String fechaCreacion = (creation != null) ? sdf.format(creation.toDate()) : "No disponible";

                            // Actualizar saludo
                            textoSaludo.setText("¡Hola, " + nombre + "!");

                            // Mostrar info en tarjeta
                            StringBuilder datos = new StringBuilder();
                            datos.append("Correo: ").append(correo).append("\n")
                                    .append("Nombre completo: ").append(nombre).append(" ").append(apellidos).append("\n")
                                    .append("Username: ").append(username).append("\n")
                                    .append("Fecha de creación: ").append(fechaCreacion);

                            textViewDatos.setText(datos.toString());

                            // Cargar avatar guardado (índice)
                            Long avatarIndex = documentSnapshot.getLong("foto");
                            if (avatarIndex != null && avatarIndex >= 0 && avatarIndex < avatarImages.length) {
                                avatarUsuario.setImageResource(avatarImages[avatarIndex.intValue()]);
                            } else {
                                avatarUsuario.setImageResource(R.drawable.user1); // Avatar por defecto
                            }

                        } else {
                            textoSaludo.setText("¡Hola!");
                            textViewDatos.setText("No se encontraron datos del usuario.");
                            avatarUsuario.setImageResource(R.drawable.user1);
                        }
                    })
                    .addOnFailureListener(e -> {
                        textoSaludo.setText("¡Hola!");
                        textViewDatos.setText("Error al obtener datos: " + e.getMessage());
                        avatarUsuario.setImageResource(R.drawable.user1);
                    });
        } else {
            textoSaludo.setText("¡Hola!");
            textViewDatos.setText("No hay sesión activa.");
            avatarUsuario.setImageResource(R.drawable.user1);
        }
    }

    private void mostrarDialogoSeleccionAvatar() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Elige tu avatar");

        // Verificar que tenemos los mismos avatares que nombres
        if (avatarImages.length != avatarNames.length) {
            Toast.makeText(getContext(), "Error en configuración de avatares", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un adaptador personalizado para mostrar imágenes
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.avatar_list_item, R.id.avatar_name, avatarNames) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView avatarImage = view.findViewById(R.id.avatar_image);
                avatarImage.setImageResource(avatarImages[position]);
                return view;
            }
        };

        builder.setAdapter(adapter, (dialog, which) -> {
            // Actualizar imagen inmediatamente
            avatarUsuario.setImageResource(avatarImages[which]);
            // Guardar selección
            guardarAvatarSeleccionado(which);

            Toast.makeText(getContext(), "Avatar seleccionado: " + avatarNames[which], Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void guardarAvatarSeleccionado(int index) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || getContext() == null) return;

        // Mostrar progreso
        ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Guardando avatar...");
        progress.setCancelable(false);
        progress.show();

        db.collection("usuarios").document(user.getUid())
                .update("foto", index)
                .addOnCompleteListener(task -> {
                    progress.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Avatar guardado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al guardar avatar", Toast.LENGTH_SHORT).show();
                        // Revertir a la imagen anterior si falla
                        cargarDatosUsuario();
                    }
                });
    }

    private void mostrarDialogoEditarPerfil() {
        if (getContext() == null || mAuth.getCurrentUser() == null) return;

        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        final EditText etNombre = new EditText(getContext());
        etNombre.setHint("Nombre");

        final EditText etApellidos = new EditText(getContext());
        etApellidos.setHint("Apellidos");

        // Contenedor para los campos
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        // Agregar campos al layout
        layout.addView(etNombre);
        layout.addView(etApellidos);

        // Cargar datos actuales
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        etNombre.setText(documentSnapshot.getString("nombre"));
                        etApellidos.setText(documentSnapshot.getString("apellidos"));
                    }
                });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Editar perfil")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = etNombre.getText().toString().trim();
                    String nuevosApellidos = etApellidos.getText().toString().trim();

                    if (nuevoNombre.isEmpty() || nuevosApellidos.isEmpty() ) {
                        Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    actualizarDatosUsuario(nuevoNombre, nuevosApellidos);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarDatosUsuario(String nombre, String apellidos) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || getContext() == null) return;

        String uid = user.getUid();

        ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Actualizando datos...");
        progress.setCancelable(false);
        progress.show();

        // Crear mapa de actualización
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("apellidos", apellidos);

        db.collection("usuarios").document(uid)
                .update(updates)
                .addOnCompleteListener(task -> {
                    progress.dismiss();

                    if (task.isSuccessful()) {
                        // Actualizar UI
                        textoSaludo.setText("¡Hola, " + nombre + "!");

                        // Refrescar datos
                        cargarDatosUsuario();

                        Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
