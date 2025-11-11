package com.example.app_osteoartrite.com.app_osteoartrite;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // 1. IMPORTAR TEXTVIEW
import android.widget.Toast;
import android.content.Intent; // 2. IMPORTAR INTENT

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registro_diagnostico extends AppCompatActivity {

    private EditText editDiagnostico, editComorbidades, editData, editProfissional, editObservacao;
    private Button btnCadastrarDiagnostico;
    private TextView txtPular; // 3. DECLARAR A VARIÁVEL

    private FirebaseFirestore db;
    private String usuarioUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_diagnostico);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        usuarioUID = getIntent().getStringExtra("USER_UID");
        if (usuarioUID == null || usuarioUID.isEmpty()) {
            Toast.makeText(this, "Erro: ID do usuário não encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Referências
        editDiagnostico = findViewById(R.id.editDiagnostico);
        editComorbidades = findViewById(R.id.editComorbidades);
        editData = findViewById(R.id.editData);
        editProfissional = findViewById(R.id.editProfissional);
        editObservacao = findViewById(R.id.editObservacao);
        btnCadastrarDiagnostico = findViewById(R.id.btnCadastrarDiagnostico);
        txtPular = findViewById(R.id.txtPular); // 4. REFERENCIAR O ID

        // Ação do botão "CADASTRAR"
        btnCadastrarDiagnostico.setOnClickListener(v -> {
            salvarDiagnostico();
        });

        // 5. ADICIONAR A AÇÃO DE CLIQUE PARA "PULAR"
        txtPular.setOnClickListener(v -> {
            // Cria a intenção de ir para a tela inicial
            Intent intent = new Intent(registro_diagnostico.this, tela_inicial.class);
            // Limpa o histórico de telas (para o usuário não voltar para o cadastro)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Fecha esta tela
        });
    }

    private void salvarDiagnostico() {
        String diagnostico = editDiagnostico.getText().toString().trim();
        String comorbidades = editComorbidades.getText().toString().trim();
        String data = editData.getText().toString().trim();
        String profissional = editProfissional.getText().toString().trim();
        String observacao = editObservacao.getText().toString().trim();

        if (diagnostico.isEmpty() || data.isEmpty() || profissional.isEmpty()) {
            Toast.makeText(this, "Preencha pelo menos Diagnóstico, Data e Profissional", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> dadosDiagnostico = new HashMap<>();
        dadosDiagnostico.put("diagnostico", diagnostico);
        dadosDiagnostico.put("comorbidades", comorbidades);
        dadosDiagnostico.put("data", data);
        dadosDiagnostico.put("profissionalResponsavel", profissional);
        dadosDiagnostico.put("observacao", observacao);

        db.collection("users").document(usuarioUID)
                .collection("diagnosticos")
                .add(dadosDiagnostico)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(registro_diagnostico.this, "Diagnóstico salvo com sucesso!", Toast.LENGTH_SHORT).show();

                    // Vai para a tela inicial DEPOIS de salvar
                    Intent intent = new Intent(registro_diagnostico.this, tela_inicial.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(registro_diagnostico.this, "Erro ao salvar diagnóstico: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}