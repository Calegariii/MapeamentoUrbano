package model;

/** Usuário administrador: pode moderar ocorrências. */
public class Administrador extends Usuario {

    private int nivelAcesso;

    public Administrador() {
    }

    public Administrador(String nome, String email, String senhaHash, int nivelAcesso) {
        super(nome, email, senhaHash);
        this.nivelAcesso = nivelAcesso;
    }

    @Override
    public String autenticar() {
        return "Administrador " + getNome() + " (nível " + nivelAcesso
                + ") autenticado com sucesso.";
    }

    public int getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(int nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }
}
