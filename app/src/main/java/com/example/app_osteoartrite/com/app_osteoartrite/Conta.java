package com.example.app_osteoartrite.com.app_osteoartrite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Conta extends AppCompatActivity {

    // --- Variáveis de UI ---
    private ImageButton btnVoltarConta, btnEditarFoto;
    private AppCompatButton btnAcessibilidade, btnAlterarSenha, btnSair;
    private TextView tvNomeUsuario, tvEmailUsuario;

    // --- Variáveis do Firebase ---
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conta);

        // Configuração do EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom); // Removido padding top
            return insets;
        });

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Conectar Views
        btnVoltarConta = findViewById(R.id.btnVoltarConta);
        btnEditarFoto = findViewById(R.id.btnEditarFoto);
        btnAcessibilidade = findViewById(R.id.btnAcessibilidade);
        btnAlterarSenha = findViewById(R.id.btnAlterarSenha);
        btnSair = findViewById(R.id.btnSair);
        tvNomeUsuario = findViewById(R.id.tvNomeUsuario);
        tvEmailUsuario = findViewById(R.id.tvEmailUsuario);

        // Carregar dados do usuário (Nome e Email)
        carregarDadosDoUsuario();

        // --- Configurar Ações (Listeners) ---

        // 1. Voltar para a tela anterior
        btnVoltarConta.setOnClickListener(v -> finish());

        // 2. Editar Foto (Função 1)
        btnEditarFoto.setOnClickListener(v -> {
            // A lógica para abrir a galeria e salvar a foto é complexa.
            // Por enquanto, mostramos um Toast.
            Toast.makeText(Conta.this, "Abrir galeria para alterar foto (a implementar)", Toast.LENGTH_SHORT).show();
        });

        // 3. Botão Acessibilidade (Função 2)
        btnAcessibilidade.setOnClickListener(v -> {
            Intent intent = new Intent(Conta.this, acessibilidade.class);
            startActivity(intent);
        });

        // 4. Botão Alterar Senha (Função 3)
        btnAlterarSenha.setOnClickListener(v -> {
            Intent intent = new Intent(Conta.this, alterar_senha.class);
            startActivity(intent);
        });

        // 5. Botão Sair (Função 4)
        btnSair.setOnClickListener(v -> {
            // Desloga o usuário do Firebase
            mAuth.signOut();

            // Envia o usuário de volta para a tela de login
            Intent intent = new Intent(Conta.this, tela_login.class);

            // Flags para limpar o histórico:
            // O usuário não poderá "voltar" para a tela de conta.
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish(); // Fecha a tela de Conta
        });
    }

    private void carregarDadosDoUsuario() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();

        if (usuarioAtual != null) {
            // 1. Definir o Email (já vem com o Firebase Auth)
            if (usuarioAtual.getEmail() != null) {
                tvEmailUsuario.setText(usuarioAtual.getEmail());
            }

            // 2. Buscar o Nome no Firestore
            String uid = usuarioAtual.getUid();
            DocumentReference docRef = db.collection("users").document(uid);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nome = documentSnapshot.getString("nome");
                    if (nome != null && !nome.isEmpty()) {
                        // Mostra apenas o primeiro nome
                        String primeiroNome = nome.split(" ")[0];
                        tvNomeUsuario.setText(primeiroNome);
                    }
                } else {
                    tvNomeUsuario.setText("Usuário");
                }
            }).addOnFailureListener(e -> {
                tvNomeUsuario.setText("Usuário");
                Toast.makeText(this, "Erro ao buscar nome", Toast.LENGTH_SHORT).show();
            });
        }
    }
}