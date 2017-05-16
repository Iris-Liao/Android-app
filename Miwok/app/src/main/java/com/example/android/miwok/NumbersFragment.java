package com.example.android.miwok;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NumbersFragment extends Fragment {
    private MediaPlayer mMediaPlayer;//to prevent our attribute from being edited
    private AudioManager mAudiaoManager;
    //audio focus change listener
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                releaseMediaPlayer();
            }
        }
    };
    

    public NumbersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        final ArrayList<word> words = new ArrayList<>();
        words.add(new word("lutti", "one", R.drawable.number_one, R.raw.number_one));//put new "word" type object into arraylists of words
        words.add(new word("otiiki", "two", R.drawable.number_two, R.raw.number_two));
        words.add(new word("tolookosu", "three", R.drawable.number_three, R.raw.number_three));
        words.add(new word("oyyisa", "four", R.drawable.number_four, R.raw.number_four));
        words.add(new word("massokka", "five", R.drawable.number_five, R.raw.number_five));
        words.add(new word("temmokka", "six", R.drawable.number_six, R.raw.number_six));
        words.add(new word("kenekaku", "seven", R.drawable.number_seven, R.raw.number_seven));
        words.add(new word("kawinta", "eight", R.drawable.number_eight, R.raw.number_eight));
        words.add(new word("wo'e", "nine", R.drawable.number_nine, R.raw.number_nine));
        words.add(new word("na'aacha", "ten", R.drawable.number_ten, R.raw.number_ten));

        //creat an instance of wordAdapter which is a custom adapter extended from arrayAdapter
        //  Fix the error by passing in a reference to the Activity that encloses this Fragment as the context.
        WordAdapter wordAdapter = new WordAdapter(getActivity(), words, R.color.category_numbers);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(wordAdapter);
        // click on the item,then play the audio file
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                releaseMediaPlayer();
                word word = words.get(position);
                //Fix the error by passing in a reference to the Activity that encloses this Fragment as the context.
                mMediaPlayer = MediaPlayer.create(getActivity(), word.getAudioResourceId());//MediaPlayer is a class
                //This is the Activity that encloses the current Fragment,
                // which will be the NumbersActivity for the NumbersFragment.
                // Then call getSystemService(String) on that Activity object.
                mAudiaoManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);//while AudioManager is a system service
                int result = mAudiaoManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mMediaPlayer.start();
                    //once stop,release the madiaplayer
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            releaseMediaPlayer();
                            mAudiaoManager.abandonAudioFocus(afChangeListener);
                        }
                    });
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        // When the activity is stopped, release the media player resources because we won't
        // be playing any more sounds.
        releaseMediaPlayer();
    }

    //mehotd to release media sources
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;
        }
    }
    /**
     * This listener gets triggered when the {@link MediaPlayer} has completed
     * playing the audio file.
     */

}
