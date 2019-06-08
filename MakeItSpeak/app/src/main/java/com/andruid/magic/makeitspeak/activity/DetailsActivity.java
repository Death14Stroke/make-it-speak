package com.andruid.magic.makeitspeak.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.andruid.magic.makeitspeak.R;
import com.andruid.magic.makeitspeak.database.AudioText;
import com.andruid.magic.makeitspeak.databinding.ActivityDetailsBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;

import timber.log.Timber;

import static com.andruid.magic.makeitspeak.data.Constants.KEY_AUDIO_TEXT;

public class DetailsActivity extends AppCompatActivity implements AudioListener {
    private static final String TAG = "exolog";
    private ActivityDetailsBinding binding;
    private SimpleExoPlayer exoPlayer;
    private DataSource.Factory dataSourceFactory;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            AudioText audioText = extras.getParcelable(KEY_AUDIO_TEXT);
            binding.setAudioText(audioText);
        }
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(binding.getAudioText() != null)
                actionBar.setTitle(binding.getAudioText().getName());
        }
        initExoPlayer();
        setMediaSource();
        binding.playerControlView.setPlayer(exoPlayer);
        binding.messageTV.setOnLongClickListener(v -> {
            shareText();
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_share_text:
                shareText();
                break;
            case R.id.menu_share_audio:
                shareAudio();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.visualizer.release();
    }

    private void shareAudio() {
        Intent shareAudioIntent = new Intent(Intent.ACTION_SEND);
        shareAudioIntent.setType("audio/*");
        shareAudioIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareAudioIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                this, getApplicationContext().getPackageName(), file));
        startActivity(Intent.createChooser(shareAudioIntent, "Share audio via..."));
    }

    private void shareText() {
        Intent shareTextIntent = new Intent(Intent.ACTION_SEND);
        shareTextIntent.setType("text/plain");
        shareTextIntent.putExtra(Intent.EXTRA_TEXT, binding.getAudioText().getMessage());
        startActivity(Intent.createChooser(shareTextIntent, "Share text via..."));
    }

    private void setMediaSource() {
        File dir = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
        if(!dir.exists()) {
            Toast.makeText(this, "Directory not found", Toast.LENGTH_SHORT).show();
            return;
        }
        file = new File(dir, binding.getAudioText().getName()+".mp3");
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(file));
        exoPlayer.prepare(mediaSource);
        exoPlayer.addAudioListener(this);
        Timber.tag(TAG).d("prepared exoplayer");
    }

    private void initExoPlayer() {
        final TrackSelector trackSelector = new DefaultTrackSelector();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector);
        dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                Util.getUserAgent(this, getString(R.string.app_name)));
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build();
        exoPlayer.setAudioAttributes(audioAttributes,true);
    }

    @Override
    public void onAudioSessionId(int audioSessionId) {
        Timber.tag(TAG).d("audio session id: %d", audioSessionId);
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        binding.visualizer.setAudioSessionId(audioSessionId);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(),
                                response.getPermissionName()+" denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}