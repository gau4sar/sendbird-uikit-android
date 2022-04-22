package com.sendbird.uikit;

import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {

    private MediaRecorder recorder = null;

    public File startRecording(Context context) {
        try {
            File output = File.createTempFile("audio_", ".mp3", context.getFilesDir());
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(output.getAbsolutePath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
            recorder.start();
            return output;
        } catch (IOException |  RuntimeException e) {
            return null;
        }
    }

    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }
}
