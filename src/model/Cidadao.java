package model;

/** Usuário cidadão: pode registrar ocorrências de segurança. */
public class Cidadao extends Usuario {

    private String cpf;

    public Cidadao() {
    }

    public Cidadao(String nome, String email, String senhaHash, String cpf) {
        super(nome, email, senhaHash);
        this.cpf = cpf;
    }

    @Override
    public String autenticar() {
        return "Cidadão " + getNome() + " autenticado com sucesso.";
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
