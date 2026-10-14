package dio.budgeting.domain;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findAllByCategory(Category category);

    List<Transaction> findAll();

    Optional<Transaction> findById(TransactionId id);

    void deleteById(TransactionId id);
}
