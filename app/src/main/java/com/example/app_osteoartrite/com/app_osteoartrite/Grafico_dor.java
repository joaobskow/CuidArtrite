package com.example.app_osteoartrite.com.app_osteoartrite;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Grafico_dor extends AppCompatActivity {

    private static final String TAG = "Grafico_dor";
    private BarChart barChart;
    private ImageButton btnVoltar;
    private FloatingActionButton fabProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final String[] diasDaSemana = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grafico_dor);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnVoltar = findViewById(R.id.btnVoltar);
        fabProfile = findViewById(R.id.fabProfile);
        barChart = findViewById(R.id.barChart);

        btnVoltar.setOnClickListener(v -> finish());

        fabProfile.setOnClickListener(v -> {
            startActivity(new Intent(Grafico_dor.this, Conta.class));
        });

        carregarDadosDoGrafico();
    }

    private void carregarDadosDoGrafico() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();

        if (usuarioAtual == null) {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = usuarioAtual.getUid();

        // --- CORREÇÃO DE FUSO HORÁRIO ---
        TimeZone timeZone = TimeZone.getTimeZone("America/Sao_Paulo");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date inicioDaSemana = cal.getTime();

        // Mapa para guardar a Média
        Map<Integer, List<Integer>> dadosDaSemana = new HashMap<>();
        dadosDaSemana.put(Calendar.SUNDAY, new ArrayList<>());
        dadosDaSemana.put(Calendar.MONDAY, new ArrayList<>());
        dadosDaSemana.put(Calendar.TUESDAY, new ArrayList<>());
        dadosDaSemana.put(Calendar.WEDNESDAY, new ArrayList<>());
        dadosDaSemana.put(Calendar.THURSDAY, new ArrayList<>());
        dadosDaSemana.put(Calendar.FRIDAY, new ArrayList<>());
        dadosDaSemana.put(Calendar.SATURDAY, new ArrayList<>());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
        sdf.setTimeZone(timeZone);

        db.collection("users").document(uid).collection("registros_dor")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int registrosEncontradosEstaSemana = 0;

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                String dataString = document.getString("data");
                                Long nivelDorLong = document.getLong("nivelDor");

                                if (dataString == null || nivelDorLong == null) continue;

                                int nivelDor = nivelDorLong.intValue();
                                Date dataRegistro = sdf.parse(dataString);

                                if (dataRegistro != null && !dataRegistro.before(inicioDaSemana)) {
                                    registrosEncontradosEstaSemana++;
                                    Calendar calRegistro = Calendar.getInstance(timeZone);
                                    calRegistro.setTime(dataRegistro);
                                    int diaDaSemana = calRegistro.get(Calendar.DAY_OF_WEEK);
                                    dadosDaSemana.get(diaDaSemana).add(nivelDor);
                                }

                            } catch (ParseException e) {
                                Log.e(TAG, "Erro ao parsear data: " + document.getString("data"), e);
                            }
                        }
                    }

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Nenhum registro de dor encontrado.", Toast.LENGTH_SHORT).show();
                    } else if (registrosEncontradosEstaSemana == 0) {
                        Toast.makeText(this, "Nenhum registro encontrado para esta semana.", Toast.LENGTH_SHORT).show();
                    }

                    configurarGrafico(dadosDaSemana);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar dados do gráfico.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao buscar registros_dor", e);
                });
    }

    private void configurarGrafico(Map<Integer, List<Integer>> dados) {
        Map<Integer, Float> mediaDosDados = new HashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : dados.entrySet()) {
            int dia = entry.getKey();
            List<Integer> doresDoDia = entry.getValue();

            if (doresDoDia.isEmpty()) {
                mediaDosDados.put(dia, 0f);
            } else {
                int soma = 0;
                for (int dor : doresDoDia) {
                    soma += dor;
                }
                float media = (float) soma / doresDoDia.size();
                mediaDosDados.put(dia, media);
            }
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, mediaDosDados.get(Calendar.SUNDAY)));
        entries.add(new BarEntry(1, mediaDosDados.get(Calendar.MONDAY)));
        entries.add(new BarEntry(2, mediaDosDados.get(Calendar.TUESDAY)));
        entries.add(new BarEntry(3, mediaDosDados.get(Calendar.WEDNESDAY)));
        entries.add(new BarEntry(4, mediaDosDados.get(Calendar.THURSDAY)));
        entries.add(new BarEntry(5, mediaDosDados.get(Calendar.FRIDAY)));
        entries.add(new BarEntry(6, mediaDosDados.get(Calendar.SATURDAY)));

        BarDataSet dataSet = new BarDataSet(entries, "Nível de Dor");
        dataSet.setColor(Color.parseColor("#3D6C4A")); // Verde do app
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(13f);
        dataSet.setDrawValues(true);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                float val = barEntry.getY();
                if (val == 0) return "";
                return String.format(Locale.getDefault(), "%.1f", val);
            }
        });

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);
        barData.setBarWidth(0.7f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setTouchEnabled(false);
        barChart.setExtraBottomOffset(10f);

        // Eixo X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(13f);
        xAxis.setTextColor(Color.parseColor("#3D6C4A"));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return diasDaSemana[(int) value % diasDaSemana.length];
            }
        });

        // Eixo Y
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setLabelCount(6);
        leftAxis.setGranularity(2f);
        leftAxis.setTextColor(Color.parseColor("#3D6C4A"));
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
