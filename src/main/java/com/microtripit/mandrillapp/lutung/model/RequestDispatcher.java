/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Blueknow Lutung
 *
 * (c) Copyright 2009-2021 Blueknow, S.L.
 *
 * ALL THE RIGHTS ARE RESERVED
 */
package com.microtripit.mandrillapp.lutung.model;

import com.microtripit.mandrillapp.lutung.util.FeatureDetector;

import java.io.IOException;
import java.util.Optional;
import java.util.ServiceLoader;

@FunctionalInterface
public interface RequestDispatcher<R> {

    /**
     * @param requestModel the request model to be executed (with the request)
     * @param <O> the response result
     * @return a suitable response
     * @throws MandrillApiError
     * @throws IOException
     */
    <O> O execute(final RequestModel<O,R> requestModel) throws MandrillApiError, IOException;

    static <R> RequestDispatcher<R> noOp() {
        return new RequestDispatcher<>( ) {
            @Override
            public <O> O execute(final RequestModel<O,R> requestModel) {
                return null;
            }
        };
    }

    interface Factory {

        @SuppressWarnings("rawtypes")
		RequestDispatcher createRequestDispatcher();

        static Factory noOp() {
            return RequestDispatcher::noOp;
        }

    }

    @SuppressWarnings("unchecked")
	static <R> RequestDispatcher<R> lookup() {
        RequestDispatcher.Factory instance = null;
        try {
            final var providers = ServiceLoader.load (Factory.class).iterator ();
            while (providers.hasNext()) {
                //retrieve the implementation, we only accept ONE
                final var spi = providers.next();
                if (instance != null) {
                    throw new IllegalStateException("Multiple RequestDispatcher implementations found: "
                                                            + spi.getClass().getName() + " and "
                                                            + instance.getClass().getName());
                }
                instance = spi;
            }
        } catch (final Throwable sce) {//[30-03-2020] if something wrong happens we must provide a NoOp Provider NOSONAR

           //return a default file
            instance = defaultFactory();
        }
        instance = Optional.ofNullable(instance).orElseGet(RequestDispatcher::defaultFactory);
        return instance.createRequestDispatcher();

    }

    private static RequestDispatcher.Factory defaultFactory() {
        return FeatureDetector.isClassPresent("org.apache.http.client.HttpClient") ? new ApacheHttpClientRequestDispatcher.Factory(): Factory.noOp();
    }
}
