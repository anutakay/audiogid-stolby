package ru.audiogid.krsk.stolby.audio;

import ru.audiogid.krsk.stolby.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class AudioActivity extends Activity {

    private Player mPlayer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = null;
        String audio = null;
        Intent intent = this.getIntent();
        if (intent != null) {
            title = intent.getExtras().getString("point_title");
            audio = intent.getExtras().getString("point_audio");
            this.setTitle(title + " " + audio);
        }

        setContentView(R.layout.activity_audio);
        mPlayer = new Player(this, (RelativeLayout) findViewById(R.id.mainView));
        play(audio);
    }

    private void play(final String audio) {
        //mPlayer.setAudio(audio, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.destroy();
    }

}
