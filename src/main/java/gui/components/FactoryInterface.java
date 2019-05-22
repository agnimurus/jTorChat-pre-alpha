package gui.components;

public interface FactoryInterface<T> {

  /**
   * Create something from a descriptor
   * @param descriptor e.g. a name or a title
   * @return
   */
  T create(String descriptor);
}
