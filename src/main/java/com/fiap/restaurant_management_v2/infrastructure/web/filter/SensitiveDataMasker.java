package com.fiap.restaurant_management_v2.infrastructure.web.filter;

import java.util.Set;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public class SensitiveDataMasker {

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
        "password",
        "taxIdentifier"
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
        } catch (Exception ex) {
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
            if (eq >= 0 && SENSITIVE_FIELDS.contains(key)) {
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
            SENSITIVE_FIELDS.forEach(field -> {
                if (object.has(field)) {
                    object.put(field, MASK);
                }
            });
            object.properties().forEach(entry -> maskNode(entry.getValue()));
        } else if (node.isArray()) {
            node.forEach(this::maskNode);
        }
    }
}
