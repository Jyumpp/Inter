package com.example.implementation;

public class Test {
    public static void main(String[] args) {
        Solver s = new Solver();

        float freq = 87.35f;
        float rate = 44000f;
        int samples = 44000;

        float[] src = new float[samples];
        float[] dst = new float[samples + 2];

        for (int i = 0; i < samples; i++) {
            src[i] = (float)Math.cos(2 * Math.PI * i * freq / rate);
        }

        //System.out.println(s.solve(samples, rate, src, dst));
    }
}
