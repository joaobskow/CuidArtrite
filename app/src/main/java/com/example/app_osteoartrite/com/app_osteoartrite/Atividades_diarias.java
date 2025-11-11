package com.example.app_osteoartrite.com.app_osteoartrite;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class Atividades_diarias extends AppCompatActivity implements AtividadeAdapter.OnStatusChangeListener {

    private static final String TAG = "Atividades_diarias";
    private static final String STATUS_TODAS = "Todas";
    private static final String STATUS_PENDENTE = "Pendente";
    private static final String STATUS_REALIZADA = "Realizada";
    private static final String STATUS_ATRASADA = "Atrasada"; // Usado apenas para filtro/exibição

    private Button buttonSelecionarData;
    private RecyclerView recyclerViewAtividades;
    private ProgressBar progressBar;
    private ChipGroup chipGroupStatusFilter;

    private AtividadeAdapter atividadeAdapter;
    private List<Atividade> listaCompletaAtividades = new ArrayList<>();
    private List<Atividade> listaFiltradaAtividades = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CollectionReference atividadesRef;

    private Calendar calendarioSelecionado;
    private SimpleDateFormat formatadorDataBotao;
    private SimpleDateFormat formatadorDiaSemana;
    private SimpleDateFormat formatadorDataFirestore;
    private Locale localeBrasil;
    private TimeZone fusoHorarioBrasil;
    private FloatingActionButton btnAjudaFloating;

    private final Map<String, Integer> diasMap = new HashMap<String, Integer>() {{
        put("Domingo", 1); put("Segunda-feira", 2); put("Terca-feira", 3);
        put("Quarta-feira", 4); put("Quinta-feira", 5); put("Sexta-feira", 6);
        put("Sabado", 7);
    }};

    private String filtroStatusAtual = STATUS_TODAS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividades_diarias);

        // --- Inicialização Firebase ---
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Usuário não autenticado.");
            finish(); return;
        }
        atividadesRef = db.collection("users").document(currentUser.getUid()).collection("atividades");

        // --- Referências da UI ---
        buttonSelecionarData = findViewById(R.id.button_selecionar_data);
        recyclerViewAtividades = findViewById(R.id.recycler_view_atividades);
        progressBar = findViewById(R.id.progress_bar);
        chipGroupStatusFilter = findViewById(R.id.chipgroup_status_filter);

        // --- Inicialização Data/Calendário ---
        fusoHorarioBrasil = TimeZone.getTimeZone("America/Sao_Paulo");
        localeBrasil = new Locale("pt", "BR");
        calendarioSelecionado = Calendar.getInstance(fusoHorarioBrasil, localeBrasil);
        formatadorDataBotao = new SimpleDateFormat("EEE, dd 'de' MMM", localeBrasil);
        formatadorDiaSemana = new SimpleDateFormat("EEEE", localeBrasil);
        formatadorDataFirestore = new SimpleDateFormat("yyyy-MM-dd", localeBrasil); // Formato para salvar/comparar dataRealizacao
        formatadorDataBotao.setTimeZone(fusoHorarioBrasil);
        formatadorDiaSemana.setTimeZone(fusoHorarioBrasil);
        formatadorDataFirestore.setTimeZone(fusoHorarioBrasil);

        // --- Configuração RecyclerView ---
        recyclerViewAtividades.setLayoutManager(new LinearLayoutManager(this));
        atividadeAdapter = new AtividadeAdapter(this, listaFiltradaAtividades, this);
        recyclerViewAtividades.setAdapter(atividadeAdapter);

        // --- Configuração de Cliques ---
        buttonSelecionarData.setOnClickListener(v -> mostrarDatePicker());
        chipGroupStatusFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_todas) filtroStatusAtual = STATUS_TODAS;
            else if (checkedId == R.id.chip_pendentes) filtroStatusAtual = STATUS_PENDENTE;
            else if (checkedId == R.id.chip_realizadas) filtroStatusAtual = STATUS_REALIZADA;
            else if (checkedId == R.id.chip_atrasadas) filtroStatusAtual = STATUS_ATRASADA;
            else filtroStatusAtual = STATUS_TODAS;
            filtrarEAtualizarLista();
        });

        ImageButton Voltar = findViewById(R.id.Btn_Voltar);
        Voltar.setOnClickListener(v -> finish());

        btnAjudaFloating = findViewById(R.id.btnAjudaFloating); // Conecta ao ID do XML
        btnAjudaFloating.setOnClickListener(v -> {
            Intent intent = new Intent(Atividades_diarias.this, chat.class);
            startActivity(intent);
        });

        // --- Carregamento Inicial ---
        atualizarTextoBotaoData();
        carregarAtividadesDoFirestore();
    }

    // Mostra o DatePickerDialog
    private void mostrarDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendarioSelecionado.set(year, month, dayOfMonth);
            atualizarTextoBotaoData();
            filtrarEAtualizarLista();
        },
                calendarioSelecionado.get(Calendar.YEAR),
                calendarioSelecionado.get(Calendar.MONTH),
                calendarioSelecionado.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Atualiza o texto do botão de data
    private void atualizarTextoBotaoData() {
        String dataFormatada = formatadorDataBotao.format(calendarioSelecionado.getTime());
        buttonSelecionarData.setText(dataFormatada.substring(0, 1).toUpperCase() + dataFormatada.substring(1));
    }

    // Carrega as atividades do Firestore
    private void carregarAtividadesDoFirestore() {
        if (atividadesRef == null) { Log.e(TAG,"Ref nula"); return; }
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewAtividades.setVisibility(View.GONE);

        atividadesRef.orderBy("diaSemana", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaCompletaAtividades.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "Coleção vazia, gerando lista padrão."); // Mudado para Warning
                        listaCompletaAtividades = gerarListaDeAtividadesPadrao();
                        salvarListaPadraoNoFirestore(listaCompletaAtividades);
                    } else {
                        Log.i(TAG, "Carregando " + queryDocumentSnapshots.size() + " atividades."); // Mudado para Info
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                Atividade atividade = document.toObject(Atividade.class);
                                atividade.setId(document.getId());
                                listaCompletaAtividades.add(atividade);
                            } catch (Exception e) { Log.e(TAG, "Erro conversão doc Firestore: ID=" + document.getId(), e);}
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    recyclerViewAtividades.setVisibility(View.VISIBLE);
                    filtrarEAtualizarLista(); // Primeiro filtro com dados carregados/gerados
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Erro crítico ao carregar atividades do Firestore", e);
                    Toast.makeText(Atividades_diarias.this, "Erro ao carregar lista de atividades.", Toast.LENGTH_LONG).show();
                    filtrarEAtualizarLista(); // Tenta exibir lista vazia
                });
    }

    // Gera a lista padrão
    private List<Atividade> gerarListaDeAtividadesPadrao() {
        // ... (Seu código para gerar a lista de 50+ atividades padrão) ...
        // Certifique-se que o status inicial seja STATUS_PENDENTE
        List<Atividade> atividades = new ArrayList<>();
        String[] diasUteis = {"Segunda-feira", "Terca-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira"};
        for (String dia : diasUteis) {
            for (int i = 1; i <= 10; i++) {
                String nome = "Alongamento Padrão " + i + " (" + dia.substring(0, 3) + ")";
                String desc = "Descrição padrão do alongamento número " + i + " para " + dia + ".";
                String tempo = (i % 3 == 0) ? "15 Repetições" : "3 Minutos";
                String local = (i <= 5) ? "Membros Sup." : "Membros Inf.";
                atividades.add(new Atividade(null, nome, desc, tempo, local, dia, STATUS_PENDENTE));
            }
        }
        return atividades;
    }

    // Salva a lista padrão no Firestore
    private void salvarListaPadraoNoFirestore(List<Atividade> atividadesParaSalvar) {
        // ... (Código para salvar em lote usando WriteBatch - igual ao anterior) ...
        if (atividadesRef == null) return;
        WriteBatch batch = db.batch();
        Log.i(TAG, "Salvando lista padrão (" + atividadesParaSalvar.size() + " itens) no Firestore.");
        for (Atividade atividade : atividadesParaSalvar) {
            DocumentReference docRef = atividadesRef.document();
            batch.set(docRef, atividade);
        }
        batch.commit()
                .addOnSuccessListener(aVoid -> Log.i(TAG, "Lista padrão salva com sucesso!"))
                .addOnFailureListener(e -> Log.e(TAG, "Erro ao salvar lista padrão", e));
    }


    // --- MÉTODO DE FILTRAGEM ATUALIZADO COM LÓGICA REFINADA ---
    private void filtrarEAtualizarLista() {
        listaFiltradaAtividades.clear(); // Limpa a lista a ser exibida

        // Dia da semana selecionado no calendário (ex: "Terça-feira")
        String diaSelecionadoStr = formatadorDiaSemana.format(calendarioSelecionado.getTime());
        diaSelecionadoStr = diaSelecionadoStr.substring(0, 1).toUpperCase() + diaSelecionadoStr.substring(1);

        // Dados de HOJE para determinar o que está atrasado
        Calendar hojeCal = Calendar.getInstance(fusoHorarioBrasil, localeBrasil);
        int indiceHoje = hojeCal.get(Calendar.DAY_OF_WEEK); // 1=Dom, 2=Seg, ...

        Log.d(TAG, "Iniciando Filtragem - Dia Selecionado: " + diaSelecionadoStr + ", Filtro Status: " + filtroStatusAtual);

        // Percorre a lista completa de atividades carregadas do Firestore
        for (Atividade atividade : listaCompletaAtividades) {
            String diaAtividadeStr = atividade.getDiaSemana();
            if (diaAtividadeStr == null) continue; // Pula atividades sem dia definido

            String statusOriginal = atividade.getStatus() != null ? atividade.getStatus() : STATUS_PENDENTE;

            // Verifica se a atividade pertence ao DIA DA SEMANA selecionado
            boolean isDiaCorreto = diaSelecionadoStr.equals(diaAtividadeStr);

            // Verifica se a atividade está REALMENTE ATRASADA (dia anterior a hoje E pendente)
            boolean isAtrasadaReal = false;
            Integer indiceAtividade = diasMap.get(diaAtividadeStr);
            if (indiceAtividade != null && indiceAtividade < indiceHoje && STATUS_PENDENTE.equals(statusOriginal)) {
                isAtrasadaReal = true;
            }

            // Define o status a ser usado para avaliação e exibição
            String statusConsiderado = isAtrasadaReal ? STATUS_ATRASADA : statusOriginal;

            // --- LÓGICA DE INCLUSÃO REFINADA ---
            boolean incluir = false;

            // 1. Verifica se a atividade pertence ao DIA DA SEMANA selecionado
            if (isDiaCorreto) {
                // Se pertence ao dia, verifica se passa pelo filtro de STATUS
                switch (filtroStatusAtual) {
                    case STATUS_TODAS:      incluir = true; break;
                    case STATUS_PENDENTE:   incluir = STATUS_PENDENTE.equals(statusConsiderado) || STATUS_ATRASADA.equals(statusConsiderado); break; // Pendentes do dia (inclui se ficou atrasada no próprio dia?) Não, simplificamos: só pendente original. -> // CORREÇÃO: Usar statusConsiderado
                    // case STATUS_PENDENTE:   incluir = STATUS_PENDENTE.equals(statusOriginal); break; // Só inclui se for PENDENTE original do dia
                    case STATUS_REALIZADA:  incluir = STATUS_REALIZADA.equals(statusConsiderado); break;
                    case STATUS_ATRASADA:   incluir = STATUS_ATRASADA.equals(statusConsiderado); break; // Só inclui se for ATRASADA real do dia (não deve acontecer pela lógica de atraso)
                }
                // Correção lógica para Pendente e Atrasada no dia selecionado:
                if (filtroStatusAtual.equals(STATUS_PENDENTE)) {
                    incluir = STATUS_PENDENTE.equals(statusOriginal); // Só pendentes originais do dia
                } else if (filtroStatusAtual.equals(STATUS_ATRASADA)) {
                    incluir = false; // O filtro "Atrasadas" não mostra itens do dia selecionado
                }

            }

            // 2. Verifica se a atividade deve ser incluída PORQUE está atrasada (mesmo não sendo do dia selecionado)
            // Isso só acontece se o filtro permitir (Todas, Pendentes ou Atrasadas)
            if (isAtrasadaReal) {
                if (filtroStatusAtual.equals(STATUS_TODAS) || filtroStatusAtual.equals(STATUS_PENDENTE) || filtroStatusAtual.equals(STATUS_ATRASADA)) {
                    incluir = true; // Inclui a atividade atrasada se o filtro permitir
                } else if (filtroStatusAtual.equals(STATUS_REALIZADA)){
                    // Se o filtro for "Realizada", não mostramos atrasadas
                    incluir = false;
                }
            }
            // --- FIM DA LÓGICA DE INCLUSÃO ---


            // Se a atividade deve ser incluída, adiciona à lista filtrada
            if (incluir) {
                // Cria uma cópia para exibir com o status correto (Pendente, Realizada, Atrasada)
                Atividade atividadeParaExibir = new Atividade(
                        atividade.getId(), atividade.getNome(), atividade.getDescricao(),
                        atividade.getTempoRepeticoes(), atividade.getLocalAfetado(),
                        atividade.getDiaSemana(), statusConsiderado // Usa o status de exibição
                );
                // Evita duplicatas (se uma atrasada for do dia selecionado e filtro for Todas/Pendentes)
                boolean jaExiste = false;
                for(Atividade a : listaFiltradaAtividades){
                    if(a.getId() != null && a.getId().equals(atividadeParaExibir.getId())){ jaExiste = true; break; }
                }
                if (!jaExiste) {
                    listaFiltradaAtividades.add(atividadeParaExibir);
                }
            }
        }

        // Ordena a lista filtrada (Atrasadas primeiro)
        Collections.sort(listaFiltradaAtividades, (a1, a2) -> {
            boolean a1Atrasada = STATUS_ATRASADA.equals(a1.getStatus());
            boolean a2Atrasada = STATUS_ATRASADA.equals(a2.getStatus());
            if (a1Atrasada && !a2Atrasada) return -1;
            if (!a1Atrasada && a2Atrasada) return 1;
            String nome1 = a1.getNome() != null ? a1.getNome() : "";
            String nome2 = a2.getNome() != null ? a2.getNome() : "";
            return nome1.compareTo(nome2);
        });

        // Atualiza o RecyclerView
        if (atividadeAdapter != null) {
            atividadeAdapter.updateData(listaFiltradaAtividades);
            Log.i(TAG, "Adapter atualizado com " + listaFiltradaAtividades.size() + " itens filtrados.");
        } else {
            Log.w(TAG, "Adapter nulo ao tentar atualizar dados filtrados.");
        }
    }


    // --- Callback do Adapter: Chamado quando o botão de status é clicado ---
    @Override
    public void onStatusChanged(Atividade atividadeClicada, String novoStatus) {
        if (atividadesRef == null || atividadeClicada == null || atividadeClicada.getId() == null) {
            Log.e(TAG, "Erro ao salvar: dados inválidos."); Toast.makeText(this, "Erro ao salvar.", Toast.LENGTH_SHORT).show(); return;
        }

        String atividadeId = atividadeClicada.getId();
        progressBar.setVisibility(View.VISIBLE);

        DocumentReference atividadeDocRef = atividadesRef.document(atividadeId);

        // Prepara os dados para salvar no Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", novoStatus);

        // Se o novo status for "Realizada", adiciona a data de hoje
        if (STATUS_REALIZADA.equals(novoStatus)) {
            String hojeFormatado = formatadorDataFirestore.format(new Date()); // Usa o formatador "yyyy-MM-dd"
            updates.put("dataRealizacao", hojeFormatado);
        } else {
            // Se voltou para "Pendente", limpa a data de realização
            updates.put("dataRealizacao", null); // Ou FieldValue.delete() se preferir remover o campo
        }

        // Atualiza os campos no Firestore
        atividadeDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Log.i(TAG, "Status atualizado no Firestore para " + novoStatus + " (ID: " + atividadeId + ")");
                    Toast.makeText(Atividades_diarias.this, "'" + atividadeClicada.getNome() + "' marcada como " + novoStatus, Toast.LENGTH_SHORT).show();

                    // Atualiza o status e a dataRealizacao na lista *completa* em memória
                    boolean found = false;
                    for (Atividade item : listaCompletaAtividades) {
                        if (item.getId() != null && item.getId().equals(atividadeId)) {
                            item.setStatus(novoStatus);
                            // Atualiza a data de realização no objeto local
                            if (STATUS_REALIZADA.equals(novoStatus)) {
                                item.setDataRealizacao(formatadorDataFirestore.format(new Date()));
                            } else {
                                item.setDataRealizacao(null);
                            }
                            found = true;
                            break;
                        }
                    }
                    if(!found) Log.w(TAG, "ID " + atividadeId + " não encontrado na lista completa pós-salvar.");

                    // Re-filtra a lista e atualiza a UI para refletir a mudança
                    filtrarEAtualizarLista();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Erro ao atualizar status no Firestore ID=" + atividadeId, e);
                    Toast.makeText(Atividades_diarias.this, "Erro ao salvar alteração de status.", Toast.LENGTH_LONG).show();
                    // Considerar recarregar ou reverter visualmente
                });
    }
}