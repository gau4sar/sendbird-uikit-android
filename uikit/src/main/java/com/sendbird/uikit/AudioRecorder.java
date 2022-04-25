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
            File output = File.createTempFile("audio_", ".m4a", context.getFilesDir());
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(output.getAbsolutePath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
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
