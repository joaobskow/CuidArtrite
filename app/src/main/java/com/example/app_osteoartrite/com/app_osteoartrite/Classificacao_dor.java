package com.example.app_osteoartrite.com.app_osteoartrite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

// --- MUDANÇA 1: Imports Corretos ---
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity; // <-- Deve ser AppCompatActivity
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// --- MUDANÇA 2: Extender AppCompatActivity ---
public class Classificacao_dor extends AppCompatActivity {

    private RecyclerView recyclerViewClassificacao;
    private ImageButton btnVoltar, btnAjudaFloating;
    private ClassificacaoDorAdapter adapter;
    private List<ClassificacaoDor> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- MUDANÇA 3: EdgeToEdge REATIVADO ---
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_classificacao_dor);

        // Configuração do EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Conectar Views
        btnVoltar = findViewById(R.id.btnVoltar);
        recyclerViewClassificacao = findViewById(R.id.recyclerViewClassificacao);
        btnAjudaFloating = findViewById(R.id.btnAjudaFloating);

        // Configurar Ações
        btnVoltar.setOnClickListener(v -> finish());
        btnAjudaFloating.setOnClickListener(v -> {
            Intent intent = new Intent(Classificacao_dor.this, chat.class);
            startActivity(intent);
        });

        // Preparar e mostrar a lista
        prepararListaDeDados();
        setupRecyclerView();
    }

    private void prepararListaDeDados() {
        // (Os dados que você forneceu entram aqui)
        items.add(new ClassificacaoDor("0 - Sem dor",
                "• Você está completamente confortável, sem nenhum desconforto"));

        items.add(new ClassificacaoDor("1-2 - Dor Mínima",
                "• Dor muito leve que você consegue ignorar\n" +
                        "• Exemplo: pequena coceira, leve desconforto ao sentar em posição errada"));

        items.add(new ClassificacaoDor("3-4 - Dor Leve",
                "• Dor perceptível mas não impede suas atividades\n" +
                        "• Exemplo: dor de cabeça leve, pequena dor muscular após exercício\n" +
                        "• Você consegue trabalhar e se concentrar normalmente"));

        items.add(new ClassificacaoDor("5-6 - Dor Moderada",
                "• Dor que interfere nas atividades mas você ainda consegue realizá-las\n" +
                        "• Exemplo: dor de dente chata, torção de tornozelo, cólica menstrual moderada\n" +
                        "• Você pode precisar de analgésico simples\n" +
                        "• Dificulta concentração em tarefas complexas"));

        items.add(new ClassificacaoDor("7-8 - Dor Intensa",
                "• Dor que domina seus sentidos e limita significativamente suas atividades\n" +
                        "• Exemplo: enxaqueca forte, cólica renal, fratura óssea\n" +
                        "• Você não consegue ignorar a dor\n" +
                        "• Dificuldade para dormir ou realizar atividades básicas\n" +
                        "• Precisa de medicação mais forte"));

        items.add(new ClassificacaoDor("9-10 - Dor Insuportável",
                "• A pior dor imaginável, você não consegue fazer nada além de lidar com ela\n" +
                        "• Exemplo: apendicite aguda, trabalho de parto em transição, queimaduras graves\n" +
                        "• Pode causar choque, náuseas, vômitos\n" +
                        "• Requer atendimento médico imediato\n" +
                        "• Muitas pessoas nunca experimentam dor nesse nível"));
    }

    private void setupRecyclerView() {
        adapter = new ClassificacaoDorAdapter(items);
        recyclerViewClassificacao.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewClassificacao.setAdapter(adapter);
    }
}