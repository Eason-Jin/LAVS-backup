package uoa.lavs.mainframe.simulator.failures;

import uoa.lavs.mainframe.simulator.IntermittentFailurePolicy;

import java.util.Random;

public class RandomPolicy implements IntermittentFailurePolicy {

    private final int threshold;
    private final boolean cumulative;
    private final Random rand = new Random();
    private int value;

    public RandomPolicy() {
        threshold = 5;
        cumulative = false;
        updateValue();
    }

    public RandomPolicy(int threshold, boolean cumulative) {
        this.threshold = threshold;
        this.cumulative = cumulative;
        updateValue();
    }

    @Override
    public boolean canSend(boolean checkOnly) {
        try {
            if (value < threshold) return true;
            value = 0;
            return false;
        } finally {
            if (!checkOnly) {
                updateValue();
            }
        }
    }

    private void updateValue() {
        value = cumulative
                ? value + rand.nextInt(0, 100)
                : rand.nextInt(0, 100);
    }
}
