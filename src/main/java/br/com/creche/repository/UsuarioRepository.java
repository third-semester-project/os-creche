package br.com.creche.repository;

import br.com.creche.infra.DB;
import br.com.creche.model.OrdemServico;
import br.com.creche.model.Usuario;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    public List<Usuario> find(int limit) {
        String sql = """
            select
            id, nome, email, perfil, ativo, criado_em, atualizado_em
            from usuarios
            order by nome asc
            limit ?
        """;

        List<Usuario> list = new ArrayList<>();
        try (var conn = DB.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario user = new Usuario();
                    user.setId(rs.getLong("id"));
                    user.setNome(rs.getString("nome"));
                    user.setEmail(rs.getString("email"));
                    user.setPerfil(rs.getString("perfil"));
                    user.setAtivo(rs.getBoolean("ativo"));

                    list.add(user);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar usuários.", e);
        }
        return list;
    }

    public Optional<Usuario> findByEmail(String email) {
        String sql = "select id, nome, email, perfil, senha_hash, ativo from usuarios where email = ? and ativo = true";
        try (var conn = DB.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setPerfil(rs.getString("perfil"));
                    u.setSenhaHash(rs.getString("senha_hash"));
                    u.setAtivo(rs.getBoolean("ativo"));
                    return Optional.of(u);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário", e);
        }
        return Optional.empty();
    }
}