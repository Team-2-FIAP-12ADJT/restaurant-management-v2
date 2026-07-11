package com.fiap.restaurant_management_v2.application.usecases.auth.login;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.InvalidCredentialsException;
import com.fiap.restaurant_management_v2.application.gateways.GeneratedToken;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.application.gateways.TokenGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserCredentialDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginInteractorTest {

    @Mock
    private UserDsGateway userDsGateway;

    @Mock
    private PasswordEncoderGateway passwordEncoder;

    @Mock
    private TokenGateway tokenGateway;

    private CapturingPresenter presenter;

    private LoginInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new LoginInteractor(
            userDsGateway,
            passwordEncoder,
            tokenGateway,
            presenter
        );
    }

    @Test
    void successfulLoginGeneratesTokenAndPresents() {
        UUID userId = UUID.randomUUID();
        var credential = new UserCredentialDsResponseModel(
            userId,
            "owner",
            "HASH",
            "Dono"
        );
        Instant expiresAt = Instant.now().plusSeconds(900);
        var generatedToken = new GeneratedToken("jwt", expiresAt);

        when(userDsGateway.findByLogin("owner")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("senha", "HASH")).thenReturn(true);
        when(tokenGateway.generate(userId, "owner", "DONO")).thenReturn(generatedToken);

        interactor.execute(new LoginRequestModel("owner", "senha"));

        assertEquals("jwt", presenter.response.accessToken());
        assertEquals(expiresAt, presenter.response.expiresAt());
        verify(tokenGateway).generate(userId, "owner", "DONO");
    }

    @Test
    void wrongPasswordThrowsInvalidCredentialsException() {
        UUID userId = UUID.randomUUID();
        var credential = new UserCredentialDsResponseModel(
            userId,
            "owner",
            "HASH",
            "Dono"
        );

        when(userDsGateway.findByLogin("owner")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("wrongpassword", "HASH")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () ->
            interactor.execute(new LoginRequestModel("owner", "wrongpassword"))
        );

        verify(tokenGateway, never()).generate(any(), any(), any());
    }

    @Test
    void nonexistentLoginThrowsInvalidCredentialsException() {
        when(userDsGateway.findByLogin("nonexistent")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () ->
            interactor.execute(new LoginRequestModel("nonexistent", "anypassword"))
        );

        verify(passwordEncoder, never()).matches(any(), any());
        verify(tokenGateway, never()).generate(any(), any(), any());
    }

    @Test
    void nullUserTypeReturnsNullAuthority() {
        UUID userId = UUID.randomUUID();
        var credential = new UserCredentialDsResponseModel(
            userId,
            "user",
            "HASH",
            null
        );
        Instant expiresAt = Instant.now().plusSeconds(900);
        var generatedToken = new GeneratedToken("jwt", expiresAt);

        when(userDsGateway.findByLogin("user")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("password", "HASH")).thenReturn(true);
        when(tokenGateway.generate(userId, "user", null)).thenReturn(generatedToken);

        interactor.execute(new LoginRequestModel("user", "password"));

        verify(tokenGateway).generate(userId, "user", null);
    }

    @Test
    void userTypeWithAccentsSanitized() {
        UUID userId = UUID.randomUUID();
        var credential = new UserCredentialDsResponseModel(
            userId,
            "user",
            "HASH",
            "Gerente Ção"
        );
        Instant expiresAt = Instant.now().plusSeconds(900);
        var generatedToken = new GeneratedToken("jwt", expiresAt);

        when(userDsGateway.findByLogin("user")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("password", "HASH")).thenReturn(true);
        when(tokenGateway.generate(userId, "user", "GERENTE_CAO")).thenReturn(generatedToken);

        interactor.execute(new LoginRequestModel("user", "password"));

        verify(tokenGateway).generate(userId, "user", "GERENTE_CAO");
    }

    private static final class CapturingPresenter implements LoginOutputBoundary {

        LoginResponseModel response;

        @Override
        public void present(LoginResponseModel response) {
            this.response = response;
        }
    }
}
