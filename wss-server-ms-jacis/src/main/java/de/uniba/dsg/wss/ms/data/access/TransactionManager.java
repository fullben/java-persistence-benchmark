package de.uniba.dsg.wss.ms.data.access;

import static java.util.Objects.requireNonNull;

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
  private final JacisContainer container;
  private final int attempts;
  private final int delay;

  public TransactionManager(JacisContainer container) {
    this(container, 5, 100);
  }

  public TransactionManager(JacisContainer container, int attempts, int delay) {
    this.container = requireNonNull(container, "Container must not be null");
    this.attempts = requireValidAttempts(attempts);
    this.delay = requireValidBackoff(delay);
  }

  public JacisContainer getContainer() {
    return container;
  }

  public int getAttempts() {
    return attempts;
  }

  public int getDelay() {
    return delay;
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
          return result;
        } catch (Throwable e) {
          txException = e;
          throw e;
        } finally {
          if (tx != null) {
            try {
              tx.rollback();
            } catch (Throwable rollbackException) {
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
          throw e;
        } else {
          try {
            Thread.sleep(delay);
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
    throw new JacisTxCommitException(
        "Unable to commit transaction after " + maxAttempts + " tries");
  }

  private int requireValidAttempts(int attempts) {
    if (attempts < 1) {
      throw new IllegalArgumentException("Max tries must be greater than zero");
    }
    return attempts;
  }

  private int requireValidBackoff(int backoff) {
    if (backoff < 1) {
      throw new IllegalArgumentException("Backoff millis must be greater than zero");
    }
    return backoff;
  }
}
