package com.andruid.magic.makeitspeak.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.andruid.magic.makeitspeak.database.AudioText;
import com.andruid.magic.makeitspeak.databinding.LayoutAudioTextBinding;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    private LayoutAudioTextBinding binding;

    public HistoryViewHolder(LayoutAudioTextBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(AudioText audioText, ItemClickListener mListener){
        binding.setAudioText(audioText);
        binding.getRoot().setOnClickListener(v -> mListener.onItemClicked(audioText));
        binding.executePendingBindings();
    }

    public interface ItemClickListener {
        void onItemClicked(AudioText audioText);
    }
}