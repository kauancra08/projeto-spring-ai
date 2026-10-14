package dio.budgeting.application;

import dio.budgeting.application.output.BalanceSummaryOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBalanceSummaryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    private GetBalanceSummaryUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetBalanceSummaryUseCase(transactionRepository);
    }

    @Test
    @DisplayName("Deve retornar resumo zerado quando não houver transações")
    void shouldReturnEmptySummaryWhenNoTransactionsExist() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        BalanceSummaryOutput summary = useCase.execute();

        assertThat(summary.totalAmount()).isEqualTo(0.0);
        assertThat(summary.totalTransactions()).isEqualTo(0);
        assertThat(summary.categoryBreakdown()).isEmpty();
    }

    @Test
    @DisplayName("Deve calcular corretamente os totais e divisão por categorias")
    void shouldCalculateTotalsAndCategoryBreakdown() {
        var t1 = new Transaction("Supermercado", 15000L, Category.GROCERIES);
        var t2 = new Transaction("Padaria", 3000L, Category.GROCERIES);
        var t3 = new Transaction("Gasolina", 20000L, Category.AUTO);

        when(transactionRepository.findAll()).thenReturn(List.of(t1, t2, t3));

        BalanceSummaryOutput summary = useCase.execute();

        assertThat(summary.totalAmount()).isEqualTo(38000.0);
        assertThat(summary.totalTransactions()).isEqualTo(3);
        assertThat(summary.categoryBreakdown()).containsEntry("GROCERIES", 18000.0);
        assertThat(summary.categoryBreakdown()).containsEntry("AUTO", 20000.0);
        assertThat(summary.highestExpenseCategory()).isEqualTo("AUTO");
    }
}
