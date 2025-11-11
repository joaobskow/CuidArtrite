package com.example.app_osteoartrite.com.app_osteoartrite;

import android.app.DatePickerDialog; // Import para o DatePickerDialog
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.core.content.ContextCompat; // Para pegar cores do resources

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton; // Para o FAB
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.app_osteoartrite.com.app_osteoartrite.Sintoma;

import java.util.Calendar; // Para o DatePickerDialog

public class Registrar_Sintoma extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText editDescricaoSintoma, editLocalSintoma, editDataSintoma, editObservacaoSintoma;
    private MaterialButton btnConfirmarSintoma;
    private FloatingActionButton btnCircular; // Referência para o FAB

    private MaterialButton btnDorLeve, btnDorMedia, btnDorGrave;
    private String nivelDorSelecionado = "";
    private MaterialButton botaoAtivo = null;
    private float defaultElevation = -1f; // Para guardar a elevação padrão
    private int corLeve;
    private int corMedia;
    private int corGrave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_sintoma);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        corLeve = Color.parseColor("#5EAF6D"); // verde
        corMedia = Color.parseColor("#E0C302"); // amarelo
        corGrave = Color.parseColor("#EF4444"); // vermelho
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Conectar Views do XML
        editDescricaoSintoma = findViewById(R.id.editDescricaoSintoma); // Agora é o campo "SINTOMA" principal
        editLocalSintoma = findViewById(R.id.editLocalSintoma);
        editDataSintoma = findViewById(R.id.editDataSintoma);
        editObservacaoSintoma = findViewById(R.id.editObservacaoSintoma); // O campo "DESCRIÇÃO" na parte de baixo

        btnConfirmarSintoma = findViewById(R.id.btnConfirmarSintoma);

        btnCircular = findViewById(R.id.btnCircular); // Referência para o FAB

        btnDorLeve = findViewById(R.id.btnDorLeve);
        btnDorMedia = findViewById(R.id.btnDorMedia);
        btnDorGrave = findViewById(R.id.btnDorGrave);

        defaultElevation = btnDorLeve.getElevation(); // Salva a elevação original

        // --- Configuração do DatePickerDialog para editDataSintoma ---
        editDataSintoma.setOnClickListener(v -> showDatePickerDialog());

        // --- Listeners para os botões de dor ---
        btnDorLeve.setOnClickListener(this::handleNivelDorButtonClick);
        btnDorMedia.setOnClickListener(this::handleNivelDorButtonClick);
        btnDorGrave.setOnClickListener(this::handleNivelDorButtonClick);

        // --- Listener para o botão Confirmar ---
        btnConfirmarSintoma.setOnClickListener(v -> {
            String sintomaPrincipal = editDescricaoSintoma.getText().toString().trim();
            String local = editLocalSintoma.getText().toString().trim();
            String data = editDataSintoma.getText().toString().trim();
            String observacao = editObservacaoSintoma.getText().toString().trim(); // Pega o texto da nova descrição

            // O nivelDorSelecionado já é atualizado pelos cliques dos botões

            if (sintomaPrincipal.isEmpty() || local.isEmpty() || data.isEmpty()) {
                Toast.makeText(this, "Preencha o sintoma, local e data.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nivelDorSelecionado.isEmpty()) {
                Toast.makeText(this, "Por favor, selecione um nível de dor.", Toast.LENGTH_SHORT).show();
                return;
            }

            // A 'descricao' para a classe Sintoma agora pode ser a combinação
            // do sintoma principal e da observação, se houver.
            String descricaoFinal = sintomaPrincipal;
            if (!observacao.isEmpty()) {
                descricaoFinal += "\nObservação: " + observacao;
            }


            // Chama a função para salvar
            salvarSintomaNoFirestore(descricaoFinal, local, data, nivelDorSelecionado);
        });

        // --- Listener para o botão circular (se ele tiver alguma função, por exemplo, cancelar ou ir para outra tela) ---
        btnCircular.setOnClickListener(v -> {
            // Exemplo: Voltar para a tela anterior
            finish();
            // Ou alguma outra ação
        });
    }

    // --- Função para mostrar o DatePickerDialog ---
    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%d", selectedDay, (selectedMonth + 1), selectedYear);
                    editDataSintoma.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void handleNivelDorButtonClick(View view) {
        MaterialButton clickedButton = (MaterialButton) view;

        // Guarda elevação padrão uma única vez
        if (defaultElevation == -1f) {
            defaultElevation = clickedButton.getElevation();
        }

        // Resetar o estilo do botão anterior (se houver)
        if (botaoAtivo != null) {
            botaoAtivo.animate().setDuration(150).translationZ(defaultElevation);
            botaoAtivo.setStrokeWidth(0);

            // Restaurar a cor original do botão anterior
            if (botaoAtivo == btnDorLeve) {
                botaoAtivo.setBackgroundTintList(ColorStateList.valueOf(corLeve));
            } else if (botaoAtivo == btnDorMedia) {
                botaoAtivo.setBackgroundTintList(ColorStateList.valueOf(corMedia));
            } else if (botaoAtivo == btnDorGrave) {
                botaoAtivo.setBackgroundTintList(ColorStateList.valueOf(corGrave));
            }
        }

        // Define qual botão foi clicado e aplica cores
        if (clickedButton == btnDorLeve) {
            nivelDorSelecionado = "Leve";
            aplicarEstiloSelecionado(clickedButton, "#A7F3D0", "#5EAF6D");
        } else if (clickedButton == btnDorMedia) {
            nivelDorSelecionado = "Média";
            aplicarEstiloSelecionado(clickedButton, "#FEF08A", "#E0C302");
        } else if (clickedButton == btnDorGrave) {
            nivelDorSelecionado = "Grave";
            aplicarEstiloSelecionado(clickedButton, "#FCA5A5", "#EF4444");
        }

        botaoAtivo = clickedButton;
    }

    private void aplicarEstiloSelecionado(MaterialButton button, String corBorda, String corFundoSuave) {
        // Anima elevação
        float selectedElevation = defaultElevation + (8 * getResources().getDisplayMetrics().density);
        button.animate().setDuration(200).translationZ(selectedElevation);

        // Define cores e borda
        button.setStrokeColor(ColorStateList.valueOf(Color.parseColor(corBorda)));
        button.setStrokeWidth(6);
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(corFundoSuave)));

        // Efeito tátil (vibração leve)
        button.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
    }

    private void salvarSintomaNoFirestore(String descricao, String local, String data, String nivelDor) {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null) {
            Toast.makeText(this, "Erro: Usuário não logado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = usuarioAtual.getUid();

        Sintoma novoSintoma = new Sintoma(descricao, local, data, nivelDor);

        db.collection("users").document(uid).collection("sintomas")
                .add(novoSintoma)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Sintoma salvo!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao salvar sintoma: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}