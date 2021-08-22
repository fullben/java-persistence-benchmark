package de.uniba.dsg.jpb.service;

public interface TransactionService<T, P> {

  P process(T t);
}
