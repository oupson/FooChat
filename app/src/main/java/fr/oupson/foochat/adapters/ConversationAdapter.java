package fr.oupson.foochat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.oupson.foochat.database.Conversation;
import fr.oupson.foochat.databinding.DeviceItemBinding;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Conversation conversation);
    }

    public final List<Conversation> conversationsList;
    private final ConversationAdapter.OnItemClickListener clickListener;

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private final DeviceItemBinding binding;

        public ConversationViewHolder(@NonNull DeviceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Conversation conversation) {
            this.binding.deviceItemNameTextView.setText(conversation.titreConv);
            this.binding.deviceItemMacTextView.setText(conversation.adresseMac);
            this.binding.getRoot().setOnClickListener((v) -> ConversationAdapter.this.clickListener.onItemClick(conversation));
        }
    }

    public ConversationAdapter(List<Conversation> conversationsList, ConversationAdapter.OnItemClickListener clickListener) {
        super();
        this.conversationsList = conversationsList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ConversationAdapter.ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationAdapter.ConversationViewHolder(
                DeviceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ConversationViewHolder holder, int position) {
        holder.bind(this.conversationsList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.conversationsList.size();
    }
}
