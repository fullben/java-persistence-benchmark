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
 * convenience methods for running transactions, some necessary for in the context of this
 * application are missing. For example, the container has no method which allows a transaction with
 * a return value being retried for a certain amount of times.
 *
 * @author Benedikt Full
 */
public class TransactionManager {

  private static final Logger LOG = LogManager.getLogger(TransactionManager.class);
  private JacisContainer container;
  private int maxTries;
  private boolean closed;

  public TransactionManager(JacisContainer container) {
    this.container = container;
    maxTries = 1;
    closed = false;
  }

  public int getMaxTries() {
    return maxTries;
  }

  public TransactionManager setMaxTries(int maxTries) {
    if (maxTries < 1) {
      throw new IllegalArgumentException("Max tries must be greater than zero");
    }
    this.maxTries = maxTries;
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
    final int maxTries = this.maxTries;
    int performedTries = 0;
    while ((maxTries - performedTries++) > 0) {
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
        if (performedTries == maxTries) {
          LOG.error("Unable to complete transaction after having tried {} times", performedTries);
          close();
          throw e;
        } else {
          try {
            Thread.sleep(10L * performedTries);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
          }
          LOG.warn(
              "Retrying transaction (performed tries: {}, max tries: {}) after having encountered exception:",
              performedTries,
              maxTries,
              e);
        }
      }
    }
    close();
    throw new JacisTxCommitException("Unable to commit transaction after " + maxTries + " tries");
  }

  private void close() {
    if (closed) {
      return;
    }
    container = null;
    closed = true;
  }
}