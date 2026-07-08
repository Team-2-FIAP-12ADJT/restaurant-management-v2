# Sonar via Docker

:::tip
Guia de análise **local** com SonarQube (servidor via Docker) + **plugin Maven do Sonar** (`sonar:sonar`), para este projeto **Spring Boot / Java 25 / Maven** (`restaurant-management-v2`).
:::

:::info
Pré-requisitos: Docker (Engine ou Desktop) e JDK 25 (para o build Maven).
:::

Materiais de apoio:

* Docker Desktop:
  * Mac: <https://docs.docker.com/desktop/setup/install/mac-install/>
  * Linux: <https://docs.docker.com/desktop/setup/install/linux/>
  * Windows: <https://docs.docker.com/desktop/setup/install/windows-install/>
* SonarQube: <https://docs.sonarsource.com/sonarqube-server/latest/try-out-sonarqube/>
* SonarScanner for Maven: <https://docs.sonarsource.com/sonarqube-server/latest/analyzing-source-code/scanners/sonarscanner-for-maven/>

:::note
Por que o plugin Maven em vez do `sonar-scanner-cli` em container? Num projeto Maven, o plugin roda no host dentro do build e **resolve sozinho** sources, testes, bytecode e classpath a partir do POM — dispensando rede Docker dedicada, bind-mount, cópia de dependências (`target/dependency`) e a configuração manual de `sonar.java.binaries`/`sonar.java.libraries`. Só o **servidor** SonarQube roda em Docker.
:::

## Passo 1 — Container SonarQube

Suba o servidor (a porta `9000` publicada é acessível do host em `http://localhost:9000`):

```bash
docker run -d --name sonarqube \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  -p 9000:9000 \
  sonarqube:latest
```

Valide:

```bash
docker ps
```

:::info
A porta `9000` do host precisa estar livre.
:::

:::warning
O SonarQube leva alguns minutos para inicializar na primeira execução. Aguarde a inicialização total antes de analisar.
:::

## Passo 2 — Setup do projeto no SonarQube

Acesse [http://localhost:9000](http://localhost:9000). Primeiro login: **admin / admin** (será exigida a troca de senha; usuário continua `admin`).

No Dashboard, **Create a local project**:

![](/api/attachments.redirect?id=c33c17f9-81b9-4e16-a322-d1cc81413f93)

Informe **Project display name** e **Project key** = `Restaurant-Management-V2` (precisa bater com `sonar.projectKey`/`sonar.projectName` do `pom.xml`) e a branch a analisar:

![](/api/attachments.redirect?id=9777d878-e71d-4b7c-91d7-f2e5667c52e5)

Base de análise do novo código: **Use the global setting**:

![](/api/attachments.redirect?id=6d38050f-75b4-49df-88c4-6ad3a71ff4ee)

Escolha **Locally** e gere o token (em **Expires In**, ex.: sem expiração para uso local):

![](/api/attachments.redirect?id=6a652fa4-3665-4f3c-a296-c4a3cafbe51b)

**Anote o token** — será passado ao Maven via `-Dsonar.token`. Volte ao dashboard por **Projects**:

![](/api/attachments.redirect?id=3c2d1745-537c-4335-bd02-81847c68a84e)

***

## Passo 3 — Configuração no `pom.xml`

Já configurada. A identificação do projeto e a cobertura ficam em `<properties>`; o resto (sources `src/main/java`, tests `src/test/java`, binaries `target/classes`, classpath) o plugin auto-detecta do POM:

```xml
<sonar.projectKey>Restaurant-Management-V2</sonar.projectKey>
<sonar.projectName>Restaurant-Management-V2</sonar.projectName>
<sonar.coverage.jacoco.xmlReportPaths>${project.build.directory}/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
<sonar.exclusions>**/Application.java</sonar.exclusions>
<sonar.coverage.exclusions>**/Application.java,**/config/OpenApiConfig.java</sonar.coverage.exclusions>
```

A versão do `sonar-maven-plugin` está pinada no `pom.xml` (build reproduzível).

:::warning
`sonar.host.url` e o **token** NÃO ficam no `pom.xml` — são passados por `-D` na linha de comando (ou via env `SONAR_TOKEN`). Nunca versione o token.
:::

:::info
`projectKey`/`projectName` do POM devem ser idênticos aos definidos no setup da UI (Passo 2).
:::

## Passo 4 — Análise

A cobertura vem do JaCoCo (`target/site/jacoco/jacoco.xml`), gerado no `verify`. Rode build + análise:

```bash
./mvnw clean verify
./mvnw sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<token do Passo 2>
```

Ou tudo num comando:

```bash
./mvnw clean verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<token>
```

Ao final: `ANALYSIS SUCCESSFUL, you can find the results at: http://localhost:9000/dashboard?id=Restaurant-Management-V2`.

:::info
Sem watch mode: repita o comando a cada nova análise.
:::

:::warning
Erros comuns:

* **Não conecta / timeout** → SonarQube ainda inicializando, ou `sonar.host.url` errado (use `http://localhost:9000`).
* **`Not authorized` / 401** → token inválido/expirado; gere outro no Passo 2.
* **Cobertura zerada** → faltou `./mvnw verify` antes (sem `target/site/jacoco/jacoco.xml`).
:::

Concluída a análise, atualize o dashboard:

![](/api/attachments.redirect?id=37e3d3c4-d0e1-4b2f-9576-b375de365911)

## Considerações finais

:::info
O container do SonarQube (Passo 1) é criado **uma vez** e reaproveitado. Passos 2–4 são por projeto.
:::

:::tip
Alias no `.bashrc`/`.zshrc` para o fluxo completo (build + análise):
:::

```bash
sonar_scan() {
  local token="$1" host="${2:-http://localhost:9000}"
  ./mvnw clean verify sonar:sonar -Dsonar.host.url="$host" -Dsonar.token="$token"
}

# uso (na raiz do projeto):
sonar_scan "<token>"
```
