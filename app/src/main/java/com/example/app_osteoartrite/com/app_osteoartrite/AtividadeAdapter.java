package com.example.app_osteoartrite.com.app_osteoartrite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList; // Adicionado para segurança no updateData
import java.util.List;

public class AtividadeAdapter extends RecyclerView.Adapter<AtividadeAdapter.AtividadeViewHolder> {

    // Interface para comunicar cliques de status para a Activity
    public interface OnStatusChangeListener {
        void onStatusChanged(Atividade atividade, String novoStatus);
    }

    private List<Atividade> listaAtividades;
    private Context context;
    private OnStatusChangeListener statusChangeListener; // Listener para a Activity

    // Construtor que recebe a lista, o contexto e o listener
    public AtividadeAdapter(Context context, List<Atividade> listaAtividades, OnStatusChangeListener listener) {
        this.context = context;
        this.listaAtividades = (listaAtividades != null) ? listaAtividades : new ArrayList<>(); // Garante que a lista não seja nula
        this.statusChangeListener = listener;
    }

    // Cria a view para cada item (infla o XML list_item_atividade.xml)
    @NonNull
    @Override
    public AtividadeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_atividade, parent, false);
        return new AtividadeViewHolder(view);
    }

    // Vincula os dados de uma Atividade específica à view do ViewHolder
    @Override
    public void onBindViewHolder(@NonNull AtividadeViewHolder holder, int position) {
        Atividade atividade = listaAtividades.get(position);

        // Define os textos nos TextViews
        holder.tvTitulo.setText(atividade.getNome());
        holder.tvDescricao.setText(atividade.getDescricao());
        holder.tvTempoRepeticoes.setText(atividade.getTempoRepeticoes());
        holder.tvLocalAfetado.setText(atividade.getLocalAfetado());
        // A data mostrada aqui pode ser a data planejada da atividade,
        // Por enquanto, mostra a data fixa do layout ou pode ser removida.

        // Configura a aparência inicial do botão de status
        atualizarStatusBotao(holder.btnStatus, atividade.getStatus());

        // Configura o clique no botão de status
        holder.btnStatus.setOnClickListener(v -> {
            String currentStatus = atividade.getStatus();
            String newStatus;

            // Define qual será o próximo status ao clicar
            if ("Pendente".equals(currentStatus) || "Atrasada".equals(currentStatus)) {
                newStatus = "Realizada";
            } else { // Se for "Realizada"
                newStatus = "Pendente"; // Volta para pendente ao clicar novamente
            }

            // Avisa a Activity que o status foi clicado, passando a atividade e o novo status desejado
            if (statusChangeListener != null) {
                statusChangeListener.onStatusChanged(atividade, newStatus);
            }
        });
    }

    // Retorna o número total de itens na lista
    @Override
    public int getItemCount() {
        return listaAtividades.size();
    }

    // Classe interna ViewHolder que segura as referências aos elementos do layout do item
    public static class AtividadeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescricao, tvTempoRepeticoes, tvLocalAfetado, tvData;
        AppCompatButton btnStatus;

        public AtividadeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.text_atividade_titulo);
            tvDescricao = itemView.findViewById(R.id.text_atividade_descricao);
            tvTempoRepeticoes = itemView.findViewById(R.id.text_tempo_repeticoes);
            tvLocalAfetado = itemView.findViewById(R.id.text_local_afetado);
            btnStatus = itemView.findViewById(R.id.button_status);
        }
    }

    // Método helper para atualizar a cor e o texto do botão de status
    private void atualizarStatusBotao(AppCompatButton button, String status) {
        if (status == null) status = "Pendente";
        button.setText(status);
        switch (status) {
            case "Pendente":
                button.setBackgroundResource(R.drawable.button_status_pendente);
                button.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                break;
            case "Realizada":
                button.setBackgroundResource(R.drawable.button_status_realizada);
                button.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                break;
            case "Atrasada":
                button.setBackgroundResource(R.drawable.button_status_atrasada);
                button.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                break;
            default:
                button.setBackgroundResource(R.drawable.button_status_pendente);
                button.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                break;
        }
    }

    // Método para a Activity poder atualizar a lista de dados que o Adapter exibe
    public void updateData(List<Atividade> novaLista) {
        if (novaLista == null) {
            this.listaAtividades = new ArrayList<>();
        } else {
            // Cria uma nova lista para evitar problemas de referência
            this.listaAtividades = new ArrayList<>(novaLista);
        }
        notifyDataSetChanged(); // Informa ao RecyclerView para redesenhar a lista
    }
}