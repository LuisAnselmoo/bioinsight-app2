package com.example.bioinsight;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Date;

public class Mensajes extends AppCompatActivity {

    private LinearLayout layoutMensajes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);

        layoutMensajes = findViewById(R.id.layoutMensajes);
        cargarMensajes();
    }

    private void cargarMensajes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference contactoRef = db.collection("contacto");

        contactoRef.orderBy("fecha")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    layoutMensajes.removeAllViews(); // Limpia antes de agregar
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String nombre = doc.getString("nombre");
                        String correo = doc.getString("email");
                        String mensaje = doc.getString("mensaje");
                        Date fecha = doc.getDate("fecha");

                        View mensajeCard = getLayoutInflater().inflate(R.layout.item_mensaje, null);
                        TextView tvNombre = mensajeCard.findViewById(R.id.tvNombre);
                        TextView tvCorreo = mensajeCard.findViewById(R.id.tvCorreo);
                        TextView tvMensaje = mensajeCard.findViewById(R.id.tvMensaje);
                        TextView tvFecha = mensajeCard.findViewById(R.id.tvFecha);
                        Button btnEliminar = mensajeCard.findViewById(R.id.btnEliminar);

                        tvNombre.setText("Nombre: " + nombre);
                        tvCorreo.setText("Correo: " + correo);
                        tvMensaje.setText("Mensaje: " + mensaje);
                        tvFecha.setText("Fecha: " + fecha);

                        btnEliminar.setOnClickListener(v -> {
                            db.collection("contacto").document(id)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Mensaje eliminado", Toast.LENGTH_SHORT).show();
                                        layoutMensajes.removeView(mensajeCard);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                                    });
                        });

                        layoutMensajes.addView(mensajeCard);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Mensajes.this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
                });
    }
}