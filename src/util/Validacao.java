package util;

import java.util.regex.Pattern;

/** Métodos estáticos de validação das regras de negócio (RN01/RN02). */
public final class Validacao {

    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validacao() {
    }

    public static String textoObrigatorio(String valor, String nomeCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo " + nomeCampo + " é obrigatório.");
        }
        return valor.trim();
    }

    public static String validarEmail(String email) {
        String valor = textoObrigatorio(email, "e-mail");
        if (!EMAIL.matcher(valor).matches()) {
            throw new IllegalArgumentException("E-mail inválido: " + valor);
        }
        return valor;
    }

    public static double validarLatitude(double latitude) {
        if (latitude < -27.95 || latitude > -27.45) {
            throw new IllegalArgumentException(
                    "A latitude deve estar na faixa de Palhoça/SC (-27.95 a -27.45).");
        }
        return latitude;
    }

    public static double validarLongitude(double longitude) {
        if (longitude < -49.05 || longitude > -48.50) {
            throw new IllegalArgumentException(
                    "A longitude deve estar na faixa de Palhoça/SC (-49.05 a -48.50).");
        }
        return longitude;
    }

    public static int validarNaoNegativo(int valor, String nomeCampo) {
        if (valor < 0) {
            throw new IllegalArgumentException(nomeCampo + " não pode ser negativo.");
        }
        return valor;
    }

    public static double validarPositivo(double valor, String nomeCampo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(nomeCampo + " deve ser maior que zero.");
        }
        return valor;
    }
}
