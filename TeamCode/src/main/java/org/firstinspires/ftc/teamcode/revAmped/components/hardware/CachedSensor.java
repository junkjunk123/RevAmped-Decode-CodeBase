package org.firstinspires.ftc.teamcode.revAmped.components.hardware;
import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import revAmped.components.math.KalmanFilter.KalmanFilterParameters;
import revAmped.components.math.TimeInvariantDiscretizedExtrapolator;

/**
 * Abstract class for a cached sensor that retrieves its value from a supplier.
 * The value is cached after the first retrieval to avoid repeated calls to the supplier.
 *
 * @param <T> the type of the sensor reading
 */
public abstract class CachedSensor<T> implements HwDevice.Sensor<T> {
    private final String id;
    protected T cachedValue;

    /**
     * Constructor for CachedSensor.
     * @param id the unique identifier for the sensor
     */
    public CachedSensor(String id) {
        this.id = id;
    }

    @Override
    public T getReading() {
        return cachedValue != null ? cachedValue : (cachedValue = getRaw());
    }

    @Override
    public String getId() {
        return id;
    };

    @NonNull
    @Override
    public String toString() {
        return getId();
    }

    @Override
    public void update() {
        cachedValue = null;
    }

    protected abstract T getRaw();

    /**
     * A simple cached value holder that caches the value returned by a supplier.
     * @param <T> the type of the cached value
     */
    public static class CachedValue<T> {
        protected final Supplier<T> supplier;
        protected T cachedValue;

        /**
         * Constructor for CachedValue.
         * @param supplier a supplier that provides the value to be cached
         */
        public CachedValue(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        /**
         * Gets the cached value, retrieving it from the supplier if not already cached.
         * @return the cached value
         */
        public T get() {
            if (cachedValue == null) {
                cachedValue = supplier.get();
            }
            return cachedValue;
        }

        /**
         * Updates the cached value, clearing it so that the next call to get() will retrieve a new value.
         */
        public void update() {
            cachedValue = null;
        }
    }

    public abstract static class CachedNumericalValue extends CachedValue<Double> {
        private Double prevValue;
        private Double prevPrevValue;
        private revAmped.components.math.KalmanFilter kalmanFilter;
        private Function<Double, Double> lowPassFilter;

        /**
         * Constructor for CachedValue.
         *
         * @param supplier a supplier that provides the value to be cached
         */
        public CachedNumericalValue(Supplier<Double> supplier) {
            super(supplier);

            Class<?> sensorClass = getClass();

            if (sensorClass.isAnnotationPresent(LowPassFilter.class)) {
                LowPassFilter filterConstants = sensorClass.getAnnotation(LowPassFilter.class);
                lowPassFilter = filterConstants.scale() ? t -> t * filterConstants.alpha() : t -> filterConstants.alpha();
            } else if (sensorClass.isAnnotationPresent(KalmanFilter.class)) {
                KalmanFilter filterConstants = sensorClass.getAnnotation(KalmanFilter.class);
                kalmanFilter = new revAmped.components.math.KalmanFilter(new KalmanFilterParameters(
                        filterConstants.modelCovariance(), filterConstants.dataCovariance()));
            }
        }

        @Override
        public Double get() {
            if (cachedValue == null) {
                Double val = supplier.get();

                if (lowPassFilter == null && kalmanFilter == null || prevPrevValue == null)  {
                    cachedValue = val;
                    return cachedValue;
                }

                if (prevValue == null) {
                    if (kalmanFilter != null) kalmanFilter.reset();
                    cachedValue = val;
                    return cachedValue;
                }

                double projection = prevValue * 2 - prevPrevValue;
                prevPrevValue = prevValue;
                prevValue = cachedValue;

                if (kalmanFilter == null) {
                    double filterScalar = lowPassFilter.apply(val);
                    cachedValue = filterScalar * val + (1 - filterScalar) * projection;
                } else {
                    kalmanFilter.update(val, projection);
                    cachedValue = kalmanFilter.getState();
                }
            }

            return cachedValue;
        }
    }

    public static abstract class CachedNumericalSensor extends CachedSensor<Double> {
        private revAmped.components.math.KalmanFilter kalmanFilter;
        private Function<Double, Double> lowPassFilter;
        private final TimeInvariantDiscretizedExtrapolator extrapolator;

        /**
         * Constructor for CachedSensor.
         *
         * @param id     the unique identifier for the sensor
         */
        public CachedNumericalSensor(String id) {
            super(id);

            Class<?> sensorClass = getClass();

            if (sensorClass.isAnnotationPresent(LowPassFilter.class)) {
                LowPassFilter filterConstants = sensorClass.getAnnotation(LowPassFilter.class);
                lowPassFilter = Objects.requireNonNull(filterConstants).scale() ? t -> t * filterConstants.alpha() : t -> filterConstants.alpha();
                extrapolator = new TimeInvariantDiscretizedExtrapolator(filterConstants.order());
            } else if (sensorClass.isAnnotationPresent(KalmanFilter.class)) {
                KalmanFilter filterConstants = sensorClass.getAnnotation(KalmanFilter.class);
                kalmanFilter = new revAmped.components.math.KalmanFilter(new KalmanFilterParameters(
                        Objects.requireNonNull(filterConstants).modelCovariance(), filterConstants.dataCovariance()));
                extrapolator = new TimeInvariantDiscretizedExtrapolator(filterConstants.order());
            } else {
                extrapolator = null;
            }
        }

        @Override
        public Double getReading() {
            if (cachedValue == null) {
                double val = getRaw();

                if (extrapolator == null) {
                    return returnUnfilteredValue(val, false);
                }

                double projection = extrapolator.extrapolate();

                if (Double.isNaN(projection)) {
                    return returnUnfilteredValue(val, true);
                }

                if (kalmanFilter == null) {
                    double filterScalar = lowPassFilter.apply(val);
                    cachedValue = filterScalar * val + (1 - filterScalar) * projection;
                } else {
                    kalmanFilter.update(val, projection);
                    cachedValue = kalmanFilter.getState();
                }

                extrapolator.update(cachedValue);
            }

            return cachedValue;
        }

        private double returnUnfilteredValue(double val, boolean updateFilter) {
            cachedValue = val;
            if (updateFilter && extrapolator != null) extrapolator.update(cachedValue);
            return cachedValue;
        }
    }
}
