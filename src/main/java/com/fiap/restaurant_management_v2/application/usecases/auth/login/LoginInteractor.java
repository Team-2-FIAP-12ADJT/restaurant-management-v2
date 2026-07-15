package com.fiap.restaurant_management_v2.application.usecases.auth.login;

import com.fiap.restaurant_management_v2.application.exception.InvalidCredentialsException;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.application.gateways.TokenGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import java.text.Normalizer;
import java.util.Locale;

public class LoginInteractor implements LoginInputBoundary {

    private final UserDsGateway userDsGateway;
    private final PasswordEncoderGateway passwordEncoder;
    private final TokenGateway tokenGateway;
    private final LoginOutputBoundary outputBoundary;

    public LoginInteractor(
        UserDsGateway userDsGateway,
        PasswordEncoderGateway passwordEncoder,
        TokenGateway tokenGateway,
        LoginOutputBoundary outputBoundary
    ) {
        this.userDsGateway = userDsGateway;
        this.passwordEncoder = passwordEncoder;
        this.tokenGateway = tokenGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(LoginRequestModel request) {
        var credential = userDsGateway.findByLogin(request.login())
            .orElseThrow(() -> new InvalidCredentialsException("Login ou senha inválidos"));
        if (!passwordEncoder.matches(request.password(), credential.passwordHash())) {
            throw new InvalidCredentialsException("Login ou senha inválidos");
        }
        String authority = sanitizeAuthority(credential.userTypeName());
        var token = tokenGateway.generate(credential.id(), credential.login(), authority);
        outputBoundary.present(new LoginResponseModel(token.token(), token.expiresAt()));
    }

    private static String sanitizeAuthority(String userTypeName) {
        if (userTypeName == null || userTypeName.isBlank()) return null;
        String noAccents = Normalizer.normalize(userTypeName, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        String upper = noAccents.toUpperCase(Locale.ROOT);
        String collapsed = stripUnderscores(upper.replaceAll("[^A-Z0-9]+", "_"));
        return collapsed.isEmpty() ? null : collapsed;
    }

    // Varredura linear em vez de replaceAll("^_+|_+$"): o regex reescaneia a
    // cada posição inicial, o que dá tempo super-linear na entrada.
    private static String stripUnderscores(String value) {
        int start = 0;
        int end = value.length();
        while (start < end && value.charAt(start) == '_') {
            start++;
        }
        while (end > start && value.charAt(end - 1) == '_') {
            end--;
        }
        return value.substring(start, end);
    }
}
