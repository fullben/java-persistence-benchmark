package de.uniba.dsg.jpb.data.gen;

public interface DataWriter<W, E, P, C> {

  void writeAll(DataProvider<W, E, P, C> dataProvider);
}
