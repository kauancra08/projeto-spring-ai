package dio.budgeting.domain;

public enum Category {
    GROCERIES("Supermercado, feira e alimentação diária"),
    PHARMA("Farmácia, remédios e cuidados de saúde"),
    AUTO("Combustível, manutenção veicular, estacionamento e transporte"),
    LEISURE("Lazer, restaurantes, viagens e entretenimento"),
    UTILITIES("Contas residenciais, energia, água, internet e telefone"),
    EDUCATION("Livros, cursos, mensalidades e material de estudo"),
    OTHER("Outras despesas gerais");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
