package br.com.creche.model;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String perfil;   // ADMIN | GESTOR | OPERADOR
    private String senhaHash;
    private boolean ativo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Perfil getPerfilEnum() {
        return Perfil.from(this.perfil);
    }

    @Override
    public String toString() {
        return this.nome;
    }
}