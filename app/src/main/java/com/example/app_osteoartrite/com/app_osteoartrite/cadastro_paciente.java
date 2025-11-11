package com.example.app_osteoartrite.com.app_osteoartrite;

import android.app.AlertDialog;
import android.content.Intent; // Importação necessária para navegação
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import android.widget.ImageButton;

// Nome da classe correto
public class cadastro_paciente extends AppCompatActivity {

    // Elementos da UI
    private EditText editNome, editCPF, editEndereco, editNumero, editCEP, editCidade, editUF, editTelefone, editEmail, editSenha;
    private CheckBox checkDoenca, checkTermos;
    private Button btnConfirmarCadastro;
    private TextView txtVerTermos, txtJaPossuiCadastro;

    // Variáveis do Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_paciente);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton Voltar = findViewById(R.id.Btn_Voltar);
        Voltar.setOnClickListener(v -> finish());

        // Inicializar o Firebase Auth e Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referenciar os IDs do XML
        editNome = findViewById(R.id.editNome);
        editCPF = findViewById(R.id.editCPF);
        editEndereco = findViewById(R.id.editEndereco);
        editNumero = findViewById(R.id.editNumero);
        editCEP = findViewById(R.id.editCEP);
        editCidade = findViewById(R.id.editCidade);
        editUF = findViewById(R.id.editUF);
        editTelefone = findViewById(R.id.editTelefone);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        checkDoenca = findViewById(R.id.checkDoenca);
        checkTermos = findViewById(R.id.checkTermos);
        btnConfirmarCadastro = findViewById(R.id.btnConfirmarCadastro);
        txtVerTermos = findViewById(R.id.txtVerTermos);
        txtJaPossuiCadastro = findViewById(R.id.txtJaPossuiCadastro);

        // --- Configuração dos Cliques ---

        // Clique do botão de cadastro
        btnConfirmarCadastro.setOnClickListener(v -> {
            validarEcadastrar();
        });

        // Clique para ver os termos
        txtVerTermos.setOnClickListener(v -> {
            mostrarPopupTermosDeUso();
        });

        // Clique para "Já possuo cadastro"
        txtJaPossuiCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(cadastro_paciente.this, tela_login.class);
            startActivity(intent);
            finish();
        });

    } // --- FIM DO MÉTODO onCreate ---

    // Método que exibe o pop-up dos Termos de Uso
    private void mostrarPopupTermosDeUso() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.termos_de_uso_titulo) // Título do strings.xml
                .setMessage(R.string.termos_de_uso_conteudo) // Conteúdo do strings.xml
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss(); // Apenas fecha o pop-up
                })
                .show();
    }

    // Método que valida os campos e inicia o cadastro
    private void validarEcadastrar() {
        // Pegar todos os valores
        String nome = editNome.getText().toString().trim();
        String cpf = editCPF.getText().toString().trim();
        String endereco = editEndereco.getText().toString().trim();
        String numero = editNumero.getText().toString().trim();
        String cep = editCEP.getText().toString().trim();
        String cidade = editCidade.getText().toString().trim();
        String uf = editUF.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        // ** LÓGICA DO CHECKBOX ATUALIZADA **
        // O '!' inverte a lógica. Se a caixa NÃO está marcada, 'possuiDoenca' será true.
        boolean possuiDoenca = !checkDoenca.isChecked();
        boolean aceitouTermos = checkTermos.isChecked();

        // --- Validações ---
        if (nome.isEmpty() || cpf.isEmpty() || endereco.isEmpty() || cep.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }
        if (senha.length() < 6) {
            Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!aceitouTermos) {
            Toast.makeText(this, "Você deve aceitar os termos de uso", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Se tudo estiver ok, começa o cadastro ---
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        // Passamos o UID e a flag 'possuiDoenca'
                        salvarDadosAdicionais(uid, nome, cpf, endereco, numero, cep, cidade, uf, telefone, email, possuiDoenca);
                    } else {
                        // Falha no cadastro (Passo A).
                        String mensagemErro = "Erro ao cadastrar.";
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthUserCollisionException e) {
                            mensagemErro = "Este e-mail já está em uso.";
                        } catch (Exception e) {
                            mensagemErro = e.getMessage();
                        }
                        Toast.makeText(cadastro_paciente.this, mensagemErro, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Método que salva os dados no Firestore (COM LÓGICA CONDICIONAL)
    private void salvarDadosAdicionais(String uid, String nome, String cpf, String endereco, String numero, String cep, String cidade, String uf, String telefone, String email, boolean possuiDoenca) {

        Map<String, Object> dadosPaciente = new HashMap<>();
        dadosPaciente.put("tipoUsuario", "paciente");
        dadosPaciente.put("nome", nome);
        dadosPaciente.put("cpf", cpf);
        dadosPaciente.put("endereco", endereco);
        dadosPaciente.put("numero", numero);
        dadosPaciente.put("cep", cep);
        dadosPaciente.put("cidade", cidade);
        dadosPaciente.put("uf", uf);
        dadosPaciente.put("telefone", telefone);
        dadosPaciente.put("email", email);
        dadosPaciente.put("possuiDoencaDeclarada", possuiDoenca); // Salva o status da doença

        db.collection("users").document(uid)
                .set(dadosPaciente)
                .addOnSuccessListener(aVoid -> {

                    // --- ESTA É A NOVA LÓGICA DE DIRECIONAMENTO ---

                    if (possuiDoenca) {
                        // O usuário NÃO marcou a caixa, então abrimos a tela de diagnóstico
                        Toast.makeText(cadastro_paciente.this, "Cadastro principal salvo! Registre seu diagnóstico.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(cadastro_paciente.this, registro_diagnostico.class);
                        intent.putExtra("USER_UID", uid); // Passamos o ID do usuário para a próxima tela
                        startActivity(intent);
                        finish(); // Fecha a tela de cadastro

                    } else {
                        // O usuário marcou a caixa (não tem doença), fluxo normal
                        Toast.makeText(cadastro_paciente.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // Fecha a tela de cadastro e volta (para login ou main)
                    }

                    // --- FIM DA NOVA LÓGICA ---

                })
                .addOnFailureListener(e -> {
                    // Falha ao salvar no Firestore (Passo C)
                    Toast.makeText(cadastro_paciente.this, "Erro ao salvar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}