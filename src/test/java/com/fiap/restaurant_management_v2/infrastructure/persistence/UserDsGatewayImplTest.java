package com.fiap.restaurant_management_v2.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.exception.UserNotFoundException;
import com.fiap.restaurant_management_v2.application.gateways.UserDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UserDsGatewayImplTest {

    @Mock
    private UserJpaRepository repository;

    private UserDsGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        gateway = new UserDsGatewayImpl(repository);
    }

    @Test
    void updateThrowsWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            gateway.update(id, "Ada", "ada@example.com", "ada", "12345678901")
        );
    }

    @Test
    void existsByIdDelegatesToRepository() {
        UUID id = UUID.randomUUID();
        when(repository.existsByIdAndDeletedAtIsNull(id)).thenReturn(true);

        assertTrue(gateway.existsById(id));

        verify(repository).existsByIdAndDeletedAtIsNull(id);
    }

    @Test
    void findAllByIdsMapsEntities() {
        UUID id = UUID.randomUUID();
        var entity = userEntity(id);
        when(repository.findAllByIdInAndDeletedAtIsNull(List.of(id))).thenReturn(List.of(entity));

        var result = gateway.findAllByIds(List.of(id));

        assertEquals(1, result.size());
        assertEquals(id, result.getFirst().id());
        assertEquals("Ada", result.getFirst().name());
    }

    @Test
    void findAllByIdMapsBindResponseWhenFound() {
        UUID id = UUID.randomUUID();
        var entity = userEntity(id);
        when(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(entity));

        var result = gateway.findAllById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.orElseThrow().id());
        assertEquals("hash", result.orElseThrow().passWord());
    }

    @Test
    void findByLoginWithUserTypeReturnsCredentialResponse() {
        UUID id = UUID.randomUUID();
        var entity = UserEntity.builder()
            .id(id)
            .login("owner")
            .password("hash")
            .name("Ada")
            .email("ada@example.com")
            .taxIdentifier("12345678901")
            .userTypeEntity(UserTypeEntity.builder().id(UUID.randomUUID()).userType("Dono").build())
            .build();
        when(repository.findByLoginAndDeletedAtIsNull("owner")).thenReturn(Optional.of(entity));

        var result = gateway.findByLogin("owner");

        assertTrue(result.isPresent());
        assertEquals(id, result.orElseThrow().id());
        assertEquals("owner", result.orElseThrow().login());
        assertEquals("hash", result.orElseThrow().passwordHash());
        assertEquals("Dono", result.orElseThrow().userTypeName());
    }

    @Test
    void findByLoginWithoutUserTypeReturnsCredentialResponseWithNullUserType() {
        UUID id = UUID.randomUUID();
        var entity = UserEntity.builder()
            .id(id)
            .login("admin")
            .password("hash")
            .name("Bob")
            .email("bob@example.com")
            .taxIdentifier("98765432101")
            .userTypeEntity(null)
            .build();
        when(repository.findByLoginAndDeletedAtIsNull("admin")).thenReturn(Optional.of(entity));

        var result = gateway.findByLogin("admin");

        assertTrue(result.isPresent());
        assertEquals(id, result.orElseThrow().id());
        assertEquals("admin", result.orElseThrow().login());
        assertEquals("hash", result.orElseThrow().passwordHash());
        assertNull(result.orElseThrow().userTypeName());
    }

    @Test
    void findByLoginReturnsEmptyWhenNotFound() {
        when(repository.findByLoginAndDeletedAtIsNull("nonexistent")).thenReturn(Optional.empty());

        var result = gateway.findByLogin("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteByIdSoftDeletesWhenFound() {
        UUID id = UUID.randomUUID();
        var entity = userEntity(id);
        when(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(entity));

        gateway.deleteById(id);

        assertNotNull(entity.getDeletedAt());
        verify(repository).save(entity);
    }

    @Test
    void deleteByIdDoesNothingWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

        gateway.deleteById(id);

        verify(repository, never()).save(any());
    }

    @Test
    void findAllBuildsPageResult() {
        var entity = userEntity(UUID.randomUUID());
        when(repository.findAll(anySpecification(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(entity)));

        var result = gateway.findAll(SearchQuery.empty(), 1, 10);

        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.page());
        assertEquals(10, result.size());

        ArgumentCaptor<Specification<UserEntity>> specCaptor =
            specificationCaptor();
        verify(repository).findAll(specCaptor.capture(), any(Pageable.class));

        Root<UserEntity> root = mockRoot();
        CriteriaQuery<UserEntity> query = mockCriteriaQuery();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> deletedAtPath = mockPath();
        Predicate predicate = mock(Predicate.class);
        when(root.get("deletedAt")).thenReturn(deletedAtPath);
        when(cb.isNull(deletedAtPath)).thenReturn(predicate);

        assertEquals(predicate, specCaptor.getValue().toPredicate(root, query, cb));
    }

    @Test
    void bindUserTypeSetsReferenceWhenUserExists() {
        UUID userId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();
        var entity = userEntity(userId);
        when(repository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(entity));

        gateway.bindUserType(userId, typeId);

        assertNotNull(entity.getUserTypeEntity());
        assertEquals(typeId, entity.getUserTypeEntity().getId());
        verify(repository).save(entity);
    }

    @Test
    void unbindUserTypeClearsReferenceForEachUser() {
        UUID typeId = UUID.randomUUID();
        var first = userEntity(UUID.randomUUID());
        var second = userEntity(UUID.randomUUID());
        first.setUserTypeEntity(UserTypeEntity.builder().id(typeId).build());
        second.setUserTypeEntity(UserTypeEntity.builder().id(typeId).build());
        when(repository.findAllByUserTypeEntityIdAndDeletedAtIsNull(typeId))
            .thenReturn(List.of(first, second));

        gateway.unbindUserType(typeId);

        assertNull(first.getUserTypeEntity());
        assertNull(second.getUserTypeEntity());
        verify(repository).save(first);
        verify(repository).save(second);
    }

    @Test
    void saveMapsRequestAndResponse() {
        UUID id = UUID.randomUUID();
        when(repository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = gateway.save(
            new UserDsRequestModel(id, "Ada", "ada@example.com", "ada", "12345678901", "hash")
        );

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        assertEquals(id, captor.getValue().getId());
        assertEquals("hash", captor.getValue().getPassword());
        assertEquals(id, result.id());
    }

    @Test
    void userFilterFieldsPrivateConstructorCanBeInvokedForCoverage() throws Exception {
        Constructor<UserFilterFields> constructor = UserFilterFields.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        constructor.newInstance();

        assertEquals(
            Map.of("name", "name", "email", "email", "login", "login", "taxIdentifier", "taxIdentifier"),
            UserFilterFields.ALLOWED
        );
    }

    private static UserEntity userEntity(UUID id) {
        return UserEntity.builder()
            .id(id)
            .name("Ada")
            .email("ada@example.com")
            .login("ada")
            .taxIdentifier("12345678901")
            .password("hash")
            .build();
    }

    @SuppressWarnings("unchecked")
    private static Specification<UserEntity> anySpecification() {
        return (Specification<UserEntity>) any(Specification.class);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static ArgumentCaptor<Specification<UserEntity>> specificationCaptor() {
        return (ArgumentCaptor) ArgumentCaptor.forClass(Specification.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> Root<T> mockRoot() {
        return (Root<T>) mock(Root.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> CriteriaQuery<T> mockCriteriaQuery() {
        return (CriteriaQuery<T>) mock(CriteriaQuery.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> Path<T> mockPath() {
        return (Path<T>) mock(Path.class);
    }
}
