package com.fiap.restaurant_management_v2.infrastructure.web.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public class SensitiveDataMasker {

    // Tokens sensíveis para substring matching. isSensitive() retorna true se
    // field.toLowerCase().contains(token), assim oldPassword, newPassword, etc.
    // mascaram automaticamente (futuro-proof). Comparação sempre em lowercase.
    private static final Set<String> SENSITIVE_TOKENS = Set.of(
        "password",
        "taxidentifier"
    );
    private static final String MASK = "***";
    private static final String UNPARSEABLE = "[unparseable body]";

    private final ObjectMapper mapper = new ObjectMapper();

    public String mask(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }
        try {
            JsonNode root = mapper.readTree(body);
            maskNode(root);
            return mapper.writeValueAsString(root);
        } catch (Exception _) {
            return UNPARSEABLE;
        }
    }

    public String maskQueryString(String query) {
        if (query == null || query.isBlank()) {
            return query;
        }
        StringBuilder masked = new StringBuilder();
        for (String pair : query.split("&", -1)) {
            if (!masked.isEmpty()) {
                masked.append('&');
            }
            int eq = pair.indexOf('=');
            String key = eq < 0 ? pair : pair.substring(0, eq);
            if (eq >= 0 && isSensitive(key)) {
                masked.append(key).append('=').append(MASK);
            } else {
                masked.append(pair);
            }
        }
        return masked.toString();
    }

    private void maskNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            List<String> names = new ArrayList<>();
            object.properties().forEach(entry -> names.add(entry.getKey()));
            for (String name : names) {
                if (isSensitive(name)) {
                    object.put(name, MASK);
                } else {
                    maskNode(object.get(name));
                }
            }
        } else if (node.isArray()) {
            node.forEach(this::maskNode);
        }
    }

    private boolean isSensitive(String field) {
        String fieldLower = field.toLowerCase(Locale.ROOT);
        return SENSITIVE_TOKENS.stream().anyMatch(fieldLower::contains);
    }
}
