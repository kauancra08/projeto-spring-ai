package dio.budgeting.infrastructure.http.response;

import dio.budgeting.application.output.BalanceSummaryOutput;

import java.util.Map;

public record BalanceSummaryResponse(
        double totalAmount,
        long totalTransactions,
        Map<String, Double> categoryBreakdown,
        String highestExpenseCategory
) {
    public static BalanceSummaryResponse from(BalanceSummaryOutput output) {
        return new BalanceSummaryResponse(
                output.totalAmount(),
                output.totalTransactions(),
                output.categoryBreakdown(),
                output.highestExpenseCategory()
        );
    }
}
