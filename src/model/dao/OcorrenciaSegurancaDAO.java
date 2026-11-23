package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Bairro;
import model.Cidadao;
import model.OcorrenciaSeguranca;
import util.ConexaoBD;

/** DAO de ocorrências de segurança, com listagem por bairro e moderação. */
public class OcorrenciaSegurancaDAO {

    private static final String SELECT_BASE =
            "SELECT o.id_ocorrencia, o.tipo_evento, o.data_hora, o.nivel_gravidade, "
            + "o.status_aprovacao, b.id_bairro, b.nome AS nome_bairro, "
            + "b.populacao_estimada, b.area_km2, u.id_usuario, u.nome AS nome_cidadao, "
            + "u.email, c.cpf "
            + "FROM ocorrencia_seguranca o "
            + "JOIN bairro b ON o.id_bairro = b.id_bairro "
            + "JOIN cidadao c ON o.id_usuario_cidadao = c.id_usuario "
            + "JOIN usuario u ON c.id_usuario = u.id_usuario ";

    /** Lista todas as ocorrências de um bairro (filtro obrigatório da RF05). */
    public List<OcorrenciaSeguranca> listarPorBairro(Long idBairro) throws SQLException {
        String sql = SELECT_BASE + "WHERE o.id_bairro = ? ORDER BY o.data_hora DESC";
        List<OcorrenciaSeguranca> lista = new ArrayList<>();
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idBairro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<OcorrenciaSeguranca> listarTodas() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY o.data_hora DESC";
        List<OcorrenciaSeguranca> lista = new ArrayList<>();
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public OcorrenciaSeguranca inserir(OcorrenciaSeguranca o) throws SQLException {
        String sql = "INSERT INTO ocorrencia_seguranca "
                + "(id_usuario_cidadao, id_bairro, tipo_evento, data_hora, "
                + "nivel_gravidade, status_aprovacao) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, o.getCidadao().getIdUsuario());
            ps.setLong(2, o.getBairro().getIdBairro());
            ps.setString(3, o.getTipoEvento());
            ps.setTimestamp(4, Timestamp.valueOf(o.getDataHora()));
            ps.setString(5, o.getNivelGravidade());
            ps.setBoolean(6, o.isStatusAprovacao());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    o.setIdOcorrencia(rs.getLong(1));
                }
            }
        }
        return o;
    }

    /** Aprovar/reprovar — operação restrita ao Administrador no controller. */
    public void atualizarStatus(Long id, boolean aprovado) throws SQLException {
        String sql = "UPDATE ocorrencia_seguranca SET status_aprovacao = ? WHERE id_ocorrencia = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, aprovado);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private OcorrenciaSeguranca mapear(ResultSet rs) throws SQLException {
        Bairro b = new Bairro(rs.getString("nome_bairro"),
                rs.getInt("populacao_estimada"), rs.getDouble("area_km2"));
        b.setIdBairro(rs.getLong("id_bairro"));

        Cidadao c = new Cidadao(rs.getString("nome_cidadao"), rs.getString("email"),
                "", rs.getString("cpf"));
        c.setIdUsuario(rs.getLong("id_usuario"));

        OcorrenciaSeguranca o = new OcorrenciaSeguranca(c, b, rs.getString("tipo_evento"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                rs.getString("nivel_gravidade"), rs.getBoolean("status_aprovacao"));
        o.setIdOcorrencia(rs.getLong("id_ocorrencia"));
        return o;
    }
}
