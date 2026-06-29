package com.fiap.restaurant_management_v2.application.usecases.user.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.DuplicateUserException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import com.fiap.restaurant_management_v2.domain.exception.InvalidUserException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserInteractorTest {

    @Mock
    private UserDsGateway userDsGateway;

    // Real executor that just runs the action (mirrors @Transactional boundary).
    private final TransactionalExecutor transactionalExecutor = Runnable::run;

    private CapturingPresenter presenter;
    private UpdateUserInteractor interactor;

    private UUID id;
    private UserDsResponseModel current;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new UpdateUserInteractor(
            userDsGateway,
            transactionalExecutor,
            presenter
        );
        id = UUID.randomUUID();
        current = new UserDsResponseModel(
            id,
            "Ada",
            "ada@example.com",
            "ada",
            "12345678901"
        );
    }

    @Test
    @DisplayName(
        "PATCH parcial: campo null mantém atual; persiste merge e apresenta"
    )
    void partialUpdateMergesAndPresents() {
        when(userDsGateway.findById(id)).thenReturn(Optional.of(current));
        var saved = new UserDsResponseModel(
            id,
            "Ada Lovelace",
            "ada@example.com",
            "ada",
            "12345678901"
        );
        when(
            userDsGateway.update(
                id,
                "Ada Lovelace",
                "ada@example.com",
                "ada",
                "12345678901"
            )
        ).thenReturn(saved);

        // só name muda; email/login/tax null = mantém
        interactor.execute(
            new UpdateUserRequestModel(id, "Ada Lovelace", null, null, null)
        );

        verify(userDsGateway).update(
            id,
            "Ada Lovelace",
            "ada@example.com",
            "ada",
            "12345678901"
        );
        // email/login inalterados => não checa unicidade (sem falso-duplicate)
        verify(userDsGateway, never()).existsByEmailExcludingId(any(), any());
        verify(userDsGateway, never()).existsByLoginExcludingId(any(), any());

        assertNotNull(presenter.response);
        assertEquals(id, presenter.response.id());
        assertEquals("Ada Lovelace", presenter.response.name());
        assertEquals("ada@example.com", presenter.response.email());
        assertEquals("ada", presenter.response.login());
    }

    @Test
    @DisplayName(
        "Usuário inexistente lança UserNotFoundException; não persiste"
    )
    void throwsWhenNotFound() {
        when(userDsGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            interactor.execute(
                new UpdateUserRequestModel(id, "X", null, null, null)
            )
        );

        verify(userDsGateway, never()).update(
            any(),
            any(),
            any(),
            any(),
            any()
        );
    }

    @Test
    @DisplayName(
        "Email novo já em uso (excluindo self) → DuplicateUserException"
    )
    void throwsWhenEmailDuplicated() {
        when(userDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(
            userDsGateway.existsByEmailExcludingId("new@example.com", id)
        ).thenReturn(true);

        assertThrows(DuplicateUserException.class, () ->
            interactor.execute(
                new UpdateUserRequestModel(
                    id,
                    null,
                    "new@example.com",
                    null,
                    null
                )
            )
        );

        verify(userDsGateway, never()).update(
            any(),
            any(),
            any(),
            any(),
            any()
        );
    }

    @Test
    @DisplayName(
        "Login novo já em uso (excluindo self) → DuplicateUserException"
    )
    void throwsWhenLoginDuplicated() {
        when(userDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(userDsGateway.existsByLoginExcludingId("taken", id)).thenReturn(
            true
        );

        assertThrows(DuplicateUserException.class, () ->
            interactor.execute(
                new UpdateUserRequestModel(id, null, null, "taken", null)
            )
        );

        verify(userDsGateway, never()).update(
            any(),
            any(),
            any(),
            any(),
            any()
        );
    }

    @Test
    @DisplayName(
        "CPF novo já em uso (excluindo self) → DuplicateUserException"
    )
    void throwsWhenTaxIdentifierDuplicated() {
        when(userDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(
            userDsGateway.existsByTaxIdentifierExcludingId("98765432100", id)
        ).thenReturn(true);

        assertThrows(DuplicateUserException.class, () ->
            interactor.execute(
                new UpdateUserRequestModel(id, null, null, null, "98765432100")
            )
        );

        verify(userDsGateway, never()).update(
            any(),
            any(),
            any(),
            any(),
            any()
        );
    }

    @Test
    @DisplayName(
        "CPF inalterado não checa unicidade (sem falso-duplicate)"
    )
    void doesNotCheckTaxUniquenessWhenUnchanged() {
        when(userDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(
            userDsGateway.update(
                id,
                "Ada",
                "ada@example.com",
                "ada",
                "12345678901"
            )
        ).thenReturn(current);

        // mesmo CPF do current => não dispara o lookup de unicidade
        interactor.execute(
            new UpdateUserRequestModel(id, null, null, null, "12345678901")
        );

        verify(userDsGateway, never()).existsByTaxIdentifierExcludingId(
            any(),
            any()
        );
    }

    @Test
    @DisplayName("CPF novo (livre) persiste o novo valor e apresenta")
    void changesTaxIdentifierPersistsNewValue() {
        when(userDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(
            userDsGateway.existsByTaxIdentifierExcludingId("98765432100", id)
        ).thenReturn(false);
        var saved = new UserDsResponseModel(
            id,
            "Ada",
            "ada@example.com",
            "ada",
            "98765432100"
        );
        when(
            userDsGateway.update(
                id,
                "Ada",
                "ada@example.com",
                "ada",
                "98765432100"
            )
        ).thenReturn(saved);

        interactor.execute(
            new UpdateUserRequestModel(id, null, null, null, "98765432100")
        );

        // o novo CPF DEVE ser propagado ao gateway (regressão do BLOCKER #1)
        verify(userDsGateway).update(
            id,
            "Ada",
            "ada@example.com",
            "ada",
            "98765432100"
        );
        assertNotNull(presenter.response);
        assertEquals("98765432100", presenter.response.taxIdentifier());
    }

    @Test
    @DisplayName("Nome só com espaços → InvalidUserException; não persiste")
    void throwsWhenNameBlank() {
        when(userDsGateway.findById(id)).thenReturn(Optional.of(current));

        assertThrows(InvalidUserException.class, () ->
            interactor.execute(
                new UpdateUserRequestModel(id, "   ", null, null, null)
            )
        );

        verify(userDsGateway, never()).update(
            any(),
            any(),
            any(),
            any(),
            any()
        );
    }

    @Test
    @DisplayName(
        "Email com formato inválido → InvalidUserException; não persiste"
    )
    void throwsWhenEmailInvalid() {
        when(userDsGateway.findById(id)).thenReturn(Optional.of(current));
        when(
            userDsGateway.existsByEmailExcludingId("bad-email", id)
        ).thenReturn(false);

        assertThrows(InvalidUserException.class, () ->
            interactor.execute(
                new UpdateUserRequestModel(id, null, "bad-email", null, null)
            )
        );

        verify(userDsGateway, never()).update(
            any(),
            any(),
            any(),
            any(),
            any()
        );
    }

    private static final class CapturingPresenter
        implements UpdateUserOutputBoundary
    {

        private UpdateUserResponseModel response;

        @Override
        public void present(UpdateUserResponseModel response) {
            this.response = response;
        }
    }
}
