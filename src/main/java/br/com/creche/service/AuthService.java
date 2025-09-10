package br.com.creche.service;

import br.com.creche.model.Usuario;
import br.com.creche.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UsuarioRepository repo = new UsuarioRepository();
    private Usuario usuarioLogado;

    public boolean login(String email, String senha) {
        Optional<Usuario> opt = repo.findByEmail(email);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            boolean ok = org.mindrot.jbcrypt.BCrypt.checkpw(senha, u.getSenhaHash());
            if (ok) {
                usuarioLogado = u;
                return true;
            }
        }
        return false;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}