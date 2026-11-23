package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controller.SistemaController;
import model.Bairro;
import model.Cidadao;
import model.OcorrenciaSeguranca;

/** Tela de registro e consulta de ocorrências de segurança por bairro. */
public class OcorrenciaView extends JFrame {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final SistemaController controller;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Bairro", "Tipo de evento", "Data/hora",
                    "Gravidade", "Situação", "Registrante"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final JComboBox<Bairro> filtroBairro = new JComboBox<>();

    public OcorrenciaView(SistemaController controller) {
        this.controller = controller;
        setTitle("Ocorrências de Segurança — Palhoça/SC");
        setSize(1080, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UI.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(UI.FUNDO);
        topo.setBorder(BorderFactory.createEmptyBorder(16, 20, 4, 20));
        topo.add(UI.criarTitulo("Ocorrências de Segurança"), BorderLayout.WEST);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filtros.setBackground(UI.FUNDO);
        filtros.add(UI.criarRotulo("Filtrar por bairro:"));
        filtroBairro.setFont(UI.NORMAL);
        filtros.add(filtroBairro);
        JButton btnFiltrar = new JButton("Consultar");
        UI.estilizarBotaoSecundario(btnFiltrar);
        filtros.add(btnFiltrar);
        topo.add(filtros, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        UI.estilizarTabela(tabela);
        tabela.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton btnNovo = new JButton("Registrar ocorrência");
        JButton btnAprovar = new JButton("Aprovar");
        JButton btnReprovar = new JButton("Reprovar");
        JButton btnAtualizar = new JButton("Atualizar lista");
        JButton btnVoltar = new JButton("Voltar ao menu");
        for (JButton b : new JButton[]{btnNovo, btnAprovar, btnAtualizar}) {
            UI.estilizarBotao(b);
        }
        UI.estilizarBotaoSecundario(btnReprovar);
        btnReprovar.setForeground(UI.PERIGO);
        UI.estilizarBotaoSecundario(btnVoltar);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.setBackground(UI.FUNDO);
        botoes.add(btnNovo);
        botoes.add(btnAprovar);
        botoes.add(btnReprovar);
        botoes.add(btnAtualizar);
        botoes.add(btnVoltar);
        add(botoes, BorderLayout.SOUTH);

        if (!controller.isAdministrador()) {
            btnAprovar.setEnabled(false);
            btnReprovar.setEnabled(false);
            btnAprovar.setToolTipText("Apenas Administrador pode moderar.");
            btnReprovar.setToolTipText("Apenas Administrador pode moderar.");
        }

        btnNovo.addActionListener(e -> {
            try {
                new FormOcorrenciaDialog(this, controller, this::carregarTabela).setVisible(true);
            } catch (Exception ex) {
                erro(ex);
            }
        });
        btnAprovar.addActionListener(e -> alterarStatus(true));
        btnReprovar.addActionListener(e -> alterarStatus(false));
        btnAtualizar.addActionListener(e -> carregarTabela());
        btnFiltrar.addActionListener(e -> carregarTabela());
        filtroBairro.addActionListener(e -> carregarTabela());
        btnVoltar.addActionListener(e -> dispose());

        carregarFiltro();
        carregarTabela();
    }

    private void carregarFiltro() {
        filtroBairro.addItem(null);
        try {
            for (Bairro b : controller.listarBairros()) {
                filtroBairro.addItem(b);
            }
        } catch (Exception ex) {
            erro(ex);
        }
        filtroBairro.setSelectedIndex(0);
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            Bairro selecionado = (Bairro) filtroBairro.getSelectedItem();
            List<OcorrenciaSeguranca> lista = controller.listarOcorrencias(
                    selecionado == null ? null : selecionado.getIdBairro());
            for (OcorrenciaSeguranca o : lista) {
                modelo.addRow(new Object[]{
                        o.getIdOcorrencia(), o.getBairro().getNome(), o.getTipoEvento(),
                        o.getDataHora().format(FORMATO), o.getNivelGravidade(),
                        o.isStatusAprovacao() ? "Aprovada" : "Pendente",
                        o.getCidadao().getNome()});
            }
        } catch (Exception ex) {
            erro(ex);
        }
    }

    private void alterarStatus(boolean aprovado) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma ocorrência na tabela.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long id = ((Number) modelo.getValueAt(
                tabela.convertRowIndexToModel(linha), 0)).longValue();
        try {
            controller.alterarStatus(id, aprovado);
            JOptionPane.showMessageDialog(this, aprovado
                    ? "Ocorrência aprovada." : "Ocorrência reprovada.");
            carregarTabela();
        } catch (Exception ex) {
            erro(ex);
        }
    }

    private void erro(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private static class FormOcorrenciaDialog extends JDialog {

        private final JComboBox<Cidadao> cboCidadao = new JComboBox<>();
        private final JComboBox<Bairro> cboBairro = new JComboBox<>();
        private final javax.swing.JTextField txtTipo = new javax.swing.JTextField(26);
        private final JComboBox<String> cboGravidade =
                new JComboBox<>(new String[]{"BAIXA", "MEDIA", "ALTA"});
        private final Runnable aoSalvar;

        FormOcorrenciaDialog(JFrame pai, SistemaController controller,
                             Runnable aoSalvar) throws Exception {
            super(pai, "Registrar Ocorrência de Segurança", true);
            this.aoSalvar = aoSalvar;
            setSize(520, 330);
            setLocationRelativeTo(pai);
            setLayout(new BorderLayout(8, 8));
            getContentPane().setBackground(UI.FUNDO);

            for (Cidadao c : controller.listarCidadaos()) {
                cboCidadao.addItem(c);
            }
            for (Bairro b : controller.listarBairros()) {
                cboBairro.addItem(b);
            }

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBackground(UI.PAINEL);
            form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            form.add(UI.criarRotulo("Bairro:*"));
            form.add(cboBairro);
            form.add(UI.criarRotulo("Cidadão registrante:*"));
            form.add(cboCidadao);
            form.add(UI.criarRotulo("Tipo de evento:*"));
            form.add(txtTipo);
            form.add(UI.criarRotulo("Nível de gravidade:*"));
            form.add(cboGravidade);
            form.add(new JLabel(" "));
            JLabel aviso = UI.criarRotulo("RN02: não informe vítimas nem endereços residenciais.");
            aviso.setForeground(UI.SECUNDARIO);
            aviso.setFont(UI.NORMAL.deriveFont(10f));
            form.add(aviso);
            add(form, BorderLayout.CENTER);

            JButton btnCancelar = new JButton("Cancelar");
            JButton btnSalvar = new JButton("Registrar");
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
                    controller.registrarOcorrencia(
                            (Cidadao) cboCidadao.getSelectedItem(),
                            (Bairro) cboBairro.getSelectedItem(),
                            txtTipo.getText(), (String) cboGravidade.getSelectedItem());
                    JOptionPane.showMessageDialog(this,
                            "Ocorrência registrada. Aguardando aprovação do Administrador.");
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
