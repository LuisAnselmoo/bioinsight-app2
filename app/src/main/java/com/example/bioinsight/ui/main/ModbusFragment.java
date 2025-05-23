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

public class ModbusFragment extends Fragment {

    private LinearLayout chartBContainer;

    public ModbusFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modbus, container, false);
        chartBContainer = view.findViewById(R.id.chartB_container);

        loadModbusData();
        return view;
    }

    private void loadModbusData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Modbus");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> xLabels = new ArrayList<>();
                List<BarEntry> co2In = new ArrayList<>();
                List<BarEntry> co2Out = new ArrayList<>();
                List<BarEntry> no2In = new ArrayList<>();
                List<BarEntry> no2Out = new ArrayList<>();
                List<BarEntry> so2In = new ArrayList<>();
                List<BarEntry> so2Out = new ArrayList<>();
                List<BarEntry> par = new ArrayList<>();
                List<BarEntry> temp1 = new ArrayList<>();
                List<BarEntry> temp2 = new ArrayList<>();

                int index = 0;

                for (DataSnapshot dataPoint : snapshot.getChildren()) {
                    Double co2_in = dataPoint.child("CO2_IN").getValue(Double.class);
                    Double co2_out = dataPoint.child("CO2_OUT").getValue(Double.class);
                    Double no2_in = dataPoint.child("NO2_IN").getValue(Double.class);
                    Double no2_out = dataPoint.child("NO2_OUT").getValue(Double.class);
                    Double so2_in = dataPoint.child("SO2_IN").getValue(Double.class);
                    Double so2_out = dataPoint.child("SO2_OUT").getValue(Double.class);
                    Double parVal = dataPoint.child("PAR").getValue(Double.class);
                    Double temp_1 = dataPoint.child("TEMP_1").getValue(Double.class);
                    Double temp_2 = dataPoint.child("TEMP_2").getValue(Double.class);

                    if (co2_in != null) co2In.add(new BarEntry(index, co2_in.floatValue()));
                    if (co2_out != null) co2Out.add(new BarEntry(index, co2_out.floatValue()));
                    if (no2_in != null) no2In.add(new BarEntry(index, no2_in.floatValue()));
                    if (no2_out != null) no2Out.add(new BarEntry(index, no2_out.floatValue()));
                    if (so2_in != null) so2In.add(new BarEntry(index, so2_in.floatValue()));
                    if (so2_out != null) so2Out.add(new BarEntry(index, so2_out.floatValue()));
                    if (parVal != null) par.add(new BarEntry(index, parVal.floatValue()));
                    if (temp_1 != null) temp1.add(new BarEntry(index, temp_1.floatValue()));
                    if (temp_2 != null) temp2.add(new BarEntry(index, temp_2.floatValue()));

                    String fecha = dataPoint.getKey();
                    xLabels.add(fecha != null ? fecha : "N/A");
                    index++;
                }

                chartBContainer.removeAllViews();
                addGroupedBarChart("CO2 IN/OUT", co2In, co2Out, xLabels, Color.parseColor("#8884d8"), Color.parseColor("#82ca9d"));
                addGroupedBarChart("NO2 IN/OUT", no2In, no2Out, xLabels, Color.parseColor("#FF8042"), Color.parseColor("#FFBB28"));
                addGroupedBarChart("SO2 IN/OUT", so2In, so2Out, xLabels, Color.parseColor("#d62728"), Color.parseColor("#7f7f7f"));
                addBarChart("PAR", par, xLabels, Color.parseColor("#0088FE"));
                addGroupedBarChart("TEMP 1/2", temp1, temp2, xLabels, Color.parseColor("#E377C2"), Color.parseColor("#17BECF"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ModbusFragment", "Error Firebase: " + error.getMessage());
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
        chartBContainer.addView(title);

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

        Description desc = new Description();
        desc.setText(label);
        desc.setTextColor(Color.GRAY);
        desc.setTextSize(12f);
        barChart.setDescription(desc);

        // Configuración de la gráfica
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
        barChart.setVisibleXRangeMaximum(6);
        barChart.moveViewToX(entries.size() - 6);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(false);
        barChart.animateY(1000);

        // Evitar que el ViewPager intercepte los gestos táctiles
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
        chartBContainer.addView(barChart);
        chartBContainer.addView(infoBox);

        barChart.invalidate();
    }

    private void addGroupedBarChart(String label, List<BarEntry> dataSet1, List<BarEntry> dataSet2, List<String> xLabels, int color1, int color2) {
        if (dataSet1.isEmpty() && dataSet2.isEmpty()) return;

        // Título del gráfico
        TextView title = new TextView(getContext());
        title.setText(label);
        title.setTextSize(20f);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 16, 0, 8);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        chartBContainer.addView(title);

        // Crear gráfica
        BarChart barChart = new BarChart(getContext());

        int fixedHeight = 400;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                fixedHeight
        );
        params.setMargins(0, 12, 0, 12);
        barChart.setLayoutParams(params);

        // Datasets
        BarDataSet set1 = new BarDataSet(dataSet1, "IN");
        set1.setColor(color1);
        set1.setDrawValues(false);

        BarDataSet set2 = new BarDataSet(dataSet2, "OUT");
        set2.setColor(color2);
        set2.setDrawValues(false);

        float groupSpace = 0.2f;
        float barSpace = 0.05f;
        float barWidth = 0.35f;

        BarData barData = new BarData(set1, set2);
        barData.setBarWidth(barWidth);

        barChart.setData(barData);
        barChart.groupBars(0f, groupSpace, barSpace);

        // Configuración de la gráfica
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setTextColor(Color.DKGRAY);
        barChart.getAxisLeft().setTextSize(11f);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setTextSize(10f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelRotationAngle(-45);

        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.setPinchZoom(false);
        barChart.setVisibleXRangeMaximum(6);
        barChart.moveViewToX(dataSet1.size() - 6);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(false);
        barChart.animateY(1000);

        // Evitar que el ViewPager intercepte los gestos táctiles
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
                String tipo = h.getDataSetIndex() == 0 ? "IN" : "OUT";

                infoText.setText("Fecha: " + fecha + "\n" + tipo + ": " + valor);
                infoBox.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected() {
                infoBox.setVisibility(View.GONE);
            }
        });

        // Añadir al contenedor
        chartBContainer.addView(barChart);
        chartBContainer.addView(infoBox);

        barChart.invalidate();
    }

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
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.add_sensor_modbus, null);

        EditText input_co2_in = dialogView.findViewById(R.id.input_co2_in);
        EditText input_co2_out = dialogView.findViewById(R.id.input_co2_out);
        EditText input_no2_in = dialogView.findViewById(R.id.input_no2_in);
        EditText input_no2_out = dialogView.findViewById(R.id.input_no2_out);
        EditText input_par = dialogView.findViewById(R.id.input_par);
        EditText input_so2_in = dialogView.findViewById(R.id.input_so2_in);
        EditText input_so2_out = dialogView.findViewById(R.id.input_so2_out);
        EditText input_temp_1 = dialogView.findViewById(R.id.input_temp_1);
        EditText input_temp_2 = dialogView.findViewById(R.id.input_temp_2);

        new AlertDialog.Builder(requireContext())
                .setTitle("Agregar datos a Modbus")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String key = String.valueOf(System.currentTimeMillis() / 1000);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Modbus").child(key);

                    ref.child("CO2_IN").setValue(parseDouble(input_co2_in.getText().toString()));
                    ref.child("CO2_OUT").setValue(parseDouble(input_co2_out.getText().toString()));
                    ref.child("NO2_IN").setValue(parseDouble(input_no2_in.getText().toString()));
                    ref.child("NO2_OUT").setValue(parseDouble(input_no2_out.getText().toString()));
                    ref.child("PAR").setValue(parseDouble(input_par.getText().toString()));
                    ref.child("SO2_IN").setValue(parseDouble(input_so2_in.getText().toString()));
                    ref.child("SO2_OUT").setValue(parseDouble(input_so2_out.getText().toString()));
                    ref.child("TEMP_1").setValue(parseDouble(input_temp_1.getText().toString()));
                    ref.child("TEMP_2").setValue(parseDouble(input_temp_2.getText().toString()));
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Modbus");

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

                    Double co2_in = data.child("CO2_IN").getValue(Double.class);
                    Double co2_out = data.child("CO2_OUT").getValue(Double.class);
                    Double no2_in = data.child("NO2_IN").getValue(Double.class);
                    Double no2_out = data.child("NO2_OUT").getValue(Double.class);
                    Double par = data.child("PAR").getValue(Double.class);
                    Double so2_in = data.child("SO2_IN").getValue(Double.class);
                    Double so2_out = data.child("SO2_OUT").getValue(Double.class);
                    Double temp_1 = data.child("TEMP_1").getValue(Double.class);
                    Double temp_2 = data.child("TEMP_2").getValue(Double.class);

                    Map<String, Double> sensores = new HashMap<>();
                    sensores.put("CO2_IN", co2_in);
                    sensores.put("CO2_OUT", co2_out);
                    sensores.put("NO2_IN", no2_in);
                    sensores.put("NO2_OUT", no2_out);
                    sensores.put("PAR", par);
                    sensores.put("SO2_IN", so2_in);
                    sensores.put("SO2_OUT", so2_out);
                    sensores.put("TEMP_1", temp_1);
                    sensores.put("TEMP_2", temp_2);

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
                            "CO2_IN: " + s.get("CO2_IN") +
                            ",CO2_OUT: " + s.get("CO2_OUT") +
                            ",NO2_IN: " + s.get("NO2_IN") +
                            ",NO2_OUT: " + s.get("NO2_OUT") +
                            ",PAR: " + s.get("PAR") +
                            ",SO2_IN: " + s.get("SO2_IN") +
                            ",SO2_OUT: " + s.get("SO2_OUT") +
                            ",TEMP_1: " + s.get("TEMP_1") +
                            ",TEMP_2: " + s.get("TEMP_2") + "\n";
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle("Editar datos de Modbus")
                        .setItems(items, (dialog, which) -> {
                            String claveCorrecta = claves.get(which);
                            mostrarDialogoEditarSensores(claveCorrecta, valoresSensores.get(which), fechas.get(which));
                        })
                        .show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarMensaje("Error al obtener datos de Modbus: " + error.getMessage());
            }
        });
    }
    private void mostrarDialogoEditarSensores(String fechaKey, Map<String, Double> sensores, String fechaFormateada) {
        // Crear un layout vertical para los EditTexts
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        Map<String, EditText> inputs = new HashMap<>();

        String[] sensoresNombres = {"CO2_IN", "CO2_OUT", "NO2_IN", "NO2_OUT", "PAR", "SO2_IN", "SO2_OUT", "TEMP_1", "TEMP_2"};

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
                            .getReference("Modbus")
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Modbus");

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

                    Double co2_in = data.child("CO2_IN").getValue(Double.class);
                    Double co2_out = data.child("CO2_OUT").getValue(Double.class);
                    Double no2_in = data.child("NO2_IN").getValue(Double.class);
                    Double no2_out = data.child("NO2_OUT").getValue(Double.class);
                    Double par = data.child("PAR").getValue(Double.class);
                    Double so2_in = data.child("SO2_IN").getValue(Double.class);
                    Double so2_out = data.child("SO2_OUT").getValue(Double.class);
                    Double temp_1 = data.child("TEMP_1").getValue(Double.class);
                    Double temp_2 = data.child("TEMP_2").getValue(Double.class);

                    Map<String, Double> sensores = new HashMap<>();
                    sensores.put("CO2_IN", co2_in);
                    sensores.put("CO2_OUT", co2_out);
                    sensores.put("NO2_IN", no2_in);
                    sensores.put("NO2_OUT", no2_out);
                    sensores.put("PAR", par);
                    sensores.put("SO2_IN", so2_in);
                    sensores.put("SO2_OUT", so2_out);
                    sensores.put("TEMP_1", temp_1);
                    sensores.put("TEMP_2", temp_2);

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
                            "CO2_IN: " + s.get("CO2_IN") +
                            ",CO2_OUT: " + s.get("CO2_OUT") +
                            ",NO2_IN: " + s.get("NO2_IN") +
                            ",NO2_OUT: " + s.get("NO2_OUT") +
                            ",PAR: " + s.get("PAR") +
                            ",SO2_IN: " + s.get("SO2_IN") +
                            ",SO2_OUT: " + s.get("SO2_OUT") +
                            ",TEMP_1: " + s.get("TEMP_1") +
                            ",TEMP_2: " + s.get("TEMP_2") + "\n";
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle("Selecciona un registro para eliminar")
                        .setItems(items, (dialog, which) -> {
                            String claveEliminar = claves.get(which);
                            Map<String, Double> datos = valoresSensores.get(which);

                            String mensaje = "¿Estás seguro de eliminar este registro?\n\n" +
                                    "Fecha: " + fechas.get(which) + "\n" +
                                    "CO2_IN: " + datos.get("CO2_IN") + "\n" +
                                    "CO2_OUT: " + datos.get("CO2_OUT") + "\n" +
                                    "NO2_IN: " + datos.get("NO2_IN") + "\n" +
                                    "NO2_OUT: " + datos.get("NO2_OUT") + "\n" +
                                    "PAR: " + datos.get("PAR") + "\n" +
                                    "SO2_IN: " + datos.get("SO2_IN") + "\n" +
                                    "SO2_OUT: " + datos.get("SO2_OUT") + "\n" +
                                    "TEMP_1: " + datos.get("TEMP_1") + "\n" +
                                    "TEMP_2: " + datos.get("TEMP_2");

                                    new AlertDialog.Builder(requireContext())
                                    .setTitle("Confirmar eliminación")
                                    .setMessage(mensaje)
                                    .setPositiveButton("Eliminar", (d, w) -> {
                                        FirebaseDatabase.getInstance().getReference("Modbus")
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