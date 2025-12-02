package br.com.creche.controller;

import br.com.creche.model.OrdemServico;
import br.com.creche.repository.OrdemServicoRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RelatoriosController implements DashboardController.RequiresAuthService {

    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFim;
    @FXML private ChoiceBox<String> cbStatus;
    @FXML private ChoiceBox<String> cbCategoria;

    private final OrdemServicoRepository repo = new OrdemServicoRepository();
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        cbStatus.setItems(FXCollections.observableArrayList("Todos", "ABERTA", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA"));
        cbStatus.getSelectionModel().selectFirst();

        cbCategoria.setItems(FXCollections.observableArrayList(
                "Todas", "Infraestrutura", "Limpeza", "Manutencao", "TI", "Administrativo"
        ));
        cbCategoria.getSelectionModel().selectFirst();
    }

    @FXML
    public void onImprimir() {
        LocalDate inicio = dpInicio.getValue();
        LocalDate fim = dpFim.getValue();
        String status = cbStatus.getValue();
        String categoria = cbCategoria.getValue();

        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            showAlert(Alert.AlertType.WARNING, "Intervalo inválido", "A data fim não pode ser anterior à data início.");
            return;
        }

        List<OrdemServico> lista = repo.findByFilters(inicio, fim, status, categoria);
        if (lista.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Sem dados", "Nenhuma O.S. encontrada para os filtros informados.");
            return;
        }

        boolean confirmar = confirmarImpressao(lista.size());
        if (!confirmar) return;

        String conteudo = gerarConteudo(lista, inicio, fim, status, categoria);
        imprimirConteudo(conteudo);
    }

    private boolean confirmarImpressao(int total) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Impressão");
        alert.setHeaderText(null);
        alert.setContentText("Encontradas " + total + " O.S. para os filtros. Deseja imprimir?");
        return alert.showAndWait().filter(bt -> bt == ButtonType.OK).isPresent();
    }

    private String gerarConteudo(List<OrdemServico> lista, LocalDate inicio, LocalDate fim, String status, String categoria) {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatorio de Ordens de Servico").append(System.lineSeparator());
        sb.append("Periodo: ").append(format(inicio)).append(" - ").append(format(fim)).append(System.lineSeparator());
        sb.append("Status: ").append(status != null ? status : "Todos").append(System.lineSeparator());
        sb.append("Categoria: ").append(categoria != null ? categoria : "Todas").append(System.lineSeparator());
        sb.append("Total: ").append(lista.size()).append(System.lineSeparator());
        sb.append(System.lineSeparator());

        for (OrdemServico os : lista) {
            sb.append(os.getNumero()).append(" | ")
                    .append(Optional.ofNullable(os.getTitulo()).orElse("-")).append(System.lineSeparator());
            sb.append("Status: ").append(os.getStatus())
                    .append(" | Categoria: ").append(Optional.ofNullable(os.getCategoria()).orElse("-"))
                    .append(" | Prioridade: ").append(Optional.ofNullable(os.getPrioridade()).orElse("-")).append(System.lineSeparator());
            sb.append("Abertura: ").append(format(os.getDataAbertura()))
                    .append(" | Prazo: ").append(format(os.getPrazo()))
                    .append(" | Responsavel: ").append(os.getAtribuidoPara() != null ? os.getAtribuidoPara().getNome() : "-")
                    .append(System.lineSeparator());
            if (os.getSolicitante() != null) {
                sb.append("Solicitante: ").append(os.getSolicitante()).append(System.lineSeparator());
            }
            if (os.getObservacoes() != null) {
                sb.append("Obs: ").append(os.getObservacoes()).append(System.lineSeparator());
            }
            sb.append("----").append(System.lineSeparator());
        }
        return sb.toString();
    }

    private void imprimirConteudo(String conteudo) {
        TextArea area = new TextArea(conteudo);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefWidth(600);
        area.setPrefHeight(800);

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showAlert(Alert.AlertType.ERROR, "Impressão", "Não foi possível acessar a impressora.");
            return;
        }

        var scene = cbStatus.getScene();
        var owner = scene != null ? scene.getWindow() : null;
        boolean confirmado = job.showPrintDialog(owner);
        if (!confirmado) {
            job.cancelJob();
            return;
        }

        boolean ok = job.printPage(area);
        if (ok) {
            job.endJob();
        } else {
            showAlert(Alert.AlertType.ERROR, "Impressão", "Falha ao imprimir o relatório.");
        }
    }

    private String format(LocalDate date) {
        return date != null ? df.format(date) : "-";
    }

    private String format(java.time.OffsetDateTime dateTime) {
        return dateTime != null ? df.format(dateTime) : "-";
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void setAuthService(br.com.creche.service.AuthService authService) {
        // não precisa para este fluxo, mas mantemos para compatibilidade com cache de telas
    }
}
