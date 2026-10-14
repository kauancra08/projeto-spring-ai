package dio.budgeting.domain;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Transaction {
    private final TransactionId id;
    private final String description;
    private final long amount;
    private final Category category;
    private final Instant createdAt;

    public Transaction(TransactionId id, String description, long amount, Category category, Instant createdAt) {
        validate(description, amount, category);
        this.id = id != null ? id : new TransactionId();
        this.description = description.trim();
        this.amount = amount;
        this.category = category;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public Transaction(TransactionId id, String description, long amount, Category category) {
        this(id, description, amount, category, Instant.now());
    }

    public Transaction(String description, long amount, Category category) {
        this(new TransactionId(), description, amount, category, Instant.now());
    }

    private static void validate(String description, long amount, Category category) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição da transação não pode ser vazia.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }
        if (category == null) {
            throw new IllegalArgumentException("A categoria da transação é obrigatória.");
        }
    }
}
