package com.luanta.testspeechui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Process;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.luanta.testspeechui.database.Score;
import com.luanta.testspeechui.database.ScoreViewModel;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.jtransforms.fft.FloatFFT_1D;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import in.goodiebag.carouselpicker.CarouselPicker;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int SELECT_USER_ACTIVITY_REQUEST_CODE = 1;
    private int userIdActive;
    private ScoreViewModel mScoreViewModel;

    private static final String TAG = "Formant";

    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    // audio fields
    private static final int SAMPLE_RATE = 44100; // Hz or samples per second
    private static final int ENCODING = AudioFormat.ENCODING_PCM_FLOAT;
    private static final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;
    private static final int BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING);
    private static final int RECORD_TIME = 1; // in seconds
    public float[] audioData;
//    public float[] magnitudes;
//    public ArrayList<Integer> peakIndexes;
    private List<CarouselPicker.PickerItem> imageItems = new ArrayList<>();
    private CarouselPicker carouselPicker;// = (CarouselPicker) findViewById(R.id.vowels_picker);
    private int picked_vowel = 0;
    private MediaPlayer mp;
    //Uri uri;
//    String uriParse = "";
//    GoalProgressBar progressBarF1;
//    GoalProgressBar progressBarF2;
    private GoalProgressBar progressBarF1F2;
//    int progF1;
//    int progF2;
    private int progF1F2;
    private int F1, F2;
    private ImageView imageView;
    private AnimationDrawable testAnimation;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean isStopButtonPressed = false;
    private boolean continueParsing;
    private boolean isAudioStarted;
    private boolean isFFTComplete = false;
    private AudioRecord audioRecord = null;
    private ImageButton recordButton;
    // reference Formants
    // TODO: add male and child reference Formants data
    // /i/, /ɪ/, /e/, /ɛ/, /æ/, /ʌ/, /ɝ/, /u/, /ʊ/, /o/, /ɔ/, /ɑ/
    private int[] childF1 = {452, 511, 564, 749, 717, 749, 586, 494, 568, 597, 803, 1002};
    private int[] childF2 = {3081, 2552, 2656, 2267, 2501, 1546, 1719, 1345, 1490, 1137, 1210, 1688};

    private int[] femaleF1 = {437, 487, 536, 731, 669, 753, 532, 459, 519, 555, 781, 936};
    private int[] femaleF2 = {2761, 2365, 2530, 2058, 2349, 1426, 1588, 1105, 1125, 1035, 1136, 1151};

    private int[] maleF1 = {342, 427, 476, 580, 588, 623, 474, 378, 469, 497, 652, 768};
    private int[] maleF2 = {2322, 2034, 2089, 1799, 1952, 1200, 1379, 997, 1122, 910, 997, 1333};

    private int[] referenceF1 = femaleF1;
    private int[] referenceF2 = femaleF2;
//    private String[] hints = {"FRONT", "BACK", "MIDDLE", "CLOSE/HIGH", "OPEN/LOW"};
//    private String hint = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.app_bar_nav_drawer);
        setContentView(R.layout.activity_nav_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        imageView = findViewById(R.id.animation);
        if (imageView == null) throw new AssertionError();

        imageView.setBackgroundResource(R.drawable.anim_v1);

        testAnimation = (AnimationDrawable) imageView.getBackground();

        testAnimation.setOneShot(true);


        ImageButton bt_listen = findViewById(R.id.bt_listen);

        bt_listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_listening();
            }
        });

        /*progressBarF1 = findViewById(R.id.progressBarF1);
        progressBarF2 = findViewById(R.id.progressBarF2);*/

        progressBarF1F2 = findViewById(R.id.progressBarF1F2);

        /*progressBarF1.setGoal(50); // TODO: set to 50
        progressBarF2.setGoal(50); // TODO: set to 50*/

        progressBarF1F2.setGoal(80);

        if (savedInstanceState == null) {
            resetProgress();
        }

        recordButton = findViewById(R.id.bt_record);
        continueParsing = true;
        isStopButtonPressed = false;
        isAudioStarted = true;

        carouselPicker = (CarouselPicker) findViewById(R.id.vowels_picker);

