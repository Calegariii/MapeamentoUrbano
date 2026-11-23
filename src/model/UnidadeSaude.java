package model;

import java.util.Locale;

/** Subclasse de PontoDeInteresse para equipamentos de saúde. */
public class UnidadeSaude extends PontoDeInteresse {

    private String tipo;
    private String especialidades;
    private String horarioFuncionamento;
    private boolean possuiAtendimento24h;

    public UnidadeSaude() {
    }

    public UnidadeSaude(String nome, double latitude, double longitude,
                        String endereco, Bairro bairro, String tipo,
                        String especialidades, String horarioFuncionamento,
                        boolean possuiAtendimento24h) {
        super(nome, latitude, longitude, endereco, bairro);
        this.tipo = tipo;
        this.especialidades = especialidades;
        this.horarioFuncionamento = horarioFuncionamento;
        this.possuiAtendimento24h = possuiAtendimento24h;
    }

    @Override
    public String obterCoordenadas() {
        return String.format(Locale.US, "%.7f, %.7f", getLatitude(), getLongitude());
    }

    @Override
    public String getCategoria() {
        return "SAUDE";
    }

    @Override
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(String especialidades) {
        this.especialidades = especialidades;
    }

    public String getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(String horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public boolean isPossuiAtendimento24h() {
        return possuiAtendimento24h;
    }

    public void setPossuiAtendimento24h(boolean possuiAtendimento24h) {
        this.possuiAtendimento24h = possuiAtendimento24h;
    }
}
