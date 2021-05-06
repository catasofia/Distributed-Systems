package pt.tecnico.exceptions;

public enum ErrorMessage {
    NO_STATION_FOUND("Não existe nenhuma estação com essa abreviatura."),
    NO_PHONE_MATCH("O número de telemóvel não corresponde ao utilizador."),
    VALUE_OUT_OF_BOUNDS("Só se pode carregar com valores entre 1 EUR e 20 EUR, inclusive."),
    FAR_AWAY("Fora de alcance.");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}
