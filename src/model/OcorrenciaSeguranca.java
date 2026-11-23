package model;

import java.time.LocalDateTime;

/** Registro de ocorrência de segurança vinculado a bairro e cidadão. */
public class OcorrenciaSeguranca {

    private Long idOcorrencia;
    private Cidadao cidadao;
    private Bairro bairro;
    private String tipoEvento;
    private LocalDateTime dataHora;
    private String nivelGravidade;
    private boolean statusAprovacao;

    public OcorrenciaSeguranca() {
    }

    public OcorrenciaSeguranca(Cidadao cidadao, Bairro bairro, String tipoEvento,
                               LocalDateTime dataHora, String nivelGravidade,
                               boolean statusAprovacao) {
        this.cidadao = cidadao;
        this.bairro = bairro;
        this.tipoEvento = tipoEvento;
        this.dataHora = dataHora;
        this.nivelGravidade = nivelGravidade;
        this.statusAprovacao = statusAprovacao;
    }

    public Long getIdOcorrencia() {
        return idOcorrencia;
    }

    public void setIdOcorrencia(Long idOcorrencia) {
        this.idOcorrencia = idOcorrencia;
    }

    public Cidadao getCidadao() {
        return cidadao;
    }

    public void setCidadao(Cidadao cidadao) {
        this.cidadao = cidadao;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getNivelGravidade() {
        return nivelGravidade;
    }

    public void setNivelGravidade(String nivelGravidade) {
        this.nivelGravidade = nivelGravidade;
    }

    public boolean isStatusAprovacao() {
        return statusAprovacao;
    }

    public void setStatusAprovacao(boolean statusAprovacao) {
        this.statusAprovacao = statusAprovacao;
    }
}
