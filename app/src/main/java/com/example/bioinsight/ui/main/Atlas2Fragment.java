package com.example.bioinsight.ui.main;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.bioinsight.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.XAxis;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import android.view.Gravity;
import android.widget.TextView;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Atlas2Fragment extends Fragment {

    private LinearLayout chart2Container;

    public Atlas2Fragment() {
        // Required empty public constructor
    }

    @NonNull
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_atlas2, container, false);
        chart2Container = view.findViewById(R.id.chart2_container);

        loadSensorData();

        return view;
    }

    private void loadSensorData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Modulo2");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BarEntry> co2Entries = new ArrayList<>();
                List<BarEntry> doEntries = new ArrayList<>();
                List<BarEntry> ecEntries = new ArrayList<>();
                List<BarEntry> humEntries = new ArrayList<>();
                List<BarEntry> phEntries = new ArrayList<>();
                List<BarEntry> rtdEntries = new ArrayList<>();
                List<String> xLabels = new ArrayList<>();

                int index = 0;

                for (DataSnapshot dataPoint : snapshot.getChildren()) {
                    Double co2 = dataPoint.child("CO2").getValue(Double.class);
                    Double doValue = dataPoint.child("DO").getValue(Double.class);
                    Double ec = dataPoint.child("EC").getValue(Double.class);
                    Double hum = dataPoint.child("HUM").getValue(Double.class);
                    Double ph = dataPoint.child("PH").getValue(Double.class);
                    Double rtd = dataPoint.child("RTD").getValue(Double.class);

                    if (co2 != null) co2Entries.add(new BarEntry(index, co2.floatValue()));
                    if (doValue != null) doEntries.add(new BarEntry(index, doValue.floatValue()));
                    if (ec != null) ecEntries.add(new BarEntry(index, ec.floatValue()));
                    if (hum != null) humEntries.add(new BarEntry(index, hum.floatValue()));
                    if (ph != null) phEntries.add(new BarEntry(index, ph.floatValue()));
                    if (rtd != null) rtdEntries.add(new BarEntry(index, rtd.floatValue()));

                    String fecha = dataPoint.getKey();
                    xLabels.add(fecha != null ? fecha : "N/A");
                    index++;
                }

                chart2Container.removeAllViews();

                addBarChart("CO2", co2Entries, xLabels, Color.parseColor("#6d6d6d"));
                addBarChart("DO", doEntries, xLabels, Color.parseColor("#ecae19"));
                addBarChart("EC", ecEntries, xLabels, Color.parseColor("#248d6c"));
                addBarChart("HUM", humEntries, xLabels, Color.parseColor("#00a08c"));
                addBarChart("PH", phEntries, xLabels, Color.parseColor("#ff392d"));
                addBarChart("RTD", rtdEntries, xLabels, Color.parseColor("#ffc600"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Atlas2Fragment", "Error Firebase: " + error.getMessage());
            }
        });
    }

    private void addBarChart(String label, List<BarEntry> entries, List<String> xLabels, int color) {
        if (entries.isEmpty()) return;

        // Título del gráfico
        TextView title = new TextView(getContext());
        title.setText(label);
        title.setTextSize(20f);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 16, 0, 8);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        chart2Container.addView(title);

        // Crear gráfica
        BarChart barChart = new BarChart(getContext());

        int fixedHeight = 400;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                fixedHeight
        );
        params.setMargins(0, 12, 0, 12);
        barChart.setLayoutParams(params);

        // Dataset
        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Descripción
        Description desc = new Description();
        desc.setText(label);
        desc.setTextColor(Color.GRAY);
        desc.setTextSize(12f);
        barChart.setDescription(desc);

        // Ejes
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setTextColor(Color.DKGRAY);
        barChart.getAxisLeft().setTextSize(11f);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setTextSize(10f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45);

        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.setPinchZoom(false);
        barChart.setVisibleXRangeMaximum(10);
        barChart.moveViewToX(entries.size() - 10);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(false);
        barChart.animateY(1000);

        // Evitar que el ViewPager intercepte los gestos táctiles cuando el usuario toca la gráfica
        barChart.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        // Crear caja de información (contenedor)
        LinearLayout infoBox = new LinearLayout(getContext());
        infoBox.setOrientation(LinearLayout.VERTICAL);
        infoBox.setBackgroundColor(Color.parseColor("#F2F2F2"));
        infoBox.setPadding(24, 16, 24, 16);
        infoBox.setGravity(Gravity.CENTER);
        infoBox.setVisibility(View.GONE);

        // Texto informativo
        TextView infoText = new TextView(getContext());
        infoText.setTextSize(20f);
        infoText.setTextColor(Color.BLACK);
        infoText.setGravity(Gravity.START);
        infoBox.addView(infoText);

        // Botón de cerrar
        TextView closeButton = new TextView(getContext());
        closeButton.setText("Cerrar Ventana");
        closeButton.setTextColor(Color.RED);
        closeButton.setPadding(0, 16, 0, 0);
        closeButton.setTextSize(20f);
        closeButton.setGravity(Gravity.END);
        closeButton.setOnClickListener(v -> {
            infoBox.setVisibility(View.GONE);
            barChart.highlightValues(null);
        });
        infoBox.addView(closeButton);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                String fechaRaw = (index >= 0 && index < xLabels.size()) ? xLabels.get(index) : "Desconocida";
                String fecha = formatDateLabel(fechaRaw);
                String valor = String.format(Locale.getDefault(), "%.2f", e.getY());

                infoText.setText("Fecha: " + fecha + "\nValor: " + valor);
                infoBox.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected() {
                infoBox.setVisibility(View.GONE);

            }
        });

        // Añadir al contenedor
        chart2Container.addView(barChart);
        chart2Container.addView(infoBox);

        barChart.invalidate();
    }

    // convertir fecha a formato legible
    private String formatDateLabel(String rawDate) {
        try {
            long timestamp = Long.parseLong(rawDate);
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        } catch (NumberFormatException e) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                Date date = inputFormat.parse(rawDate);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException | NullPointerException ex) {
                return rawDate;
            }
        }
    }

    /* AGREGAR DATOS */
    public void mostrarDialogoAgregar() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.add_sensor, null);

        EditText inputCo2 = dialogView.findViewById(R.id.input_co2);
        EditText inputDo = dialogView.findViewById(R.id.input_do);
        EditText inputEc = dialogView.findViewById(R.id.input_ec);
        EditText inputHum = dialogView.findViewById(R.id.input_hum);
        EditText inputPh = dialogView.findViewById(R.id.input_ph);
        EditText inputRtd = dialogView.findViewById(R.id.input_rtd);

        new AlertDialog.Builder(requireContext())
                .setTitle("Agregar datos a Atlas2")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String key = String.valueOf(System.currentTimeMillis() / 1000);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Modulo2").child(key);

                    ref.child("CO2").setValue(parseDouble(inputCo2.getText().toString()));
                    ref.child("DO").setValue(parseDouble(inputDo.getText().toString()));
                    ref.child("EC").setValue(parseDouble(inputEc.getText().toString()));
                    ref.child("HUM").setValue(parseDouble(inputHum.getText().toString()));
                    ref.child("PH").setValue(parseDouble(inputPh.getText().toString()));
                    ref.child("RTD").setValue(parseDouble(inputRtd.getText().toString()));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /* EDITAR DATOS */
    public void mostrarDialogoEditar() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Modulo2");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> fechas = new ArrayList<>();
                List<String> claves = new ArrayList<>();
                List<Map<String, Double>> valoresSensores = new ArrayList<>();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                for (DataSnapshot data : snapshot.getChildren()) {
                    String fechaKey = data.getKey();
                    if (fechaKey == null) continue;
                    claves.add(fechaKey);
                    String fechaFormateada;
                    try {
                        long timestamp = Long.parseLong(fechaKey);
                        fechaFormateada = sdf.format(new Date(timestamp));
                    } catch (NumberFormatException e) {
                        fechaFormateada = fechaKey;
                    }

                    Double co2 = data.child("CO2").getValue(Double.class);
                    Double doVal = data.child("DO").getValue(Double.class);
                    Double ec = data.child("EC").getValue(Double.class);
                    Double hum = data.child("HUM").getValue(Double.class);
                    Double ph = data.child("PH").getValue(Double.class);
                    Double rtd = data.child("RTD").getValue(Double.class);

                    Map<String, Double> sensores = new HashMap<>();
                    sensores.put("CO2", co2);
                    sensores.put("DO", doVal);
                    sensores.put("EC", ec);
                    sensores.put("HUM", hum);
                    sensores.put("PH", ph);
                    sensores.put("RTD", rtd);

                    fechas.add(fechaFormateada);
                    valoresSensores.add(sensores);
                }

                if (fechas.isEmpty()) {
                    mostrarMensaje("No hay datos para editar.");
                    return;
                }

                String[] items = new String[fechas.size()];
                for (int i = 0; i < fechas.size(); i++) {
                    Map<String, Double> s = valoresSensores.get(i);
                    items[i] = fechas.get(i) + "\n" +
                            "CO2: " + s.get("CO2") +
                            ", DO: " + s.get("DO") +
                            ", EC: " + s.get("EC") +
                            ", HUM: " + s.get("HUM") +
                            ", PH: " + s.get("PH") +
                            ", RTD: " + s.get("RTD") + "\n";
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle("Selecciona un registro para editar")
                        .setItems(items, (dialog, which) -> {
                            String claveCorrecta = claves.get(which);
                            mostrarDialogoEditarSensores(claveCorrecta, valoresSensores.get(which), fechas.get(which));
                        })
                        .show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarMensaje("Error al obtener datos.");
            }
        });
    }

    private void mostrarDialogoEditarSensores(String fechaKey, Map<String, Double> sensores, String fechaFormateada) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        Map<String, EditText> inputs = new HashMap<>();

        String[] sensoresNombres = {"CO2", "DO", "EC", "HUM", "PH", "RTD"};

        for (String sensor : sensoresNombres) {
            EditText input = new EditText(requireContext());
            input.setHint(sensor);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            Double valorActual = sensores.get(sensor);
            if (valorActual != null) {
                input.setText(String.valueOf(valorActual));
            }

            layout.addView(input);
            inputs.put(sensor, input);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar sensores\n" + fechaFormateada)
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance()
                            .getReference("Modulo2")
                            .child(fechaKey);

                    for (String sensor : sensoresNombres) {
                        String valorTexto = inputs.get(sensor).getText().toString();
                        Double nuevoValor = parseDouble(valorTexto);
                        ref.child(sensor).setValue(nuevoValor);
                    }

                    mostrarMensaje("Datos actualizados correctamente.");
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarMensaje(String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }

    /* Eliminar */
    public void mostrarDialogoEliminar() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Modulo2");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> claves = new ArrayList<>();
                List<String> fechas = new ArrayList<>();
                List<Map<String, Double>> valoresSensores = new ArrayList<>();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                for (DataSnapshot data : snapshot.getChildren()) {
                    String clave = data.getKey();
                    if (clave == null) continue;
                    claves.add(clave);

                    String fechaFormateada;
                    try {
                        long timestamp = Long.parseLong(clave);
                        fechaFormateada = sdf.format(new Date(timestamp));
                    } catch (NumberFormatException e) {
                        fechaFormateada = clave;
                    }

                    Double co2 = data.child("CO2").getValue(Double.class);
                    Double doVal = data.child("DO").getValue(Double.class);
                    Double ec = data.child("EC").getValue(Double.class);
                    Double hum = data.child("HUM").getValue(Double.class);
                    Double ph = data.child("PH").getValue(Double.class);
                    Double rtd = data.child("RTD").getValue(Double.class);

                    Map<String, Double> sensores = new HashMap<>();
                    sensores.put("CO2", co2);
                    sensores.put("DO", doVal);
                    sensores.put("EC", ec);
                    sensores.put("HUM", hum);
                    sensores.put("PH", ph);
                    sensores.put("RTD", rtd);

                    fechas.add(fechaFormateada);
                    valoresSensores.add(sensores);
                }

                if (claves.isEmpty()) {
                    mostrarMensaje("No hay datos para eliminar.");
                    return;
                }

                String[] items = new String[claves.size()];
                for (int i = 0; i < claves.size(); i++) {
                    Map<String, Double> s = valoresSensores.get(i);
                    items[i] = fechas.get(i) + "\n" +
                            "CO2: " + s.get("CO2") +
                            ", DO: " + s.get("DO") +
                            ", EC: " + s.get("EC") +
                            ", HUM: " + s.get("HUM") +
                            ", PH: " + s.get("PH") +
                            ", RTD: " + s.get("RTD") + "\n";
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle("Selecciona un registro para eliminar")
                        .setItems(items, (dialog, which) -> {
                            String claveEliminar = claves.get(which);
                            Map<String, Double> datos = valoresSensores.get(which);

                            String mensaje = "¿Estás seguro de eliminar este registro?\n\n" +
                                    "Fecha: " + fechas.get(which) + "\n" +
                                    "CO2: " + datos.get("CO2") + "\n" +
                                    "DO: " + datos.get("DO") + "\n" +
                                    "EC: " + datos.get("EC") + "\n" +
                                    "HUM: " + datos.get("HUM") + "\n" +
                                    "PH: " + datos.get("PH") + "\n" +
                                    "RTD: " + datos.get("RTD");

                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Confirmar eliminación")
                                    .setMessage(mensaje)
                                    .setPositiveButton("Eliminar", (d, w) -> {
                                        FirebaseDatabase.getInstance().getReference("Modulo2")
                                                .child(claveEliminar).removeValue();
                                        mostrarMensaje("Registro eliminado.");
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        })
                        .show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarMensaje("Error al cargar los datos.");
            }
        });
    }
}