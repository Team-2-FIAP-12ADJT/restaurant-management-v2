# Análise do Projeto — restaurant-management-v2

## Stack
- **Spring Boot 4.0.6** com **Java 25**
- **Maven** como build tool
- **PostgreSQL** + **Liquibase** (migrations)
- **Springdoc OpenAPI** (Swagger UI)
- **Lombok**, **Jakarta Validation**
- **Testcontainers** (testes de integração)
- **JaCoCo** (cobertura)
- **ArchUnit** (arquitetura)

## Arquitetura: Clean Architecture / Hexagonal

### Domain (`domain/`)
- `User.java` — entidade central
- `UserType.java` — entidade de tipo de usuário
- Exceções: `InvalidUserException`, `InvalidUserTypeException`, `InvalidUserTypeUuidException`

### Application (`application/`)
- **Use Cases** em `application/usecases/`:
  - `user/create/` — CreateUser
  - `user/delete/` — DeleteUserById
  - `user/get_all/` — GetAllUsers (com paginação e filtros)
  - `user/get_user_by_id/` — GetUserById
  - `usertype/create/` — CreateUserType
  - `usertype/get_all/` — GetAllUsersType
  - `usertype/get_type_by_id/` — GetUserTypeById
  - `usertype/update/` — UpdateUserType
  - `usertype/bind_user/` — BindUserTypeToUser
  - `usertype/delete/` — DeleteUserTypeById (criado, soft delete)
- **Gateways** (`application/gateways/`): interfaces e DTOs para acesso a dados
- **Search/Filter** (`application/gateways/search/`): `FilterCriteria`, `FilterOperator`, `SearchQuery`
- **Pagination** (`application/pagination/`): `PageQuery`, `PageResult`
- **Exceções**: `DuplicateUserException`, `DuplicateUserTypeException`, `InvalidFilterException`, `UserNotFoundException`, `UserTypeNotFoundException`
- **Senha**: `PasswordEncoderGateway` — interface para encoding de senha

### Adapters (`adapters/`)
- **Controllers**: `UserController`, `UserTypeController` (orquestram use cases)
- **Presenters**: `CreateUserPresenter`, `GetAllUsersPresenter`, etc.
- **ViewModels** em `adapters/presenters/viewmodel/`

### Infrastructure (`infrastructure/`)
- **Persistence** (`infrastructure/persistence/`): JPA entities, repositories, gateways, mappers
- **Web** (`infrastructure/web/`):
  - APIs REST: `UserApi`, `TypeUserApi`
  - DTOs em `infrastructure/web/dto/`
  - `GlobalExceptionHandler` — trata exceções globalmente (+ IllegalArgumentException retorna 400)
  - `ApiPaths` — constantes de paths
- **Config** (`infrastructure/config/`): `OpenApiConfig`, `SharedConfiguration`, `UserConfiguration`, `UserTypeConfiguration`

### Recursos (`src/main/resources/`)
- `application.yaml` — config principal (timezone America/Sao_Paulo adicionado)
- `application-dev.yaml` — profile dev (SQL logado)
- `db/changelog/` — Liquibase migrations + seeds
- `liquibase.properties.example`

## Rotas do TypeUserApi (`/api/v1/users-type`)
| Método | Rota | Função |
|---|---|---|
| GET | `/` | Listar tipos (paginado) |
| GET | `/{id}` | Buscar por ID |
| POST | `/` | Criar tipo |
| PUT | `/{id}` | Atualizar tipo |
| POST | `/bind` | Vincular tipo a usuário |
| DELETE | `/{id}` | Excluir lógico (soft delete + unbind) |

## Fluxo bindUser
- `POST /bind` → `TypeUserApi` → `UserTypeController` → `BindUserTypeToUserInteractor`
- Interactor valida usuário existe (`userDsGateway.findAllById`), valida tipo existe (`userTypeDsGateway.findById`), chama `userDsGateway.bindUserType(userId, typeId)`
- `UserDsGatewayImpl.bindUserType()` faz soft update: seta `userTypeEntity` no `UserEntity` e salva

## Fluxo deleteUserType
- `DELETE /{id}` → `TypeUserApi` → `UserTypeController` → `DeleteUserTypeByIdInteractor`
- Interactor valida tipo existe, chama `userDsGateway.unbindUserType(id)` (limpa user_type_id dos users), depois `userTypeDsGateway.deleteById(id)` (soft delete com deletedAt)
- ⚠️ Pendente: transação p/ garantir atomicidade unbind + delete

## Testes criados (TypeUserApi)
### Integração (1)
- `infrastructure/web/TypeUserApiIT.java` — 11 testes, cobre todos os endpoints

### Unitários — Use Cases (6)
- `application/usecases/usertype/create/CreateUserTypeInteractorTest.java`
- `application/usecases/usertype/delete/DeleteUserTypeByIdInteractorTest.java`
- `application/usecases/usertype/get_all/GetAllUsersTypeInteractorTest.java`
- `application/usecases/usertype/get_type_by_id/GetUserTypeByIdInteractorTest.java`
- `application/usecases/usertype/update/UpdateUserTypeInteractorTest.java`
- `application/usecases/usertype/bind_user/BindUserTypeToUserInteractorTest.java`

### Unitários — Presenters (4)
- `adapters/presenters/CreateUserTypePresenterTest.java`
- `adapters/presenters/GetUserTypeByIdPresenterTest.java`
- `adapters/presenters/UpdateUserTypePresenterTest.java`
- `adapters/presenters/GetAllUsersTypePresenterTest.java`

### Unitário — Domínio (1)
- `domain/UserTypeTest.java` — 7 testes

## Timezone
- Configurado `America/Sao_Paulo` no `application.yaml` (hibernate.jdbc.time_zone + jackson.time-zone)
- Resolve diferença de 3h (UTC → BRT) nos campos created_at, updated_at, deleted_at

## Próximos passos (registrados na sessão)
- Pensar sobre solução de transação para atomicidade do unbind + delete
- Possível necessidade de adicionar TZ no docker-compose
