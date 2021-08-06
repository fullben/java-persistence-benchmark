package de.uniba.dsg.jpb.server.service;

public interface TransactionService<T, P> {

  P process(T t);
}
