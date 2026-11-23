package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Administrador;
import model.Cidadao;
import model.GestorPublico;
import model.Usuario;
import util.ConexaoBD;

/**
 * DAO de usuários. No autenticar(), o tipo concreto é decidido em tempo de
 * execução — ponto importante para demonstrar polimorfismo no login.
 */
public class UsuarioDAO {

    /** Autentica e devolve Cidadao, Administrador ou GestorPublico. */
    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT id_usuario, nome, email, senha_hash FROM usuario "
                + "WHERE email = ? AND senha_hash = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Long id = rs.getLong("id_usuario");
                String nome = rs.getString("nome");
                String mail = rs.getString("email");
                String hash = rs.getString("senha_hash");

                Usuario u = buscarSubtipo(conn, id, nome, mail, hash);
                return u == null ? new Cidadao(nome, mail, hash, "") : u;
            }
        }
    }

    private Usuario buscarSubtipo(Connection conn, Long id, String nome,
                                  String email, String senha) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nivel_acesso FROM administrador WHERE id_usuario = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Administrador(nome, email, senha, rs.getInt("nivel_acesso"));
                }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT cpf FROM cidadao WHERE id_usuario = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cidadao c = new Cidadao(nome, email, senha, rs.getString("cpf"));
                    c.setIdUsuario(id);
                    return c;
                }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT cargo, orgao_vinculado FROM gestor_publico WHERE id_usuario = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GestorPublico(nome, email, senha,
                            rs.getString("cargo"), rs.getString("orgao_vinculado"));
                }
            }
        }
        return null;
    }

    /** Lista cidadãos para o combo de registro de ocorrência. */
    public List<Cidadao> listarCidadaos() throws SQLException {
        String sql = "SELECT u.id_usuario, u.nome, u.email, c.cpf "
                + "FROM cidadao c JOIN usuario u ON c.id_usuario = u.id_usuario "
                + "ORDER BY u.nome";
        List<Cidadao> lista = new ArrayList<>();
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cidadao c = new Cidadao(rs.getString("nome"), rs.getString("email"),
                        "", rs.getString("cpf"));
                c.setIdUsuario(rs.getLong("id_usuario"));
                lista.add(c);
            }
        }
        return lista;
    }
}
