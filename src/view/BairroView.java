package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.SistemaController;
import model.Bairro;

/** Tela de cadastro (Create) e listagem (Read) de bairros de Palhoça. */
public class BairroView extends JFrame {

    private final SistemaController controller;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Bairro", "População estimada", "Área (km²)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public BairroView(SistemaController controller) {
        this.controller = controller;
        setTitle("Bairros de Palhoça");
        setSize(760, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UI.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(UI.FUNDO);
        topo.setBorder(BorderFactory.createEmptyBorder(16, 20, 4, 20));
        topo.add(UI.criarTitulo("Bairros de Palhoça"), BorderLayout.WEST);
        topo.add(UI.criarRotulo("Todas as localidades cadastradas"), BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        UI.estilizarTabela(tabela);
        tabela.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton btnNovo = new JButton("Cadastrar bairro");
        JButton btnAtualizar = new JButton("Atualizar lista");
        JButton btnVoltar = new JButton("Voltar ao menu");
        UI.estilizarBotao(btnNovo);
        UI.estilizarBotao(btnAtualizar);
        UI.estilizarBotaoSecundario(btnVoltar);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.setBackground(UI.FUNDO);
        botoes.add(btnNovo);
        botoes.add(btnAtualizar);
        botoes.add(btnVoltar);
        add(botoes, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> {
            new FormBairroDialog(this, controller, this::carregarTabela).setVisible(true);
        });
        btnAtualizar.addActionListener(e -> carregarTabela());
        btnVoltar.addActionListener(e -> dispose());
        carregarTabela();
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            List<Bairro> bairros = controller.listarBairros();
            for (Bairro b : bairros) {
                modelo.addRow(new Object[]{b.getIdBairro(), b.getNome(),
                        b.getPopulacaoEstimada(), String.format("%.2f", b.getAreaKm2())});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class FormBairroDialog extends JDialog {

        private final JTextField txtNome = new JTextField(22);
        private final JTextField txtPopulacao = new JTextField("0", 12);
        private final JTextField txtArea = new JTextField("1.00", 12);

        FormBairroDialog(JFrame pai, SistemaController controller, Runnable aoSalvar) {
            super(pai, "Cadastrar novo bairro", true);
            setSize(440, 260);
            setLocationRelativeTo(pai);
            setLayout(new BorderLayout(8, 8));
            getContentPane().setBackground(UI.FUNDO);

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBackground(UI.PAINEL);
            form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            form.add(UI.criarRotulo("Nome do bairro:*"));
            form.add(txtNome);
            form.add(UI.criarRotulo("População estimada:*"));
            form.add(txtPopulacao);
            form.add(UI.criarRotulo("Área em km²:*"));
            form.add(txtArea);
            add(form, BorderLayout.CENTER);

            JButton btnCancelar = new JButton("Cancelar");
            JButton btnSalvar = new JButton("Cadastrar");
            UI.estilizarBotaoSecundario(btnCancelar);
            UI.estilizarBotao(btnSalvar);
            JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            botoes.setBackground(UI.FUNDO);
            botoes.add(btnCancelar);
            botoes.add(btnSalvar);
            add(botoes, BorderLayout.SOUTH);

            btnCancelar.addActionListener(e -> dispose());
            btnSalvar.addActionListener(e -> {
                try {
                    int pop = Integer.parseInt(txtPopulacao.getText().trim());
                    double area = Double.parseDouble(txtArea.getText().replace(',', '.'));
                    controller.inserirBairro(txtNome.getText(), pop, area);
                    JOptionPane.showMessageDialog(this, "Bairro cadastrado com sucesso!");
                    dispose();
                    aoSalvar.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Erro de validação", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
}
