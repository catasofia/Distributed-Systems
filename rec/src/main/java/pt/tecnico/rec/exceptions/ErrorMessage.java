package pt.tecnico.rec.exceptions;

public enum ErrorMessage {
    NO_AVAILABLE_BIKES("Erro: Não há bicicletas disponiveis para requisitar."),
    ALREADY_HAS_BIKE("Erro: Este utilizador não pode requisitar mais bicicletas."),
    NOT_ENOUGH_BALANCE("Erro: Este utilizador não pode requisitar bicicletas. " +
            "Conta com dinheiro insuficiente."),
    FULL_DOCK("Erro: Não pode devolver a bicicleta nesta doca. Doca cheia."),
    NO_BIKE_TO_DELIVER("Erro: Este utilizador não tem bicicleta para devolver.");


    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}
