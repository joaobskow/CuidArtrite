package com.example.app_osteoartrite.com.app_osteoartrite;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton; // Use AppCompatButton
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Importações do Google e Firebase
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class tela_login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    // Elementos da UI
    private EditText editEmail, editSenha;
    private AppCompatButton btnEntrar; // Usando AppCompatButton
    private SignInButton btnGoogleLogin;
    private TextView txtEsqueciSenha, txtCadastrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configuração do Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializar o "ouvinte" do resultado do login Google
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            Toast.makeText(tela_login.this, "Falha no login com Google.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Referências dos Elementos
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnEntrar = findViewById(R.id.btnEntrar); // Referência ao botão de Email/Senha
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);
        txtCadastrar = findViewById(R.id.txtCadastrar);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin); // Referência ao botão Google

        // --- Cliques ---

        // Clique em "Entrar" (Email/Senha) - Lógica Completa
        btnEntrar.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String senha = editSenha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(tela_login.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tentar fazer login com o Firebase (Email/Senha)
            mAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // SUCESSO!
                            irParaTelaInicial(); // Chama o método helper

                        } else {
                            // FALHA no login
                            String mensagemErro;
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidUserException e) {
                                mensagemErro = "Email não cadastrado.";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                mensagemErro = "Senha incorreta.";
                            } catch (Exception e) {
                                mensagemErro = "Falha ao fazer login.";
                            }
                            Toast.makeText(tela_login.this, mensagemErro, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Clique no botão Google
        btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        // Clique em "Cadastrar-se" (leva para MainActivity)
        txtCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(tela_login.this, cadastro_paciente.class);
            startActivity(intent);
        });

        // Clique em "Esqueci minha senha"
        txtEsqueciSenha.setOnClickListener(v -> {
            Intent intent = new Intent(tela_login.this, alterar_senha.class);
            startActivity(intent);
        });
    }

    // Método para autenticar no Firebase com o Google (igual ao anterior)
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login no Firebase OK!

                        AuthResult authResult = task.getResult(); // Pega o resultado da task
                        AdditionalUserInfo additionalUserInfo = authResult.getAdditionalUserInfo(); // Pega as informações adicionais
                        boolean isNewUser = false; // Valor padrão
                        if (additionalUserInfo != null) {
                            isNewUser = additionalUserInfo.isNewUser(); // Verifica se é novo usuário
                        }

                        if (isNewUser) {
                            // Se for um usuário novo, salvamos os dados dele no Firestore
                            salvarNovoUsuarioGoogle();
                        } else {
                            // Se for antigo, apenas levamos para a tela inicial
                            irParaTelaInicial();
                        }
                    } else {
                        // Falha no login com Firebase
                        Toast.makeText(tela_login.this, "Falha na autenticação.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para salvar novos usuários do Google (igual ao anterior)
    private void salvarNovoUsuarioGoogle() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        String nome = user.getDisplayName();
        String email = user.getEmail();

        Map<String, Object> dadosPaciente = new HashMap<>();
        dadosPaciente.put("tipoUsuario", "paciente");
        dadosPaciente.put("nome", nome);
        dadosPaciente.put("email", email);
        dadosPaciente.put("uid", uid);
        // Os outros campos (CPF, Endereço, etc.) não são coletados pelo Google.
        // O usuário precisaria preenchê-los depois em uma tela de "Editar Perfil".

        db.collection("users").document(uid)
                .set(dadosPaciente)
                .addOnSuccessListener(aVoid -> irParaTelaInicial())
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao salvar perfil.", Toast.LENGTH_SHORT).show();
                    irParaTelaInicial();
                });
    }

    // Método helper para navegar
    private void irParaTelaInicial() {
        Toast.makeText(tela_login.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(tela_login.this, tela_inicial.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}