package com.microtripit.mandrillapp.lutung.logging;

import static com.microtripit.mandrillapp.lutung.util.FeatureDetector.isCommonsLoggingAvailable;

/**
 * @author aldenquimby@gmail.com
 */
public class LoggerFactory {

    /**
     * Get a named logger instance.
     *
     * @param clazz class from which a log name will be derived
     * @return the logger
     */
    public static Logger getLogger(final Class<?> clazz) {
        if (isCommonsLoggingAvailable()) {
            return new CommonsLogger(clazz);
        }
        else {
            return new NoOpLogger();
        }
    }

}