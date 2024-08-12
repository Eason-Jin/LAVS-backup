package uoa.lavs.dataoperations;

import uoa.lavs.models.Customer;

public interface Updater {
    void updateData(String id, Customer customer);
}