// Case 1 : To populate the picker with images
//        List<CarouselPicker.PickerItem> imageItems = new ArrayList<>();
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v1_fr1_long_i_eat));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v2_fr2_short_i_pin));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v3_fr3_e_eight));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v4_fr4_epsilon_bed));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v5_fr5_ae_at));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v6_ct1_inv_v_sun));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v7_ct2_inv_epsilon_bird));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v8_bk1_u_drew));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v9_bk2_inv_omega_foot));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v10_bk3_o_both));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v11_bk4_inv_c_jaw));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v12_bk5_short_o_clock));
//Create an adapter
        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this, imageItems, 0);
//Set the adapter
        carouselPicker.setAdapter(imageAdapter);

        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                picked_vowel = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Set up the ScoreViewModel
        mScoreViewModel = ViewModelProviders.of(this)
                .get(com.luanta.testspeechui.database.ScoreViewModel.class);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Override this method enable (inflate) display of Menu in Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile_child:
                referenceF1 = childF1;
                referenceF2 = childF2;
                setMenuItemOptionActive(item);
                return true;
            case R.id.profile_female:
                referenceF1 = femaleF1;
                referenceF2 = femaleF2;
                setMenuItemOptionActive(item);
                return true;
            case R.id.profile_male:
                referenceF1 = maleF1;
                referenceF2 = maleF2;
                setMenuItemOptionActive(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setMenuItemOptionActive(MenuItem item) {
        if(item.isChecked()) item.setChecked(false);
        else item.setChecked(true);
    }


    public void startAnim(View view) {
//        imageView.setVisibility(View.VISIBLE);
        //TODO: Switch animations based on current selected sound
        getAnimation();

        if (testAnimation.isRunning()) {
            testAnimation.stop();
        }
        testAnimation.start();
        Toast.makeText(this, "Playing visual feedback...", Toast.LENGTH_SHORT).show();
    }

    private void getAnimation() {

        switch (picked_vowel) {
            case 0:
                imageView.setBackgroundResource(R.drawable.anim_v1);
                break;
            case 1:
                imageView.setBackgroundResource(R.drawable.anim_v2);
                break;
            case 2:
                imageView.setBackgroundResource(R.drawable.anim_v3);
                break;
            case 3:
                imageView.setBackgroundResource(R.drawable.anim_v4);
                break;
            case 4:
                imageView.setBackgroundResource(R.drawable.anim_v5);
                break;
            case 5:
                imageView.setBackgroundResource(R.drawable.anim_v6);
                break;
            case 6:
                imageView.setBackgroundResource(R.drawable.anim_v7);
                break;
            case 7:
                imageView.setBackgroundResource(R.drawable.anim_v8);
                break;
            case 8:
                imageView.setBackgroundResource(R.drawable.anim_v9);
                break;
            case 9:
                imageView.setBackgroundResource(R.drawable.anim_v10);
                break;
            case 10:
                imageView.setBackgroundResource(R.drawable.anim_v11);
                break;
            case 11:
                imageView.setBackgroundResource(R.drawable.anim_v12);
                break;
        }
//        imageView.setBackgroundResource(R.drawable.test_animation);

        testAnimation = (AnimationDrawable) imageView.getBackground();

        testAnimation.setOneShot(true);

    }

    private void onRecord(boolean start) {

        if (start) {
            startRecording();
//            resetProgress();
            Toast.makeText(this, "Recording...Tap to stop", Toast.LENGTH_SHORT).show();
        } else {
            resetProgress();
            stopRecording();
            Toast.makeText(this, "Record has been stopped.", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        isStopButtonPressed = true; // to release resource inside thread (avoiding crash)
    }

    @Override
    public void onStop() {
        super.onStop();
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    public void record(View view) {

        isStopButtonPressed = false;

        onRecord(isAudioStarted);

        if (isAudioStarted) {
            recordButton.setImageResource(R.drawable.recorder_stop);
        } else {
            recordButton.setImageResource(R.drawable.sound_recorder_icon);
        }

        isAudioStarted = !isAudioStarted;
    }


    // TODO: calculate progF1, progF2 based on F1, F2 value
    public void resetProgress() {
        /*progressBarF1.setProgress(progF1);
        progressBarF2.setProgress(progF2);*/

        progressBarF1F2.setProgress(progF1F2);
    }

    @Override
    protected void onDestroy() {
        mp.release();
        super.onDestroy();
    }

    private void start_listening() {

        switch (picked_vowel) {
            case 0:
                mp = MediaPlayer.create(this, R.raw.v1_fr1_long_i_eat);
                break;
            case 1:
                mp = MediaPlayer.create(this, R.raw.v2_fr2_short_i_pin);
                break;
            case 2:
                mp = MediaPlayer.create(this, R.raw.v3_fr3_e_eight);
                break;
            case 3:
                mp = MediaPlayer.create(this, R.raw.v4_fr4_epsilon_bed);
                break;
            case 4:
                mp = MediaPlayer.create(this, R.raw.v5_fr5_ae_at);
                break;
            case 5:
                mp = MediaPlayer.create(this, R.raw.v6_ct1_inv_v_sun);
                break;
            case 6:
                mp = MediaPlayer.create(this, R.raw.v7_ct2_inv_epsilon_bird);
                break;
            case 7:
                mp = MediaPlayer.create(this, R.raw.v8_bk1_u_drew);
                break;
            case 8:
                mp = MediaPlayer.create(this, R.raw.v9_bk2_inv_omega_foot);
                break;
            case 9:
                mp = MediaPlayer.create(this, R.raw.v10_bk3_o_both);
                break;
            case 10:
                mp = MediaPlayer.create(this, R.raw.v11_bk4_inv_c_jaw);
                break;
            case 11:
                mp = MediaPlayer.create(this, R.raw.v12_bk5_short_o_clock);
                break;
        }

        try {
            //mp.setDataSource(path + File.separator + fileName);
            //mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Toast.makeText(this, "Sample sound is playing..." + uriParse, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Sample sound is playing...", Toast.LENGTH_SHORT).show();

    }

    /* begin public void startRecording() { */
    //TODO: Auto detect speech signal for START/ STOP recording
    public void startRecording() {

        audioData = new float[SAMPLE_RATE * RECORD_TIME];

        Thread startThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);

                audioRecord = new AudioRecord.Builder()
                        .setAudioSource(MediaRecorder.AudioSource.MIC)
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(ENCODING) // ENCODING = AudioFormat.ENCODING_PCM_FLOAT;
                                .setSampleRate(SAMPLE_RATE) // SAMPLE_RATE = 44100
                                .setChannelMask(CHANNEL_MASK) // CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO
                                .build())
                        .setBufferSizeInBytes(BUFFER_SIZE) // BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING)
                        .build();

                audioRecord.startRecording();

                while (!isStopButtonPressed) {
                    int shortsRead = 0;
                    audioData = new float[SAMPLE_RATE * RECORD_TIME];
                    while (shortsRead < audioData.length) {
                        int numberOfIndexs = audioRecord.read(audioData, 0, audioData.length, AudioRecord.READ_NON_BLOCKING);
                        shortsRead += numberOfIndexs;
                    }
                    generateGraphData(audioData.clone());
                    while (!isFFTComplete) ;
                    while (continueParsing) ;
                    isFFTComplete = false;
                    continueParsing = true;
                }

//                generateGraphData(audioData.clone());

                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
        });//.start();

        startThread.start();
    }
    /* end public void startRecording() { */

    /* begin public void generateGraphData(final float[] data) { */
    public void generateGraphData(final float[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);

                final float[] magnitude = calculateFFT(data);
                final ArrayList<Integer> peakIndex = calculatePeaks(magnitude, 500);
//                magnitudes = magnitude;
//                peakIndexes = peakIndex;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //displayF1F2(magnitude.clone(), (ArrayList<Integer>) peakIndex.clone());
                        displayF1F2((ArrayList<Integer>) peakIndex.clone());
                    }
                });

                isFFTComplete = true;
                continueParsing = false;
            }
        }).start();
    }
    /* end public void generateGraphData(final float[] data) { */

    /* begin public float[] calculateFFT(float[] audioData) { */
    public float[] calculateFFT(float[] audioData) {

        FloatFFT_1D fft = new FloatFFT_1D(audioData.length);
        fft.realForward(audioData);

        float[] magnitudes = new float[audioData.length / 2];
        for (int frequencyBin = 0; frequencyBin < audioData.length / 2; frequencyBin++) {
            float real = audioData[frequencyBin * 2];
            float imaginary = audioData[2 * frequencyBin + 1];
            float magnitude = (float) Math.sqrt(real * real + imaginary * imaginary);
            magnitudes[frequencyBin] = magnitude;
        }
        return magnitudes;
    }
    /* end public float[] calculateFFT(float[] audioData) { */

    /* begin private void displayF1F2(float[] magnitude, ArrayList<Integer> peaks) { */
