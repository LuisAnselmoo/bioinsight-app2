package com.example.bioinsight;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.bioinsight.ui.main.Atlas1Fragment;
import com.example.bioinsight.ui.main.Atlas2Fragment;
import com.example.bioinsight.ui.main.ModbusFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.example.bioinsight.ui.main.SectionsPagerAdapter;
import com.example.bioinsight.databinding.ActivityHomeBinding;

public class home extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private FloatingActionButton fabMain, fabAdd, fabEdit, fabDelete;
    private boolean isFabOpen = false;
    private int currentPage = 0;

    private Button btnMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        Button btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> cerrarSesion());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                currentPage = position; // Guarda la posición actual
                updateFABForPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        // Referencias a los FABs
        fabMain = binding.fab;
        fabAdd = findViewById(R.id.fab_add);
        fabEdit = findViewById(R.id.fab_edit);
        fabDelete = findViewById(R.id.fab_delete);

        viewPager.setCurrentItem(0); // Forzamos a que inicie en la pestaña 0 (Home)
        updateFABForPage(0); // Configura el FAB correctamente desde el inicio

        fabAdd.setOnClickListener(v -> {
            switch (currentPage) {
                case 1: // Atlas1
                    Atlas1Fragment atlas1Fragment = (Atlas1Fragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 1);
                    if (atlas1Fragment != null) {
                        atlas1Fragment.mostrarDialogoAgregar();
                    }
                    break;
                case 2: // Atlas2
                    Atlas2Fragment atlas2Fragment = (Atlas2Fragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 2);
                    if (atlas2Fragment != null) {
                        atlas2Fragment.mostrarDialogoAgregar();
                    }
                    break;
                case 3: // Modbus
                    ModbusFragment modbusFragment = (ModbusFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 3);
                    if (modbusFragment != null) {
                        modbusFragment.mostrarDialogoAgregar();
                    }
                    break;
            }
        });

        fabEdit.setOnClickListener(v -> {
            switch (currentPage) {
                case 1:
                    Atlas1Fragment fragment = (Atlas1Fragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 1);
                    if (fragment != null) {
                        fragment.mostrarDialogoEditar();
                    }
                    break;
                case 2:
                    Atlas2Fragment fragment2 = (Atlas2Fragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 2);
                    if (fragment2 != null) {
                        fragment2.mostrarDialogoEditar();
                    }
                    break;
                case 3:
                    ModbusFragment modbusFragment = (ModbusFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 3);
                    if (modbusFragment != null) {
                        modbusFragment.mostrarDialogoEditar();
                    }
                    break;
            }
        });

        fabDelete.setOnClickListener(v -> {
            switch (currentPage) {
                case 1:
                    Atlas1Fragment fragment = (Atlas1Fragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 1);
                    if (fragment != null) {
                        fragment.mostrarDialogoEliminar();
                    }
                    break;
                case 2:
                    Atlas2Fragment fragment2 = (Atlas2Fragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 2);
                    if (fragment2 != null) {
                        fragment2.mostrarDialogoEliminar();
                    }
                    break;
                case 3:
                    ModbusFragment modbusFragment = (ModbusFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 3);
                    if (modbusFragment != null) {
                        modbusFragment.mostrarDialogoEliminar();
                    }
                    break;
            }
        });
    }

    private void showFABMenu() {
        fabAdd.setVisibility(View.VISIBLE);
        fabEdit.setVisibility(View.VISIBLE);
        fabDelete.setVisibility(View.VISIBLE);

        int radius = 250;

        moveFab(fabAdd, radius, 180);
        moveFab(fabEdit, radius, 135);
        moveFab(fabDelete, radius, 90);
    }

    private void closeFABMenu() {
        moveFab(fabAdd, 0, 0);
        moveFab(fabEdit, 0, 0);
        moveFab(fabDelete, 0, 0);

        fabAdd.setVisibility(View.GONE);
        fabEdit.setVisibility(View.GONE);
        fabDelete.setVisibility(View.GONE);
    }

    private void moveFab(FloatingActionButton fab, int radius, int angleDegree) {
        double angleRad = Math.toRadians(angleDegree);
        float translationX = (float) (Math.cos(angleRad) * radius);
        float translationY = (float) (-Math.sin(angleRad) * radius);

        fab.animate()
                .translationX(translationX)
                .translationY(translationY)
                .setDuration(300)
                .start();
    }

    private void updateFABForPage(int position) {
        closeFABMenu(); // cerramos el menú al cambiar de página
        fabMain.hide(); // Ocultamos el FAB mientras se actualiza

        switch (position) {
            case 1: // Atlas1
            case 2: // Atlas2
            case 3: // Modbus
                fabMain.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                fabMain.setOnClickListener(v -> {
                    if (!isFabOpen) {
                        showFABMenu();
                    } else {
                        closeFABMenu();
                    }
                    isFabOpen = !isFabOpen;
                });
                fabMain.show();
                break;

            case 0: // Home
            case 4: // Nuevo Usuario
            case 5: // Gestionar Usuarios
                fabMain.setImageResource(android.R.drawable.ic_dialog_email);
                fabMain.setOnClickListener(v -> {
                    closeFABMenu();
                    Intent intent = new Intent(home.this, Mensajes.class);
                    startActivity(intent);
                });
                fabMain.show();
                break;

            default:
                fabMain.setVisibility(View.GONE);
                break;
        }

    }

    private void cerrarSesion() {
        new AlertDialog.Builder(home.this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("BioInsightPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(home.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}