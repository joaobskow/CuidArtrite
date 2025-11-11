package com.example.app_osteoartrite.com.app_osteoartrite;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import android.app.AlertDialog;

public class alterar_senha extends AppCompatActivity {

    private EditText editEmailRecuperacao;
    private Button btnEnviarEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alterar_senha);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton Voltar = findViewById(R.id.Btn_Voltar);
        Voltar.setOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();
        editEmailRecuperacao = findViewById(R.id.editEmailRecuperacao);
        btnEnviarEmail = findViewById(R.id.btnEnviarEmail);

        btnEnviarEmail.setOnClickListener(v -> {
            String email = editEmailRecuperacao.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(alterar_senha.this, "Por favor, digite seu e-mail", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarEmailRecuperacao(email);
        });
    }

    private void enviarEmailRecuperacao(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        new AlertDialog.Builder(alterar_senha.this)
                                .setTitle("E-mail Enviado!")
                                .setMessage("Enviamos um link de recuperação para o seu e-mail.\n\nPor favor, verifique sua caixa de SPAM ou Lixo Eletrônico.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    // Quando o usuário clicar em "OK", a tela fecha
                                    finish();
                                })
                                .setIcon(android.R.drawable.ic_dialog_email) // Ícone de e-mail
                                .show(); // Exibe o pop-up

                    } else {
                        // ERRO
                        String mensagemErro = "Falha ao enviar e-mail.";
                        if (task.getException() != null) {
                            mensagemErro = task.getException().getMessage();
                        }
                        Toast.makeText(alterar_senha.this, mensagemErro, Toast.LENGTH_LONG).show();
                    }
                });
    }
}