package de.uniba.dsg.jpb.transaction;

public interface TransactionService<T, P> {

  P process(T t);
}
