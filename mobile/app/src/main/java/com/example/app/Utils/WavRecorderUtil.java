package com.example.app.Utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class WavRecorderUtil {
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecord;
    private int bufferSize;
    private Thread recordingThread;
    private boolean isRecording = false;
    private String currentFilePath;

    private Context context;

    public WavRecorderUtil(Context context) {
        this.context = context;
    }

    // 录音前检查权限
    public static boolean checkAndRequestPermissions(Context context, AppCompatActivity activity) {
        boolean hasAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        boolean hasStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!hasAudio || !hasStorage) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);
            return false;
        }
        return true;
    }


    public String startRecording() throws IOException {
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

        // 创建录音文件
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File dir = context.getExternalFilesDir("AppRecordings");
        if (!dir.exists()) dir.mkdirs();
        File wavFile = new File(dir, "REC_" + timeStamp + ".wav");
        currentFilePath = wavFile.getAbsolutePath();

        // 先创建空文件并写入WAV头
        writeWavHeader(new FileOutputStream(wavFile));

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize);

        audioRecord.startRecording();
        isRecording = true;

        // 启动录音线程
        recordingThread = new Thread(new RecordingRunnable(wavFile), "AudioRecorder Thread");
        recordingThread.start();

        return currentFilePath;
    }

    public void stopRecording() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }

        if (recordingThread != null) {
            try {
                recordingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recordingThread = null;
        }

        // 更新WAV文件头信息
        updateWavHeader();
    }

    private File getRecordingDirectory() {
        File dir = new File(Environment.getExternalStorageDirectory(), "AppRecordings");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private class RecordingRunnable implements Runnable {
        private final File outputFile;

        public RecordingRunnable(File outputFile) {
            this.outputFile = outputFile;
        }

        @Override
        public void run() {
            try (FileOutputStream fos = new FileOutputStream(outputFile, true)) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
                byte[] audioData = new byte[bufferSize];

                while (isRecording) {
                    int read = audioRecord.read(audioData, 0, bufferSize);
                    if (read > 0) {
                        fos.write(audioData, 0, read);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeWavHeader(FileOutputStream out) throws IOException {
        // WAV头格式 (44 bytes)
        out.write(new byte[] {
                'R', 'I', 'F', 'F',     // ChunkID
                0, 0, 0, 0,             // ChunkSize (临时填0)
                'W', 'A', 'V', 'E',     // Format
                'f', 'm', 't', ' ',     // Subchunk1ID
                16, 0, 0, 0,            // Subchunk1Size (16 for PCM)
                1, 0,                   // AudioFormat (1 for PCM)
                1, 0,                   // NumChannels (1 for mono)
                (byte)(SAMPLE_RATE & 0xff), (byte)((SAMPLE_RATE >> 8) & 0xff),
                (byte)((SAMPLE_RATE >> 16) & 0xff), (byte)((SAMPLE_RATE >> 24) & 0xff), // SampleRate
                (byte)((SAMPLE_RATE * 2) & 0xff), (byte)((SAMPLE_RATE * 2 >> 8) & 0xff),
                (byte)((SAMPLE_RATE * 2 >> 16) & 0xff), (byte)((SAMPLE_RATE * 2 >> 24) & 0xff), // ByteRate
                2, 0,                   // BlockAlign
                16, 0,                  // BitsPerSample
                'd', 'a', 't', 'a',     // Subchunk2ID
                0, 0, 0, 0              // Subchunk2Size (临时填0)
        });
    }

    private void updateWavHeader() {
        try (RandomAccessFile wavFile = new RandomAccessFile(currentFilePath, "rw")) {
            long fileSize = wavFile.length();

            // 更新ChunkSize (file size - 8)
            wavFile.seek(4);
            wavFile.writeInt((int)(fileSize - 8));

            // 更新Subchunk2Size (file size - 44)
            wavFile.seek(40);
            wavFile.writeInt((int)(fileSize - 44));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }
}
