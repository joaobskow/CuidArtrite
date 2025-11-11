package com.example.app_osteoartrite.com.app_osteoartrite;

import android.os.Bundle;
// Import Button removed as we use AppCompatButton
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent; // Importação essencial para Intents

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class tela_inicial extends AppCompatActivity {

    private TextView txtSaudacao, txtDate;
    private AppCompatButton btnAtividadesDiarias, btnNivelDor,
            btnInformacoesOsteoartrite, btnConta, btnAlerta,
            btnSintomasRegistrados;

    private ImageButton btnAjudaFloating;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_inicial);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referenciar IDs
        txtSaudacao = findViewById(R.id.txtSaudacao);
        txtDate = findViewById(R.id.txtDate);
        btnAtividadesDiarias = findViewById(R.id.btnAtividadesDiarias);
        btnSintomasRegistrados = findViewById(R.id.sintomas_registrados);
        btnNivelDor = findViewById(R.id.btnNivelDor);
        btnInformacoesOsteoartrite = findViewById(R.id.btnInformacoesOsteoartrite);
        btnConta = findViewById(R.id.btnConta);
        btnAlerta = findViewById(R.id.btnAlerta);
        btnAjudaFloating = findViewById(R.id.btnAjudaFloating);

        carregarDadosUsuario();
        carregarDataAtual();

        btnAtividadesDiarias.setOnClickListener(v -> {
            Intent intent = new Intent(tela_inicial.this, Atividades_diarias.class);
            startActivity(intent);
        });

        btnSintomasRegistrados.setOnClickListener(v -> {
            Intent intent = new Intent(tela_inicial.this, AcompanhamentoSintomasActivity.class);
            startActivity(intent);
        });

        btnNivelDor.setOnClickListener(v -> {
            Intent intent = new Intent(tela_inicial.this, Nivel_dor.class);
            startActivity(intent);
        });

        btnInformacoesOsteoartrite.setOnClickListener(v -> {
            Intent intent = new Intent(tela_inicial.this, Informacoes.class);
            startActivity(intent);
        });

        btnConta.setOnClickListener(v -> {
            Intent intent = new Intent(tela_inicial.this, Conta.class);
            startActivity(intent);
        });

        btnAlerta.setOnClickListener(v -> {
            Intent intent = new Intent(tela_inicial.this, Registrar_Sintoma.class);
            startActivity(intent);
        });

        btnAjudaFloating.setOnClickListener(v -> {
            Intent intent = new Intent(tela_inicial.this, chat.class);
            startActivity(intent);
        });
    }


    private void carregarDataAtual() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMM. 'de' yyyy", new Locale("pt", "BR"));
        sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        Date dataDeHoje = new Date();
        String dataFormatada = sdf.format(dataDeHoje);
        txtDate.setText(dataFormatada.substring(0, 1).toUpperCase() + dataFormatada.substring(1));
    }

    private void carregarDadosUsuario() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual != null) {
            String uid = usuarioAtual.getUid();
            DocumentReference docRef = db.collection("users").document(uid);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nome = documentSnapshot.getString("nome");
                    if (nome != null && !nome.isEmpty()) {
                        String primeiroNome = nome.split(" ")[0];
                        txtSaudacao.setText("Bom dia, " + primeiroNome + " 👋\nComo você está hoje?");
                    } else {
                        txtSaudacao.setText("Bom dia! 👋\nComo você está hoje?");
                    }
                }
            }).addOnFailureListener(e -> {
                txtSaudacao.setText("Bom dia! 👋\nComo você está hoje?");
                Toast.makeText(this, "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show();
            });
        } else {
            txtSaudacao.setText("Bom dia! 👋\nComo você está hoje?");
        }
    }
}