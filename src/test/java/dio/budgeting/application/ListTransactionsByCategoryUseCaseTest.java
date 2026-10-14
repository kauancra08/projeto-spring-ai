package dio.budgeting.application;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListTransactionsByCategoryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    private ListTransactionsByCategoryUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListTransactionsByCategoryUseCase(transactionRepository);
    }

    @Test
    @DisplayName("Deve filtrar as transações pela categoria fornecida")
    void shouldFilterTransactionsByCategory() {
        var t1 = new Transaction("Remédio", 5000L, Category.PHARMA);
        when(transactionRepository.findAllByCategory(Category.PHARMA)).thenReturn(List.of(t1));

        List<TransactionOutput> results = useCase.execute(Category.PHARMA);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).category()).isEqualTo("PHARMA");
        assertThat(results.get(0).description()).isEqualTo("Remédio");
    }
}
