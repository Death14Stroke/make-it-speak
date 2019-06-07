package com.andruid.magic.makeitspeak.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.andruid.magic.makeitspeak.R;
import com.andruid.magic.makeitspeak.service.TtsService;
import com.andruid.magic.makeitspeak.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import timber.log.Timber;

import static com.andruid.magic.makeitspeak.data.Constants.INTENT_TTS;
import static com.andruid.magic.makeitspeak.data.Constants.KEY_INPUT;
import static com.andruid.magic.makeitspeak.data.Constants.KEY_UTTERANCE_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ttslog";
    private ActivityMainBinding binding;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                String utteranceId = extras.getString(KEY_UTTERANCE_ID);
                Timber.tag(TAG).d("done %s", utteranceId);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        startService(new Intent(this, TtsService.class));
        binding.audioBtn.setOnClickListener(v -> convertToAudio());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(INTENT_TTS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, TtsService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_history)
            startActivity(new Intent(this, HistoryActivity.class));
        return true;
    }

    private void convertToAudio() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        String input = binding.messageET.getText().toString().trim();
                        if(input.isEmpty()){
                           Toast.makeText(MainActivity.this, "Text cannot be empty",
                                   Toast.LENGTH_SHORT).show();
                           return;
                        }
                        Intent intent = new Intent(MainActivity.this, TtsService.class);
                        intent.setAction(INTENT_TTS);
                        intent.putExtra(KEY_INPUT, input);
                        startService(intent);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, response.getPermissionName()+" denied",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}