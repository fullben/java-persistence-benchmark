package de.uniba.dsg.wss.service;

/**
 * Can be thrown whenever some sort of issue prevents a data transaction involving MicroStream from
 * successful completion.
 *
 * @author Johannes Manner
 */
public class MsTransactionException extends RuntimeException {

  private static final long serialVersionUID = 5089174648043655677L;

  public MsTransactionException(String message) {
    super(message);
  }
}
