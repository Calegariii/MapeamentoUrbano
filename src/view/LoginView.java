package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controller.SistemaController;
import model.Usuario;

/** Tela 1 — Login. Demonstra transição para o Menu Principal. */
public class LoginView extends JFrame {

    private final SistemaController controller;
    private final JTextField txtEmail = new JTextField("admin@palhoca.sc.gov.br", 20);
    private final JPasswordField txtSenha = new JPasswordField("admin", 20);

    public LoginView(SistemaController controller) {
        this.controller = controller;
        setTitle("Plataforma de Mapeamento Urbano — Acesso ao Sistema");
        setSize(560, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBackground(UI.FUNDO);

        // Cabeçalho
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(UI.FUNDO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(28, 28, 10, 28));
        JLabel titulo = UI.criarTitulo("Plataforma de Mapeamento");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitulo = UI.criarRotulo("e Visualização de Dados Urbanos — Palhoça/SC");
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        cabecalho.add(titulo, BorderLayout.NORTH);
        cabecalho.add(subtitulo, BorderLayout.SOUTH);
        add(cabecalho, BorderLayout.NORTH);

        // Formulário
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(UI.PAINEL);
        painel.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 6, 8, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;

        g.gridy = 0;
        painel.add(UI.criarRotulo("E-mail:"), g);
        g.gridy = 1;
        txtEmail.setFont(UI.NORMAL);
        painel.add(txtEmail, g);

        g.gridy = 2;
        painel.add(UI.criarRotulo("Senha:"), g);
        g.gridy = 3;
        txtSenha.setFont(UI.NORMAL);
        painel.add(txtSenha, g);

        g.gridy = 4;
        JButton btnEntrar = new JButton("Entrar no sistema");
        UI.estilizarBotao(btnEntrar);
        btnEntrar.setPreferredSize(new Dimension(220, 38));
        painel.add(btnEntrar, g);

        g.gridy = 5;
        JLabel dica = UI.criarRotulo("Demonstração: admin@palhoca.sc.gov.br / admin");
        dica.setForeground(UI.SECUNDARIO);
        dica.setFont(UI.NORMAL.deriveFont(11f));
        painel.add(dica, g);

        g.gridy = 6;
        JButton btnConexao = new JButton("Testar conexão com o banco");
        UI.estilizarBotaoSecundario(btnConexao);
        painel.add(btnConexao, g);
        add(painel, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBackground(UI.FUNDO);
        JLabel info = UI.criarRotulo("Integrantes: Gabriel Calegari • Gabriel Dario • Luiz Fernando Gobbi");
        info.setForeground(UI.SECUNDARIO);
        info.setFont(UI.NORMAL.deriveFont(11f));
        rodape.add(info);
        add(rodape, BorderLayout.SOUTH);

        btnEntrar.addActionListener(e -> entrar());
        txtSenha.addActionListener(e -> entrar());
        btnConexao.addActionListener(e ->
                JOptionPane.showMessageDialog(this, controller.statusConexao(),
                        "Status do banco", JOptionPane.INFORMATION_MESSAGE));

        getContentPane().setBackground(UI.FUNDO);
    }

    private void entrar() {
        try {
            Usuario usuario = controller.login(txtEmail.getText(),
                    new String(txtSenha.getPassword()));
            // Polimorfismo: cada perfil retorna sua própria mensagem.
            JOptionPane.showMessageDialog(this, usuario.autenticar(),
                    "Acesso concedido", JOptionPane.INFORMATION_MESSAGE);
            new MenuPrincipalView(controller).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Falha no login", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            // mantém o LookAndFeel padrão do Java
        }
        SwingUtilities.invokeLater(() ->
                new LoginView(new SistemaController()).setVisible(true));
    }
}
