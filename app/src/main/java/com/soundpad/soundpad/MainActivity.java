package com.soundpad.soundpad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView; //광고

    ImageButton Play_All_Btn;
    ImageButton Play_Stop_Btn;
    Button Recoding_Btn;

    CheckBox  repeat_CheckBox;
    CheckBox  speed_CheckBox;
    int repeat_checkBox_int;
    float speed_checkBox_float;


    private long time= 0; //뒤로가기 2번 클릭시 종료용


    ImageButton Sound_1_1_Btn;
    ImageButton Sound_1_2_Btn;
    ImageButton Sound_1_3_Btn;
    ImageButton Sound_1_4_Btn;
    ImageButton Sound_1_5_Btn;
    ImageButton Sound_2_1_Btn;
    ImageButton Sound_2_2_Btn;
    ImageButton Sound_2_3_Btn;
    ImageButton Sound_2_4_Btn;
    ImageButton Sound_2_5_Btn;
    ImageButton Sound_3_1_Btn;
    ImageButton Sound_3_2_Btn;
    ImageButton Sound_3_3_Btn;
    ImageButton Sound_3_4_Btn;
    ImageButton Sound_3_5_Btn;
    ImageButton Sound_4_1_Btn;
    ImageButton Sound_4_2_Btn;
    ImageButton Sound_4_3_Btn;
    ImageButton Sound_4_4_Btn;
    ImageButton Sound_4_5_Btn;
    ImageButton Sound_5_1_Btn;
    ImageButton Sound_5_2_Btn;
    ImageButton Sound_5_3_Btn;
    ImageButton Sound_5_4_Btn;
    ImageButton Sound_5_5_Btn;


    SoundPool sp;

    int soundID_1_1;
    int soundID_1_2;
    int soundID_1_3;
    int soundID_1_4;
    int soundID_1_5;
    int soundID_2_1;
    int soundID_2_2;
    int soundID_2_3;
    int soundID_2_4;
    int soundID_2_5;
    int soundID_3_1;
    int soundID_3_2;
    int soundID_3_3;
    int soundID_3_4;
    int soundID_3_5;
    int soundID_4_1;
    int soundID_4_2;
    int soundID_4_3;
    int soundID_4_4;
    int soundID_4_5;
    int soundID_5_1;
    int soundID_5_2;
    int soundID_5_3;
    int soundID_5_4;
    int soundID_5_5;

    /* 퍼미션 */
    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int MULTIPLE_PERMISSIONS = 101;


    /* 녹음 */
    MediaRecorder recorder;
    String filename;
    boolean isRecording = false;

    /* 재생 */
    MediaPlayer mPlayer = null;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 광고 */
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        /* 폴더가 존재하는지 확인*/
        getSaveFolder();

        /* 안드로이드 6.0 이상일 경우 퍼미션 체크  */
        if (Build.VERSION.SDK_INT >= 23) { // 안드로이드 6.0 이상일 경우 퍼미션 체크
            checkPermissions();
        }

        /* 녹음 */
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundPAD";
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(filePath, "recorded.wav");
        filename = file.getAbsolutePath();

        recorder = new MediaRecorder();

        Recoding_Btn = (Button)findViewById(R.id.Recoding_Btn);
        Recoding_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording == false) {
                    recordAudio();
                    isRecording = true;
                    Recoding_Btn.setText("녹음 멈추기");
                } else {
                    stopRecording();
                    isRecording = false;
                    Recoding_Btn.setText("녹음");
                }
            }
        });

        /* 반복_체크박스 상태에 따른 코드들 */
        repeat_checkBox_int = 0;
        repeat_CheckBox = (CheckBox)findViewById(R.id.Repeat_CheckBox);
        repeat_CheckBox.setChecked(false);

        if(repeat_CheckBox.isChecked()==true){
            repeat_checkBox_int = -1;
        }else if(repeat_CheckBox.isChecked()==false){
            repeat_checkBox_int = 0;
        }
        repeat_CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(repeat_CheckBox.isChecked()==true){
                    repeat_checkBox_int = -1;
                }else if(repeat_CheckBox.isChecked()==false){
                    repeat_checkBox_int = 0;
                }
            }
        });

        /* 배속_체크박스 상태에 따른 코드들 */
        speed_checkBox_float= 1f;
        speed_CheckBox = (CheckBox)findViewById(R.id.Speed_CheckBox);
        speed_CheckBox.setChecked(false);
        if(speed_CheckBox.isChecked()==true){
            speed_checkBox_float = 2f;
        }else if(speed_CheckBox.isChecked()==false){
            speed_checkBox_float = 1f;
        }
        speed_CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speed_CheckBox.isChecked()==true){
                    speed_checkBox_float = 2f;
                }else if(speed_CheckBox.isChecked()==false){
                    speed_checkBox_float = 1f;
                }
            }
        });



        /* SoundPool( 최대 음악파일의 개수, 스트림 타입, 음질(기본값:0) */
        sp = new SoundPool(25, AudioManager.STREAM_MUSIC, 0);

        // 각각의 재생하고자하는 음악을 미리 준비한다
        /* load(현재 화면의 제어권자, 음악파일, 우선순위) */
        soundID_1_1 = sp.load(this, R.raw.sound_1, 1);
        soundID_1_2 = sp.load(this, R.raw.sound_2, 1);
        soundID_1_3 = sp.load(this, R.raw.sound_3, 1);
        soundID_1_4 = sp.load(this, R.raw.sound_4, 1);
        soundID_1_5 = sp.load(this, R.raw.sound_5, 1);
        soundID_2_1 = sp.load(this, R.raw.sound_6, 1);
        soundID_2_2 = sp.load(this, R.raw.sound_7, 1);
        soundID_2_3 = sp.load(this, R.raw.sound_8, 1);
        soundID_2_4 = sp.load(this, R.raw.sound_9, 1);
        soundID_2_5 = sp.load(this, R.raw.sound_10, 1);
        soundID_3_1 = sp.load(this, R.raw.sound_11, 1);
        soundID_3_2 = sp.load(this, R.raw.sound_12, 1);
        soundID_3_3 = sp.load(this, R.raw.sound_13, 1);
        soundID_3_4 = sp.load(this, R.raw.sound_14, 1);
        soundID_3_5 = sp.load(this, R.raw.sound_15, 1);
        soundID_4_1 = sp.load(this, R.raw.sound_16, 1);
        soundID_4_2 = sp.load(this, R.raw.sound_17, 1);
        soundID_4_3 = sp.load(this, R.raw.sound_18, 1);
        soundID_4_4 = sp.load(this, R.raw.sound_19, 1);
        soundID_4_5 = sp.load(this, R.raw.sound_20, 1);
        soundID_5_1 = sp.load(this, R.raw.sound_21, 1);
        soundID_5_2 = sp.load(this, R.raw.sound_22, 1);
        soundID_5_3 = sp.load(this, R.raw.sound_23, 1);
        soundID_5_4 = sp.load(this, R.raw.sound_24, 1);
        soundID_5_5 = sp.load(this, R.raw.sound_25, 1);

        /* 사운드 소리 버튼들 클릭시 소리 출력  */
        Button.OnClickListener sound_OnClick =new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.Sound_1_1_Btn:
                        sp.play(soundID_1_1,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_1_2_Btn:
                        sp.play(soundID_1_2,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_1_3_Btn:
                        sp.play(soundID_1_3,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_1_4_Btn:
                        sp.play(soundID_1_4,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_1_5_Btn:
                        sp.play(soundID_1_5,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_2_1_Btn:
                        sp.play(soundID_2_1,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_2_2_Btn:
                        sp.play(soundID_2_2,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_2_3_Btn:
                        sp.play(soundID_2_3,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_2_4_Btn:
                        sp.play(soundID_2_4,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_2_5_Btn:
                        sp.play(soundID_2_5,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_3_1_Btn:
                        sp.play(soundID_3_1,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_3_2_Btn:
                        sp.play(soundID_3_2,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_3_3_Btn:
                        sp.play(soundID_3_3,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_3_4_Btn:
                        sp.play(soundID_3_4,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_3_5_Btn:
                        sp.play(soundID_3_5,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_4_1_Btn:
                        sp.play(soundID_4_1,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_4_2_Btn:
                        sp.play(soundID_4_2,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_4_3_Btn:
                        sp.play(soundID_4_3,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_4_4_Btn:
                        sp.play(soundID_4_4,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_4_5_Btn:
                        sp.play(soundID_4_5,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_5_1_Btn:
                        sp.play(soundID_5_1,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_5_2_Btn:
                        sp.play(soundID_5_2,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_5_3_Btn:
                        sp.play(soundID_5_3,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_5_4_Btn:
                        sp.play(soundID_5_4,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;
                    case R.id.Sound_5_5_Btn:
                        sp.play(soundID_5_5,1,1,0,repeat_checkBox_int,speed_checkBox_float);
                        break;

                }
            }
        };
        /* 사용되는 버튼  */
        Play_All_Btn = (ImageButton)findViewById(R.id.Play_All_Btn);
        Play_Stop_Btn = (ImageButton)findViewById(R.id.Play_Stop_Btn);

        Sound_1_1_Btn = (ImageButton)findViewById(R.id.Sound_1_1_Btn);
        Sound_1_2_Btn = (ImageButton)findViewById(R.id.Sound_1_2_Btn);
        Sound_1_3_Btn = (ImageButton)findViewById(R.id.Sound_1_3_Btn);
        Sound_1_4_Btn = (ImageButton)findViewById(R.id.Sound_1_4_Btn);
        Sound_1_5_Btn = (ImageButton)findViewById(R.id.Sound_1_5_Btn);
        Sound_2_1_Btn = (ImageButton)findViewById(R.id.Sound_2_1_Btn);
        Sound_2_2_Btn = (ImageButton)findViewById(R.id.Sound_2_2_Btn);
        Sound_2_3_Btn = (ImageButton)findViewById(R.id.Sound_2_3_Btn);
        Sound_2_4_Btn = (ImageButton)findViewById(R.id.Sound_2_4_Btn);
        Sound_2_5_Btn = (ImageButton)findViewById(R.id.Sound_2_5_Btn);
        Sound_3_1_Btn = (ImageButton)findViewById(R.id.Sound_3_1_Btn);
        Sound_3_2_Btn = (ImageButton)findViewById(R.id.Sound_3_2_Btn);
        Sound_3_3_Btn = (ImageButton)findViewById(R.id.Sound_3_3_Btn);
        Sound_3_4_Btn = (ImageButton)findViewById(R.id.Sound_3_4_Btn);
        Sound_3_5_Btn = (ImageButton)findViewById(R.id.Sound_3_5_Btn);
        Sound_4_1_Btn = (ImageButton)findViewById(R.id.Sound_4_1_Btn);
        Sound_4_2_Btn = (ImageButton)findViewById(R.id.Sound_4_2_Btn);
        Sound_4_3_Btn = (ImageButton)findViewById(R.id.Sound_4_3_Btn);
        Sound_4_4_Btn = (ImageButton)findViewById(R.id.Sound_4_4_Btn);
        Sound_4_5_Btn = (ImageButton)findViewById(R.id.Sound_4_5_Btn);
        Sound_5_1_Btn = (ImageButton)findViewById(R.id.Sound_5_1_Btn);
        Sound_5_2_Btn = (ImageButton)findViewById(R.id.Sound_5_2_Btn);
        Sound_5_3_Btn = (ImageButton)findViewById(R.id.Sound_5_3_Btn);
        Sound_5_4_Btn = (ImageButton)findViewById(R.id.Sound_5_4_Btn);
        Sound_5_5_Btn = (ImageButton)findViewById(R.id.Sound_5_5_Btn);

        Sound_1_1_Btn.setOnClickListener(sound_OnClick);
        Sound_1_2_Btn.setOnClickListener(sound_OnClick);
        Sound_1_3_Btn.setOnClickListener(sound_OnClick);
        Sound_1_4_Btn.setOnClickListener(sound_OnClick);
        Sound_1_5_Btn.setOnClickListener(sound_OnClick);
        Sound_2_1_Btn.setOnClickListener(sound_OnClick);
        Sound_2_2_Btn.setOnClickListener(sound_OnClick);
        Sound_2_3_Btn.setOnClickListener(sound_OnClick);
        Sound_2_4_Btn.setOnClickListener(sound_OnClick);
        Sound_2_5_Btn.setOnClickListener(sound_OnClick);
        Sound_3_1_Btn.setOnClickListener(sound_OnClick);
        Sound_3_2_Btn.setOnClickListener(sound_OnClick);
        Sound_3_3_Btn.setOnClickListener(sound_OnClick);
        Sound_3_4_Btn.setOnClickListener(sound_OnClick);
        Sound_3_5_Btn.setOnClickListener(sound_OnClick);
        Sound_4_1_Btn.setOnClickListener(sound_OnClick);
        Sound_4_2_Btn.setOnClickListener(sound_OnClick);
        Sound_4_3_Btn.setOnClickListener(sound_OnClick);
        Sound_4_4_Btn.setOnClickListener(sound_OnClick);
        Sound_4_5_Btn.setOnClickListener(sound_OnClick);
        Sound_5_1_Btn.setOnClickListener(sound_OnClick);
        Sound_5_2_Btn.setOnClickListener(sound_OnClick);
        Sound_5_3_Btn.setOnClickListener(sound_OnClick);
        Sound_5_4_Btn.setOnClickListener(sound_OnClick);
        Sound_5_5_Btn.setOnClickListener(sound_OnClick);


        Play_Stop_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Play_Stop();
            }
        });

        /* 재생 */
        Play_All_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying == false) {
                    try {
                        mPlayer.setDataSource(filename);
                        mPlayer.prepare();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayer.start();
                    Play_All_Btn.setImageResource(R.drawable.pause_36dp);
                    isPlaying = true;
                }
                else {
                    mPlayer.stop();
                    Play_All_Btn.setImageResource(R.drawable.play_36dp);
                    isPlaying = false;
                }
            }
        });
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                Play_All_Btn.setImageResource(R.drawable.play_36dp);
            }
        });


    } // end of onCreate

    /* 폴더생성 */
    private File getSaveFolder() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundPAD");
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

    /* 녹음 */
    public void recordAudio(){
        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT); //오디오가 입력될 장치
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB); // 오디오가 저장될 형태
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); // 오디오 인코더
        recorder.setOutputFile(filename); // 녹음된 오디오 파일이 저장될 위치
        try {
            recorder.prepare();
            recorder.start();
            Toast.makeText(getApplicationContext(),"녹음시작",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /* 녹음 */
    public void stopRecording(){
        if(recorder != null){
            recorder.stop();
            recorder.reset();
            recorder.release();
            Toast.makeText(getApplicationContext(),"녹음저장",Toast.LENGTH_SHORT).show();
            recorder = null;
        }
    }

    public void Play_Stop(){
        sp.stop(soundID_1_1);
        sp.stop(soundID_1_2);
        sp.stop(soundID_1_3);
        sp.stop(soundID_1_4);
        sp.stop(soundID_1_5);
        sp.stop(soundID_2_1);
        sp.stop(soundID_2_2);
        sp.stop(soundID_2_3);
        sp.stop(soundID_2_4);
        sp.stop(soundID_2_5);
        sp.stop(soundID_3_1);
        sp.stop(soundID_3_2);
        sp.stop(soundID_3_3);
        sp.stop(soundID_3_4);
        sp.stop(soundID_3_5);
        sp.stop(soundID_4_1);
        sp.stop(soundID_4_2);
        sp.stop(soundID_4_3);
        sp.stop(soundID_4_4);
        sp.stop(soundID_4_5);
        sp.stop(soundID_5_1);
        sp.stop(soundID_5_2);
        sp.stop(soundID_5_3);
        sp.stop(soundID_5_4);
        sp.stop(soundID_5_5);
    }






    /* 퍼미션 요청 */
    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[i])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast_PermissionDeny();
                            }
                        }
                    }
                } else {
                    showToast_PermissionDeny();
                }
                return;
            }
        }
    }
    private void showToast_PermissionDeny() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
    }

    //뒤로가기 버튼을 두번 연속으로 눌러야 종료되게끔 하는 메소드
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Play_Stop();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            finish();
        }
    }


}//전체 마지막

