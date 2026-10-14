package dio.budgeting.application.output;

import java.util.Map;

public record BalanceSummaryOutput(
        double totalAmount,
        long totalTransactions,
        Map<String, Double> categoryBreakdown,
        String highestExpenseCategory
) {
}
