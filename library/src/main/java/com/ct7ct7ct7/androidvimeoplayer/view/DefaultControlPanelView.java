package com.ct7ct7ct7.androidvimeoplayer.view;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ct7ct7ct7.androidvimeoplayer.R;
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerReadyListener;
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerStateListener;
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerTimeListener;
import com.ct7ct7ct7.androidvimeoplayer.utils.Utils;

public class DefaultControlPanelView {
    private View vimeoPanelView;
    private View vimeoShadeView;
    private ImageView vimeoSettingsButton;
    private ImageView vimeoFullscreenButton;
    private SeekBar vimeoSeekBar;
    private TextView vimeoCurrentTimeTextView;
    private ImageView vimeoPlayButton;
    private ImageView vimeoPauseButton;
    private ImageView vimeoReplayButton;
    private TextView vimeoTitleTextView;
    private View controlsRootView;
    private boolean ended = false;

    public DefaultControlPanelView(final VimeoPlayerView vimeoPlayerView) {
        View defaultControlPanelView = View.inflate(vimeoPlayerView.getContext(), R.layout.view_default_control_panel, vimeoPlayerView);
        vimeoPanelView = defaultControlPanelView.findViewById(R.id.vimeoPanelView);
        vimeoShadeView = defaultControlPanelView.findViewById(R.id.vimeoShadeView);
        vimeoSettingsButton = defaultControlPanelView.findViewById(R.id.vimeoSettingsButton);
        vimeoFullscreenButton = defaultControlPanelView.findViewById(R.id.vimeoFullscreenButton);
        vimeoSeekBar = defaultControlPanelView.findViewById(R.id.vimeoSeekBar);
        vimeoCurrentTimeTextView = defaultControlPanelView.findViewById(R.id.vimeoCurrentTimeTextView);
        vimeoPlayButton = defaultControlPanelView.findViewById(R.id.vimeoPlayButton);
        vimeoPauseButton = defaultControlPanelView.findViewById(R.id.vimeoPauseButton);
        vimeoReplayButton = defaultControlPanelView.findViewById(R.id.vimeoReplayButton);
        vimeoTitleTextView = defaultControlPanelView.findViewById(R.id.vimeoTitleTextView);
        controlsRootView = defaultControlPanelView.findViewById(R.id.controlsRootView);

        vimeoSeekBar.setVisibility(View.INVISIBLE);
        vimeoPanelView.setVisibility(View.VISIBLE);
        vimeoShadeView.setVisibility(View.VISIBLE);
        controlsRootView.setVisibility(View.GONE);

        vimeoPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vimeoPlayerView.pause();
                dismissControls(4000);
            }
        });
        vimeoPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vimeoPlayerView.play();
            }
        });
        vimeoReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vimeoPlayerView.seekTo(0);
                vimeoPlayerView.play();
            }
        });


        vimeoPlayerView.addTimeListener(new VimeoPlayerTimeListener() {
            @Override
            public void onCurrentSecond(float second) {
                vimeoCurrentTimeTextView.setText(Utils.formatTime(second));
                vimeoSeekBar.setProgress((int) second);
            }
        });

        vimeoPlayerView.addReadyListener(new VimeoPlayerReadyListener() {
            @Override
            public void onReady(String title, float duration) {
                vimeoSeekBar.setMax((int) duration);
                vimeoTitleTextView.setText(title);
                vimeoPanelView.setVisibility(View.VISIBLE);
                controlsRootView.setVisibility(View.VISIBLE);
                vimeoShadeView.setVisibility(View.GONE);
            }

            @Override
            public void onInitFailed() {

            }
        });

        vimeoPlayerView.addStateListener(new VimeoPlayerStateListener() {
            @Override
            public void onPlaying(float duration) {
                ended = false;
                vimeoSeekBar.setVisibility(View.VISIBLE);
                vimeoPanelView.setBackgroundColor(Color.TRANSPARENT);
                vimeoPauseButton.setVisibility(View.VISIBLE);
                vimeoPlayButton.setVisibility(View.GONE);
                vimeoReplayButton.setVisibility(View.GONE);
                dismissControls(4000);
            }

            @Override
            public void onPaused(float seconds) {
                if (ended) {
                    vimeoPanelView.setBackgroundColor(Color.BLACK);
                    vimeoReplayButton.setVisibility(View.VISIBLE);
                    vimeoPauseButton.setVisibility(View.GONE);
                    vimeoPlayButton.setVisibility(View.GONE);
                } else {
                    vimeoReplayButton.setVisibility(View.GONE);
                    vimeoPauseButton.setVisibility(View.GONE);
                    vimeoPlayButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEnded(float duration) {
                ended = true;
                showControls(false);
            }
        });

        vimeoTitleTextView.setVisibility(vimeoPlayerView.defaultOptions.title ? View.VISIBLE : View.INVISIBLE);

        if (vimeoPlayerView.defaultOptions.color != vimeoPlayerView.defaultColor) {
            vimeoSeekBar.getThumb().setColorFilter(vimeoPlayerView.defaultOptions.color, PorterDuff.Mode.SRC_ATOP);
            vimeoSeekBar.getProgressDrawable().setColorFilter(vimeoPlayerView.defaultOptions.color, PorterDuff.Mode.SRC_ATOP);
        }

        vimeoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vimeoCurrentTimeTextView.setText(Utils.formatTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vimeoPlayerView.pause();
                showControls(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vimeoPlayerView.seekTo(seekBar.getProgress());
                vimeoPlayerView.play();
                dismissControls(4000);
            }
        });

        vimeoPanelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showControls(true);
            }
        });

        vimeoSettingsButton.setVisibility(vimeoPlayerView.defaultOptions.settingsOption ? View.VISIBLE : View.GONE);
        vimeoFullscreenButton.setVisibility(vimeoPlayerView.defaultOptions.fullscreenOption ? View.VISIBLE : View.GONE);
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable dismissRunnable = new Runnable() {
        @Override
        public void run() {
            controlsRootView.setVisibility(View.GONE);
        }
    };

    public void dismissControls(final int duration) {
        handler.removeCallbacks(dismissRunnable);

        handler.postDelayed(dismissRunnable, duration);
    }

    public void showControls(final boolean autoMask) {
        handler.removeCallbacks(dismissRunnable);

        controlsRootView.setVisibility(View.VISIBLE);
        if (autoMask) {
            dismissControls(3000);
        }
    }

    public void setFullscreenVisibility(int value) {
        vimeoFullscreenButton.setVisibility(value);
    }

    public void setFullscreenClickListener(final View.OnClickListener onClickListener) {
        vimeoFullscreenButton.setOnClickListener(onClickListener);
    }

    public void setSettingsVisibility(int value) {
        vimeoSettingsButton.setVisibility(value);
    }

    public void setSettingsClickListener(final View.OnClickListener onClickListener) {
        vimeoSettingsButton.setOnClickListener(onClickListener);
    }
}
