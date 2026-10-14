package dio.budgeting.application;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersistTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    private PersistTransactionUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new PersistTransactionUseCase(transactionRepository);
    }

    @Test
    @DisplayName("Deve persistir uma transação com sucesso quando os dados forem válidos")
    void shouldPersistTransactionSuccessfully() {
        var input = new PersistTransactionInput("Almoço executivo", 4500L, Category.GROCERIES);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionOutput output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.description()).isEqualTo("Almoço executivo");
        assertThat(output.category()).isEqualTo("GROCERIES");
        assertThat(output.value()).isEqualTo(4500.0);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualTo(4500L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar transação com valor zero ou negativo")
    void shouldThrowExceptionWhenAmountIsZeroOrNegative() {
        var inputZero = new PersistTransactionInput("Café", 0L, Category.LEISURE);
        var inputNegative = new PersistTransactionInput("Café", -10L, Category.LEISURE);

        assertThatThrownBy(() -> useCase.execute(inputZero))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maior que zero");

        assertThatThrownBy(() -> useCase.execute(inputNegative))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maior que zero");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar transação com descrição vazia")
    void shouldThrowExceptionWhenDescriptionIsBlank() {
        var input = new PersistTransactionInput("   ", 100L, Category.OTHER);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pode ser vazia");

        verify(transactionRepository, never()).save(any());
    }
}
