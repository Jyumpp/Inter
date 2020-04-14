package com.example.implementation

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource.MIC
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.paramsen.noise.Noise
import org.jetbrains.anko.doAsync
import java.util.*
import java.util.jar.Manifest
import kotlin.math.cos
import kotlin.math.sin
import kotlin.collections.first as first

class MainActivity : AppCompatActivity() {
    private val TAG = "RecordPerm"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupPermissions()

        val test = findViewById<TextView>(R.id.hello)
        test.text = getString(R.string.hello)
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
                if (!active) {
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

                    for (i in 0 until 60) {
                        active = true
                        mic.read(src, 0, samples, AudioRecord.READ_BLOCKING)
                        val foundFreq = s.solve(samples, rate, src)
                        if (foundFreq > 400 - range && foundFreq < 400 + range) {
                            runOnUiThread { test.text = getString(R.string.freq400) }
                        } else if (foundFreq > 600 - range && foundFreq < 600 + range) {
                            runOnUiThread { test.text = getString(R.string.freq600) }
                        } else if (foundFreq > 800 - range && foundFreq < 800 + range) {
                            runOnUiThread { test.text = getString(R.string.freq800) }
                        } else {
                            runOnUiThread { test.text = getString(R.string.freqnone) }
                        }

                    }
                    active = false
                    //test.text = s.solve(samples, rate, src).toString()

                    mic.release()
                }
            }
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
        test.text = "Ready!"
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
