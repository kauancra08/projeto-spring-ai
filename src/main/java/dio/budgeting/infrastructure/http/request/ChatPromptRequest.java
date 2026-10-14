package dio.budgeting.infrastructure.http.request;

import jakarta.validation.constraints.NotBlank;

public record ChatPromptRequest(
        @NotBlank(message = "A mensagem não pode ser vazia")
        String message
) {
}
