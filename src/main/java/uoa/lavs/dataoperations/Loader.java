package uoa.lavs.dataoperations;

public interface Loader<T> {
    T loadData(String id);
}