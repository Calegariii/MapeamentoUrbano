package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import model.InstituicaoEnsino;
import model.PontoDeInteresse;
import model.UnidadeSaude;

/** Tela 3 — Cadastro, listagem, filtro e manutenção de pontos de interesse. */
public class PontoDeInteresseView extends JFrame {

    private final SistemaController controller;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nome", "Categoria", "Tipo", "Bairro", "Endereço", "Coordenadas"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final JComboBox<String> filtro = new JComboBox<>(new String[]{
            "Todos", "UBS", "UPA", "Hospital", "Escola Estadual",
            "Escola Municipal", "Universidade"});
    private final JLabel status = UI.criarRotulo("");

    public PontoDeInteresseView(SistemaController controller) {
        this.controller = controller;
        setTitle("Pontos de Interesse — Palhoça/SC");
        setSize(1120, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UI.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(UI.FUNDO);
        topo.setBorder(BorderFactory.createEmptyBorder(16, 20, 4, 20));
        topo.add(UI.criarTitulo("Pontos de Interesse"), BorderLayout.WEST);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filtros.setBackground(UI.FUNDO);
        filtros.add(UI.criarRotulo("Filtrar por tipo:"));
        filtro.setFont(UI.NORMAL);
        filtros.add(filtro);
        JButton btnFiltrar = new JButton("Aplicar filtro");
        UI.estilizarBotaoSecundario(btnFiltrar);
        filtros.add(btnFiltrar);
        topo.add(filtros, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        UI.estilizarTabela(tabela);
        tabela.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.setBackground(UI.FUNDO);
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 20, 6, 20));
        JButton btnNovo = new JButton("Novo ponto");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar lista");
        JButton btnVoltar = new JButton("Voltar ao menu");
        for (JButton b : new JButton[]{btnNovo, btnEditar, btnAtualizar}) {
            UI.estilizarBotao(b);
            botoes.add(b);
        }
        UI.estilizarBotaoSecundario(btnExcluir);
        btnExcluir.setForeground(UI.PERIGO);
        UI.estilizarBotaoSecundario(btnVoltar);
        botoes.add(btnExcluir);
        botoes.add(btnVoltar);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setBackground(UI.FUNDO);
        inferior.add(botoes, BorderLayout.NORTH);
        status.setForeground(UI.SECUNDARIO);
        JPanel barraStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barraStatus.setBackground(UI.FUNDO);
        barraStatus.add(status);
        inferior.add(barraStatus, BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> {
            PontoDeInteresse p = selecionado();
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Selecione um ponto na tabela.",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                abrirFormulario(controller.buscarPonto(p.getIdPonto()));
            } catch (Exception ex) {
                erro(ex);
            }
        });
        btnExcluir.addActionListener(e -> excluir());
        btnAtualizar.addActionListener(e -> carregarTabela());
        btnFiltrar.addActionListener(e -> carregarTabela());
        filtro.addActionListener(e -> carregarTabela());
        btnVoltar.addActionListener(e -> dispose());

        carregarTabela();
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            String f = filtro.getSelectedIndex() == 0 ? null
                    : (String) filtro.getSelectedItem();
            List<PontoDeInteresse> pontos = controller.listarPontos(f);
            for (PontoDeInteresse p : pontos) {
                modelo.addRow(new Object[]{
                        p.getIdPonto(), p.getNome(),
                        "SAUDE".equals(p.getCategoria()) ? "Saúde" : "Educação",
                        p.getTipo(), p.getBairro().getNome(), p.getEndereco(),
                        p.obterCoordenadas()});
            }
            status.setText(pontos.size() + " ponto(s) encontrado(s). "
                    + controller.statusConexao());
        } catch (Exception ex) {
            erro(ex);
        }
    }

    private PontoDeInteresse selecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return null;
        }
        int id = ((Number) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0)).intValue();
        try {
            return controller.buscarPonto((long) id);
        } catch (Exception ex) {
            erro(ex);
            return null;
        }
    }

    private void excluir() {
        PontoDeInteresse p = selecionado();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Selecione um ponto na tabela.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Confirma a exclusão de \"" + p.getNome() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            try {
                controller.excluirPonto(p.getIdPonto());
                JOptionPane.showMessageDialog(this, "Ponto excluído com sucesso.");
                carregarTabela();
            } catch (Exception ex) {
                erro(ex);
            }
        }
    }

    private void abrirFormulario(PontoDeInteresse ponto) {
        try {
            new FormPontoDialog(this, controller, ponto, this::carregarTabela).setVisible(true);
        } catch (Exception ex) {
            erro(ex);
        }
    }

    private void erro(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /** Formulário modal de inclusão/edição. */
    private static class FormPontoDialog extends JDialog {

        private final JTextField txtNome = new JTextField(28);
        private final JTextField txtEndereco = new JTextField(28);
        private final JTextField txtLat = new JTextField("-27.6500000", 12);
        private final JTextField txtLng = new JTextField("-48.6700000", 12);
        private final JComboBox<Bairro> cboBairro = new JComboBox<>();
        private final JComboBox<String> cboCategoria =
                new JComboBox<>(new String[]{"SAUDE", "EDUCACAO"});
        private final JComboBox<String> cboTipoSaude =
                new JComboBox<>(new String[]{"UBS", "UPA", "Hospital", "Pronto-Socorro"});
        private final JTextField txtEspecialidades = new JTextField(24);
        private final JTextField txtHorario = new JTextField(24);
        private final JCheckBox chk24h = new JCheckBox("Atendimento 24 horas");
        private final JComboBox<String> cboTipoEnsino =
                new JComboBox<>(new String[]{"Escola Municipal", "Escola Estadual",
                        "Universidade", "Faculdade", "CEI"});
        private final JTextField txtEsfera = new JTextField("Pública", 18);
        private final JTextField txtNiveis = new JTextField(24);
        private final JCheckBox chkVagas = new JCheckBox("Possui vagas abertas");
        private final JPanel camposEspecificos = new JPanel(new CardLayout());
        private final PontoDeInteresse original;
        private final Runnable aoSalvar;

        FormPontoDialog(JFrame pai, SistemaController controller,
                        PontoDeInteresse ponto, Runnable aoSalvar) throws Exception {
            super(pai, ponto == null ? "Novo Ponto de Interesse" : "Editar Ponto de Interesse", true);
            this.original = ponto;
            this.aoSalvar = aoSalvar;
            setSize(620, 590);
            setLocationRelativeTo(pai);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(UI.FUNDO);

            for (Bairro b : controller.listarBairros()) {
                cboBairro.addItem(b);
            }

            JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
            form.setBackground(UI.PAINEL);
            form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
            form.add(UI.criarRotulo("Nome:*"));
            form.add(txtNome);
            form.add(UI.criarRotulo("Categoria:*"));
            form.add(cboCategoria);
            form.add(UI.criarRotulo("Endereço:*"));
            form.add(txtEndereco);
            form.add(UI.criarRotulo("Bairro:*"));
            form.add(cboBairro);
            form.add(UI.criarRotulo("Latitude:*"));
            form.add(txtLat);
            form.add(UI.criarRotulo("Longitude:*"));
            form.add(txtLng);
            add(form, BorderLayout.NORTH);

            JPanel saude = new JPanel(new GridLayout(0, 2, 10, 8));
            saude.setBackground(UI.PAINEL);
            saude.add(UI.criarRotulo("Tipo de unidade:*"));
            saude.add(cboTipoSaude);
            saude.add(UI.criarRotulo("Especialidades:"));
            saude.add(txtEspecialidades);
            saude.add(UI.criarRotulo("Horário de funcionamento:"));
            saude.add(txtHorario);
            saude.add(new JLabel());
            saude.add(chk24h);

            JPanel ensino = new JPanel(new GridLayout(0, 2, 10, 8));
            ensino.setBackground(UI.PAINEL);
            ensino.add(UI.criarRotulo("Tipo de instituição:*"));
            ensino.add(cboTipoEnsino);
            ensino.add(UI.criarRotulo("Esfera:"));
            ensino.add(txtEsfera);
            ensino.add(UI.criarRotulo("Níveis atendidos:"));
            ensino.add(txtNiveis);
            ensino.add(new JLabel());
            ensino.add(chkVagas);

            camposEspecificos.add(saude, "SAUDE");
            camposEspecificos.add(ensino, "EDUCACAO");
            JPanel centro = new JPanel(new BorderLayout());
            centro.setBackground(UI.PAINEL);
            centro.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
            centro.add(camposEspecificos, BorderLayout.CENTER);
            add(centro, BorderLayout.CENTER);

            JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            acoes.setBackground(UI.FUNDO);
            JButton btnCancelar = new JButton("Cancelar");
            JButton btnSalvar = new JButton(original == null ? "Cadastrar" : "Salvar alterações");
            UI.estilizarBotaoSecundario(btnCancelar);
            UI.estilizarBotao(btnSalvar);
            acoes.add(btnCancelar);
            acoes.add(btnSalvar);
            add(acoes, BorderLayout.SOUTH);

            cboCategoria.addActionListener(e -> trocarCategoria());
            btnCancelar.addActionListener(e -> dispose());
            btnSalvar.addActionListener(e -> {
                try {
                    salvar(controller);
                    dispose();
                    aoSalvar.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Erro de validação", JOptionPane.ERROR_MESSAGE);
                }
            });

            if (original != null) {
                preencher(original);
            }
            trocarCategoria();
        }

        private void trocarCategoria() {
            String cat = (String) cboCategoria.getSelectedItem();
            ((CardLayout) camposEspecificos.getLayout()).show(camposEspecificos, cat);
            camposEspecificos.setPreferredSize(new Dimension(0, 150));
            revalidate();
        }

        private void preencher(PontoDeInteresse p) {
            txtNome.setText(p.getNome());
            txtEndereco.setText(p.getEndereco());
            txtLat.setText(String.valueOf(p.getLatitude()));
            txtLng.setText(String.valueOf(p.getLongitude()));
            cboCategoria.setSelectedItem(p.getCategoria());
            for (int i = 0; i < cboBairro.getItemCount(); i++) {
                if (cboBairro.getItemAt(i).getIdBairro().equals(p.getBairro().getIdBairro())) {
                    cboBairro.setSelectedIndex(i);
                    break;
                }
            }
            if (p instanceof UnidadeSaude) {
                UnidadeSaude u = (UnidadeSaude) p;
                cboTipoSaude.setSelectedItem(u.getTipo());
                txtEspecialidades.setText(u.getEspecialidades());
                txtHorario.setText(u.getHorarioFuncionamento());
                chk24h.setSelected(u.isPossuiAtendimento24h());
            } else if (p instanceof InstituicaoEnsino) {
                InstituicaoEnsino i = (InstituicaoEnsino) p;
                cboTipoEnsino.setSelectedItem(i.getTipo());
                txtEsfera.setText(i.getEsfera());
                txtNiveis.setText(i.getNiveisAtendidos());
                chkVagas.setSelected(i.isPossuiVagasAbertas());
            }
        }

        private void salvar(SistemaController controller) throws Exception {
            double lat = Double.parseDouble(txtLat.getText().replace(',', '.'));
            double lng = Double.parseDouble(txtLng.getText().replace(',', '.'));
            Bairro bairro = (Bairro) cboBairro.getSelectedItem();
            String nome = txtNome.getText();
            String endereco = txtEndereco.getText();

            PontoDeInteresse ponto;
            if ("SAUDE".equals(cboCategoria.getSelectedItem())) {
                ponto = new UnidadeSaude(nome, lat, lng, endereco, bairro,
                        (String) cboTipoSaude.getSelectedItem(), txtEspecialidades.getText(),
                        txtHorario.getText(), chk24h.isSelected());
            } else {
                ponto = new InstituicaoEnsino(nome, lat, lng, endereco, bairro,
                        (String) cboTipoEnsino.getSelectedItem(), txtEsfera.getText(),
                        txtNiveis.getText(), chkVagas.isSelected());
            }
            if (original != null) {
                ponto.setIdPonto(original.getIdPonto());
            }
            controller.salvarPonto(ponto, original == null);
            JOptionPane.showMessageDialog(this, original == null
                    ? "Ponto de interesse cadastrado com sucesso!"
                    : "Ponto de interesse atualizado com sucesso!");
        }
    }
}
