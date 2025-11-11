package com.example.app_osteoartrite.com.app_osteoartrite;

import android.content.Context;
import android.text.Html; // Para formatar HTML básico
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_BOT = 1;
    private static final int VIEW_TYPE_USER = 2;

    private List<ChatMessage> messageList;
    private Context context;

    public ChatAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).isUserMessage()) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_BOT) {
            View view = inflater.inflate(R.layout.item_chat_bot, parent, false);
            return new BotViewHolder(view);
        } else { // VIEW_TYPE_USER
            View view = inflater.inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_BOT) {
            ((BotViewHolder) holder).bind(message);
        } else {
            ((UserViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder para mensagens do Bot
    static class BotViewHolder extends RecyclerView.ViewHolder {
        ImageView botAvatar;
        TextView botMessageText;

        BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botAvatar = itemView.findViewById(R.id.bot_avatar);
            botMessageText = itemView.findViewById(R.id.bot_message_text);
        }

        void bind(ChatMessage message) {
            // Usa Html.fromHtml para interpretar tags como <b> e \n
            botMessageText.setText(Html.fromHtml(message.getMessageText(), Html.FROM_HTML_MODE_LEGACY));
            // Definir imagem do avatar se necessário
        }
    }

    // ViewHolder para mensagens do Usuário
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userMessageText;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageText = itemView.findViewById(R.id.user_message_text);
        }

        void bind(ChatMessage message) {
            userMessageText.setText(message.getMessageText());
        }
    }
}