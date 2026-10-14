package dio.budgeting.infrastructure.http;

import dio.budgeting.application.GetBalanceSummaryUseCase;
import dio.budgeting.application.ListAllTransactionsUseCase;
import dio.budgeting.application.ListTransactionsByCategoryUseCase;
import dio.budgeting.application.PersistTransactionUseCase;
import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.application.output.BalanceSummaryOutput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.infrastructure.http.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private PersistTransactionUseCase persistTransactionUseCase;

    @Mock
    private ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;

    @Mock
    private ListAllTransactionsUseCase listAllTransactionsUseCase;

    @Mock
    private GetBalanceSummaryUseCase getBalanceSummaryUseCase;

    @Mock
    private TranscriptionModel transcriptionModel;

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private TextToSpeechModel textToSpeechModel;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws IOException {
        when(chatClientBuilder.defaultSystem(any(String.class))).thenReturn(chatClientBuilder);
        when(chatClientBuilder.defaultTools(any(), any(), any(), any())).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);

        var systemPrompt = new ByteArrayResource("Você é um assistente".getBytes());

        TransactionController controller = new TransactionController(
                persistTransactionUseCase,
                listTransactionsByCategoryUseCase,
                listAllTransactionsUseCase,
                getBalanceSummaryUseCase,
                transcriptionModel,
                systemPrompt,
                chatClientBuilder,
                textToSpeechModel
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve criar transação com sucesso via endpoint POST /transactions")
    void shouldCreateTransactionViaPost() throws Exception {
        var transaction = new Transaction("Supermercado", 12000L, Category.GROCERIES);
        when(persistTransactionUseCase.execute(any(PersistTransactionInput.class)))
                .thenReturn(TransactionOutput.from(transaction));

        String requestJson = """
                {
                    "description": "Supermercado",
                    "category": "GROCERIES",
                    "amount": 12000
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Supermercado"))
                .andExpect(jsonPath("$.category").value("GROCERIES"))
                .andExpect(jsonPath("$.amount").value(12000.0));
    }

    @Test
    @DisplayName("Deve listar todas as transações via GET /transactions")
    void shouldListAllTransactions() throws Exception {
        var output = new TransactionOutput("123", "Gasolina", "AUTO", 250.0, Instant.now());
        when(listAllTransactionsUseCase.execute()).thenReturn(List.of(output));

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Gasolina"))
                .andExpect(jsonPath("$[0].category").value("AUTO"));
    }

    @Test
    @DisplayName("Deve retornar resumo financeiro via GET /transactions/summary")
    void shouldReturnFinancialSummary() throws Exception {
        var summary = new BalanceSummaryOutput(1500.0, 5, Map.of("GROCERIES", 1000.0, "AUTO", 500.0), "GROCERIES");
        when(getBalanceSummaryUseCase.execute()).thenReturn(summary);

        mockMvc.perform(get("/transactions/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(1500.0))
                .andExpect(jsonPath("$.totalTransactions").value(5))
                .andExpect(jsonPath("$.highestExpenseCategory").value("GROCERIES"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando payload for inválido")
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        String invalidJson = """
                {
                    "description": "",
                    "category": null,
                    "amount": -50
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }
}
