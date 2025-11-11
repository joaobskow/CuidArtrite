package com.example.app_osteoartrite.com.app_osteoartrite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// MUDANÇA 1: Certifique-se que está usando a classe "ClassificacaoDor" (o modelo)
public class ClassificacaoDorAdapter extends RecyclerView.Adapter<ClassificacaoDorAdapter.ViewHolder> {

    // MUDANÇA 2: A lista deve ser do tipo "ClassificacaoDor"
    private List<ClassificacaoDor> items;

    public ClassificacaoDorAdapter(List<ClassificacaoDor> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classificacao_dor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // MUDANÇA 3: O item é do tipo "ClassificacaoDor"
        ClassificacaoDor item = items.get(position);

        // Define os dados
        holder.tvTitle.setText(item.getTitle());
        holder.tvDetailDescription.setText(item.getDescription()); // <-- Esta linha agora vai funcionar

        // Verifica se o card está expandido
        boolean isExpanded = item.isExpanded();
        holder.layoutExpandable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivArrow.setRotation(isExpanded ? 180f : 0f); // Gira a seta

        // Ação de clique para expandir/recolher
        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position); // Atualiza apenas este item
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // O ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetailDescription;
        ImageView ivArrow;
        LinearLayout layoutExpandable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDetailDescription = itemView.findViewById(R.id.tvDetailDescription);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            layoutExpandable = itemView.findViewById(R.id.layoutExpandable);
        }
    }
}