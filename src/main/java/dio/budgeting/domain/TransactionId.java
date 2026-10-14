package dio.budgeting.domain;

import java.util.UUID;

public record TransactionId(UUID uuid) {
    public TransactionId {
        if (uuid == null) {
            throw new IllegalArgumentException("TransactionId UUID cannot be null");
        }
    }

    public TransactionId() {
        this(UUID.randomUUID());
    }

    public static TransactionId of(String value) {
        return new TransactionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
