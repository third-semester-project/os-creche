package br.com.creche.controller;

import br.com.creche.model.OrdemServico;
import br.com.creche.model.Perfil;
import br.com.creche.model.Usuario;
import br.com.creche.repository.OrdemServicoRepository;
import br.com.creche.service.AuthService;
import br.com.creche.ui.SceneFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class OSListaController implements DashboardController.RequiresAuthService {

    @FXML private ChoiceBox<String> cbFiltroStatusLista;
    @FXML private TextField txtBuscaOS;
    @FXML private Button btnNovaOSLista;
    @FXML private Button btnEditarOSLista;
    @FXML private Button btnApagarOSLista;

    @FXML private TableView<OrdemServico> tvOS;
    @FXML private TableColumn<OrdemServico, String> colNumero;
    @FXML private TableColumn<OrdemServico, String> colTitulo;
    @FXML private TableColumn<OrdemServico, String> colCategoria;
    @FXML private TableColumn<OrdemServico, String> colSolicitante;
    @FXML private TableColumn<OrdemServico, String> colResponsavel;
    @FXML private TableColumn<OrdemServico, String> colPrioridade;
    @FXML private TableColumn<OrdemServico, String> colStatus;
    @FXML private TableColumn<OrdemServico, String> colPrazo;

    private final OrdemServicoRepository repo = new OrdemServicoRepository();

    private AuthService authService;

    @Override
    public void setAuthService(AuthService authService) {
        this.authService = authService;
        configurarPermissoes();
    }

    @FXML
    public void initialize() {
        cbFiltroStatusLista.setItems(FXCollections.observableArrayList("Todos", "ABERTA", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA"));
        cbFiltroStatusLista.getSelectionModel().selectFirst();
        cbFiltroStatusLista.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> carregar());

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colSolicitante.setCellValueFactory(new PropertyValueFactory<>("solicitante"));

        colResponsavel.setCellValueFactory(cd -> {
            Usuario u = cd.getValue().getAtribuidoPara();
            return new SimpleStringProperty(u != null ? u.getNome() : "");
        });

        colPrioridade.setCellValueFactory(new PropertyValueFactory<>("prioridade"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrazo.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getPrazo() != null ? NovaOSControllerHelper.formatDate(cd.getValue().getPrazo()) : ""
        ));

        tvOS.setRowFactory(tv -> {
            TableRow<OrdemServico> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty()) {
                    abrirEditor(row.getItem());
                }
            });
            return row;
        });

        carregar();
    }

    private void configurarPermissoes() {
        if (authService == null || authService.getUsuarioLogado() == null) return;

        Usuario user = authService.getUsuarioLogado();
        Perfil perfil = Perfil.from(user.getPerfil());

        boolean podeCriar =
                perfil == Perfil.ADMIN ||
                        perfil == Perfil.GESTOR ||
                        perfil == Perfil.OPERADOR;

        if (btnNovaOSLista != null) {
            btnNovaOSLista.setDisable(!podeCriar);
        }
        if (btnApagarOSLista != null) {
            btnApagarOSLista.setDisable(!podeCriar);
        }
    }

    @FXML
    public void onBuscar() {
        carregar();
    }

    private void carregar() {
        List<OrdemServico> list = repo.findRecentes(500);

        String filtroStatus = cbFiltroStatusLista.getValue();
        if (filtroStatus != null && !"Todos".equalsIgnoreCase(filtroStatus)) {
            list = list.stream()
                    .filter(o -> filtroStatus.equalsIgnoreCase(o.getStatus()))
                    .collect(Collectors.toList());
        }

        String q = txtBuscaOS.getText();
        if (q != null && !q.trim().isEmpty()) {
            String term = q.trim().toLowerCase(Locale.ROOT);
            list = list.stream().filter(o -> {
                String numero = safe(o.getNumero());
                String titulo = safe(o.getTitulo());
                return numero.contains(term) || titulo.contains(term);
            }).collect(Collectors.toList());
        }

        tvOS.setItems(FXCollections.observableArrayList(list));
    }

    private String safe(String s) { return s == null ? "" : s.toLowerCase(Locale.ROOT); }

    private void abrirEditor(OrdemServico os) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editar-os.fxml"));
            Parent root = loader.load();

            EditarOSController controller = loader.getController();
            controller.setAuthService(this.authService);
            controller.setOrdemServico(os);
            controller.setOnSaved(this::carregar);

            Scene scene = new Scene(root);
            URL css = getClass().getResource("/css/app.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Editar O.S. - " + os.getNumero());
            stage.setScene(scene);
            SceneFactory.applyAppIcon(stage);
            stage.initOwner(tvOS.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Falha ao abrir editor: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onNovaOS() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nova-os.fxml"));
            Parent root = loader.load();

            NovaOSController controller = loader.getController();
            controller.setAuthService(this.authService);

            // 🔥 IMPORTANTE: recarrega a lista após salvar
            controller.setOnSaved(this::carregar);

            Scene scene = SceneFactory.createScene(root);

            Stage stage = new Stage();
            stage.setTitle("Nova Ordem de Serviço");
            stage.setScene(scene);
            SceneFactory.applyAppIcon(stage);
            stage.initOwner(btnNovaOSLista.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.sizeToScene();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Falha ao abrir Nova O.S.: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onEditarOS() {
        OrdemServico os = tvOS.getSelectionModel().getSelectedItem();
        if (os != null) {
            abrirEditor(os);
        } else {
            new Alert(Alert.AlertType.WARNING, "Selecione uma Ordem de Servico na tabela para editar.").showAndWait();
        }
    }

    @FXML
    public void onApagarOS() {
        OrdemServico selecionada = tvOS.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma Ordem de Servico na tabela para apagar.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Exclusao");
        confirm.setHeaderText("Deseja realmente apagar esta O.S.?");
        confirm.setContentText("Numero: " + selecionada.getNumero());

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            repo.delete(selecionada.getId());
            new Alert(Alert.AlertType.INFORMATION, "A O.S. foi apagada com sucesso.").showAndWait();
            carregar();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao apagar O.S.: " + ex.getMessage()).showAndWait();
        }
    }

    /**
     * Helper to keep date formatting consistent without duplicating formatter.
     */
    private static class NovaOSControllerHelper {
        private static final java.time.format.DateTimeFormatter DF = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        static String formatDate(java.time.OffsetDateTime date) {
            return DF.format(date);
        }
    }
}
