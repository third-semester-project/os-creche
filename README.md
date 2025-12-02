# Sistema de O.S. - Creche

Aplicação desktop JavaFX para gestão de ordens de serviço (O.S.) de uma creche, com autenticação, controle de usuários, temas claro/escuro e geração de relatórios impressos.

## Tecnologias
- Java 17
- JavaFX (FXML, Scene)
- JDBC + PostgreSQL
- BCrypt para hash de senha
- Maven

## Estrutura resumida
- `src/main/java/br/com/creche`
  - `MainApp` – inicializa a aplicação e define o ícone/tema inicial.
  - `controller` – lógica das telas (login, dashboard, O.S., usuários, perfil, relatórios, etc.).
  - `repository` – JDBC direto para `usuarios` e `ordens_servico`.
  - `model` – modelos simples (`Usuario`, `OrdemServico`, `Perfil`).
  - `service/AuthService` – autenticação e estado do usuário logado.
  - `infra/DB` e `DBHealth` – conexão e verificação de saúde do banco.
  - `ui/SceneFactory`, `ThemeManager` – criação de cenas e temas.
  - `util/CryptoUtils` – criptografia AES para lembrar login em `Preferences`.
- `src/main/resources`
  - `application.properties` – URL, usuário, senha e schema do PostgreSQL.
  - `fxml/` – telas em FXML (login, dashboard, os-lista, nova/editar O.S., usuários, perfil, relatórios, configurações).
  - `css/` – temas claro (`app.css`) e escuro (`dark.css`).
  - `images/logo.png` – ícone e logo do app.

## Banco de dados
Configure `src/main/resources/application.properties` com:
```
db.url=jdbc:postgresql://<host>:<porta>/<db>
db.user=<usuario>
db.password=<senha>
db.schema=public
```
Tabelas esperadas (campos principais):
- `usuarios` (id, nome, email, perfil, senha_hash, ativo, criado_em, atualizado_em)
- `ordens_servico` (id, numero, titulo, descricao, categoria, prioridade, status, solicitante, prazo, criado_por, atribuido_para, observacoes, data_abertura, data_conclusao)
- `os_historico` (os_id, acao, de_status, para_status, usuario_id, observacao, criado_em)

## Como executar
1. Instale JDK 17 e Maven.
2. Ajuste o `application.properties` para seu PostgreSQL.
3. Rode:
   ```bash
   mvn clean javafx:run
   ```
   (ou abra o projeto em sua IDE preferida e execute `MainApp`).

## Fluxos principais
- **Login**: valida credenciais no banco; opção “lembrar” usa `Preferences` com AES para senha.
- **Dashboard**: KPIs, lista recente de O.S., atalhos para criar/editar.
- **Ordens de Serviço**: listar, filtrar, buscar por número/título, criar, editar e apagar; responsável opcional.
- **Usuários**: listar, buscar, criar, editar, inativar; perfis `ADMIN | GESTOR | OPERADOR`.
- **Perfil**: o usuário logado edita nome, e-mail e pode definir nova senha.
- **Relatórios**: filtro por período/status/categoria e impressão direta.
- **Temas**: claro/escuro/sistema salvos em `Preferences`; aplicados ao abrir cenas.

## Notas de segurança e uso
- Senhas são armazenadas com BCrypt (`jbcrypt`).
- O “lembrar login” guarda a senha cifrada localmente; desative se não quiser cache de credenciais.
- Não exponha `application.properties` com credenciais reais em repositórios públicos.

## Build de runtime customizado (opcional)
```bash
mvn clean package jlink:jlink
```
Gera imagem em `target/image` com o runtime enxuto.
