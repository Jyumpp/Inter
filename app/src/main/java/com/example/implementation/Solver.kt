package com.example.implementation

import com.paramsen.noise.Noise
import kotlin.math.abs

class Solver {
    private fun indexToFreq(rate: Int, samples: Int, index: Int): Float {
        return rate * (index + 1) / samples.toFloat()
    }

    fun solve(samples: Int, rate: Int, src: FloatArray): Pair<Float, Float> {
        val noise = Noise.real(samples)
        val dst = FloatArray(samples + 2)

        val fft: FloatArray = noise.fft(src, dst)

        val pairs: MutableList<Pair<Float, Float>> = mutableListOf()

        for (i in 2 until fft.size step 2) {
            val real = fft[i]
            val imag = fft[i + 1]

            pairs.add(Pair(real, imag))
        }

        var greatest: Int = 0
        var sum = 0f
        for (i in pairs.indices) {
            if (pairs[i].first > pairs[greatest].first && indexToFreq(rate, samples, i) < 900) {
                greatest = i
            }
        }

        for (i in src) {
            sum += abs(i)
        }



        return indexToFreq(rate, samples, greatest) to sum / src.size
    }
}