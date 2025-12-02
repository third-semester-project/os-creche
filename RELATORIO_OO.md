# Conceitos de Orientação a Objetos Utilizados no Projeto os-creche

Este relatório analisa como os princípios de programação orientada a objetos aparecem no código do projeto, conectando teoria e trechos reais implementados.

## Herança
Herança permite que uma classe especialize outra, reutilizando atributos e comportamentos comuns.
```java
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load());
        // ...
    }
}
```
A classe `MainApp` herda de `javafx.application.Application`, sobrescrevendo o método `start` para definir o ciclo de vida da interface. O framework invoca o método sobrescrito em tempo de execução, mostrando como a classe derivada especializa o comportamento padrão da classe base.

## Polimorfismo
Polimorfismo permite tratar objetos de diferentes classes de forma uniforme por meio de um contrato comum.
```java
Object controller = loader.getController();
if (controller instanceof RequiresAuthService c && this.authService != null) {
    c.setAuthService(this.authService);
}

public interface RequiresAuthService {
    void setAuthService(AuthService authService);
}
```
No `DashboardController`, qualquer controlador carregado que implemente a interface `RequiresAuthService` recebe o mesmo método `setAuthService`, independentemente de sua classe concreta. A verificação `instanceof` com pattern matching aplica o comportamento adequado em tempo de execução, exemplificando polimorfismo por interface.

## Encapsulamento
Encapsulamento restringe o acesso direto ao estado de um objeto, expondo-o por meio de métodos de acesso controlados.
```java
public class OrdemServico {
    private Long id;
    private String numero;
    private String titulo;
    // ... demais campos privados

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    // ... getters e setters para os demais atributos
}
```
Os atributos de `OrdemServico` são privados e só podem ser lidos ou alterados pelos getters/setters. Isso protege a consistência interna da entidade e permite validar ou adaptar acesso sem expor diretamente as variáveis de instância.

## Abstração
Abstração isola detalhes complexos atrás de uma interface simples, expondo apenas o necessário para o uso correto.
```java
public class DB {
    private static String url;
    private static String user;
    private static String password;
    private static String schema;

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            if (schema != null && !schema.isBlank()) {
                try (var st = conn.createStatement()) {
                    st.execute("set search_path to " + schema);
                }
            }
            return conn;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao conectar no banco", e);
        }
    }
}
```
A classe `DB` esconde carregamento de propriedades, criação de `Connection` e configuração do schema, oferecendo apenas `getConnection()` para os repositórios. Os consumidores não precisam conhecer detalhes de driver, URL ou schema, apenas usam a abstração para obter conexões prontas.

## Agregação
Agregação representa um relacionamento "tem-um" em que os objetos podem existir de forma independente.
```java
public class OrdemServico {
    // ...
    private Usuario atribuidoPara;
    // ...
    public Usuario getAtribuidoPara() {
        return atribuidoPara;
    }

    public void setAtribuidoPara(Usuario atribuidoPara) {
        this.atribuidoPara = atribuidoPara;
    }
}
```
Uma `OrdemServico` mantém uma referência a um `Usuario` responsável, mas o usuário existe no sistema independentemente da ordem. O relacionamento mostra agregação porque `Usuario` pode ser compartilhado por outras ordens e não depende do ciclo de vida da `OrdemServico`.

## Composição
Composição indica que um objeto é formado por partes que não fazem sentido fora dele.
```java
public static class UsuarioItem {
    private final Long id;
    private final String nome;
    private final String email;

    public UsuarioItem(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    @Override
    public String toString() {
        return nome + (email != null ? " (" + email + ")" : "");
    }
}
```
A classe aninhada `UsuarioItem` em `NovaOSController` só existe para preencher o combo box daquela tela e não é usada em outro lugar. Sua vida útil está presa ao controlador que a contém, tornando-a um exemplo de composição onde o objeto parte não tem significado fora do todo.

## Injeção de dependências e delegação
Delegar responsabilidades a dependências explicitamente injetadas reduz acoplamento e melhora testabilidade.
```java
public void setAuthService(AuthService authService) {
    this.authService = authService;
    configurarPermissoes(); // aplica visibilidade e habilitação de botões
}
```
O `DashboardController` recebe um `AuthService` externo em vez de criá-lo internamente, e delega a verificação de permissões a esse serviço. Isso separa a lógica de autenticação da camada de UI e permite substituir a implementação sem alterar o controlador.

## Conclusão
O projeto utiliza princípios fundamentais de POO para organizar responsabilidades: herança e polimorfismo apoiam a integração com JavaFX e a reutilização de contratos; encapsulamento e abstração protegem o estado e escondem detalhes de infraestrutura; relações de agregação e composição modelam o domínio e a UI; e a injeção de dependências delega tarefas especializadas. Esses elementos tornam o código mais estruturado, expansível e aderente às boas práticas orientadas a objetos.
