package view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/** Recursos visuais compartilhados pelas telas (tema escuro/azul). */
public final class UI {

    public static final Color FUNDO = new Color(15, 23, 42);
    public static final Color PAINEL = new Color(30, 41, 59);
    public static final Color TEXTO = new Color(241, 245, 249);
    public static final Color SECUNDARIO = new Color(148, 163, 184);
    public static final Color DESTAQUE = new Color(20, 184, 166);
    public static final Color PERIGO = new Color(239, 68, 68);
    public static final Font TITULO = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 13);

    private UI() {
    }

    public static void estilizarBotao(JButton botao) {
        botao.setBackground(DESTAQUE);
        botao.setForeground(Color.WHITE);
        botao.setFont(NORMAL.deriveFont(Font.BOLD));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
    }

    public static void estilizarBotaoSecundario(JButton botao) {
        botao.setBackground(PAINEL);
        botao.setForeground(TEXTO);
        botao.setFont(NORMAL);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
    }

    public static void estilizarTabela(JTable tabela) {
        tabela.setBackground(PAINEL);
        tabela.setForeground(TEXTO);
        tabela.setGridColor(new Color(71, 85, 105));
        tabela.setRowHeight(26);
        tabela.setFont(NORMAL);
        tabela.setSelectionBackground(DESTAQUE);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.getTableHeader().setBackground(new Color(51, 65, 85));
        tabela.getTableHeader().setForeground(TEXTO);
        tabela.getTableHeader().setFont(NORMAL.deriveFont(Font.BOLD));
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setBackground(PAINEL);
        render.setForeground(TEXTO);
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(render);
        }
    }

    public static JLabel criarTitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(TITULO);
        lbl.setForeground(DESTAQUE);
        return lbl;
    }

    public static JLabel criarRotulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(NORMAL);
        lbl.setForeground(TEXTO);
        return lbl;
    }
}
