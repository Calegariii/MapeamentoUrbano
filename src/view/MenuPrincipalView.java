package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.SistemaController;
import model.Usuario;

/** Tela 2 — Menu Principal com acesso aos módulos do sistema. */
public class MenuPrincipalView extends JFrame {

    private final SistemaController controller;

    public MenuPrincipalView(SistemaController controller) {
        this.controller = controller;
        setTitle("Painel Principal — Palhoça/SC");
        setSize(820, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(UI.FUNDO);

        Usuario usuario = controller.getUsuarioLogado();

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(UI.FUNDO);
        topo.setBorder(BorderFactory.createEmptyBorder(20, 28, 6, 28));
        JLabel titulo = UI.criarTitulo("Plataforma de Mapeamento e Visualização de Dados Urbanos");
        JLabel sub = UI.criarRotulo("Bem-vindo, " + usuario.getNome()
                + "  |  Perfil: " + usuario.getClass().getSimpleName());
        sub.setForeground(UI.SECUNDARIO);
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(sub, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 2, 18, 18));
        cards.setBackground(UI.FUNDO);
        cards.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        cards.add(criarCard("🗺  Pontos de Interesse",
                "Cadastrar, listar, filtrar e manter UBS, UPAs, escolas e faculdades.",
                e -> new PontoDeInteresseView(controller).setVisible(true)));
        cards.add(criarCard("🏙  Bairros",
                "Cadastrar e consultar os bairros de Palhoça.",
                e -> new BairroView(controller).setVisible(true)));
        cards.add(criarCard("🛡  Ocorrências de Segurança",
                "Registrar ocorrências e consultar por bairro.",
                e -> new OcorrenciaView(controller).setVisible(true)));
        cards.add(criarCard("🔌  Status do Banco",
                "Verificar a conexão com o PostgreSQL.",
                e -> JOptionPane.showMessageDialog(this, controller.statusConexao(),
                        "Status do banco", JOptionPane.INFORMATION_MESSAGE)));
        add(cards, BorderLayout.CENTER);

        JButton btnSair = new JButton("Sair do sistema");
        UI.estilizarBotaoSecundario(btnSair);
        btnSair.setPreferredSize(new Dimension(140, 34));
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(UI.FUNDO);
        rodape.add(btnSair);
        btnSair.addActionListener(e -> {
            controller.logout();
            new LoginView(controller).setVisible(true);
            dispose();
        });
        add(rodape, BorderLayout.SOUTH);

        // Barra de menu — mais uma forma de navegação entre telas.
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Módulos");
        JMenuItem itemPonto = new JMenuItem("Pontos de Interesse");
        JMenuItem itemBairro = new JMenuItem("Bairros");
        JMenuItem itemOcorrencia = new JMenuItem("Ocorrências");
        itemPonto.addActionListener(e -> new PontoDeInteresseView(controller).setVisible(true));
        itemBairro.addActionListener(e -> new BairroView(controller).setVisible(true));
        itemOcorrencia.addActionListener(e -> new OcorrenciaView(controller).setVisible(true));
        menu.add(itemPonto);
        menu.add(itemBairro);
        menu.add(itemOcorrencia);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private JPanel criarCard(String titulo, String descricao, java.awt.event.ActionListener acao) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(UI.PAINEL);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel t = UI.criarTitulo(titulo);
        t.setFont(UI.TITULO.deriveFont(16f));
        JLabel d = UI.criarRotulo(descricao);
        d.setForeground(UI.SECUNDARIO);
        JButton botao = new JButton("Acessar módulo  →");
        UI.estilizarBotao(botao);
        botao.setAlignmentX(LEFT_ALIGNMENT);
        botao.addActionListener(acao);
        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        card.add(botao, BorderLayout.SOUTH);
        return card;
    }
}
