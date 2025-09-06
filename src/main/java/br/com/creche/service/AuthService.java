package br.com.creche.service;

import br.com.creche.model.Usuario;
import br.com.creche.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UsuarioRepository repo = new UsuarioRepository();
    private Usuario usuarioLogado;

    public boolean login(String email, String senha) {
        System.out.println("[LOGIN] email=" + email);
        System.out.println(BCrypt.hashpw("fatec", BCrypt.gensalt()));
        Optional<Usuario> opt = repo.findByEmail(email);
        System.out.println("[LOGIN] usuarioEncontrado=" + opt.isPresent());
        if (opt.isPresent()) {
            Usuario u = opt.get();
            System.out.println("[LOGIN] perfil=" + u.getPerfil() + ", ativo=" + u.isAtivo());
            boolean ok = org.mindrot.jbcrypt.BCrypt.checkpw(senha, u.getSenhaHash());
            System.out.println("[LOGIN] checkpw=" + ok);
            if (ok) {
                usuarioLogado = u;
                System.out.println(getUsuarioLogado());
                System.out.println("logado: " + usuarioLogado);
                return true;
            }
        }
        return false;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}