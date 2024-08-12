package uoa.lavs.dataoperations;

import java.util.List;

public interface Finder<T> {
  List<T> findData(String id);
}
