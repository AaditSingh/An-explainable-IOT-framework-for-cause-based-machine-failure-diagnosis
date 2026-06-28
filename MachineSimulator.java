package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class MachineSimulator {
    public static void main(String[] args) {
        int totalTimeSteps = 200;
        int faultInjectTime = 50;
        int propagationDelay = 3;

        double currentLoad = 2000.0;
        double baseVibration = 2.0;
        double baseTemperature = 45.0;

        VirtualSensor vibSensor = new VirtualSensor(0.5, 10.0);
        VirtualSensor tempSensor = new VirtualSensor(0.2, 100.0);

        try (FileWriter writer = new FileWriter("sensor_logs.csv")) {
            writer.write("Timestamp,Load,Vibration_ADC,Temperature_ADC\n");

            double actualVibration = baseVibration;
            double actualTemperature = baseTemperature;

            for (int t = 0; t < totalTimeSteps; t++) {

                if (t >= faultInjectTime) {
                    actualVibration = baseVibration + 4.5;
                }

                if (t >= faultInjectTime + propagationDelay) {
                    actualTemperature += 0.8;
                }

                int vibAdc = vibSensor.readQuantized(actualVibration);
                int tempAdc = tempSensor.readQuantized(actualTemperature);

                writer.write(t + "," + currentLoad + "," + vibAdc + "," + tempAdc + "\n");
            }
            System.out.println("Simulation complete. Data written to sensor_logs.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class VirtualSensor {
    private double noiseLevel;
    private double maxScale;
    private Random random;

    public VirtualSensor(double noiseLevel, double maxScale) {
        this.noiseLevel = noiseLevel;
        this.maxScale = maxScale;
        this.random = new Random();
    }

    public int readQuantized(double realValue) {
        double noisyValue = realValue + (random.nextGaussian() * noiseLevel);
        int adcValue = (int) ((noisyValue / maxScale) * 1023);
        return Math.max(0, Math.min(1023, adcValue));
    }
}