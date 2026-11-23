package model;

/**
 * Entidade principal do sistema. Abstrata para permitir a herança
 * UnidadeSaude / InstituicaoEnsino e o polimorfismo em obterCoordenadas().
 */
public abstract class PontoDeInteresse {

    private Long idPonto;
    private String nome;
    private double latitude;
    private double longitude;
    private String endereco;
    private Bairro bairro;

    public PontoDeInteresse() {
    }

    public PontoDeInteresse(String nome, double latitude, double longitude,
                            String endereco, Bairro bairro) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.endereco = endereco;
        this.bairro = bairro;
    }

    /** Método polimórfico de coordenadas, redefinido nas subclasses. */
    public abstract String obterCoordenadas();

    /** Categoria do ponto: SAUDE ou EDUCACAO. */
    public abstract String getCategoria();

    /** Tipo detalhado do ponto (UBS, UPA, Escola, Universidade...). */
    public abstract String getTipo();

    public Long getIdPonto() {
        return idPonto;
    }

    public void setIdPonto(Long idPonto) {
        this.idPonto = idPonto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }
}