//    private void displayF1F2(float[] magnitude, ArrayList<Integer> peaks) {
    private void displayF1F2(ArrayList<Integer> peaks) {

        setCurrentF(peaks); // TODO: replace with calculate F1, F2 for progressBar inputs

        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                continueParsing = false;
            }
        }, 1000);
*/
    }
    /* end private void displayF1F2(float[] magnitude, ArrayList<Integer> peaks) { */

    /* begin public ArrayList<Integer> calculatePeaks(float[] magnitudes, int minimumDistance) { */
    public ArrayList<Integer> calculatePeaks(float[] magnitudes, int minimumDistance) {
        ArrayList<Integer> peakIndexes = new ArrayList<>();
        int frequencyBin = 0;
        float max = magnitudes[0];
        int lastPeakIndex = 0;
        while (frequencyBin < magnitudes.length - 1) {
            while (frequencyBin < magnitudes.length - 1 && magnitudes[frequencyBin + 1] >= max) {
                frequencyBin++;
                max = magnitudes[frequencyBin];
            }
            if (!peakIndexes.isEmpty() && frequencyBin - lastPeakIndex < minimumDistance) {
                if (magnitudes[lastPeakIndex] > magnitudes[frequencyBin]) {
                    //Do not do anything, old peak is better
                } else {
                    //new peak is higher so replace the old close by one
                    peakIndexes.remove(peakIndexes.size() - 1);
                    peakIndexes.add(frequencyBin);
                    lastPeakIndex = frequencyBin;
                    //do not change peakIndex
                }
            } else {
                //Add a new peak not near any others
                peakIndexes.add(frequencyBin);
                lastPeakIndex = frequencyBin;
            }

            //while (frequencyBin < magnitudes.length - 1 && magnitudes[frequencyBin + 1] <= max) {
            while (frequencyBin < magnitudes.length - 1 && magnitudes[frequencyBin + 1] < max) {
                frequencyBin++;
                max = magnitudes[frequencyBin];
            }
        }
        return peakIndexes;
    }
    /* end public ArrayList<Integer> calculatePeaks(float[] magnitudes, int minimumDistance) { */


    // TODO: replace with calculate F1, F2 for progressBar inputs
    private void setCurrentF(ArrayList<Integer> list) {

        F1 = list.get(0) * 2;
        F2 = list.get(1) * 2;

        int deltaF1 = 100 * (F1 - referenceF1[picked_vowel]) / referenceF1[picked_vowel];

        int deltaF2 = 100 * (F2 - referenceF2[picked_vowel]) / referenceF2[picked_vowel];

        int scoreF1F2 = 100 - (Math.abs(deltaF1) + Math.abs(deltaF2))/2;

        if(scoreF1F2 < 0) {
            Random rd = new Random();
            progF1F2 = 1 + rd.nextInt(5);
        }
        else progF1F2 = scoreF1F2;

        Score score = new Score(userIdActive,picked_vowel+1,progF1F2,
                new Timestamp(System.currentTimeMillis()).toString());
        mScoreViewModel.insert(score);

//        progF1F2 = 100 - (Math.abs(deltaF1) + Math.abs(deltaF2))/2;

//        Log.i(TAG,"F1: " + F1);
//        Log.i(TAG,"F2: " + F2);
//        Log.i(TAG,"scoreF1F2: " + scoreF1F2);
//        Log.i(TAG,"progF1F2: " + progF1F2);
        Log.i(TAG,"referenceF1: " + referenceF1[picked_vowel]);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // TODO: implement menuItem selected here
        int id = menuItem.getItemId();

        if (id != 0) {
            if(id == R.id.nav_select_user) {
                Intent intent = new Intent(this,UsersActivity.class);
                startActivityForResult(intent,SELECT_USER_ACTIVITY_REQUEST_CODE);
            }
            else if(id == R.id.nav_scores) {
                Intent intent = new Intent(this,ScoresActivity.class);
                startActivity(intent);
            }
            else Toast.makeText(this, "onNavigationItemSelected...", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        userIdActive = data.getIntExtra(UsersActivity.EXTRA_REPLY,-1);
//        Log.d("_onItemClick","userIdSelected: " + userIdSelected);
    }
}
