package model;

/** Entidade Bairro de Palhoça. */
public class Bairro {

    private Long idBairro;
    private String nome;
    private int populacaoEstimada;
    private double areaKm2;

    public Bairro() {
    }

    public Bairro(String nome, int populacaoEstimada, double areaKm2) {
        this.nome = nome;
        this.populacaoEstimada = populacaoEstimada;
        this.areaKm2 = areaKm2;
    }

    public Long getIdBairro() {
        return idBairro;
    }

    public void setIdBairro(Long idBairro) {
        this.idBairro = idBairro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPopulacaoEstimada() {
        return populacaoEstimada;
    }

    public void setPopulacaoEstimada(int populacaoEstimada) {
        this.populacaoEstimada = populacaoEstimada;
    }

    public double getAreaKm2() {
        return areaKm2;
    }

    public void setAreaKm2(double areaKm2) {
        this.areaKm2 = areaKm2;
    }

    @Override
    public String toString() {
        return nome;
    }
}
