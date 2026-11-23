package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Bairro;
import model.InstituicaoEnsino;
import model.PontoDeInteresse;
import model.UnidadeSaude;
import util.ConexaoBD;

/** DAO com CRUD completo de pontos de interesse e filtro por tipo. */
public class PontoDeInteresseDAO {

    private static final String SELECT_BASE =
            "SELECT p.id_ponto, p.nome, p.latitude, p.longitude, p.endereco, p.categoria, "
            + "b.id_bairro, b.nome AS nome_bairro, b.populacao_estimada, b.area_km2, "
            + "u.tipo AS tipo_saude, u.especialidades, u.horario_funcionamento, "
            + "u.possui_atendimento_24h, i.tipo AS tipo_ensino, i.esfera, "
            + "i.niveis_atendidos, i.possui_vagas_abertas "
            + "FROM ponto_de_interesse p "
            + "JOIN bairro b ON p.id_bairro = b.id_bairro "
            + "LEFT JOIN unidade_saude u ON p.id_ponto = u.id_ponto "
            + "LEFT JOIN instituicao_ensino i ON p.id_ponto = i.id_ponto ";

    /** Lista todos ou, se filtro != null, apenas um tipo (UBS, UPA, Escola...). */
    public List<PontoDeInteresse> listar(String filtroTipo) throws SQLException {
        String sql = SELECT_BASE
                + "WHERE (? IS NULL OR COALESCE(u.tipo, i.tipo) = ?) "
                + "ORDER BY p.categoria, b.nome, p.nome";
        List<PontoDeInteresse> lista = new ArrayList<>();
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filtroTipo);
            ps.setString(2, filtroTipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public PontoDeInteresse buscarPorId(Long id) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.id_ponto = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Insert polimórfico: grava na tabela-base e na tabela da subclasse. */
    public PontoDeInteresse inserir(PontoDeInteresse p) throws SQLException {
        String sql = "INSERT INTO ponto_de_interesse "
                + "(nome, latitude, longitude, endereco, id_bairro, categoria) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setDouble(2, p.getLatitude());
            ps.setDouble(3, p.getLongitude());
            ps.setString(4, p.getEndereco());
            ps.setLong(5, p.getBairro().getIdBairro());
            ps.setString(6, p.getCategoria());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdPonto(rs.getLong(1));
                }
            }
            inserirSubtipo(conn, p);
        }
        return p;
    }

    public void atualizar(PontoDeInteresse p) throws SQLException {
        String sql = "UPDATE ponto_de_interesse SET nome = ?, latitude = ?, longitude = ?, "
                + "endereco = ?, id_bairro = ?, categoria = ? WHERE id_ponto = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setDouble(2, p.getLatitude());
            ps.setDouble(3, p.getLongitude());
            ps.setString(4, p.getEndereco());
            ps.setLong(5, p.getBairro().getIdBairro());
            ps.setString(6, p.getCategoria());
            ps.setLong(7, p.getIdPonto());
            ps.executeUpdate();

            // Se a categoria mudou, remove o registro da subclasse antiga.
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM unidade_saude WHERE id_ponto = ?")) {
                del.setLong(1, p.getIdPonto());
                del.executeUpdate();
            }
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM instituicao_ensino WHERE id_ponto = ?")) {
                del.setLong(1, p.getIdPonto());
                del.executeUpdate();
            }
            inserirSubtipo(conn, p);
        }
    }

    public void excluir(Long id) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM unidade_saude WHERE id_ponto = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM instituicao_ensino WHERE id_ponto = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM ponto_de_interesse WHERE id_ponto = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        }
    }

    private void inserirSubtipo(Connection conn, PontoDeInteresse p) throws SQLException {
        if (p instanceof UnidadeSaude) {
            UnidadeSaude u = (UnidadeSaude) p;
            String sql = "INSERT INTO unidade_saude "
                    + "(id_ponto, tipo, especialidades, horario_funcionamento, possui_atendimento_24h) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, p.getIdPonto());
                ps.setString(2, u.getTipo());
                ps.setString(3, u.getEspecialidades());
                ps.setString(4, u.getHorarioFuncionamento());
                ps.setBoolean(5, u.isPossuiAtendimento24h());
                ps.executeUpdate();
            }
        } else if (p instanceof InstituicaoEnsino) {
            InstituicaoEnsino i = (InstituicaoEnsino) p;
            String sql = "INSERT INTO instituicao_ensino "
                    + "(id_ponto, tipo, esfera, niveis_atendidos, possui_vagas_abertas) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, p.getIdPonto());
                ps.setString(2, i.getTipo());
                ps.setString(3, i.getEsfera());
                ps.setString(4, i.getNiveisAtendidos());
                ps.setBoolean(5, i.isPossuiVagasAbertas());
                ps.executeUpdate();
            }
        } else {
            throw new SQLException("Subtipo de ponto de interesse desconhecido.");
        }
    }

    private PontoDeInteresse mapear(ResultSet rs) throws SQLException {
        Bairro b = new Bairro(rs.getString("nome_bairro"),
                rs.getInt("populacao_estimada"), rs.getDouble("area_km2"));
        b.setIdBairro(rs.getLong("id_bairro"));

        PontoDeInteresse p;
        if ("SAUDE".equals(rs.getString("categoria"))) {
            UnidadeSaude u = new UnidadeSaude(rs.getString("nome"),
                    rs.getDouble("latitude"), rs.getDouble("longitude"),
                    rs.getString("endereco"), b, rs.getString("tipo_saude"),
                    rs.getString("especialidades"), rs.getString("horario_funcionamento"),
                    rs.getBoolean("possui_atendimento_24h"));
            p = u;
        } else {
            p = new InstituicaoEnsino(rs.getString("nome"),
                    rs.getDouble("latitude"), rs.getDouble("longitude"),
                    rs.getString("endereco"), b, rs.getString("tipo_ensino"),
                    rs.getString("esfera"), rs.getString("niveis_atendidos"),
                    rs.getBoolean("possui_vagas_abertas"));
        }
        p.setIdPonto(rs.getLong("id_ponto"));
        return p;
    }
}
