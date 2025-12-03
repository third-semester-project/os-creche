package br.com.creche.controller;

import br.com.creche.model.Usuario;
import br.com.creche.repository.UsuarioRepository;
import br.com.creche.service.AuthService;
import br.com.creche.ui.SceneFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class PerfilController {

    @FXML
    private TextField txtNome, txtEmail;
    @FXML
    private PasswordField txtNovaSenha, txtConfirmaSenha;
    @FXML
    private Label lblErro;

    private final UsuarioRepository repository = new UsuarioRepository();
    private AuthService authService;
    private Usuario usuario;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        this.usuario = authService != null ? authService.getUsuarioLogado() : null;
        preencherCampos();
    }

    private void preencherCampos() {
        if (usuario == null) return;
        txtNome.setText(usuario.getNome());
        txtEmail.setText(usuario.getEmail());
    }

    @FXML
    public void onSalvar() {
        try {
            validar();
            if (usuario == null) {
                throw new IllegalStateException("Sessao expirada. Entre novamente.");
            }

            usuario.setNome(txtNome.getText().trim());
            usuario.setEmail(txtEmail.getText().trim().toLowerCase());

            String novaSenha = txtNovaSenha.getText();
            if (novaSenha != null && !novaSenha.isBlank()) {
                String senhaHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
                usuario.setSenhaHash(senhaHash);
            } else {
                usuario.setSenhaHash(null); // mantém a atual
            }

            repository.update(usuario);

            // mantém o usuário logado sincronizado
            if (authService != null && authService.getUsuarioLogado() != null) {
                authService.getUsuarioLogado().setNome(usuario.getNome());
                authService.getUsuarioLogado().setEmail(usuario.getEmail());
                if (usuario.getSenhaHash() != null) {
                    authService.getUsuarioLogado().setSenhaHash(usuario.getSenhaHash());
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Perfil atualizado com sucesso!", ButtonType.OK);
            alert.showAndWait();
            fechar();
        } catch (Exception ex) {
            lblErro.setText(Optional.ofNullable(ex.getMessage()).orElse(ex.toString()));
            lblErro.setVisible(true);
            lblErro.setManaged(true);
        }
    }

    private void validar() {
        StringBuilder erros = new StringBuilder();

        if (isBlank(txtNome.getText())) {
            erros.append("- Nome é obrigatório.\n");
        }
        if (isBlank(txtEmail.getText())) {
            erros.append("- E-mail é obrigatório.\n");
        } else if (!isEmailValido(txtEmail.getText())) {
            erros.append("- E-mail inválido.\n");
        }

        String novaSenha = txtNovaSenha.getText();
        String confirma = txtConfirmaSenha.getText();
        if (!isBlank(novaSenha)) {
            if (novaSenha.length() < 6) {
                erros.append("- Senha deve ter no mínimo 6 caracteres.\n");
            }
            if (!novaSenha.equals(confirma)) {
                erros.append("- Confirmação de senha não coincide.\n");
            }
        }

        if (erros.length() > 0) {
            throw new IllegalArgumentException(erros.toString());
        }
    }

    @FXML
    public void onCancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isEmailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
