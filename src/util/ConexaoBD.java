package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária responsável por abrir conexões com o PostgreSQL.
 * A URL, o usuário e a senha podem ser sobrescritos por propriedades do
 * sistema (-Ddb.url, -Ddb.user, -Ddb.password), facilitando a execução
 * em outra máquina sem alterar o código-fonte.
 */
public final class ConexaoBD {

    private static final String URL = System.getProperty(
            "db.url", "jdbc:postgresql://localhost:5432/mapeamento_urbano");
    private static final String USUARIO = System.getProperty("db.user", "admin");
    private static final String SENHA = System.getProperty("db.password", "admin");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "Driver JDBC do PostgreSQL não encontrado. "
                    + "Adicione lib/postgresql-42.7.4.jar ao Build Path.");
        }
    }

    private ConexaoBD() {
    }

    /** @return uma nova conexão com o banco de dados */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    /** Testa a conexão e retorna uma mensagem amigável. */
    public static String testarConexao() {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            st.execute("SELECT 1");
            return "Conexão com PostgreSQL OK: " + URL;
        } catch (SQLException e) {
            return "Falha na conexão (" + URL + "): " + e.getMessage();
        }
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUsuario() {
        return USUARIO;
    }
}
