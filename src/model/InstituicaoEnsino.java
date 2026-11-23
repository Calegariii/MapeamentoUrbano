package model;

import java.util.Locale;

/** Subclasse de PontoDeInteresse para instituições de ensino. */
public class InstituicaoEnsino extends PontoDeInteresse {

    private String tipo;
    private String esfera;
    private String niveisAtendidos;
    private boolean possuiVagasAbertas;

    public InstituicaoEnsino() {
    }

    public InstituicaoEnsino(String nome, double latitude, double longitude,
                             String endereco, Bairro bairro, String tipo,
                             String esfera, String niveisAtendidos,
                             boolean possuiVagasAbertas) {
        super(nome, latitude, longitude, endereco, bairro);
        this.tipo = tipo;
        this.esfera = esfera;
        this.niveisAtendidos = niveisAtendidos;
        this.possuiVagasAbertas = possuiVagasAbertas;
    }

    @Override
    public String obterCoordenadas() {
        return String.format(Locale.US, "%.7f, %.7f", getLatitude(), getLongitude());
    }

    @Override
    public String getCategoria() {
        return "EDUCACAO";
    }

    @Override
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEsfera() {
        return esfera;
    }

    public void setEsfera(String esfera) {
        this.esfera = esfera;
    }

    public String getNiveisAtendidos() {
        return niveisAtendidos;
    }

    public void setNiveisAtendidos(String niveisAtendidos) {
        this.niveisAtendidos = niveisAtendidos;
    }

    public boolean isPossuiVagasAbertas() {
        return possuiVagasAbertas;
    }

    public void setPossuiVagasAbertas(boolean possuiVagasAbertas) {
        this.possuiVagasAbertas = possuiVagasAbertas;
    }
}
