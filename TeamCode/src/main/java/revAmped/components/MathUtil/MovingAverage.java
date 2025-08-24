package revAmped.components.MathUtil;

import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * A class that calculates the moving average of a dataset.
 * It maintains a list of data points and computes the average
 * over a specified period.
 */
public class MovingAverage {
    // queue used to store list so that we get the average
    private final ArrayList<Double> Dataset = new ArrayList<Double>();
    private int period;
    private double sum = 0;

    // constructor to initialize period
    public MovingAverage(int period) {
        this.period = period;
    }

    /**
     * Function to add new data in the
     * @param num the new data to be added
     */
    // function to add new data in the
    // list and update the sum so that
    // we get the new mean
    public void addData(double num)
    {
        sum += num;
        Dataset.add(num);

        // Updating size so that length
        // of data set should be equal
        // to period as a normal mean has
        if (Dataset.size() > period) {
            sum -= Dataset.remove(0);
        }
    }

    /**
     * Subtracts a value from each entry of the dataset
     * @param val the value to be subtracted from all elements in the dataset
     */
    public void updateValsRetroactively(double val){
        sum -= val*Dataset.size();
        for (int i = 0; i < Dataset.size(); i ++){
            Dataset.set(i,Dataset.get(i) - val);
        }
    }

    /**
     * Calculates the mean
     * @return the mean
     */
    // function to calculate mean
    public double getMovingAverageForNum() {
        if (!Dataset.isEmpty()) {
            return sum / Dataset.size();
        } else {
            return 0;
        }
    }

    /**
     * Returns the sample size
     * @return the sample size
     */
    public double getSampleSize() {
        return Dataset.size();
    }

    /**
     * Returns if the period is completed
     * @return if the period is completed
     */
    public boolean isCompleted() {
        if (Dataset.size() >= period) {
            return true;
        } else {
            return false;
        }
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    /**
     * Resets the dataset
     */
    public void reset() {
        Dataset.clear();
        sum = 0;
    }

    public void uniqueAddData(double data) {
        if (Dataset.get(Dataset.size() - 1) != data) {
            addData(data);
        }
    }

    public boolean sameAsPrevious(double data) {
        if (Dataset.isEmpty()) {
            return false;
        }

        return data == Dataset.get(Dataset.size() - 1);
    }
}