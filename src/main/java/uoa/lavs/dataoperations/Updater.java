package uoa.lavs.dataoperations;

public interface Updater<T> {
  void updateData(String id, T data);
}
