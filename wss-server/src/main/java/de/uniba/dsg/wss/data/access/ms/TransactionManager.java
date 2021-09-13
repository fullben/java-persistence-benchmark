package de.uniba.dsg.wss.data.access.ms;

import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jacis.container.JacisContainer;
import org.jacis.exception.JacisStaleObjectException;
import org.jacis.exception.JacisTxCommitException;
import org.jacis.plugin.txadapter.local.JacisLocalTransaction;

/**
 * Helper for running JACIS transactions. While the {@link JacisContainer} provides various
 * convenience methods for running transactions, some necessary in the context of this application
 * are missing. For example, the container has no method which allows a transaction with a return
 * value being retried for a certain amount of times.
 *
 * @author Benedikt Full
 */
public class TransactionManager {

  private static final Logger LOG = LogManager.getLogger(TransactionManager.class);
  private JacisContainer container;
  private int attempts;
  private int backoff;
  private boolean closed;

  public TransactionManager(JacisContainer container) {
    this.container = container;
    attempts = 3;
    backoff = 100;
    closed = false;
  }

  public int getAttempts() {
    return attempts;
  }

  /**
   * Sets the number of attempts for which the transaction executed with this manager is tried
   * before failing.
   *
   * @param attempts the max number of times the transaction can be executed for, must be a positive
   *     value
   * @return this manager
   */
  public TransactionManager setAttempts(int attempts) {
    if (attempts < 1) {
      throw new IllegalArgumentException("Max tries must be greater than zero");
    }
    this.attempts = attempts;
    return this;
  }

  public int getBackoff() {
    return backoff;
  }

  /**
   * Sets the delay between any transaction execution attempts.
   *
   * @param backoffMillis the delay between the attempts, a milliseconds value, must be greater than
   *     zero
   * @return this manager
   */
  public TransactionManager setBackoff(int backoffMillis) {
    if (backoffMillis < 1) {
      throw new IllegalArgumentException("Backoff millis must be greater than zero");
    }
    backoff = backoffMillis;
    return this;
  }

  public <T> T commit(Supplier<T> transaction) {
    return executeAndCommit(transaction);
  }

  public void commit(Runnable transaction) {
    executeAndCommit(
        () -> {
          transaction.run();
          return null;
        });
  }

  private <T> T executeAndCommit(Supplier<T> transaction) {
    if (closed) {
      throw new IllegalStateException("Transaction manager is closed");
    }
    final int maxAttempts = this.attempts;
    int performedAttempts = 0;
    while ((maxAttempts - performedAttempts++) > 0) {
      try {
        JacisLocalTransaction tx = container.beginLocalTransaction();
        Throwable txException = null;
        try {
          T result = transaction.get();
          tx.prepare();
          tx.commit();
          tx = null;
          close();
          return result;
        } catch (Throwable e) {
          txException = e;
          throw e;
        } finally {
          if (tx != null) {
            try {
              tx.rollback();
            } catch (Throwable rollbackException) {
              close();
              RuntimeException exceptionToThrow =
                  new RuntimeException("Rollback failed after " + txException, txException);
              exceptionToThrow.addSuppressed(rollbackException);
              // noinspection ThrowFromFinallyBlock
              throw exceptionToThrow;
            }
          }
        }
      } catch (JacisStaleObjectException e) {
        if (performedAttempts == maxAttempts) {
          LOG.error(
              "Unable to complete transaction after having attempted it for {} times",
              performedAttempts);
          close();
          throw e;
        } else {
          try {
            Thread.sleep(backoff);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
          }
          LOG.warn(
              "Retrying transaction (performed attempts: {}, max attempts: {}) after having encountered exception: {}: {}",
              performedAttempts,
              maxAttempts,
              e.getClass().getName(),
              e.getMessage());
        }
      }
    }
    close();
    throw new JacisTxCommitException(
        "Unable to commit transaction after " + maxAttempts + " tries");
  }

  private void close() {
    if (closed) {
      return;
    }
    container = null;
    closed = true;
  }
}
