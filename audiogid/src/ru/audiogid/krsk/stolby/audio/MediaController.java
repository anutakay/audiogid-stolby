package ru.audiogid.krsk.stolby.audio;

import java.util.Formatter;
import java.util.Locale;

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

public class MediaController extends FrameLayout {
	
	private MediaPlayerControl  mPlayer;
	
	private View                mRoot;
	private RelativeLayout           mAnchor;
	private Context             mContext;
	private ImageButton         mPauseButton;
	private ImageButton         mReplayButton;
	// private ProgressBar         mProgress;
	 
	 StringBuilder               mFormatBuilder;
	 Formatter                   mFormatter;
	 
	// private boolean             mDragging;
	 private boolean	showed; 
	
	 public MediaController(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        mRoot = null;
	        mContext = context;
	    }

	    public MediaController(Context context) {
	    	super(context);
	        this.mContext = context;
	    }
	    
	    public void setMediaPlayer(MediaPlayerControl player) {
	        mPlayer = player;
	        updatePausePlay();
	    }
	    
	    public void setAnchorView(RelativeLayout anchorView) {
	        mAnchor = anchorView;

	        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
	                ViewGroup.LayoutParams.MATCH_PARENT,
	                ViewGroup.LayoutParams.MATCH_PARENT
	        );

	        removeAllViews();
	        View v = makeControllerView();
	        addView(v, frameParams);
	    }
	    
	    public void show() {
	    	if(showed == false && mAnchor != null) {
	    		RelativeLayout.LayoutParams tlp = new RelativeLayout.LayoutParams(
	                ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT
	            );
	    		tlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
	    		tlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
	    		mAnchor.addView(this, tlp);
	    		showed = true;
	    	}
	    	updatePausePlay();
	    }
	    
	    public void hide() {
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
	    
	    @SuppressLint("InflateParams")
		protected View makeControllerView() {
	        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        mRoot = inflate.inflate(R.layout.media_controller, null);

	        initControllerView(mRoot);

	        return mRoot;
	    }
	    
	    @Override
	    public void onFinishInflate() {
	        if (mRoot != null)
	            initControllerView(mRoot);
	    }
	    
	    private void initControllerView(View v) {
	        mPauseButton = (ImageButton) v.findViewById(R.id.pause);
	        if (mPauseButton != null) {
	            //mPauseButton.requestFocus();
	            mPauseButton.setOnClickListener(mPauseListener);
	        }
	        
	        mReplayButton = (ImageButton) v.findViewById(R.id.replay);
	        if (mReplayButton != null) {
	        	//mReplayButton.requestFocus();
	            mReplayButton.setOnClickListener(mReplayListener);
	        }
/*
	        mProgress = (ProgressBar) v.findViewById(R.id.mediacontroller_progress);
	        if (mProgress != null) {
	            if (mProgress instanceof SeekBar) {
	                SeekBar seeker = (SeekBar) mProgress;
	                seeker.setOnSeekBarChangeListener(mSeekListener);
	            }
	            mProgress.setMax(1000);
	        }*/
	        
	        mFormatBuilder = new StringBuilder();
	        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

	    }
	    
	    private View.OnClickListener mPauseListener = new View.OnClickListener() {
	        public void onClick(View v) {
	            doPauseResume();
	        }
	    };
	    
	    private View.OnClickListener mReplayListener = new View.OnClickListener() {
	        public void onClick(View v) {
	        	mPlayer.seekTo(0);
	        	mPlayer.start();
	        	updatePausePlay();
	        }
	    };
	    
	    /*private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
	        public void onStartTrackingTouch(SeekBar bar) {
	            mDragging = true;
	        }

	        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
	            if (mPlayer == null) {
	                return;
	            }
	            
	            if (!fromuser) {
	                return;
	            }
	            long duration = mPlayer.getDuration();
	            long newposition = (duration * progress) / 1000L;
	            mPlayer.seekTo( (int) newposition);
	        }

	        public void onStopTrackingTouch(SeekBar bar) {
	            mDragging = false;
	            setProgress();
	            updatePausePlay();
	        }
	    };*/
	    
	    private void doPauseResume() {
	        if (mPlayer == null) {
	            return;
	        }
	        
	        if (mPlayer.isPlaying()) {
	            mPlayer.pause();
	        } else {
	            mPlayer.start();
	        }
	        updatePausePlay();
	    }
	    
	    /*private int setProgress() {
	        if (mPlayer == null || mDragging) {
	            return 0;
	        } 
	        int position = mPlayer.getCurrentPosition();
	        int duration = mPlayer.getDuration();
	        if (mProgress != null) {
	            if (duration > 0) {
	                // use long to avoid overflow
	                long pos = 1000L * position / duration;
	                mProgress.setProgress( (int) pos);
	            }
	            int percent = mPlayer.getBufferPercentage();
	            mProgress.setSecondaryProgress(percent * 10);
	        }
	        return position;
	    }*/

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
	    
}
