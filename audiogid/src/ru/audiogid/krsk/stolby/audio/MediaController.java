package ru.audiogid.krsk.stolby.audio;

import ru.audiogid.krsk.stolby.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;

public class MediaController extends FrameLayout implements IMediaController {

    private MediaPlayerControl mPlayer;

    private View mRoot;

    private RelativeLayout mAnchor;

    private Context mContext;

    private ImageButton mPauseButton;

    private ImageButton mReplayButton;

    private boolean showed;

    public MediaController(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mRoot = null;
        mContext = context;
    }

    public MediaController(Context context) {
        super(context);
        this.mContext = context;
    }

    @SuppressLint("InflateParams")
    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.media_controller, null);
        initControllerView(mRoot);
        return mRoot;
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initControllerView(final View v) {
        mPauseButton = (ImageButton) v.findViewById(R.id.pause);
        mReplayButton = (ImageButton) v.findViewById(R.id.replay);
        if (mPauseButton != null) {
            mPauseButton.setOnClickListener(mPauseListener);
        }
        if (mReplayButton != null) {
            mReplayButton.setOnClickListener(mReplayListener);
        }
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPausePlay();
        }
    };

    private View.OnClickListener mReplayListener = new View.OnClickListener() {
        public void onClick(View v) {
            mPlayer.seekTo(0);
            mPlayer.start();
            mPauseButton.setEnabled(true);
            updatePausePlay();
        }
    };

    @Override
    public void setAnchorView(final RelativeLayout anchorView) {
        mAnchor = anchorView;
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }

    @Override
    public void setMediaPlayer(final MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    @Override
    public void showOverlay() {
        if (showed == false && mAnchor != null) {
            RelativeLayout.LayoutParams tlp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            tlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            mAnchor.addView(this, tlp);
            showed = true;
        }
        updatePausePlay();
    }

    @Override
    public void hideOverlay() {
        if (mAnchor == null) {
            return;
        }
        try {
            mAnchor.removeView(this);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        showed = false;
    }

    @Override
    public void doPausePlay() {
        if (mPlayer == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        unfreezePausePlay();
        updatePausePlay();
    }

    @Override
    public void updatePausePlay() {
        if (mRoot == null || mPauseButton == null || mPlayer == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_media_pause);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_media_play);
        }
    }

    @Override
    public void freezePausePlay() {
        mPauseButton.setEnabled(false);
    }

    @Override
    public void unfreezePausePlay() {
        mPauseButton.setEnabled(true);
    }

}
