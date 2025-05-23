package com.example.bioinsight.ui.main;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bioinsight.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GestionarUsuarios extends Fragment {

    private RecyclerView recyclerUsuarios;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public GestionarUsuarios() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestionar_usuarios, container, false);

        recyclerUsuarios = view.findViewById(R.id.recyclerUsuarios);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarUsuarios();

        return view;
    }

    private void cargarUsuarios() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("usuarios")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, Object>> usuarios = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Map<String, Object> usuario = document.getData();
                                usuario.put("userId", document.getId());

                                String email = (String) usuario.get("email");
                                if (email == null) {
                                    String username = (String) usuario.get("username");
                                    if (username != null) {
                                        email = username + "@bioinsight.com";
                                        usuario.put("email", email);
                                    }
                                }

                                usuarios.add(usuario);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        UsuarioAdapter adapter = new UsuarioAdapter(usuarios, getContext());
                        recyclerUsuarios.setAdapter(adapter);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        Toast.makeText(getContext(), "Error al cargar usuarios: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}