package fr.foo.foochat.adapters;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.foo.foochat.database.Message;
import fr.foo.foochat.databinding.MessageItemBinding;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Message message);
    }

    public List<Message> messageList;
    private final MessageAdapter.OnItemClickListener clickListener;

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final MessageItemBinding binding;

        public MessageViewHolder(@NonNull MessageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message) {
            this.binding.messageContentTextView.setText(message.texte);

            if (message.mine) {
                this.binding.getRoot().setGravity(Gravity.END);
            }else {
                this.binding.getRoot().setGravity(Gravity.START);
            }
            this.binding.getRoot().setOnClickListener((v) ->  Log.d("tab", Boolean.toString(message.mine)) /*MessageAdapter.this.clickListener.onItemClick(message)*/);
        }
    }

    public MessageAdapter(List<Message> messageList, MessageAdapter.OnItemClickListener clickListener) {
        super();
        this.messageList = messageList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageAdapter.MessageViewHolder(
                MessageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        holder.bind(this.messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.messageList.size();
    }
}
