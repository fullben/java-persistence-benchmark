package de.uniba.dsg.wss.service.ms;

/**
 * Custom runtime exception for the application.
 *
 * @author Johannes Manner
 */
public class MsTransactionException extends RuntimeException{
    public MsTransactionException(String message) {
        super(message);
    }
}
