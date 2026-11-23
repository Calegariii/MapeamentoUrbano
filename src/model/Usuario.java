package model;

import java.time.LocalDate;

/**
 * Classe abstrata que representa um usuário da plataforma.
 * Demonstra encapsulamento (atributos privados + getters/setters) e serve
 * de base para a herança: Cidadao, Administrador e GestorPublico.
 *
 * Projeto Integrador — Plataforma de Mapeamento e Visualização de Dados Urbanos.
 */
public abstract class Usuario {

    private Long idUsuario;
    private String nome;
    private String email;
    private String senhaHash;
    private LocalDate dataCadastro;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senhaHash) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.dataCadastro = LocalDate.now();
    }

    /**
     * Método polimórfico: cada subclasse retorna uma mensagem específica
     * do seu perfil. Chamado no Login para evidenciar o polimorfismo.
     *
     * @return mensagem de autenticação do perfil
     */
    public abstract String autenticar();

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return nome;
    }
}
