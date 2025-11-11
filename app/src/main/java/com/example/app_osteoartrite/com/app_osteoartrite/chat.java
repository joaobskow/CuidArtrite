package com.example.app_osteoartrite.com.app_osteoartrite;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class chat extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> conversationHistory;
    private LinearLayout optionsContainer;
    private Map<String, ChatNode> chatTree;
    private String currentNodeId = "root";
    private ImageButton Voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupRecyclerView();
        buildChatTree();
        displayNode(currentNodeId);

        Voltar = findViewById(R.id.Btn_Voltar);
        Voltar.setOnClickListener(v -> finish());
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        optionsContainer = findViewById(R.id.options_container);
    }

    private void setupRecyclerView() {
        conversationHistory = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, conversationHistory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void buildChatTree() {
        chatTree = new HashMap<>();

        // --- NÓ RAIZ ---
        ChatNode root = new ChatNode();
        root.id = "root";
        root.message = "👋 Olá! Sou sua assistente de saúde.\nO que você quer aprender hoje sobre osteoartrite?";
        root.options = Arrays.asList(
                new ChatOption("✅ Entendendo a condição", "entendendo"),
                new ChatOption("❗ Sintomas", "sintomas"),
                new ChatOption("💊 Tratamento", "tratamento"),
                new ChatOption("🥦 Alimentação", "alimentacao"),
                new ChatOption("🌈 Qualidade de vida", "qualidade_vida")
        );
        chatTree.put(root.id, root);

        // --- NÍVEL 1: TÓPICOS PRINCIPAIS ---

        // 1. Entendendo a Condição
        ChatNode entendendo = new ChatNode();
        entendendo.id = "entendendo";
        entendendo.message = "Ótimo! Entender a osteoartrite é o primeiro passo.\nSobre qual aspecto você quer saber mais?";
        entendendo.options = Arrays.asList(
                new ChatOption("O que acontece na articulação?", "entendendo_o_que_acontece"),
                new ChatOption("Quais articulações são mais afetadas?", "entendendo_articulacoes"),
                new ChatOption("Por que ela acontece (causas)?", "entendendo_causas"),
                new ChatOption("⬅️ Voltar ao Menu", "root")
        );
        chatTree.put(entendendo.id, entendendo);

        // 2. Sintomas
        ChatNode sintomas = new ChatNode();
        sintomas.id = "sintomas";
        sintomas.message = "Reconhecer os sinais é importante.\nO que você gostaria de ver?";
        sintomas.options = Arrays.asList(
                new ChatOption("Sintomas principais", "sintomas_principais"),
                new ChatOption("Padrão comum da dor/rigidez", "sintomas_padrao"),
                new ChatOption("Sinais de Alerta Urgente ❗", "sintomas_urgente"),
                new ChatOption("⬅️ Voltar ao Menu", "root")
        );
        chatTree.put(sintomas.id, sintomas);

        // 3. Tratamento
        ChatNode tratamento = new ChatNode();
        tratamento.id = "tratamento";
        tratamento.message = "Existem várias formas de tratar e controlar a osteoartrite!\nO objetivo é reduzir a dor e manter o movimento.\nQual tipo de tratamento te interessa?";
        tratamento.options = Arrays.asList(
                new ChatOption("A) Medicamentos", "tratamento_medicamentos"),
                new ChatOption("B) Práticas Integrativas (PICs) ✨", "tratamento_pics"),
                new ChatOption("C) Fisioterapia", "tratamento_fisio"),
                new ChatOption("D) Mudanças no Estilo de Vida", "tratamento_estilo_vida"),
                new ChatOption("E) Tratamentos Avançados", "tratamento_avancados"),
                new ChatOption("Abordagem Integrada (Combinação)", "tratamento_combinado"),
                new ChatOption("⬅️ Voltar ao Menu", "root")
        );
        chatTree.put(tratamento.id, tratamento);

        // 4. Alimentação
        ChatNode alimentacao = new ChatNode();
        alimentacao.id = "alimentacao";
        alimentacao.message = "A alimentação pode ajudar a aliviar a inflamação.\nO que você quer saber?";
        alimentacao.options = Arrays.asList(
                new ChatOption("Alimentos Amigos ✅", "alimentacao_amigos"),
                new ChatOption("Alimentos a Evitar ❌", "alimentacao_evitar"),
                new ChatOption("Importância da Hidratação 💧", "alimentacao_hidratacao"),
                new ChatOption("Chás recomendados 🍵", "alimentacao_chas"),
                new ChatOption("⬅️ Voltar ao Menu", "root")
        );
        chatTree.put(alimentacao.id, alimentacao);

        // 5. Qualidade de Vida
        ChatNode qualidadeVida = new ChatNode();
        qualidadeVida.id = "qualidade_vida";
        qualidadeVida.message = "Viver bem com osteoartrite envolve diversos aspectos.\nQual deles você gostaria de explorar?";
        qualidadeVida.options = Arrays.asList(
                new ChatOption("Adaptações Práticas no dia a dia", "qualidade_adaptacoes"),
                new ChatOption("Cuidando da Mente / Emoções 😊", "qualidade_mente"),
                new ChatOption("Sinais de Alerta / Quando procurar ajuda", "sinais_alerta"),
                new ChatOption("Princípios para Qualidade de Vida", "qualidade_principios"),
                new ChatOption("⬅️ Voltar ao Menu", "root")
        );
        chatTree.put(qualidadeVida.id, qualidadeVida);


        // --- NÍVEL 2+: ENTENDENDO A CONDIÇÃO ---

        ChatNode entendendoOQueAcontece = new ChatNode();
        entendendoOQueAcontece.id = "entendendo_o_que_acontece";
        entendendoOQueAcontece.message = "<b>O que acontece?</b>\nA osteoartrite (ou artrose) é o desgaste natural da cartilagem que protege suas articulações. Com o tempo, os ossos ficam mais próximos e causam dor e rigidez.\n\nPense assim: É como o desgaste de um pneu de carro - com o uso ao longo dos anos, a proteção vai diminuindo.\n\n✅ <b>IMPORTANTE SABER:</b>\n- É muito comum após os 60 anos\n- NÃO é culpa sua\n- Tem tratamento e controle\n- Você pode viver bem com osteoartrite";
        entendendoOQueAcontece.options = Arrays.asList(
                new ChatOption("Ver articulações afetadas", "entendendo_articulacoes"),
                new ChatOption("Ver causas", "entendendo_causas"),
                new ChatOption("⬅️ Voltar (Entendendo)", "entendendo"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(entendendoOQueAcontece.id, entendendoOQueAcontece);

        ChatNode entendendoArticulacoes = new ChatNode();
        entendendoArticulacoes.id = "entendendo_articulacoes";
        entendendoArticulacoes.message = "<b>Articulações mais afetadas:</b>\n- Joelhos\n- Mãos e dedos\n- Quadril\n- Coluna\n- Pés";
        entendendoArticulacoes.options = Arrays.asList(
                new ChatOption("O que acontece?", "entendendo_o_que_acontece"),
                new ChatOption("Ver causas", "entendendo_causas"),
                new ChatOption("⬅️ Voltar (Entendendo)", "entendendo"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(entendendoArticulacoes.id, entendendoArticulacoes);

        ChatNode entendendoCausas = new ChatNode();
        entendendoCausas.id = "entendendo_causas";
        entendendoCausas.message = "<b>POR QUE ACONTECE?</b>\nCausas principais:\n- Idade: desgaste natural\n- Uso repetitivo: trabalhos que sobrecarregam\n- Lesões anteriores: fraturas, torções\n- Sobrepeso: pressão extra\n- Genética: pode ser de família\n- Postura inadequada\n\n<b>FATORES QUE VOCÊ PODE CONTROLAR:</b>\n✅ Peso corporal\n✅ Atividade física regular\n✅ Postura no dia a dia\n✅ Proteção das articulações\n✅ Alimentação saudável";
        entendendoCausas.options = Arrays.asList(
                new ChatOption("O que acontece?", "entendendo_o_que_acontece"),
                new ChatOption("Ver articulações afetadas", "entendendo_articulacoes"),
                new ChatOption("⬅️ Voltar (Entendendo)", "entendendo"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(entendendoCausas.id, entendendoCausas);

        // --- NÍVEL 2+: SINTOMAS ---

        ChatNode sintomasPrincipais = new ChatNode();
        sintomasPrincipais.id = "sintomas_principais";
        sintomasPrincipais.message = "<b>Sintomas principais:</b>\n✓ Dor nas articulações (piora com movimento)\n✓ Rigidez pela manhã (melhora em ~30 min)\n✓ Inchaço leve nas juntas\n✓ Estalos ao movimentar\n✓ Dificuldade para tarefas simples\n✓ Sensação de \"travamento\"";
        sintomasPrincipais.options = Arrays.asList(
                new ChatOption("Ver padrão comum", "sintomas_padrao"),
                new ChatOption("Ver Sinais de Alerta Urgente", "sintomas_urgente"),
                new ChatOption("⬅️ Voltar (Sintomas)", "sintomas"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(sintomasPrincipais.id, sintomasPrincipais);

        ChatNode sintomasPadrao = new ChatNode();
        sintomasPadrao.id = "sintomas_padrao";
        sintomasPadrao.message = "<b>Padrão comum:</b>\nManhã: mais rígido\nTarde: melhora com movimento suave\nNoite: pode doer após atividades\n\n<b>A dor varia:</b> Alguns dias melhor, outros pior - é normal!";
        sintomasPadrao.options = Arrays.asList(
                new ChatOption("Ver sintomas principais", "sintomas_principais"),
                new ChatOption("Ver Sinais de Alerta Urgente", "sintomas_urgente"),
                new ChatOption("⬅️ Voltar (Sintomas)", "sintomas"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(sintomasPadrao.id, sintomasPadrao);

        ChatNode sintomasUrgente = new ChatNode();
        sintomasUrgente.id = "sintomas_urgente";
        sintomasUrgente.message = "<b>❗ QUANDO PROCURAR AJUDA URGENTE:</b>\n- Dor muito forte e súbita\n- Inchaço grande e vermelhidão\n- Febre junto com dor\n- Impossibilidade de mover a articulação";
        sintomasUrgente.options = Arrays.asList(
                new ChatOption("Ver sintomas principais", "sintomas_principais"),
                new ChatOption("Ver padrão comum", "sintomas_padrao"),
                new ChatOption("⬅️ Voltar (Sintomas)", "sintomas"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(sintomasUrgente.id, sintomasUrgente);


        // --- NÍVEL 2+: TRATAMENTO (NÓS ADICIONADOS) ---

        ChatNode tratamentoMedicamentos = new ChatNode();
        tratamentoMedicamentos.id = "tratamento_medicamentos";
        tratamentoMedicamentos.message = "<b>A) Medicamentos:</b>\nO tratamento foca em aliviar a dor. Os médicos podem receitar:\n\n✓ <b>Analgésicos:</b> Como Paracetamol, para dor leve.\n✓ <b>Anti-inflamatórios (AINEs):</b> Como Ibuprofeno ou Naproxeno, para dor e inflamação.\n✓ <b>Tópicos:</b> Cremes e pomadas que você aplica na pele.\n\n❗ <b>Importante:</b> Nunca se automedique. Sempre consulte seu médico.";
        tratamentoMedicamentos.options = Arrays.asList(
                new ChatOption("Ver Práticas Integrativas ✨", "tratamento_pics"),
                new ChatOption("Ver Fisioterapia", "tratamento_fisio"),
                new ChatOption("⬅️ Voltar (Tratamento)", "tratamento"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(tratamentoMedicamentos.id, tratamentoMedicamentos);

        ChatNode tratamentoPics = new ChatNode();
        tratamentoPics.id = "tratamento_pics";
        tratamentoPics.message = "<b>B) Práticas Integrativas (PICs) ✨:</b>\nSão terapias complementares que ajudam no bem-estar geral e no controle da dor, em conjunto com o tratamento médico.\n\nExemplos incluem:\n✓ Acupuntura\n✓ Meditação\n✓ Yoga\n✓ Quiropraxia\n\nElas ajudam a relaxar o corpo e a mente.";
        tratamentoPics.options = Arrays.asList(
                new ChatOption("Ver Fisioterapia", "tratamento_fisio"),
                new ChatOption("Ver Medicamentos", "tratamento_medicamentos"),
                new ChatOption("⬅️ Voltar (Tratamento)", "tratamento"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(tratamentoPics.id, tratamentoPics);

        ChatNode tratamentoFisio = new ChatNode();
        tratamentoFisio.id = "tratamento_fisio";
        tratamentoFisio.message = "<b>C) Fisioterapia:</b>\nÉ um dos pilares do tratamento! Um fisioterapeuta criará um plano para:\n\n✓ <b>Fortalecer os músculos:</b> Músculos fortes protegem a articulação.\n✓ <b>Melhorar a mobilidade:</b> Exercícios para manter o movimento.\n✓ <b>Aliviar a dor:</b> Usando técnicas e exercícios específicos.\n\nExercícios de baixo impacto como caminhada e natação também são muito recomendados.";
        tratamentoFisio.options = Arrays.asList(
                new ChatOption("Ver Mudanças no Estilo de Vida", "tratamento_estilo_vida"),
                new ChatOption("Ver Medicamentos", "tratamento_medicamentos"),
                new ChatOption("⬅️ Voltar (Tratamento)", "tratamento"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(tratamentoFisio.id, tratamentoFisio);

        ChatNode tratamentoEstiloVida = new ChatNode();
        tratamentoEstiloVida.id = "tratamento_estilo_vida";
        tratamentoEstiloVida.message = "<b>D) Mudanças no Estilo de Vida:</b>\nO que você faz todo dia tem um impacto GIGANTE:\n\n✓ <b>Controle de Peso:</b> Perder até mesmo um pouco de peso alivia muito a pressão sobre os joelhos e quadril.\n✓ <b>Atividade Física:</b> Mantenha-se ativo! Movimento lubrifica as articulações. Prefira baixo impacto (caminhada, bicicleta, natação).\n✓ <b>Evite sobrecarga:</b> Cuidado com postura e ao levantar peso.";
        tratamentoEstiloVida.options = Arrays.asList(
                new ChatOption("Ver Tratamentos Avançados", "tratamento_avancados"),
                new ChatOption("Ver Fisioterapia", "tratamento_fisio"),
                new ChatOption("⬅️ Voltar (Tratamento)", "tratamento"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(tratamentoEstiloVida.id, tratamentoEstiloVida);

        ChatNode tratamentoAvancados = new ChatNode();
        tratamentoAvancados.id = "tratamento_avancados";
        tratamentoAvancados.message = "<b>E) Tratamentos Avançados:</b>\nQuando a dor não melhora, o médico pode sugerir:\n\n✓ <b>Infiltração:</b> Injeção de medicamentos (como ácido hialurônico ou corticoides) direto na articulação para lubrificar e reduzir a inflamação.\n✓ <b>Cirurgia (Artroplastia):</b> Em casos mais severos, pode ser recomendada a cirurgia para substituir a articulação por uma prótese.";
        tratamentoAvancados.options = Arrays.asList(
                new ChatOption("Ver Abordagem Integrada", "tratamento_combinado"),
                new ChatOption("Ver Estilo de Vida", "tratamento_estilo_vida"),
                new ChatOption("⬅️ Voltar (Tratamento)", "tratamento"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(tratamentoAvancados.id, tratamentoAvancados);

        ChatNode tratamentoCombinado = new ChatNode();
        tratamentoCombinado.id = "tratamento_combinado";
        tratamentoCombinado.message = "<b>Abordagem Integrada:</b>\nEste é o segredo! O melhor resultado não vem de uma coisa só, mas da <b>combinação</b> de várias delas.\n\nUm bom plano inclui:\n1. Fisioterapia (para força)\n2. Controle de peso (para aliviar a carga)\n3. Medicamentos (para crises de dor)\n4. Alimentação anti-inflamatória\n\nUm cuida do outro!";
        tratamentoCombinado.options = Arrays.asList(
                new ChatOption("Ver Medicamentos", "tratamento_medicamentos"),
                new ChatOption("Ver Fisioterapia", "tratamento_fisio"),
                new ChatOption("⬅️ Voltar (Tratamento)", "tratamento"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(tratamentoCombinado.id, tratamentoCombinado);


        // --- NÍVEL 2+: ALIMENTAÇÃO (NÓS ADICIONADOS) ---

        ChatNode alimentacaoAmigos = new ChatNode();
        alimentacaoAmigos.id = "alimentacao_amigos";
        alimentacaoAmigos.message = "<b>✅ Alimentos Amigos:</b>\nFoque em alimentos com poder anti-inflamatório:\n\n✓ <b>Ricos em Ômega-3:</b> Salmão, sardinha, linhaça, chia.\n✓ <b>Frutas Vermelhas e Cítricas:</b> Morango, mirtilo, laranja (ricas em Vitamina C).\n✓ <b>Vegetais Verde-Escuros:</b> Espinafre, couve, brócolis.\n✓ <b>Gorduras Boas:</b> Azeite de oliva, abacate, castanhas.\n✓ <b>Temperos:</b> Cúrcuma (açafrão-da-terra) e gengibre.";
        alimentacaoAmigos.options = Arrays.asList(
                new ChatOption("Ver Alimentos a Evitar ❌", "alimentacao_evitar"),
                new ChatOption("Ver Chás recomendados 🍵", "alimentacao_chas"),
                new ChatOption("⬅️ Voltar (Alimentação)", "alimentacao"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(alimentacaoAmigos.id, alimentacaoAmigos);

        ChatNode alimentacaoEvitar = new ChatNode();
        alimentacaoEvitar.id = "alimentacao_evitar";
        alimentacaoEvitar.message = "<b>❌ Alimentos a Evitar:</b>\nAlguns alimentos podem aumentar a inflamação. Tente reduzir:\n\n- <b>Açúcares e Doces:</b> Refrigerantes, bolos, doces em geral.\n- <b>Farinhas Refinadas:</b> Pão branco, massas comuns.\n- <b>Carnes Processadas:</b> Salsicha, presunto, salame.\n- <b>Frituras e Gorduras Trans:</b> Batata frita, salgadinhos de pacote.";
        alimentacaoEvitar.options = Arrays.asList(
                new ChatOption("Ver Alimentos Amigos ✅", "alimentacao_amigos"),
                new ChatOption("Ver Importância da Hidratação 💧", "alimentacao_hidratacao"),
                new ChatOption("⬅️ Voltar (Alimentação)", "alimentacao"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(alimentacaoEvitar.id, alimentacaoEvitar);

        ChatNode alimentacaoHidratacao = new ChatNode();
        alimentacaoHidratacao.id = "alimentacao_hidratacao";
        alimentacaoHidratacao.message = "<b>💧 Importância da Hidratação:</b>\nEssencial! A cartilagem das suas articulações é composta por uma grande porcentagem de água.\n\nBeber água ajuda a:\n✓ Manter as articulações lubrificadas.\n✓ Reduzir o atrito entre os ossos.\n✓ Facilitar o transporte de nutrientes.\n\nNão espere ter sede. Beba água ao longo de todo o dia.";
        alimentacaoHidratacao.options = Arrays.asList(
                new ChatOption("Ver Chás recomendados 🍵", "alimentacao_chas"),
                new ChatOption("Ver Alimentos a Evitar ❌", "alimentacao_evitar"),
                new ChatOption("⬅️ Voltar (Alimentação)", "alimentacao"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(alimentacaoHidratacao.id, alimentacaoHidratacao);

        ChatNode alimentacaoChas = new ChatNode();
        alimentacaoChas.id = "alimentacao_chas";
        alimentacaoChas.message = "<b>🍵 Chás recomendados:</b>\nAlguns chás são conhecidos por suas propriedades anti-inflamatórias naturais:\n\n✓ <b>Chá de Gengibre:</b> Ótimo para aliviar dores.\n✓ <b>Chá de Cúrcuma (Açafrão):</b> Um potente anti-inflamatório. (Dica: adicione uma pitada de pimenta preta para aumentar a absorção).\n✓ <b>Chá Verde:</b> Rico em antioxidantes.";
        alimentacaoChas.options = Arrays.asList(
                new ChatOption("Ver Alimentos Amigos ✅", "alimentacao_amigos"),
                new ChatOption("Ver Importância da Hidratação 💧", "alimentacao_hidratacao"),
                new ChatOption("⬅️ Voltar (Alimentação)", "alimentacao"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(alimentacaoChas.id, alimentacaoChas);


        // --- NÍVEL 2+: QUALIDADE DE VIDA (NÓS ADICIONADOS) ---

        ChatNode qualidadeAdaptacoes = new ChatNode();
        qualidadeAdaptacoes.id = "qualidade_adaptacoes";
        qualidadeAdaptacoes.message = "<b>Adaptações Práticas no dia a dia:</b>\nPequenas mudanças que fazem uma grande diferença para proteger suas articulações:\n\n✓ <b>No Banheiro:</b> Use tapetes antiderrapantes e considere instalar barras de apoio.\n✓ <b>Na Cozinha:</b> Deixe os itens mais usados em locais de fácil alcance (sem precisar agachar ou subir).\n✓ <b>Ao se Vestir:</b> Use sapatos sem cadarço (slip-on) ou calçadeiras.\n✓ <b>Apoio:</b> Não tenha receio de usar uma bengala, se recomendado. Ela alivia a carga na articulação.";
        qualidadeAdaptacoes.options = Arrays.asList(
                new ChatOption("Ver Cuidando da Mente 😊", "qualidade_mente"),
                new ChatOption("Ver Princípios de Qualidade de Vida", "qualidade_principios"),
                new ChatOption("⬅️ Voltar (Qualidade de Vida)", "qualidade_vida"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(qualidadeAdaptacoes.id, qualidadeAdaptacoes);

        ChatNode qualidadeMente = new ChatNode();
        qualidadeMente.id = "qualidade_mente";
        qualidadeMente.message = "<b>Cuidando da Mente / Emoções 😊:</b>\nLidar com dor crônica é cansativo e pode afetar o humor, gerando ansiedade ou estresse. É fundamental cuidar da saúde mental:\n\n✓ <b>Aceite seus limites:</b> Entenda que alguns dias serão melhores que outros.\n✓ <b>Técnicas de Relaxamento:</b> Meditação e respiração profunda ajudam a controlar a percepção da dor.\n✓ <b>Mantenha-se social:</b> Converse com amigos e família.";
        qualidadeMente.options = Arrays.asList(
                new ChatOption("Ver Adaptações Práticas", "qualidade_adaptacoes"),
                new ChatOption("Ver Sinais de Alerta", "sinais_alerta"),
                new ChatOption("⬅️ Voltar (Qualidade de Vida)", "qualidade_vida"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(qualidadeMente.id, qualidadeMente);

        ChatNode sinaisAlerta = new ChatNode();
        sinaisAlerta.id = "sinais_alerta";
        sinaisAlerta.message = "<b>Sinais de Alerta / Quando procurar ajuda:</b>\nEmbora a osteoartrite seja crônica, alguns sinais exigem atenção médica imediata:\n\n❗ Dor muito forte e súbita.\n❗ Inchaço grande, vermelhidão e calor na articulação.\n❗ Febre junto com a dor articular.\n❗ Incapacidade total de mover a articulação ou apoiar o peso.\n\nNestes casos, procure um médico.";
        sinaisAlerta.options = Arrays.asList(
                new ChatOption("Ver Princípios de Qualidade de Vida", "qualidade_principios"),
                new ChatOption("Ver Cuidando da Mente 😊", "qualidade_mente"),
                new ChatOption("⬅️ Voltar (Qualidade de Vida)", "qualidade_vida"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(sinaisAlerta.id, sinaisAlerta);

        ChatNode qualidadePrincipios = new ChatNode();
        qualidadePrincipios.id = "qualidade_principios";
        qualidadePrincipios.message = "<b>Princípios para Qualidade de Vida:</b>\nViver bem com osteoartrite é um equilíbrio. Os princípios são:\n\n1. <b>Mover-se:</b> O movimento é seu aliado. Não fique parado.\n2. <b>Proteger:</b> Evite sobrecargas, cuide da postura e use apoios se precisar.\n3. <b>Nutrir:</b> Coma alimentos anti-inflamatórios e controle o peso.\n4. <b>Descansar:</b> Durma bem, pois o descanso repara o corpo.\n5. <b>Cuidar da Mente:</b> Sua saúde emocional afeta sua dor.";
        qualidadePrincipios.options = Arrays.asList(
                new ChatOption("Ver Adaptações Práticas", "qualidade_adaptacoes"),
                new ChatOption("Ver Cuidando da Mente 😊", "qualidade_mente"),
                new ChatOption("⬅️ Voltar (Qualidade de Vida)", "qualidade_vida"),
                new ChatOption("🏠 Menu Principal", "root")
        );
        chatTree.put(qualidadePrincipios.id, qualidadePrincipios);

        // --- NÓ FINAL ---

        ChatNode fim = new ChatNode();
        fim.id = "fim"; // ID genérico para um ponto final
        fim.message = "Espero ter ajudado! Lembre-se que estas informações são gerais. Sempre converse com seu médico ou fisioterapeuta sobre seu caso específico.";
        fim.options = Arrays.asList(new ChatOption("Voltar ao Menu Principal 🔄", "root"));
        chatTree.put(fim.id, fim);

        Log.d(TAG, "Árvore do chat construída com " + chatTree.size() + " nós.");
    }

    /** Exibe o conteúdo do nó **/
    private void displayNode(String nodeId) {
        ChatNode node = chatTree.get(nodeId);
        if (node == null) {
            Log.e(TAG, "Nó não encontrado: " + nodeId);
            node = chatTree.get("root");
        }
        currentNodeId = node.id;

        // Efeito de digitação simulada
        addTypingEffect(node.message, false);

        final ChatNode nodeFinal = node;

        // Exibir opções depois de um pequeno delay (para parecer natural)
        new Handler().postDelayed(() -> displayOptions(nodeFinal.options), 600);
    }

    /** Exibe opções de resposta **/
    // Exibe as opções clicáveis para o usuário
    private void displayOptions(List<ChatOption> options) {
        optionsContainer.removeAllViews(); // Limpa opções anteriores

        if (options == null || options.isEmpty()) {
            Log.w(TAG, "Nó " + currentNodeId + " não tem opções.");
            // Poderia adicionar uma opção padrão de "Voltar" aqui
            return;
        }

        // MUDANÇA 1: Loop 'for' tradicional para pegar o índice (i)
        for (int i = 0; i < options.size(); i++) {
            ChatOption option = options.get(i); // Pega a opção atual
            int optionNumber = i + 1;          // Cria o número (começando em 1)

            // Cria um TextView para cada opção
            TextView optionView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // MUDANÇA 2: Usar MATCH_PARENT para alinhar
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8); // Margens
            optionView.setLayoutParams(params);

            // MUDANÇA 3: Formata o texto para incluir o número
            String displayText = optionNumber + ". " + option.text;
            optionView.setText(displayText);

            optionView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Tamanho do texto
            optionView.setTextColor(Color.WHITE); // Cor do texto
            optionView.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bubble_background)); // Fundo azul arredondado
            optionView.setPadding(24, 16, 24, 16); // Padding interno

            // MUDANÇA 4 (Estilo): Alinha o texto à esquerda, fica melhor para listas
            optionView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

            optionView.setClickable(true);
            optionView.setFocusable(true);

            // Adiciona o listener de clique (sem alteração aqui)
            optionView.setOnClickListener(v -> handleOptionClick(option));

            optionsContainer.addView(optionView);
        }
    }

    /** Quando o usuário clica em uma opção **/
    private void handleOptionClick(ChatOption option) {
        addMessageToHistory(option.text, true);
        new Handler().postDelayed(() -> displayNode(option.nextNodeId), 400);
    }

    /** Adiciona a mensagem ao histórico **/
    private void addMessageToHistory(String text, boolean isUser) {
        conversationHistory.add(new ChatMessage(text, isUser));
        chatAdapter.notifyItemInserted(conversationHistory.size() - 1);
        chatRecyclerView.smoothScrollToPosition(conversationHistory.size() - 1);
    }

    /** Simula digitação do bot **/
    private void addTypingEffect(String text, boolean isUser) {
        new Handler().postDelayed(() -> addMessageToHistory(text, isUser), 400);
    }
}
