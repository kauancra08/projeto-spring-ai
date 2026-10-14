package dio.budgeting.application.input;

import dio.budgeting.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

public record PersistTransactionInput(
        @ToolParam(description = "Descrição detalhada do gasto ou transação") String description,
        @ToolParam(description = "Valor numérico do gasto") long amount,
        @ToolParam(description = "Categoria mais adequada para a transação: GROCERIES, PHARMA, AUTO, LEISURE, UTILITIES, EDUCATION, OTHER") Category category
) {
}
