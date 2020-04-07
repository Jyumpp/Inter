package com.example.implementation

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource.MIC
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.paramsen.noise.Noise
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.collections.first as first

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val test = findViewById<TextView>(R.id.hello)
        test.text = getString(R.string.hello)
        val button = findViewById<Button>(R.id.connect)

        val freq = 87.35
        val rate = 44000
        val samples = 44000

        val noise = Noise.real(samples)

        /*button.setOnClickListener {

            val mic = AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, 1, AudioFormat.ENCODING_PCM_FLOAT, samples)

            mic.startRecording()

            val src = FloatArray(samples)
            val dst = FloatArray(samples + 2)

            mic.read(src, 0, 10000, AudioRecord.READ_NON_BLOCKING)
            mic.release()
            *//*for (i in src.indices) {
                    src[i] = cos(2 * Math.PI * i * freq / rate).toFloat()
                }*//*

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

            test.text = (rate * (greatest + 1) / samples).toString()

        }*/

        val src = FloatArray(samples)
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

        test.text = (rate * (greatest + 1) / samples).toString()


    }
}
