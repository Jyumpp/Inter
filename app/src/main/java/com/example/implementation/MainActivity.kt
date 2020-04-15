package com.example.implementation

import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.paramsen.noise.Noise
import org.jetbrains.anko.doAsync

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

        val startButton = findViewById<Button>(R.id.connect)
        val stopButton = findViewById<Button>(R.id.disconnect)

        val freq = 80
        val rate = 44100
        val samples = 44100
        val range = 25
        val noise = Noise.real(samples)

        var active = false
        startButton.setOnClickListener {
            doAsync {
                if (!switch.isChecked) {
                    active = true
                    runOnUiThread { switch.isChecked=true }
                    val s = Solver()
                    val mic = AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        rate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_FLOAT,
                        samples * 4
                    )
                    val src = FloatArray(samples)
                    mic.startRecording()

                    while(switch.isChecked) {

                        mic.read(src, 0, samples, AudioRecord.READ_BLOCKING)
                        val foundFreq = s.solve(samples, rate, src)
                        if (foundFreq > 400 - range && foundFreq < 400 + range) {
                            runOnUiThread { status.text = getString(R.string.freq400) }
                        } else if (foundFreq > 600 - range && foundFreq < 600 + range) {
                            runOnUiThread { status.text = getString(R.string.freq600) }
                        } else if (foundFreq > 800 - range && foundFreq < 800 + range) {
                            runOnUiThread { status.text = getString(R.string.freq800) }
                        } else {
                            runOnUiThread { status.text = getString(R.string.freqnone) }
                        }

                    }
                    active = false
                    runOnUiThread { switch.isChecked=false }
                    //test.text = s.solve(samples, rate, src).toString()
                    runOnUiThread { status.text = getString(R.string.ready) }
                    mic.release()
                }
            }
        }

        stopButton.setOnClickListener{
            switch.isChecked=false
        }

        /*val src = FloatArray(samples)
        val dst = FloatArray(samples + 2)


        for (i in src.indices) {
            src[i] = cos(2 * Math.PI * i * freq / rate).toFloat()
        }

        val fft: FloatArray = noise.fft(src, dst)

        val pairs: MutableList<Pair<Float, Float>> = mutableListOf()

        for (i in 2 until fft.size step 2) {
            val real = fft[i]
            val imag = fft[i + 1]

            pairs.add(Pair(real, imag))
        }

        var greatest: Int = 0
        for (i in pairs.indices) {
            if (pairs[i].first > pairs[greatest].first) {
                greatest = i
            }
        }

        test.text = (rate * (greatest + 1) / samples).toString()*/
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
