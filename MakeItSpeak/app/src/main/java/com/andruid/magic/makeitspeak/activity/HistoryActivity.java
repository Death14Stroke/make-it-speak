package com.andruid.magic.makeitspeak.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.andruid.magic.makeitspeak.R;
import com.andruid.magic.makeitspeak.adapter.HistoryAdapter;
import com.andruid.magic.makeitspeak.database.AudioText;
import com.andruid.magic.makeitspeak.database.HistoryViewModel;
import com.andruid.magic.makeitspeak.databinding.ActivityHistoryBinding;
import com.andruid.magic.makeitspeak.viewholder.HistoryViewHolder;

import static com.andruid.magic.makeitspeak.data.Constants.KEY_AUDIO_TEXT;

public class HistoryActivity extends AppCompatActivity implements HistoryViewHolder.ItemClickListener {
    private ActivityHistoryBinding binding;
    private HistoryAdapter historyAdapter;
    private HistoryViewModel historyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history);
        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        historyAdapter = new HistoryAdapter(this);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(historyAdapter);
        binding.swipeRefresh.setRefreshing(true);
        binding.swipeRefresh.setOnRefreshListener(this::loadHistory);
        loadHistory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    private void loadHistory() {
        historyViewModel.getPagedListLiveData().observe(this, audioTexts -> {
            historyAdapter.submitList(audioTexts);
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    @Override
    public void onItemClicked(AudioText audioText) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(KEY_AUDIO_TEXT, audioText);
        startActivity(intent);
    }
}