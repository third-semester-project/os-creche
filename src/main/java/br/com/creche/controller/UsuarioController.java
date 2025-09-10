package br.com.creche.controller;

import br.com.creche.model.OrdemServico;
import br.com.creche.model.Usuario;
import br.com.creche.repository.UsuarioRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UsuarioController {

//    @FXML private TextField txtBuscaUsuario;
//    @FXML private Button btnNovoUsuario;
//    @FXML private Button btnBuscarUsuario;

    @FXML private TableView<Usuario> tvUsuarios;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colPapeis;
    @FXML private TableColumn<Usuario, String> colAtivo;

    private UsuarioRepository usuarioRepository = new UsuarioRepository();

    @FXML
    public void initialize() {
        carregar();

        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPapeis.setCellValueFactory(new PropertyValueFactory<>("papel"));
        colAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));
    }

    public void carregar() {
        List<Usuario> list = usuarioRepository.find(500);

        tvUsuarios.setItems(FXCollections.observableArrayList(list));
    }
}
