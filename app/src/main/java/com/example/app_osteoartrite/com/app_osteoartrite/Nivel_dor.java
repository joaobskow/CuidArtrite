package com.example.app_osteoartrite.com.app_osteoartrite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2; // Importante para o carrossel
import androidx.appcompat.widget.AppCompatButton;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class Nivel_dor extends AppCompatActivity {

    // --- Variáveis de UI ---
    private ImageButton btnVoltar, btnAjudaFloating;

    private TextView tvNomeUsuario, tvEmailUsuario;
    private AppCompatButton btnRegistroDor, btnGrafico, btnEscalaDores;
    private FloatingActionButton fabProfile;
    private ViewPager2 viewPagerMotivacional; // Adicionado para o carrossel

    // --- Variáveis do Firebase ---
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nivel_dor);

        // Configuração do EdgeToEdge (remove o padding do topo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Conectar Views
        btnVoltar = findViewById(R.id.btnVoltar);
        tvNomeUsuario = findViewById(R.id.tvNomeUsuario);
        tvEmailUsuario = findViewById(R.id.tvEmailUsuario);
        btnRegistroDor = findViewById(R.id.btnRegistroDor);
        btnGrafico = findViewById(R.id.btnGrafico);
        btnEscalaDores = findViewById(R.id.btnEscalaDores);
        btnAjudaFloating = findViewById(R.id.btnAjudaFloating);
        viewPagerMotivacional = findViewById(R.id.viewPagerMotivacional); // Conectar o ViewPager2

        // Carregar dados do usuário no header
        carregarDadosDoUsuario();

        // --- Configurar o Carrossel de Frases Motivacionais ---
        setupFrasesMotivacionais();

        // --- Configurar Ações (Listeners) ---

        // Botão Voltar (no header)
        btnVoltar.setOnClickListener(v -> finish()); // Fecha a tela e volta

        // Botão 1: "Como está sua dor..."
        btnRegistroDor.setOnClickListener(v -> {
            Intent intent = new Intent(Nivel_dor.this, Registre_dor.class);
            startActivity(intent);
        });

        // Botão 2: "Gráfico"
        btnGrafico.setOnClickListener(v -> {
            Intent intent = new Intent(Nivel_dor.this, Grafico_dor.class);
            startActivity(intent);
        });

        // Botão 3: "Escala das dores"
        btnEscalaDores.setOnClickListener(v -> {
            Intent intent = new Intent(Nivel_dor.this, Classificacao_dor.class);
            startActivity(intent);
        });

        // FAB: Foto do Perfil
        btnAjudaFloating.setOnClickListener(v -> {
            Intent intent = new Intent(Nivel_dor.this, chat.class);
            startActivity(intent);
        });
    }

    private void setupFrasesMotivacionais() {
        // Frases Motivacionais (você pode adicionar mais ou mudar as existentes)
        List<String> frases = Arrays.asList(
                "Cada passo é uma vitória. Mantenha-se ativo! --- App CuidaArtrite",
                "A dor de hoje é a força de amanhã. Não desista da sua jornada. --- Equipe de Saúde",
                "Pequenas mudanças fazem uma grande diferença no manejo da osteoartrite. --- Consciência Fitness",
                "Sua saúde é seu maior bem. Cuide-se com carinho e persistência. --- Bem-Estar Diário",
                "Encontre alegria nos movimentos. Seu corpo agradece cada esforço. --- Mente e Corpo Sãos"
        );

        FraseMotivacionalAdapter adapter = new FraseMotivacionalAdapter(frases);
        viewPagerMotivacional.setAdapter(adapter);

        // Opcional: Para fazer o carrossel rolar automaticamente
        // Você pode implementar um Handler e um Runnable para isso, se desejar.
        // Exemplo básico (você pode pesquisar "ViewPager2 auto scroll Android" para uma implementação robusta):
        // final Handler handler = new Handler(Looper.getMainLooper());
        // final Runnable runnable = new Runnable() {
        //     @Override
        //     public void run() {
        //         int currentItem = viewPagerMotivacional.getCurrentItem();
        //         int totalItems = frases.size();
        //         int nextItem = (currentItem + 1) % totalItems;
        //         viewPagerMotivacional.setCurrentItem(nextItem, true);
        //         handler.postDelayed(this, 3000); // Rola a cada 3 segundos
        //     }
        // };
        // viewPagerMotivacional.postDelayed(runnable, 3000);
    }

    private void carregarDadosDoUsuario() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();

        if (usuarioAtual != null) {
            if (usuarioAtual.getEmail() != null) {
                tvEmailUsuario.setText(usuarioAtual.getEmail());
            }

            String uid = usuarioAtual.getUid();
            DocumentReference docRef = db.collection("users").document(uid);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nome = documentSnapshot.getString("nome");
                    if (nome != null && !nome.isEmpty()) {
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