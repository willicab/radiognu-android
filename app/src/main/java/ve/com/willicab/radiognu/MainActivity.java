package ve.com.willicab.radiognu;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import ve.com.willicab.radiognu.Fragments.ReproFragment;

//import com.github.nkzawa.socketio.client.IO;

public class MainActivity extends Activity {
    public String url_api = "https://api.radiognu.org";
    public TextView tvSong, tvArtist, tvDisc, tvLicense, tvBuffer, tvState;
    public ProgressBar pbLoad;
    public ImageView ivCover;
    public int width, height;
    public String[] Result;
    public ImageButton btnPlay, btnStop;
    public Intent iCatalogo;

    //Streaming
    private final static String RADIO_STATION_URL = "http://audio.radiognu.org/radiometagnuam.ogg";
    private MediaPlayer player;

    //private Socket mSocket;
    //private Emitter.Listener onJoined, onError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Vars.size = displaymetrics.widthPixels;

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(Color.WHITE, Color.GRAY);
        tabLayout.addTab(tabLayout.newTab().setText("Reproductor"));
        tabLayout.addTab(tabLayout.newTab().setText("Catálogo"));
        tabLayout.addTab(tabLayout.newTab().setText("Ajustes"));

        ReproFragment mfr = new ReproFragment();
        FragmentManager mfm = getFragmentManager();
        mfm.beginTransaction().replace(R.id.flRepro, mfr).commit();

/*
        tvSong = (TextView) findViewById(R.id.tvSong);
        tvArtist = (TextView) findViewById(R.id.tvArtist);
        tvDisc = (TextView) findViewById(R.id.tvDisc);
        tvLicense = (TextView) findViewById(R.id.tvLicense);
        tvBuffer = (TextView) findViewById(R.id.tvBuffer);
        tvState = (TextView) findViewById(R.id.tvState);
        ivCover = (ImageView) findViewById(R.id.ivCover);
        pbLoad = (ProgressBar) findViewById(R.id.pbLoad);
        initializeMediaPlayer();
        iCatalogo = new Intent(this, CatalogoActivity.class);

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnStop = (ImageButton) findViewById(R.id.btnStop);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("RadioGNU", String.valueOf(tab.getPosition()));
                switch (tab.getPosition()) {
                    case 1:
                        startActivity(iCatalogo);
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnPlay.setVisibility(View.GONE);
                pbLoad.setVisibility(View.VISIBLE);
                try {
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            btnStop.setVisibility(View.VISIBLE);
                            pbLoad.setVisibility(View.GONE);
                            player.start();
                        }
                    });
                } catch (IllegalStateException e) {
                    btnStop.setVisibility(View.VISIBLE);
                    pbLoad.setVisibility(View.GONE);
                    player.start();
                }
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
                }
                return false;
            }
        });
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        Result = new String[16];

        new JSONTask().execute(url_api);
    }

    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource(RADIO_STATION_URL);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.i("Buffering", "" + percent);
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJSON = buffer.toString();
                Log.i("RadioGNUJSON", finalJSON);
                JSONObject parentObject = new JSONObject(finalJSON);
                JSONObject licenseObject = new JSONObject(parentObject.getString("license"));

                Result[0] = parentObject.getString("artist");
                Result[1] = parentObject.getString("title");
                Result[2] = parentObject.getString("album");
                Result[3] = parentObject.getString("id");
                Result[4] = parentObject.getString("cover");
                Result[5] = parentObject.getString("genre");
                Result[6] = parentObject.getString("country");
                Result[7] = parentObject.getString("year");
                Result[8] = parentObject.getString("url");
                Result[9] = parentObject.getString("duration");
                Result[10] = parentObject.getString("country");
                Result[11] = parentObject.getString("listeners");
                Result[12] = parentObject.getString("isLive");
                Result[13] = licenseObject.getString("name");
                Result[14] = licenseObject.getString("shortname");
                Result[15] = licenseObject.getString("url");


                return "ok";

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "fail";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("ok")) {
                super.onPostExecute(result);
                tvSong.setText(Result[1]);
                tvArtist.setText(Result[0] + " (" + Result[6] + ")");
                tvDisc.setText(Result[2] + " (" + Result[7] + ")");
                tvLicense.setText(Result[14]);
                //Log.i("RadioGNU", Result[4]);
                byte[] decodedString = Base64.decode(Result[4].replace("data:image/png;base64,", "").replace("=", ""), Base64.DEFAULT);
                Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                //ivCover.setImageBitmap(bitMap);
                ivCover.setImageBitmap(Bitmap.createScaledBitmap(bitMap, width, width, false));
            }
        }
   */
    }
}