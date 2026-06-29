package com.fiap.restaurant_management_v2.adapters.presenters;

/**
 * Formats a stored CPF (11 bare digits) into the masked presentation form
 * {@code XXX.XXX.XXX-XX}. Presentation concern: storage stays raw, the view
 * gets the mask. Framework-free (circle 3).
 */
public final class CpfFormatter {

    private CpfFormatter() {}

    public static String format(String digits) {
        if (digits == null || !digits.matches("^\\d{11}$")) {
            return digits;
        }
        return digits.replaceFirst(
            "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
            "$1.$2.$3-$4"
        );
    }
}
