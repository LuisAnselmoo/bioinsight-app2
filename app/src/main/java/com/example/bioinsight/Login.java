package com.example.bioinsight;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

public class Login extends DialogFragment {

    private EditText usuario, password, nuevoUsuario, nuevaPassword, nuevoNombre, nuevoApellido, confirmarPassword;
    private LinearLayout layoutLogin, layoutRegistro;
    private Button botonLogin, botonRegistrar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView botonToggle;

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inputs Login
        usuario = view.findViewById(R.id.usernameInput);
        password = view.findViewById(R.id.passwordInput);
        // Inputs Registro
        nuevoUsuario = view.findViewById(R.id.newUsuario);
        nuevaPassword = view.findViewById(R.id.newPassword);
        confirmarPassword = view.findViewById(R.id.confirmarPassword);
        nuevoNombre = view.findViewById(R.id.newNombre);
        nuevoApellido = view.findViewById(R.id.newApellido);

        // Layouts
        layoutLogin = view.findViewById(R.id.layoutLogin);
        layoutRegistro = view.findViewById(R.id.layoutRegistro);

        // Botones
        botonLogin = view.findViewById(R.id.btnLogin);
        botonRegistrar = view.findViewById(R.id.btnRegistrar);
        botonToggle = view.findViewById(R.id.tvToggle);

        botonToggle.setOnClickListener(v -> {
            if (layoutLogin.getVisibility() == View.VISIBLE) {
                layoutLogin.setVisibility(View.GONE);
                layoutRegistro.setVisibility(View.VISIBLE);
                botonToggle.setText("¿Ya tienes cuenta? Inicia sesión aquí");
            } else {
                layoutRegistro.setVisibility(View.GONE);
                layoutLogin.setVisibility(View.VISIBLE);
                botonToggle.setText("¿No tienes cuenta? Regístrate aquí");
            }
        });

        // Login
        botonLogin.setOnClickListener(v -> {
            String email = usuario.getText().toString().trim() + "@bioinsight.com";
            String pass = password.getText().toString().trim();

            if (usuario.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Obtener datos del usuario desde Firestore
                            db.collection("usuarios").document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(doc -> {
                                        if (doc.exists()) {
                                            String nombre = doc.getString("nombre");
                                            Toast.makeText(getContext(), "Bienvenido " + nombre, Toast.LENGTH_SHORT).show();
                                            if (getActivity() == null || getContext() == null) return;

                                            SharedPreferences prefs = getActivity().getSharedPreferences("BioInsightPrefs", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putBoolean("isLoggedIn", true);
                                            editor.apply();

                                            startActivity(new Intent(getActivity(), home.class));
                                            getActivity().finish();
                                            dismiss();
                                        } else {
                                            Toast.makeText(getContext(), "No se encontraron datos en Firestore.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getContext(), "Credenciales incorrectas.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Registro
        botonRegistrar.setOnClickListener(v -> {
            String email = nuevoUsuario.getText().toString().trim() + "@bioinsight.com";
            String pass = nuevaPassword.getText().toString().trim();
            String confirmarPass = confirmarPassword.getText().toString().trim();

            if (!pass.equals(confirmarPass)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Guardar más datos del usuario en Firestore
                            Map<String, Object> datos = new HashMap<>();
                            datos.put("admin", false);
                            datos.put("nombre", nuevoNombre.getText().toString().trim());
                            datos.put("apellidos", nuevoApellido.getText().toString().trim());
                            datos.put("username", nuevoUsuario.getText().toString().trim());
                            datos.put("creationdate", FieldValue.serverTimestamp());
                            datos.put("foto", 0);

                            db.collection("usuarios").document(user.getUid())
                                    .set(datos)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getContext(), "Usuario registrado correctamente.", Toast.LENGTH_SHORT).show();

                                        SharedPreferences prefs = getActivity().getSharedPreferences("BioInsightPrefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.apply();

                                        // Enviar a pantalla de inicio automáticamente
                                        startActivity(new Intent(getActivity(), home.class));
                                        getActivity().finish();
                                        dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getContext(), "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }
}