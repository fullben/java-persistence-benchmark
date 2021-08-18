package de.uniba.dsg.jpb.server.data.gen;

public interface DatabaseWriter<W, E, P, C> {

  void writeAll(DataProvider<W, E, P, C> dataProvider);
}
