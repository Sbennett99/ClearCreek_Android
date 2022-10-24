package app.clearcreek.catering.data.model;

public enum PaymentMethod {
    CASH(1, "Cash"),
    CREDIT(2, "Credit"),
    DEBIT(3, "Debit");

    private long id;
    private String name;

    PaymentMethod(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
