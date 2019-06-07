package com.andruid.magic.makeitspeak.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.andruid.magic.makeitspeak.R;
import com.andruid.magic.makeitspeak.database.AudioText;
import com.andruid.magic.makeitspeak.database.HistoryDatabase;

import java.io.File;
import java.util.Locale;

import timber.log.Timber;

import static com.andruid.magic.makeitspeak.data.Constants.INTENT_TTS;
import static com.andruid.magic.makeitspeak.data.Constants.KEY_INPUT;
import static com.andruid.magic.makeitspeak.data.Constants.KEY_UTTERANCE_ID;

public class TtsService extends Service implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean ttsInit = false;
    private File dir;
    private static final String TAG = "ttslog";
    private HistoryDatabase database;
    private String message;
    private long created;

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this, this);
        database = HistoryDatabase.getInstance(getApplicationContext());
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {}

            @Override
            public void onDone(String utteranceId) {
                AudioText audioText = new AudioText(utteranceId, message, created);
                database.historyDao().insertRecord(audioText);
                Intent intent = new Intent(INTENT_TTS);
                intent.putExtra(KEY_UTTERANCE_ID, utteranceId);
                LocalBroadcastManager.getInstance(TtsService.this).sendBroadcast(intent);
            }

            @Override
            public void onError(String utteranceId) {}
        });
        dir = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(INTENT_TTS.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                String input = extras.getString(KEY_INPUT);
                if(ttsInit)
                    convertToAudio(input);
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.getDefault());
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                Toast.makeText(this, "Text to Speech not available", Toast.LENGTH_SHORT).show();
            else {
                ttsInit = true;
                Timber.tag(TAG).d("tts init");
            }
        }
    }

    private void convertToAudio(String input){
        if(!dir.exists()){
            boolean created = dir.mkdir();
            Timber.d("created dir %s", created);
        }
        created = System.currentTimeMillis();
        message = input;
        String utteranceId = "tts_"+created;
        File file = new File(dir, utteranceId+".mp3");
        tts.synthesizeToFile(input, null, file, utteranceId);
    }
}