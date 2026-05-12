import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class IDECompilador extends JFrame {

    private JTextArea areaCodigo;
    private JTextArea areaConsole;
    private JButton btnCompilar;
    private JTable tabelaSimbolo;
    private DefaultTableModel modeloTabelaSimbolo;
    private JTextArea areaAvisos;
    private Semantico ultimoSemantico;

    public IDECompilador() {
        // Configurações da Janela Principal
        setTitle("IDE do Analisador Sintático com Análise Semântica");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Área do Código Fonte (Requisito: Fonte tamanho 14)
        areaCodigo = new JTextArea();
        areaCodigo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaCodigo.setTabSize(4);
        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        scrollCodigo.setBorder(BorderFactory.createTitledBorder("Editor de Código Fonte"));

        // Criando JTabbedPane para organizar os painéis de saída
        JTabbedPane abas = new JTabbedPane();

        // Aba 1: Console de Erros
        areaConsole = new JTextArea();
        areaConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaConsole.setEditable(false);
        areaConsole.setForeground(Color.RED);
        JScrollPane scrollConsole = new JScrollPane(areaConsole);
        abas.addTab("Erros e Mensagens", scrollConsole);

        // Aba 2: Tabela de Símbolos
        modeloTabelaSimbolo = new DefaultTableModel(
                new String[]{"Nome", "Tipo", "Modalidade", "Escopo", "Inicializado", "Usado"}, 0);
        tabelaSimbolo = new JTable(modeloTabelaSimbolo);
        tabelaSimbolo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        tabelaSimbolo.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollTabela = new JScrollPane(tabelaSimbolo);
        abas.addTab("Tabela de Símbolos", scrollTabela);

        // Aba 3: Avisos Semânticos
        areaAvisos = new JTextArea();
        areaAvisos.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaAvisos.setEditable(false);
        areaAvisos.setForeground(new Color(180, 100, 0)); // Cor laranja para avisos
        JScrollPane scrollAvisos = new JScrollPane(areaAvisos);
        abas.addTab("Avisos Semânticos", scrollAvisos);

        // Painel superior contém:
        // - Área de código (esquerda)
        // - Abas de saída (direita)
        JSplitPane painelPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, abas);
        painelPrincipal.setDividerLocation(400);

        // Botão de Compilar
        btnCompilar = new JButton("▶ Realizar Análise (Léxica, Sintática e Semântica)");
        btnCompilar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCompilar.setBackground(new Color(50, 150, 50));
        btnCompilar.setForeground(Color.WHITE);
        btnCompilar.setFocusPainted(false);

        btnCompilar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compilarCodigo();
            }
        });

        // Painel superior para o botão
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoes.add(btnCompilar);

        // Adicionando componentes à janela
        add(painelBotoes, BorderLayout.NORTH);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private void compilarCodigo() {
        String codigoFonte = areaCodigo.getText();
        areaConsole.setForeground(Color.BLACK);
        areaConsole.setText("Compilando...\n");

        // Limpar tabela anterior
        modeloTabelaSimbolo.setRowCount(0);
        areaAvisos.setText("");

        if (codigoFonte.trim().isEmpty()) {
            areaConsole.setForeground(Color.RED);
            areaConsole.setText("Erro: O código fonte está vazio.");
            return;
        }

        try {
            // Instancia os analisadores
            Lexico lexico = new Lexico(codigoFonte);
            Sintatico sintatico = new Sintatico();
            Semantico semantico = new Semantico();

            this.ultimoSemantico = semantico;

            // Inicia a análise
            sintatico.parse(lexico, semantico);

            // Sucesso: compilação concluída
            areaConsole.setForeground(new Color(0, 128, 0)); // Verde
            areaConsole.setText("✓ Compilação concluída com sucesso!\n\n" +
                    "Análise léxica: OK\n" +
                    "Análise sintática: OK\n" +
                    "Análise semântica: OK");

            // Mostrar tabela de símbolos
            atualizarTabelaSimbolo(semantico);

            // Mostrar avisos semânticos
            atualizarAvisosSemanticos(semantico);

        } catch (LexicalError e) {
            areaConsole.setForeground(Color.RED);
            areaConsole.setText("✗ Erro Léxico\n\n" +
                    "Posição: " + e.getPosition() + "\n" +
                    "Mensagem: " + e.getMessage());
            modeloTabelaSimbolo.setRowCount(0);
            areaAvisos.setText("");

        } catch (SyntacticError e) {
            areaConsole.setForeground(Color.RED);
            areaConsole.setText("✗ Erro Sintático\n\n" +
                    "Posição: " + e.getPosition() + "\n" +
                    "Mensagem: " + e.getMessage());
            modeloTabelaSimbolo.setRowCount(0);
            areaAvisos.setText("");

        } catch (SemanticError e) {
            areaConsole.setForeground(Color.RED);
            areaConsole.setText("✗ Erro Semântico\n\n" +
                    "Posição: " + e.getPosition() + "\n" +
                    "Mensagem: " + e.getMessage());
            // Mesmo com erro semântico, mostrar a tabela construída até o ponto do erro
            if (ultimoSemantico != null) {
                atualizarTabelaSimbolo(ultimoSemantico);
                atualizarAvisosSemanticos(ultimoSemantico);
            }
        }
    }

    private void atualizarTabelaSimbolo(Semantico semantico) {
        modeloTabelaSimbolo.setRowCount(0);

        TabelaSimbolos tabela = semantico.getTabela();
        List<Simbolo> simbolos = tabela.getTabela();

        for (Simbolo s : simbolos) {
            modeloTabelaSimbolo.addRow(new Object[]{
                    s.nome,
                    s.tipo,
                    s.modalidade,
                    s.escopo,
                    s.inicializado ? "✓ Sim" : "✗ Não",
                    s.usado ? "✓ Sim" : "✗ Não"
            });
        }
    }

    private void atualizarAvisosSemanticos(Semantico semantico) {
        List<String> avisos = semantico.getAvisosSemanticos();

        if (avisos.isEmpty()) {
            areaAvisos.setText("Nenhum aviso semântico.\n\nTodas as variáveis foram declaradas e usadas corretamente.");
            areaAvisos.setForeground(new Color(0, 128, 0));
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < avisos.size(); i++) {
                sb.append("[").append(i + 1).append("] ").append(avisos.get(i)).append("\n\n");
            }
            areaAvisos.setText(sb.toString());
            areaAvisos.setForeground(new Color(180, 100, 0)); // Laranja
        }
    }

    public static void main(String[] args) {
        // Look and Feel padrão do sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            IDECompilador ide = new IDECompilador();
            ide.setVisible(true);
        });
    }
}