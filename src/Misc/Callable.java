package Misc;

public interface Callable<T, K> {
  public T call(K k);
}