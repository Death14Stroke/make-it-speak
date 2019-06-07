package com.andruid.magic.makeitspeak.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class HistoryViewModel extends AndroidViewModel {
    private static final int PAGE_SIZE = 10;
    private LiveData<PagedList<AudioText>> pagedListLiveData;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        HistoryDao dao = HistoryDatabase.getInstance(application.getApplicationContext()).historyDao();
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();
        pagedListLiveData = new LivePagedListBuilder<>(dao.getAllPaged(), config).build();
    }

    public LiveData<PagedList<AudioText>> getPagedListLiveData() {
        return pagedListLiveData;
    }
}
