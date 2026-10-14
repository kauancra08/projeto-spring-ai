package dio.budgeting.application;

import dio.budgeting.application.output.BalanceSummaryOutput;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetBalanceSummaryUseCase {
    private final TransactionRepository transactionRepository;

    public GetBalanceSummaryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "get-balance-summary", description = "Calcula e retorna o resumo geral de gastos, saldo total acumulado e detalhamento por categoria")
    public BalanceSummaryOutput execute() {
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) {
            return new BalanceSummaryOutput(0.0, 0, Collections.emptyMap(), "Nenhuma despesa cadastrada");
        }

        long totalAmountRaw = transactions.stream().mapToLong(Transaction::getAmount).sum();
        double totalAmount = BigDecimal.valueOf(totalAmountRaw).setScale(2, RoundingMode.HALF_UP).doubleValue();

        Map<String, Double> breakdown = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().name(),
                        Collectors.summingDouble(t -> BigDecimal.valueOf(t.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue())
                ));

        String highestCategory = breakdown.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        return new BalanceSummaryOutput(totalAmount, transactions.size(), breakdown, highestCategory);
    }
}
