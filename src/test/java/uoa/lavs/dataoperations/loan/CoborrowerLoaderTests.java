package uoa.lavs.dataoperations.loan;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;

public class CoborrowerLoaderTests {

    @Test
    public void testLoadCoborrowersLocally() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        String loanId = "1-01";

        List<String> coborrowers = CoborrowerLoader.loadFromDatabase(loanId);

        assert coborrowers.size() > 0;
    }

    @Test
    public void testLoadCoborrowersLocallyFailure() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        String loanId = "invalid-id";

        List<String> coborrowers = CoborrowerLoader.loadFromDatabase(loanId);

        assert coborrowers.size() == 0;
    }

    @Test
    public void testLoadCoborrowersFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        String loanId = "123-09";

        List<String> coborrowers = CoborrowerLoader.loadFromMainframe(loanId);

        assert coborrowers.size() > 0;
    }

    @Test
    public void testLoadCoborrowersFailure() {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        String loanId = "invalid-id";

        assertThrows(
                Exception.class,
                () -> {
                    CoborrowerLoader.loadFromMainframe(loanId);
                });
    }

    @Test
    public void testLoadCoborrowersFailureDatabase() {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        String loanId = "invalid-id";

        List<String> coborrowers = CoborrowerLoader.loadData(loanId);

        assert coborrowers.size() == 0;
    }

    @Test
    public void testLoadCoborrowersDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        String loanId = "1-01";

        List<String> coborrowers = CoborrowerLoader.loadData(loanId);

        assert coborrowers.size() > 0;
    }

    @Test
    public void testLoadCoborrowersMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        String loanId = "123-09";

        List<String> coborrowers = CoborrowerLoader.loadData(loanId);

        assert coborrowers.size() > 0;
    }

    @Test
    public void testLoadCoborrowersFailureMainframe() {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        String loanId = "invalid-id";

        List<String> coborrowers = CoborrowerLoader.loadData(loanId);

        assert coborrowers.size() == 0;
    }
}
