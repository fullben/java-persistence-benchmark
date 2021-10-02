package de.uniba.dsg.wss.service;

/**
 * Defines the base API for the services which are used to implement the business transactions the
 * server is capable of performing.
 *
 * @param <T> the type of object accepted for processing by this service
 * @param <P> the type of object returned as result of the processing performed by this service
 * @author Benedikt Full
 */
public interface TransactionService<T, P> {

  P process(T t);
}
