package model;

/** Usuário gestor público: vinculado a um órgão municipal. */
public class GestorPublico extends Usuario {

    private String cargo;
    private String orgaoVinculado;

    public GestorPublico() {
    }

    public GestorPublico(String nome, String email, String senhaHash,
                         String cargo, String orgaoVinculado) {
        super(nome, email, senhaHash);
        this.cargo = cargo;
        this.orgaoVinculado = orgaoVinculado;
    }

    @Override
    public String autenticar() {
        return "Gestor " + getNome() + " — " + cargo + " (" + orgaoVinculado
                + ") autenticado com sucesso.";
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getOrgaoVinculado() {
        return orgaoVinculado;
    }

    public void setOrgaoVinculado(String orgaoVinculado) {
        this.orgaoVinculado = orgaoVinculado;
    }
}
