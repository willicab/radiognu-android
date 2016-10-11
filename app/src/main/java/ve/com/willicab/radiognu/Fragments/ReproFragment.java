package ve.com.willicab.radiognu.Fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ve.com.willicab.radiognu.GetJSON;
import ve.com.willicab.radiognu.R;
import ve.com.willicab.radiognu.Vars;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReproFragment extends Fragment {
    Context c;
    View v;

    TextView tvSong, tvArtist, tvDisc, tvLicense, tvBuffer, tvLive;
    ProgressBar pbLoad;
    ImageView ivCover;
    MediaPlayer player;
    ImageButton btnPlay, btnStop;

    public ReproFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_repro, container, false);
        c = v.getContext();

        tvSong = (TextView) v.findViewById(R.id.tvSong);
        tvArtist = (TextView) v.findViewById(R.id.tvArtist);
        tvDisc = (TextView) v.findViewById(R.id.tvDisc);
        tvLicense = (TextView) v.findViewById(R.id.tvLicense);
        tvBuffer = (TextView) v.findViewById(R.id.tvBuffer);
        tvLive = (TextView) v.findViewById(R.id.tvLive);
        ivCover = (ImageView) v.findViewById(R.id.ivCover);
        pbLoad = (ProgressBar) v.findViewById(R.id.pbLoad);
        btnPlay = (ImageButton) v.findViewById(R.id.btnPlay);
        btnStop = (ImageButton) v.findViewById(R.id.btnStop);
        initializeMediaPlayer();
        updateMetadata();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnPlay.setVisibility(View.GONE);
                pbLoad.setVisibility(View.VISIBLE);
                tvBuffer.setVisibility(View.VISIBLE);
                try {
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            btnStop.setVisibility(View.VISIBLE);
                            pbLoad.setVisibility(View.GONE);
                            tvBuffer.setVisibility(View.GONE);
                            player.start();
                        }
                    });
                } catch (IllegalStateException e) {
                    btnStop.setVisibility(View.VISIBLE);
                    pbLoad.setVisibility(View.GONE);
                    tvBuffer.setVisibility(View.GONE);
                    player.start();
                }
            }
        });

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.i("Buffering", String.valueOf(percent));
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);

                player.pause();
            }
        });

        player.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        tvBuffer.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        tvBuffer.setVisibility(View.GONE);
                        break;
                    case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        Log.i("ChangeMetadata", "Nueva metadata");
                        break;
                }
                return false;
            }
        });

        return v;
    }

    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource(Vars.radioAmUrl);
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMetadata() {
        new GetJSON() {
            @Override
            public void onGetJson(JSONObject data) throws JSONException {
                Log.i("JSONObject", String.valueOf(data));
                JSONObject license = new JSONObject(data.getString("license"));
                tvSong.setText(data.getString("title"));
                tvArtist.setText(data.getString("artist") + " (" + data.getString("country") + ")");
                tvDisc.setText(data.getString("album") + " (" + data.getString("year") + ")");
                tvLicense.setText(license.getString("shortname"));
                tvLive.setVisibility(!data.getBoolean("isLive") ? View.GONE : View.VISIBLE);
                byte[] decodedString = Base64.decode(data.getString("cover").replace("data:image/png;base64,", "").replace("=", ""), Base64.DEFAULT);
                Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivCover.setImageBitmap(Bitmap.createScaledBitmap(bitMap, Vars.size, Vars.size, false));
            }
        }.execute(Vars.apiURL);
    }
}