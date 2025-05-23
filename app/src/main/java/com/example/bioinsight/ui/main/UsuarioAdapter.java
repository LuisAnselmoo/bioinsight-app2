package com.example.bioinsight.ui.main;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bioinsight.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Map<String, Object>> usuarios;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public UsuarioAdapter(List<Map<String, Object>> usuarios, Context context) {
        this.usuarios = usuarios;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Map<String, Object> usuario = usuarios.get(position);
        String userId = (String) usuario.get("userId");
        String nombre = (String) usuario.get("nombre");
        String apellido = (String) usuario.get("apellidos");
        String email = (String) usuario.get("email");
        boolean isAdmin = usuario.get("admin") != null && (boolean) usuario.get("admin");
        Date fechaRegistro = null;
        Object fechaObj = usuario.get("creationdate");

        if (fechaObj instanceof Timestamp) {
            fechaRegistro = ((Timestamp) fechaObj).toDate();
        } else if (fechaObj instanceof Date) {
            fechaRegistro = (Date) fechaObj;
        }

        holder.tvNombre.setText(nombre + " " + apellido);
        holder.tvEmail.setText(email);

        if (fechaRegistro != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvFechaRegistro.setText("Registrado: " + sdf.format(fechaRegistro));
        }

        holder.switchAdmin.setOnCheckedChangeListener(null);
        holder.switchAdmin.setChecked(isAdmin);
        holder.switchAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (userId != null && isChecked != isAdmin) {
                confirmarCambioRol(userId, isChecked);
            }
        });

        holder.btnResetPassword.setOnClickListener(v -> {
            if (email != null) {
                mostrarDialogoRestablecerPassword(email);
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (userId != null) {
                confirmarEliminarUsuario(userId, email);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    private void confirmarCambioRol(String userId, boolean nuevoRol) {
        new AlertDialog.Builder(context)
                .setTitle("Cambiar rol")
                .setMessage("¿Estás seguro de cambiar el rol de este usuario?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    actualizarRolUsuario(userId, nuevoRol);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    notifyDataSetChanged();
                })
                .show();
    }

    private void actualizarRolUsuario(String userId, boolean isAdmin) {
        db.collection("usuarios").document(userId)
                .update("admin", isAdmin)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Rol actualizado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al actualizar el rol", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                });
    }

    private void mostrarDialogoRestablecerPassword(String defaultEmail) {
        final EditText input = new EditText(context);
        input.setText(defaultEmail);
        input.setSelection(defaultEmail.length());

        new AlertDialog.Builder(context)
                .setTitle("Restablecer contraseña")
                .setMessage("Ingresa el correo al que se enviará el enlace:")
                .setView(input)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String correo = input.getText().toString().trim();
                    if (!correo.isEmpty()) {
                        restablecerPassword(correo);
                    } else {
                        Toast.makeText(context, "Correo no válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void restablecerPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Enlace de restablecimiento enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Exception e = task.getException();
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void confirmarEliminarUsuario(String userId, String email) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de eliminar a " + email + "? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarUsuario(userId);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(String userId) {
        db.collection("usuarios").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Usuario eliminado de la base de datos", Toast.LENGTH_SHORT).show();
                    usuarios.removeIf(u -> userId.equals(u.get("userId")));
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvFechaRegistro;
        Switch switchAdmin;
        Button btnResetPassword, btnEliminar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvFechaRegistro = itemView.findViewById(R.id.tvFechaRegistro);
            switchAdmin = itemView.findViewById(R.id.switchAdmin);
            btnResetPassword = itemView.findViewById(R.id.btnResetPassword);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}