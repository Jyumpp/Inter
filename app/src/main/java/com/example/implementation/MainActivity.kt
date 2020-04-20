package com.example.implementation

import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.paramsen.noise.Noise
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private val TAG = "RecordPerm"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupPermissions()

        val status = findViewById<TextView>(R.id.status)
        status.text = getString(R.string.ready)

        val switch = findViewById<Switch>(R.id.switch1)
        switch.isChecked = false

        val volume = findViewById<TextView>(R.id.volume)
        volume.text = getString(R.string.volume)

        val volumeBar = findViewById<ProgressBar>(R.id.volumeBar)
        volumeBar.progress = 0

        val startButton = findViewById<Button>(R.id.connect)
        val stopButton = findViewById<Button>(R.id.disconnect)

        val rate = 44100
        val samples = 44100
        val range = 25

        startButton.setOnClickListener {
            doAsync {
                if (!switch.isChecked) {
                    runOnUiThread { switch.isChecked = true }
                    val s = Solver()
                    val mic = AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        rate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_FLOAT,
                        samples * 4
                    )
                    Thread.sleep(500)
                    val src = FloatArray(samples)
                    mic.startRecording()

                    while (switch.isChecked) {

                        mic.read(src, 0, samples, AudioRecord.READ_BLOCKING)
                        val found = s.solve(samples, rate, src)
                        val foundFreq = found.first
                        val foundVol = sqrt(found.second * propBar.progress * 100)
                        if (foundFreq > 400 - range && foundFreq < 400 + range) {
                            runOnUiThread { status.text = getString(R.string.freq400) }
                        } else if (foundFreq > 600 - range && foundFreq < 600 + range) {
                            runOnUiThread { status.text = getString(R.string.freq600) }
                        } else if (foundFreq > 800 - range && foundFreq < 800 + range) {
                            runOnUiThread { status.text = getString(R.string.freq800) }
                        } else {
                            runOnUiThread {
                                status.text = getString(R.string.freqnone)
                                volume.text = getString(R.string.volume)
                                volumeBar.progress = 0
                            }
                            continue
                        }

                        runOnUiThread {
                            volumeBar.progress = (foundVol).toInt()
                            val volString: String = when {
                                foundVol < 37.5 -> "1"
                                foundVol < 62.5 -> "2"
                                else -> "3"
                            }
                            volume.text = volString
                        }
                    }
                    runOnUiThread {
                        switch.isChecked = false
                        volume.text = getString(R.string.volume)
                        volumeBar.progress = 0
                    }
                    //test.text = s.solve(samples, rate, src).toString()
                    runOnUiThread { status.text = getString(R.string.ready) }
                    mic.release()
                }
            }
        }

        stopButton.setOnClickListener {
            switch.isChecked = false
        }
        
        status.text = "Ready!"
    }


    private fun setupPermissions() {
        val permission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                101
            )
        }
    }

}
