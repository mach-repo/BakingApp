package com.example.android.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.bakingapp.data.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bakingapp.StepActivity.CURRENT_STEP;



public class StepDetailFragment extends Fragment {


    /* for the exoplayer */
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    private String mVideoUrl;

    public static final String VIDEO_URL = "video-url";
    public static final String VIDEO_POSITION = "video-position";
    public static final String VIDEO_PLAYER_STATE = "video-player-state";

    private boolean mPlayVideoWhenForegrounded = true;
    private long mLastPosition = 0;

    private boolean mPlayerInitialised = false;

    public StepDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializePlayer(Uri.parse(mVideoUrl));

        if(savedInstanceState != null){

            mVideoUrl = savedInstanceState.getString(VIDEO_URL);
            mLastPosition = savedInstanceState.getLong(VIDEO_POSITION);
            mPlayVideoWhenForegrounded = savedInstanceState.getBoolean(VIDEO_PLAYER_STATE);

            // restore player state after initialization
            // Seek to the last position of the player.
            mExoPlayer.seekTo(mLastPosition);
            // Put the player into the last state we were in.
            mExoPlayer.setPlayWhenReady(mPlayVideoWhenForegrounded);


        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // make the fragment retainable
        setRetainInstance(true);

        // the step
        Step currentStep = this.getArguments().getParcelable(CURRENT_STEP);

        // inflating the view
        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);


        // the 3 views we have
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.video_view);
        ImageView thumbnailImageView = (ImageView) rootView.findViewById(R.id.thumbnail_image_view);
        TextView stepDescriptionTextView = (TextView) rootView.findViewById(R.id.step_description);

        mVideoUrl = currentStep.getVideoURL();

        // binding the 3 views to their data
        if(!TextUtils.isEmpty(currentStep.getVideoURL())){
            // the first view ( the video view ) displayed if not empty
            mPlayerView.setVisibility(View.VISIBLE);
            thumbnailImageView.setVisibility(View.GONE);

        }
        else if (!TextUtils.isEmpty(currentStep.getThumbnailURL())){
            // the second view ( the image view ) displayed if available
            //mPlayerView.setVisibility(View.GONE);
            thumbnailImageView.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(currentStep.getThumbnailURL()).into(thumbnailImageView);
        }
        // the third view always available ( text description of the step )
        stepDescriptionTextView.setText(currentStep.getDescription());


        return rootView;
    }


    private void initializePlayer(Uri mediaUri){
        if(mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer =  ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            //Data Source
            String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getActivity(),
                    userAgent), new DefaultExtractorsFactory(),null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(false);

            Log.d("hhh" , "inside initplayer inside if");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    private void releasePlayer(){

        if(mExoPlayer != null){
            // Store off if we were playing so we know if we should start when we're foregrounded again.
            mPlayVideoWhenForegrounded = mExoPlayer.getPlayWhenReady();
            // Store off the last position our player was in before we paused it.
            mLastPosition = mExoPlayer.getCurrentPosition();
            // Pause the player
            mExoPlayer.setPlayWhenReady(false);

            mExoPlayer.stop();
            mExoPlayer.release();
        }
        mExoPlayer = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // there was a video
        if(!TextUtils.isEmpty(mVideoUrl)){
            outState.putString(VIDEO_URL, mVideoUrl);
            outState.putLong(VIDEO_POSITION, mLastPosition);
            outState.putBoolean(VIDEO_PLAYER_STATE, mPlayVideoWhenForegrounded);
        }

    }


    @Override
    public void onPause()
    {
        super.onPause();
        releasePlayer();
    }
}
