package com.fiap.restaurant_management_v2.application.usecases.user.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateUserInteractorTest {

    @Mock
    private UserDsGateway userDsGateway;

    @Mock
    private PasswordEncoderGateway passwordEncoder;

    @Mock
    private LoggerGateway loggerGateway;

    private CapturingPresenter presenter;

    private CreateUserInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new CreateUserInteractor(
            userDsGateway,
            passwordEncoder,
            presenter,
            loggerGateway
        );
    }

    @Test
    @DisplayName(
        "Cadastra com sucesso, codifica a senha e apresenta o resultado sem senha"
    )
    void createsUserHashingPassword() {
        var request = new CreateUserRequestModel(
            "Ada",
            "ada@example.com",
            "ada",
            "12345678901",
            "secret123"
        );
        when(userDsGateway.existsByEmail("ada@example.com")).thenReturn(false);
        when(userDsGateway.existsByLogin("ada")).thenReturn(false);
        when(userDsGateway.existsByTaxIdentifier("12345678901")).thenReturn(
            false
        );
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret");
        when(userDsGateway.save(any(UserDsRequestModel.class))).thenAnswer(
            call -> {
                UserDsRequestModel ds = call.getArgument(0);
                return new UserDsResponseModel(
                    ds.id(),
                    ds.name(),
                    ds.email(),
                    ds.login(),
                    ds.taxIdentifier(),
                    UUID.randomUUID(),
                    "DONO"
                );
            }
        );

        interactor.execute(request);

        verify(passwordEncoder).encode("secret123");
        ArgumentCaptor<UserDsRequestModel> captor = ArgumentCaptor.forClass(
            UserDsRequestModel.class
        );
        verify(userDsGateway).save(captor.capture());
        assertEquals("hashed-secret", captor.getValue().password());

        assertNotNull(presenter.response);
        assertNotNull(presenter.response.id());
        assertEquals("ada@example.com", presenter.response.email());
        assertEquals("ada", presenter.response.login());
    }

    @Test
    @DisplayName("Email duplicado não persiste nem apresenta")
    void rejectsDuplicateEmail() {
        var request = new CreateUserRequestModel(
            "Ada",
            "ada@example.com",
            "ada",
            "12345678901",
            "secret123"
        );
        when(userDsGateway.existsByEmail("ada@example.com")).thenReturn(true);

        assertThrows(DuplicateUserException.class, () ->
            interactor.execute(request)
        );
        verify(userDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("Login duplicado não persiste nem apresenta")
    void rejectsDuplicateLogin() {
        var request = new CreateUserRequestModel(
            "Ada",
            "ada@example.com",
            "ada",
            "12345678901",
            "secret123"
        );
        when(userDsGateway.existsByEmail("ada@example.com")).thenReturn(false);
        when(userDsGateway.existsByLogin("ada")).thenReturn(true);

        assertThrows(DuplicateUserException.class, () ->
            interactor.execute(request)
        );
        verify(userDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    @Test
    @DisplayName("CPF duplicado não persiste nem apresenta")
    void rejectsDuplicateTaxIdentifier() {
        var request = new CreateUserRequestModel(
            "Ada",
            "ada@example.com",
            "ada",
            "12345678901",
            "secret123"
        );
        when(userDsGateway.existsByEmail("ada@example.com")).thenReturn(false);
        when(userDsGateway.existsByLogin("ada")).thenReturn(false);
        when(userDsGateway.existsByTaxIdentifier("12345678901")).thenReturn(
            true
        );

        assertThrows(DuplicateUserException.class, () ->
            interactor.execute(request)
        );
        verify(userDsGateway, never()).save(any());
        assertNull(presenter.response);
    }

    private static final class CapturingPresenter
        implements CreateUserOutputBoundary
    {

        private CreateUserResponseModel response;

        @Override
        public void present(CreateUserResponseModel response) {
            this.response = response;
        }
    }
}
