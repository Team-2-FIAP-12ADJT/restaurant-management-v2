package com.fiap.restaurant_management_v2.application.usecases.user.updatepassword;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.IncorrectPasswordException;
import com.fiap.restaurant_management_v2.application.exception.SamePasswordException;
import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import com.fiap.restaurant_management_v2.application.gateways.UserCredentialDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserPasswordInteractorTest {

    @Mock
    private UserDsGateway userDsGateway;

    @Mock
    private PasswordEncoderGateway passwordEncoder;

    // Real executor that just runs the action (mirrors @Transactional boundary).
    private final TransactionalExecutor transactionalExecutor = Runnable::run;

    @Mock
    private LoggerGateway loggerGateway;

    private CapturingPresenter presenter;

    private UpdateUserPasswordInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new UpdateUserPasswordInteractor(
            userDsGateway,
            passwordEncoder,
            transactionalExecutor,
            presenter,
            loggerGateway
        );
    }

    @Test
    @DisplayName("Throws UserNotFoundException when user not found")
    void throwsUserNotFoundWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userDsGateway.findCredentialById(userId)).thenReturn(Optional.empty());

        var request = new UpdateUserPasswordRequestModel(
            userId,
            "OldPass123",
            "NewPass456"
        );

        assertThrows(UserNotFoundException.class, () -> interactor.execute(request));
    }

    @Test
    @DisplayName("Throws IncorrectPasswordException when old password does not match")
    void throwsIncorrectPasswordWhenOldPasswordDoesNotMatch() {
        UUID userId = UUID.randomUUID();
        String hashedPassword = "hashedOldPassword";

        UserCredentialDsResponseModel credential = new UserCredentialDsResponseModel(
            userId,
            "login",
            hashedPassword,
            "DONO"
        );

        when(userDsGateway.findCredentialById(userId))
            .thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("WrongOldPass", hashedPassword))
            .thenReturn(false);

        var request = new UpdateUserPasswordRequestModel(
            userId,
            "WrongOldPass",
            "NewPass456"
        );

        assertThrows(IncorrectPasswordException.class, () -> interactor.execute(request));
    }

    @Test
    @DisplayName("Throws SamePasswordException when new password matches current password")
    void throwsSamePasswordWhenNewPasswordMatchesCurrent() {
        UUID userId = UUID.randomUUID();
        String hashedPassword = "hashedPassword";

        UserCredentialDsResponseModel credential = new UserCredentialDsResponseModel(
            userId,
            "login",
            hashedPassword,
            "DONO"
        );

        when(userDsGateway.findCredentialById(userId))
            .thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("OldPass123", hashedPassword))
            .thenReturn(true);
        when(passwordEncoder.matches("OldPass123", hashedPassword))
            .thenReturn(true);

        var request = new UpdateUserPasswordRequestModel(
            userId,
            "OldPass123",
            "OldPass123"
        );

        assertThrows(SamePasswordException.class, () -> interactor.execute(request));
    }

    @Test
    @DisplayName("Updates password successfully with correct old password and different new password")
    void updatesPasswordSuccessfully() {
        UUID userId = UUID.randomUUID();
        String oldHashedPassword = "hashedOldPassword";
        String newHashedPassword = "hashedNewPassword";

        UserCredentialDsResponseModel credential = new UserCredentialDsResponseModel(
            userId,
            "login",
            oldHashedPassword,
            "DONO"
        );

        when(userDsGateway.findCredentialById(userId))
            .thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("OldPass123", oldHashedPassword))
            .thenReturn(true);
        when(passwordEncoder.matches("NewPass456", oldHashedPassword))
            .thenReturn(false);
        when(passwordEncoder.encode("NewPass456"))
            .thenReturn(newHashedPassword);

        var request = new UpdateUserPasswordRequestModel(
            userId,
            "OldPass123",
            "NewPass456"
        );

        interactor.execute(request);

        verify(userDsGateway).updatePassword(userId, newHashedPassword);
        verify(loggerGateway).info("password updated for user id={}", userId);
        assertTrue(presenter.hasPresented());
    }

    private static final class CapturingPresenter
        implements UpdateUserPasswordOutputBoundary
    {

        private boolean presented;

        @Override
        public void present() {
            this.presented = true;
        }

        boolean hasPresented() {
            return presented;
        }
    }
}
