package com.example.bioinsight;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.google.firebase.Timestamp;
import java.util.Map;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    //Slider Imagenes
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;

    // formulario contacto
    private FirebaseFirestore db;
    private EditText nombreCon, correoCon, mensajeCon;
    private Button botonContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();

        // Verificar si ya está logueado
        SharedPreferences prefs = getSharedPreferences("BioInsightPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            // Si ya está logueado, ir directamente a Inicio
            startActivity(new Intent(this, home.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Inputs Formulario
        botonContacto = findViewById(R.id.btnContacto);
        nombreCon = findViewById(R.id.contactoNombre);
        correoCon = findViewById(R.id.contactoEmail);
        mensajeCon = findViewById(R.id.contactoMensaje);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        botonContacto.setOnClickListener(v -> {
            String nombreContacto = nombreCon.getText().toString();
            String correoContacto = correoCon.getText().toString();
            String mensajeContacto = mensajeCon.getText().toString();

            if (nombreCon.getText().toString().trim().isEmpty() || correoCon.getText().toString().trim().isEmpty() || mensajeCon.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombreContacto.toString().trim());
            datos.put("email", correoContacto.toString().trim());
            datos.put("mensaje", mensajeContacto.toString().trim());
            datos.put("fecha", Timestamp.now());

            db.collection("contacto")
                    .add(datos)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Mensaje enviado correctamente.", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Error al enviar mensaje: " + e.getMessage() + ".", Toast.LENGTH_SHORT).show();
                    });
        });

        inicializarSlider();
        inicializarLogin();
    }

    private void inicializarSlider() {
        viewPager2 = findViewById(R.id.imageSlider);
        List<Integer> images = Arrays.asList(
                R.drawable.inyector02,
                R.drawable.modbus02,
                R.drawable.inyector03
        );
        SliderAdapter adapter = new SliderAdapter(images);
        viewPager2.setAdapter(adapter);

        sliderRunnable = () -> {
            int current = viewPager2.getCurrentItem();
            int next = (current + 1) % images.size();
            viewPager2.setCurrentItem(next, true);
            sliderHandler.postDelayed(sliderRunnable, 3000);
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private void limpiarFormulario() {
        nombreCon.setText("");
        correoCon.setText("");
        mensajeCon.setText("");
    }

    private void inicializarLogin() {
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnIniciarSesion.setOnClickListener(v -> {
            Login dialog = new Login();
            dialog.show(getSupportFragmentManager(), "LoginDialog");
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}