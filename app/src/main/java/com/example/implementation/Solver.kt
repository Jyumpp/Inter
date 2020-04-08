package com.example.implementation

import com.paramsen.noise.Noise

class Solver {
    fun solve(samples: Int, rate: Float, src: FloatArray, dst: FloatArray): Float {
        val noise = Noise.real(samples)

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

        return (rate * (greatest + 1) / samples)
    }
}