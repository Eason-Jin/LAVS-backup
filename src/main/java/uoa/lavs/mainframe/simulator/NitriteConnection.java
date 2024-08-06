package uoa.lavs.mainframe.simulator;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.index.IndexOptions;
import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.mvstore.MVStoreModule;
import uoa.lavs.mainframe.*;
import uoa.lavs.mainframe.messages.All;
import uoa.lavs.mainframe.messages.customer.*;
import uoa.lavs.mainframe.simulator.nitrite.*;

import java.io.IOException;
import java.util.HashMap;

public class NitriteConnection implements Connection {
    private final Nitrite database;
    private final HashMap<Integer, MessageProcessor> messageProcessors = new HashMap<>();
    private long transactionId = 0;

    public NitriteConnection(String dataPath) {
        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath(dataPath)
                .compress(true)
                .build();

        database = Nitrite.builder()
                .loadModule(storeModule)
                .openOrCreate();
        initializeMessageProcessors();
        initializeIndices();
    }

    public NitriteConnection(Nitrite database) {
        this.database = database;
        initializeMessageProcessors();
        initializeIndices();
    }

    public NitriteConnection() {
        this(Nitrite.builder().openOrCreate());
    }

    private void initializeIndices() {
        database.getCollection(Internal.CUSTOMERS_COLLECTION)
                .createIndex(IndexOptions.indexOptions(IndexType.FULL_TEXT), LoadCustomer.Fields.NAME);
    }

    private void initializeMessageProcessors() {
        messageProcessors.put(All.FindCustomer, new FindCustomerProcessor(database));
        messageProcessors.put(All.FindCustomerAdvanced, new FindCustomerAdvancedProcessor(database));
        messageProcessors.put(All.LoadCustomer, new LoadCustomerProcessor(database));
        messageProcessors.put(All.LoadCustomerAddress,
                new LoadCustomerItemProcessor(database,
                        "addresses",
                        MessageErrorStatus.CUSTOMER_ADDRESS_NOT_FOUND,
                        LoadCustomerAddress.Fields.OUTPUT));
        messageProcessors.put(All.LoadCustomerAddresses, new LoadCustomerAddressesProcessor(database));
        messageProcessors.put(All.LoadCustomerEmail,
                new LoadCustomerItemProcessor(database,
                        "emails",
                        MessageErrorStatus.CUSTOMER_EMAIL_NOT_FOUND,
                        LoadCustomerEmail.Fields.OUTPUT));
        messageProcessors.put(All.LoadCustomerEmails, new LoadCustomerEmailsProcessor(database));
        messageProcessors.put(All.LoadCustomerEmployer,
                new LoadCustomerItemProcessor(database,
                        "employers",
                        MessageErrorStatus.CUSTOMER_EMPLOYER_NOT_FOUND,
                        LoadCustomerEmployer.Fields.OUTPUT));
        messageProcessors.put(All.LoadCustomerPhoneNumber,
                new LoadCustomerItemProcessor(database,
                        "phoneNumbers",
                        MessageErrorStatus.CUSTOMER_PHONE_NUMBER_NOT_FOUND,
                        LoadCustomerPhoneNumber.Fields.OUTPUT));
        messageProcessors.put(All.LoadCustomerPhoneNumbers, new LoadCustomerPhoneNumbersProcessor(database));
        messageProcessors.put(All.LoadCustomerNote, new LoadCustomerNoteProcessor(database));
        messageProcessors.put(All.LoadCustomerUpdateStatus, new LoadCustomerUpdateStatusProcessor(database));
        messageProcessors.put(All.LoadLoan, new LoadLoanProcessor(database));
        messageProcessors.put(All.LoadLoanCoborrowers, new LoadLoanCoborrowersProcessor(database));
        messageProcessors.put(All.LoadLoanPayments, new LoadLoanPaymentsProcessor(database));
        messageProcessors.put(All.LoadLoanSummary, new LoadLoanSummaryProcessor(database));
        messageProcessors.put(All.UpdateCustomer, new UpdateCustomerProcessor(database));
        messageProcessors.put(All.UpdateCustomerAddress,
                new UpdateCustomerItemProcessor(database,
                        "addresses",
                        UpdateCustomerAddress.Fields.INPUT,
                        UpdateCustomerAddress.Fields.OUTPUT,
                        LoadCustomerUpdateStatus.Fields.LAST_ADDRESS_CHANGE));
        messageProcessors.put(All.UpdateCustomerEmail,
                new UpdateCustomerItemProcessor(database,
                        "emails",
                        UpdateCustomerEmail.Fields.INPUT,
                        UpdateCustomerEmail.Fields.OUTPUT,
                        LoadCustomerUpdateStatus.Fields.LAST_EMAIL_CHANGE));
        messageProcessors.put(All.UpdateCustomerEmployer,
                new UpdateCustomerItemProcessor(database,
                        "employers",
                        UpdateCustomerEmployer.Fields.INPUT,
                        UpdateCustomerEmployer.Fields.OUTPUT,
                        LoadCustomerUpdateStatus.Fields.LAST_DETAILS_CHANGE));
        messageProcessors.put(All.UpdateCustomerNote, new UpdateCustomerNoteProcessor(database));
        messageProcessors.put(All.UpdateCustomerPhoneNumber,
                new UpdateCustomerItemProcessor(database,
                        "phoneNumbers",
                        UpdateCustomerPhoneNumber.Fields.INPUT,
                        UpdateCustomerPhoneNumber.Fields.OUTPUT,
                        LoadCustomerUpdateStatus.Fields.LAST_PHONE_NUMBER_CHANGE));
        messageProcessors.put(All.UpdateLoan, new UpdateLoanProcessor(database));
        messageProcessors.put(All.UpdateLoanCoborrower, new UpdateLoanCoborrowerProcessor(database));
        messageProcessors.put(All.UpdateLoanStatus, new UpdateLoanStatusProcessor(database));
    }

    @Override
    public Response send(Request request) {
        MessageProcessor processor = messageProcessors.get(request.getRequestType());
        if (processor == null) {
            return MessageErrorStatus.UNKNOWN_MESSAGE.generateEmptyResponse(++transactionId);
        }

        try {
            return processor.process(request, ++transactionId);
        } catch (Exception ex) {
            return new Response(
                    new Status(MessageErrorStatus.INTERNAL_FAILURE.getCode(), ex.getMessage(), transactionId),
                    new HashMap<>());
        }
    }

    @Override
    public void close() throws IOException {
        database.close();
    }

    public class Internal {
        public static final String CUSTOMERS_COLLECTION = "customers";
        public static final String IDS_COLLECTION = "ids";
        public static final String LOANS_COLLECTION = "loans";

        public static final String NEXT_LOAN_ID = "nextLoanId";

        public static final String ITEM_ADDRESSES = "addresses";
        public static final String ITEM_COBORROWERS = "coborrowers";
        public static final String ITEM_EMAILS = "emails";
        public static final String ITEM_EMPLOYERS = "employers";
        public static final String ITEM_NOTES = "notes";
        public static final String ITEM_PHONE_NUMBERS = "phoneNumbers";
    }
}
