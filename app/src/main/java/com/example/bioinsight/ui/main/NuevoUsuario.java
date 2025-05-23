package com.example.bioinsight.ui.main;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bioinsight.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

public class NuevoUsuario extends Fragment {

    private EditText newNombre, newApellido, newUsuario, newPassword, confirmarPassword;
    private RadioGroup radioGroupTipoUsuario;
    private RadioButton radioUsuario, radioAdmin;
    private Button btnRegistrar;
    private TextView tvMensaje;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public NuevoUsuario() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nuevo_usuario, container, false);

        newNombre = view.findViewById(R.id.newNombre);
        newApellido = view.findViewById(R.id.newApellido);
        newUsuario = view.findViewById(R.id.newUsuario);
        newPassword = view.findViewById(R.id.newPassword);
        confirmarPassword = view.findViewById(R.id.confirmarPassword);
        radioGroupTipoUsuario = view.findViewById(R.id.radioGroupTipoUsuario);
        radioUsuario = view.findViewById(R.id.radioUsuario);
        radioAdmin = view.findViewById(R.id.radioAdmin);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        tvMensaje = view.findViewById(R.id.tvMensaje);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        return view;
    }

    private void registrarUsuario() {
        // Obtener valores de los campos
        String nombre = newNombre.getText().toString().trim();
        String apellido = newApellido.getText().toString().trim();
        String username = newUsuario.getText().toString().trim();
        String password = newPassword.getText().toString().trim();
        String confirmPass = confirmarPassword.getText().toString().trim();
        boolean isAdmin = radioAdmin.isChecked();

        // Validar campos
        if (nombre.isEmpty() || apellido.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            mostrarMensaje("Por favor complete todos los campos");
            return;
        }

        if (!password.equals(confirmPass)) {
            mostrarMensaje("Las contraseñas no coinciden");
            return;
        }

        if (password.length() < 6) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        // Crear email con dominio @bioinsight.com
        String email = username + "@bioinsight.com";
        //String email = username + "@gmail.com";

        // Registrar usuario en Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            guardarDatosUsuario(user.getUid(), nombre, apellido, username, isAdmin);
                        }
                    } else {
                        mostrarMensaje("Error al registrar: " + task.getException().getMessage());
                    }
                });
    }

    private void guardarDatosUsuario(String userId, String nombre, String apellido, String username, boolean isAdmin) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("apellidos", apellido);
        usuario.put("username", username);
        usuario.put("admin", isAdmin);
        usuario.put("foto", 0);
        usuario.put("creationdate", FieldValue.serverTimestamp());

        db.collection("usuarios").document(userId)
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                    limpiarFormulario();
                })
                .addOnFailureListener(e -> {
                    mostrarMensaje("Error al guardar datos: " + e.getMessage());
                });
    }

    private void mostrarMensaje(String mensaje) {
        tvMensaje.setText(mensaje);
        tvMensaje.setVisibility(View.VISIBLE);
    }

    private void limpiarFormulario() {
        newNombre.setText("");
        newApellido.setText("");
        newUsuario.setText("");
        newPassword.setText("");
        confirmarPassword.setText("");
        radioUsuario.setChecked(true);
        tvMensaje.setVisibility(View.GONE);
    }
}