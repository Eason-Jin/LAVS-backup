package uoa.lavs.mainframe.messages.loan;

import uoa.lavs.mainframe.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateLoan implements Message, MessageDescription {
    public static final int REQUEST_CODE = 2201;
    public static final String[] INPUT = {"id", "customerId", "principal", "rate.value", "rate.type", "date", "period", "payment.amount", "payment.freq", "compounding"};
    public static final String[] OUTPUT = {"id", "customerId", "customerName", "status", "principal", "rate.value", "rate.type", "date", "period", "term", "payment.amount", "payment.freq", "compounding"};

    private Response response;
    private String loanId;
    private String customerId;
    private Double principal;
    private Double rateValue;
    private RateType rateType;
    private LocalDate startDate;
    private Integer period;
    private Double paymentAmount;
    private Frequency paymentFrequency;
    private Frequency compounding;

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (loanId != null) request.setValue("id", loanId.toString());
        if (customerId != null) request.setValue("customerId", customerId.toString());
        if (principal != null) request.setValue("principal", principal.toString());
        if (rateValue != null) request.setValue("rate.value", rateValue.toString());
        switch (rateType)
        {
            case Floating:
                request.setValue("rate.type", "1");
                break;
            case Fixed:
                request.setValue("rate.type", "2");
                break;
            case InterestOnly:
                request.setValue("rate.type", "3");
                break;
        }
        if (startDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            request.setValue("date", formatter.format(startDate));
        }
        if (period != null) request.setValue("period", period.toString());
        if (paymentAmount != null) request.setValue("payment.amount", paymentAmount.toString());
        switch (paymentFrequency)
        {
            case Weekly:
                request.setValue("payment.freq", "2");
                break;
            case Fortnightly:
                request.setValue("payment.freq", "3");
                break;
            case Monthly:
                request.setValue("payment.freq", "4");
                break;
        }
        switch (compounding)
        {
            case Weekly:
                request.setValue("compounding", "2");
                break;
            case Monthly:
                request.setValue("compounding", "4");
                break;
            case Yearly:
                request.setValue("compounding", "7");
                break;
        }
        response = connection.send(request);
        return response.getStatus();
    }

    // sets loan id [loanId]
    public void setLoanId(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 14) {
            throw new IllegalArgumentException("loanId is too long - max length is 14");
        }
        loanId = value;
     }

    // sets customer id [customerId]
    public void setCustomerId(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 10) {
            throw new IllegalArgumentException("customerId is too long - max length is 10");
        }
        customerId = value;
     }

    // sets principal [principal]
    public void setPrincipal(Double value)
     {
        principal = value;
     }

    // sets rate value [rateValue]
    public void setRateValue(Double value)
     {
        rateValue = value;
     }

    // sets rate type [rateType]
    public void setRateType(RateType value)
     {
        rateType = value;
     }

    // sets start date [startDate]
    public void setStartDate(LocalDate value)
     {
        startDate = value;
     }

    // sets period [period]
    public void setPeriod(Integer value)
     {
        period = value;
     }

    // sets payment amount [paymentAmount]
    public void setPaymentAmount(Double value)
     {
        paymentAmount = value;
     }

    // sets payment frequency [paymentFrequency]
    public void setPaymentFrequency(Frequency value)
     {
        paymentFrequency = value;
     }

    // sets compounding [compounding]
    public void setCompounding(Frequency value)
     {
        compounding = value;
     }

    // gets loan id [id] from server
    public String getLoanIdFromServer()
     {
        String key = "id";
        return response.getValue(key);
     }

    // gets customer id [customerId] from server
    public String getCustomerIdFromServer()
     {
        String key = "customerId";
        return response.getValue(key);
     }

    // gets customer name [customerName] from server
    public String getCustomerNameFromServer()
     {
        String key = "customerName";
        return response.getValue(key);
     }

    // gets status from server
    public String getStatusFromServer()
     {
        String key = "status";
        return response.getValue(key);
     }

    // gets principal from server
    public Double getPrincipalFromServer()
     {
        String key = "principal";
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
     }

    // gets rate value [rate.value] from server
    public Double getRateValueFromServer()
     {
        String key = "rate.value";
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
     }

    // gets rate type [rate.type] from server
    public RateType getRateTypeFromServer()
     {
        String key = "rate.type";
        String value = response.getValue(key);
        switch (value)
        {
            case "1":
                return RateType.Floating;
            case "2":
                return RateType.Fixed;
            case "3":
                return RateType.InterestOnly;
        }
        return RateType.Unknown;
     }

    // gets start date [date] from server
    public LocalDate getStartDateFromServer()
     {
        String key = "date";
        String value = response.getValue(key);
        if (value == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(value, formatter);
     }

    // gets period from server
    public Integer getPeriodFromServer()
     {
        String key = "period";
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets term from server
    public Integer getTermFromServer()
     {
        String key = "term";
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets payment amount [payment.amount] from server
    public Double getPaymentAmountFromServer()
     {
        String key = "payment.amount";
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
     }

    // gets payment frequency [payment.freq] from server
    public Frequency getPaymentFrequencyFromServer()
     {
        String key = "payment.freq";
        String value = response.getValue(key);
        switch (value)
        {
            case "2":
                return Frequency.Weekly;
            case "3":
                return Frequency.Fortnightly;
            case "4":
                return Frequency.Monthly;
        }
        return Frequency.Unknown;
     }

    // gets compounding from server
    public Frequency getCompoundingFromServer()
     {
        String key = "compounding";
        String value = response.getValue(key);
        switch (value)
        {
            case "2":
                return Frequency.Weekly;
            case "4":
                return Frequency.Monthly;
            case "7":
                return Frequency.Yearly;
        }
        return Frequency.Unknown;
     }

    @Override
    public String[] getInputFields()
    {
        return INPUT;
    }

    @Override
    public String[] getOutputFields()
    {
        return OUTPUT;
    }
}
