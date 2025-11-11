package com.example.app_osteoartrite.com.app_osteoartrite;

public class Atividade {
    private String id; // Para identificar unicamente no futuro (ex: do Firestore)
    private String nome;
    private String descricao;
    private String tempoRepeticoes;
    private String localAfetado;
    private String diaSemana; //  "Segunda-feira", "Terca-feira"
    private String status; // "Pendente", "Realizada", "Atrasada"
    private String dataRealizacao;

    // Construtor vazio (necessário para Firestore)
    public Atividade() {}

    // Construtor principal
    public Atividade(String id, String nome, String descricao, String tempoRepeticoes, String localAfetado, String diaSemana, String status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tempoRepeticoes = tempoRepeticoes;
        this.localAfetado = localAfetado;
        this.diaSemana = diaSemana;
        this.status = status;
        this.dataRealizacao = null;

    }

    // Getters e Setters para todos os campos

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTempoRepeticoes() { return tempoRepeticoes; }
    public void setTempoRepeticoes(String tempoRepeticoes) { this.tempoRepeticoes = tempoRepeticoes; }
    public String getLocalAfetado() { return localAfetado; }
    public void setLocalAfetado(String localAfetado) { this.localAfetado = localAfetado; }
    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(String dataRealizacao) { this.dataRealizacao = dataRealizacao; }
}