package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Bairro;
import util.ConexaoBD;

/** DAO da entidade Bairro (Create e Read). */
public class BairroDAO {

    /** Lista todos os bairros ordenados por nome. */
    public List<Bairro> listarTodos() throws SQLException {
        String sql = "SELECT id_bairro, nome, populacao_estimada, area_km2 "
                + "FROM bairro ORDER BY nome";
        List<Bairro> lista = new ArrayList<>();
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Bairro buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id_bairro, nome, populacao_estimada, area_km2 "
                + "FROM bairro WHERE id_bairro = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Insere um bairro e devolve o objeto com o id gerado. */
    public Bairro inserir(Bairro bairro) throws SQLException {
        String sql = "INSERT INTO bairro (nome, populacao_estimada, area_km2) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bairro.getNome());
            ps.setInt(2, bairro.getPopulacaoEstimada());
            ps.setDouble(3, bairro.getAreaKm2());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bairro.setIdBairro(rs.getLong(1));
                }
            }
        }
        return bairro;
    }

    private Bairro mapear(ResultSet rs) throws SQLException {
        Bairro b = new Bairro(rs.getString("nome"),
                rs.getInt("populacao_estimada"), rs.getDouble("area_km2"));
        b.setIdBairro(rs.getLong("id_bairro"));
        return b;
    }
}
