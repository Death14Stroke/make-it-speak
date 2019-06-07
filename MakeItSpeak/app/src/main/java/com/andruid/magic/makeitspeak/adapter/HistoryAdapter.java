package com.andruid.magic.makeitspeak.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.andruid.magic.makeitspeak.database.AudioText;
import com.andruid.magic.makeitspeak.databinding.LayoutAudioTextBinding;
import com.andruid.magic.makeitspeak.viewholder.HistoryViewHolder;

public class HistoryAdapter extends PagedListAdapter<AudioText, HistoryViewHolder> {
    private HistoryViewHolder.ItemClickListener mListener;
    private static DiffUtil.ItemCallback<AudioText> DIFF_CALLBACK = new DiffUtil.ItemCallback<AudioText>() {
        @Override
        public boolean areItemsTheSame(@NonNull AudioText oldItem, @NonNull AudioText newItem) {
            return oldItem.getCreated() == newItem.getCreated();
        }

        @Override
        public boolean areContentsTheSame(@NonNull AudioText oldItem, @NonNull AudioText newItem) {
            return oldItem.equals(newItem);
        }
    };

    public HistoryAdapter(HistoryViewHolder.ItemClickListener mListener){
        super(DIFF_CALLBACK);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutAudioTextBinding binding = LayoutAudioTextBinding.inflate(inflater, parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        AudioText audioText = getItem(position);
        holder.bind(audioText, mListener);
    }
}
