package com.luanta.testspeechui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.jtransforms.fft.FloatFFT_1D;

import java.util.ArrayList;
import java.util.List;

import in.goodiebag.carouselpicker.CarouselPicker;

public class MainActivity extends AppCompatActivity {

    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    // audio fields
    private static final int SAMPLE_RATE = 44100; // Hz or samples per second
    private static final int ENCODING = AudioFormat.ENCODING_PCM_FLOAT;
    private static final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;
    private static final int BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING);
    private static final int RECORD_TIME = 1; // in seconds
    public float[] audioData;
    public float[] magnitudes;
    public ArrayList<Integer> peakIndexes;
    CarouselPicker carouselPicker;// = (CarouselPicker) findViewById(R.id.vowels_picker);
    int picked_vowel = 0;
    MediaPlayer mp;
    //Uri uri;
//    String uriParse = "";
    GoalProgressBar progressBarF1;
    GoalProgressBar progressBarF2;
    int progF1;
    int progF2;
    int F1, F2;
    ImageView imageView;
    AnimationDrawable testAnimation;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean isStopButtonPressed = false;
    private boolean continueParsing;
    private boolean isAudioStarted;
    private boolean isFFTComplete = false;
    private AudioRecord audioRecord = null;
    private ImageButton recordButton;
    // reference Formants
    private int[] femaleF1 = {437, 487, 536, 731, 669, 459, 519, 555, 781, 936, 753, 532};
    private int[] femaleF2 = {2761, 2365, 2530, 2058, 2349, 1105, 1125, 1035, 1136, 1151, 1426, 1588};
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
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        imageView = findViewById(R.id.animation);
        if (imageView == null) throw new AssertionError();

        imageView.setBackgroundResource(R.drawable.test_animation);

        testAnimation = (AnimationDrawable) imageView.getBackground();

        testAnimation.setOneShot(true);


        ImageButton bt_listen = findViewById(R.id.bt_listen);

        bt_listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_listening();
            }
        });

        progressBarF1 = findViewById(R.id.progressBarF1);
        progressBarF2 = findViewById(R.id.progressBarF2);

        progressBarF1.setGoal(50); // TODO: set to 50
        progressBarF2.setGoal(50); // TODO: set to 50

        if (savedInstanceState == null) {
            resetProgress();
        }

        recordButton = findViewById(R.id.bt_record);
        continueParsing = true;
        isStopButtonPressed = false;
        isAudioStarted = true;

        carouselPicker = (CarouselPicker) findViewById(R.id.vowels_picker);

// Case 1 : To populate the picker with images
        List<CarouselPicker.PickerItem> imageItems = new ArrayList<>();
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_fr1_long_i));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_fr2_short_i));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_fr3_single_a));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_fr4_ae));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_fr5_single_e));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_fr6_epsilon));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_ct1_revert_e));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_ct2_revert_v));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_bk1_long_o));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_bk2_long_u));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_bk3_short_o));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.v_bk4_short_u));
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
//                tvSelected.setText("Selected item in image carousel is  : "+position);
                picked_vowel = position;
//
//                if(position <= 4) {
//                    hint = "FRONT";
//                }
//                else if (position <= 7) {
//                    hint = "MIDDLE";
//                }
//                else hint = "BACK";
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    public void startAnim(View view) {
//        imageView.setVisibility(View.VISIBLE);
        if (testAnimation.isRunning()) {
            testAnimation.stop();
        }
        testAnimation.start();
        Toast.makeText(this, "Playing visual feedback...", Toast.LENGTH_SHORT).show();
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
        progressBarF1.setProgress(progF1);
        progressBarF2.setProgress(progF2);
    }

    @Override
    protected void onDestroy() {
        mp.release();
        super.onDestroy();
    }

    private void start_listening() {

        switch (picked_vowel) {
            case 0:
                mp = MediaPlayer.create(this, R.raw.v_fr1_long_i);
                break;
            case 1:
                mp = MediaPlayer.create(this, R.raw.v_fr2_short_i);
                break;
            case 2:
                mp = MediaPlayer.create(this, R.raw.v_fr3_single_a);
                break;
            case 3:
                mp = MediaPlayer.create(this, R.raw.v_fr4_ae);
                break;
            case 4:
                mp = MediaPlayer.create(this, R.raw.v_fr5_single_e);
                break;
            case 5:
                mp = MediaPlayer.create(this, R.raw.v_fr6_epsilon);
                break;
            case 6:
                mp = MediaPlayer.create(this, R.raw.v_bk4_short_u);
                break;
            case 7:
                mp = MediaPlayer.create(this, R.raw.v_ct2_revert_v);
                break;
            case 8:
                mp = MediaPlayer.create(this, R.raw.v_bk1_long_o);
                break;
            case 9:
                mp = MediaPlayer.create(this, R.raw.v_bk2_long_u);
                break;
            case 10:
                mp = MediaPlayer.create(this, R.raw.v_bk3_short_o);
                break;
            case 11:
                mp = MediaPlayer.create(this, R.raw.v_bk4_short_u);
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
                magnitudes = magnitude;
                peakIndexes = peakIndex;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //m(magnitude.clone(), (ArrayList<Integer>) peakIndex.clone());
                        m((ArrayList<Integer>) peakIndex.clone());
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

    /* begin private void m(float[] magnitude, ArrayList<Integer> peaks) { */
//    private void m(float[] magnitude, ArrayList<Integer> peaks) {
    private void m(ArrayList<Integer> peaks) {

        setCurrentF(peaks); // TODO: replace with calculate F1, F2 for progressBar inputs

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                continueParsing = false;
            }
        }, 1000);

    }
    /* end private void m(float[] magnitude, ArrayList<Integer> peaks) { */

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

            while (frequencyBin < magnitudes.length - 1 && magnitudes[frequencyBin + 1] <= max) {
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

        int deltaF1 = 100 * (F1 - femaleF1[picked_vowel]) / femaleF1[picked_vowel];

        if (Math.abs(deltaF1) <= 5) {
            progF1 = 50;
        } else if (Math.abs(deltaF1) <= 10) {
            if (deltaF1 > 0) progF1 = 60;
            else progF1 = 40;
        } else {
            if (deltaF1 > 0) progF1 = 75;
            else progF1 = 25;
        }

        int deltaF2 = 100 * (F2 - femaleF2[picked_vowel]) / femaleF2[picked_vowel];

        if (Math.abs(deltaF2) <= 5) {
            progF2 = 50;
        } else if (Math.abs(deltaF2) <= 10) {
            if (deltaF2 > 0) progF2 = 60;
            else progF2 = 40;
        } else {
            if (deltaF2 > 0) progF2 = 75;
            else progF2 = 25;
        }

    }


}
