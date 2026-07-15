package com.fiap.restaurant_management_v2.adapters.presenters;

/**
 * Formats a stored CNPJ (14 raw chars — dígitos ou alfanumérico no novo formato)
 * into the masked presentation form {@code XX.XXX.XXX/XXXX-XX}. Storage stays raw,
 * a view recebe a máscara. Framework-free (circle 3). Espelha {@link CpfFormatter}.
 */
public final class CnpjFormatter {

    private CnpjFormatter() {}

    public static String format(String value) {
        if (value == null || !value.matches("^[A-Z0-9]{14}$")) {
            return value;
        }
        return value.replaceFirst(
            "([A-Z0-9]{2})([A-Z0-9]{3})([A-Z0-9]{3})([A-Z0-9]{4})(\\d{2})",
            "$1.$2.$3/$4-$5"
        );
    }
}
