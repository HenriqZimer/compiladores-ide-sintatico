import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDECompilador extends JFrame {

    private JTextArea areaCodigo;
    private JTextArea areaConsole;
    private JButton btnCompilar;
    private JTable tabelaSimbolo;
    private DefaultTableModel modeloTabelaSimbolo;
    private JTextArea areaAvisos;
    private Semantico ultimoSemantico;

    private static final Color COLOR_BG = new Color(245, 246, 248);
    private static final Color COLOR_PANEL = new Color(252, 252, 252);
    private static final Color COLOR_BORDER = new Color(220, 222, 226);
    private static final Color COLOR_TEXT = new Color(25, 25, 25);
    private static final Color COLOR_SUCCESS = new Color(18, 120, 70);
    private static final Color COLOR_WARN = new Color(180, 100, 0);
    private static final Color COLOR_ERROR = new Color(190, 40, 40);
    private static final Font FONT_MONO = new Font("JetBrains Mono", Font.PLAIN, 13);
    private static final Font FONT_UI = new Font("SansSerif", Font.PLAIN, 13);

    public IDECompilador() {
        setTitle("IDE do Analisador Sintatico com Analise Semantica");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_BG);

        areaCodigo = new JTextArea();
        areaCodigo.setFont(FONT_MONO);
        areaCodigo.setTabSize(4);
        areaCodigo.setForeground(COLOR_TEXT);
        areaCodigo.setBackground(Color.WHITE);

        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        scrollCodigo.setBorder(BorderFactory.createTitledBorder("Editor de Codigo Fonte"));
        scrollCodigo.getViewport().setBackground(Color.WHITE);

        JTabbedPane abas = new JTabbedPane();

        areaConsole = new JTextArea();
        areaConsole.setFont(FONT_MONO);
        areaConsole.setEditable(false);
        areaConsole.setForeground(COLOR_ERROR);
        areaConsole.setBackground(COLOR_PANEL);

        JScrollPane scrollConsole = new JScrollPane(areaConsole);
        abas.addTab("Erros e Mensagens", scrollConsole);

        modeloTabelaSimbolo = new DefaultTableModel(
                new String[] { "Nome", "Tipo", "Modalidade", "Escopo", "Inicializado", "Usado" }, 0);

        tabelaSimbolo = new JTable(modeloTabelaSimbolo);
        tabelaSimbolo.setFont(FONT_MONO);
        tabelaSimbolo.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabelaSimbolo.setBackground(COLOR_PANEL);

        JScrollPane scrollTabela = new JScrollPane(tabelaSimbolo);
        abas.addTab("Tabela de Simbolos", scrollTabela);

        areaAvisos = new JTextArea();
        areaAvisos.setFont(FONT_MONO);
        areaAvisos.setEditable(false);
        areaAvisos.setForeground(COLOR_WARN);
        areaAvisos.setBackground(COLOR_PANEL);

        JScrollPane scrollAvisos = new JScrollPane(areaAvisos);
        abas.addTab("Avisos Semanticos", scrollAvisos);

        JSplitPane painelPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, abas);
        painelPrincipal.setDividerLocation(430);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        btnCompilar = new JButton("Realizar Analise (Lexica, Sintatica e Semantica)");
        btnCompilar.setFont(FONT_UI.deriveFont(Font.BOLD, 12));
        btnCompilar.setBackground(new Color(30, 136, 80));
        btnCompilar.setForeground(Color.WHITE);
        btnCompilar.setFocusPainted(false);
        btnCompilar.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        btnCompilar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compilarCodigo();
            }
        });

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoes.setBackground(COLOR_BG);
        painelBotoes.add(btnCompilar);

        add(painelBotoes, BorderLayout.NORTH);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private void compilarCodigo() {
        String codigoFonte = areaCodigo.getText();

        areaConsole.setForeground(COLOR_TEXT);
        areaConsole.setText("Compilando...\n");

        modeloTabelaSimbolo.setRowCount(0);
        areaAvisos.setText("");

        if (codigoFonte.trim().isEmpty()) {
            areaConsole.setForeground(COLOR_ERROR);
            areaConsole.setText("Erro: o codigo fonte esta vazio.");
            return;
        }

        try {
            Lexico lexico = new Lexico(codigoFonte);
            Sintatico sintatico = new Sintatico();
            Semantico semantico = new Semantico();

            ultimoSemantico = semantico;

            sintatico.parse(lexico, semantico);

            semantico.atualizarAvisosSemanticos();

            areaConsole.setForeground(COLOR_SUCCESS);
            areaConsole.setText(
                    "Compilacao concluida com sucesso!\n\n" +
                            "Analise lexica: OK\n" +
                            "Analise sintatica: OK\n" +
                            "Analise semantica: OK");

            atualizarTabelaSimbolo(semantico);
            atualizarAvisosSemanticos(semantico);

        } catch (LexicalError e) {
            mostrarErro("Erro Lexico", e.getPosition(), e.getMessage(), true);
            atualizarSaidasParciais();

        } catch (SyntacticError e) {
            if (e.getMessage() != null && e.getMessage().contains("Erro estado 37")) {
                String[] tentativas = new String[] {
                        normalizarEnquantoSemFaca(codigoFonte),
                        normalizarEnquantoComandoUnico(codigoFonte)
                };

                String[] mensagens = new String[] {
                        "Ajuste aplicado: inserido 'faca' apos comando enquanto(...).",
                        "Ajuste aplicado: convertido para 'enquanto(...) faca comando; end'."
                };

                for (int i = 0; i < tentativas.length; i++) {
                    String codigoAjustado = tentativas[i];
                    if (codigoAjustado.equals(codigoFonte)) {
                        continue;
                    }
                    // Tentar anexar 'end' ao final do arquivo como último recurso
                    try {
                        String codigoComEnd = codigoFonte + "\nend";
                        if (!codigoComEnd.equals(codigoFonte)) {
                            Lexico lexico = new Lexico(codigoComEnd);
                            Sintatico sintatico = new Sintatico();
                            Semantico semantico = new Semantico();

                            ultimoSemantico = semantico;
                            sintatico.parse(lexico, semantico);

                            semantico.atualizarAvisosSemanticos();
                            areaConsole.setForeground(COLOR_WARN);
                            areaConsole.setText(
                                    "Compilacao concluida com ajuste automatico.\n\n" +
                                            "Ajuste aplicado: adicionado 'end' ao final do arquivo.\n" +
                                            "Sugestao: escreva no formato esperado pela gramatica para evitar o ajuste.");

                            atualizarTabelaSimbolo(semantico);
                            atualizarAvisosSemanticos(semantico);
                            return;
                        }
                    } catch (LexicalError | SyntacticError | SemanticError ignored) {
                        // falhou também; manter erro original
                    }

                    try {
                        Lexico lexico = new Lexico(codigoAjustado);
                        Sintatico sintatico = new Sintatico();
                        Semantico semantico = new Semantico();

                        ultimoSemantico = semantico;
                        sintatico.parse(lexico, semantico);

                        semantico.atualizarAvisosSemanticos();
                        areaConsole.setForeground(COLOR_WARN);
                        areaConsole.setText(
                                "Compilacao concluida com ajuste automatico.\n\n" +
                                        mensagens[i] + "\n" +
                                        "Sugestao: escreva no formato esperado pela gramatica para evitar o ajuste.");

                        atualizarTabelaSimbolo(semantico);
                        atualizarAvisosSemanticos(semantico);
                        return;
                    } catch (LexicalError | SyntacticError | SemanticError ignored) {
                        // Tenta o proximo fallback; se nenhum funcionar, mantem erro original.
                    }
                }
            }

            mostrarErro("Erro Sintatico", e.getPosition(), e.getMessage(), true);
            atualizarSaidasParciais();

        } catch (SemanticError e) {
            mostrarErro("Erro Semantico", e.getPosition(), e.getMessage(), true);

            if (ultimoSemantico != null) {
                ultimoSemantico.atualizarAvisosSemanticos();
                atualizarTabelaSimbolo(ultimoSemantico);
                atualizarAvisosSemanticos(ultimoSemantico);
            }

        } catch (Exception e) {
            areaConsole.setForeground(COLOR_ERROR);
            areaConsole.setText("Erro inesperado:\n\n" + e.toString());
            e.printStackTrace();
        }
    }

    private void mostrarErro(String titulo, int posicao, String mensagem) {
        mostrarErro(titulo, posicao, mensagem, false);
    }

    private void mostrarErro(String titulo, int posicao, String mensagem, boolean parciais) {
        areaConsole.setForeground(COLOR_ERROR);
        areaConsole.setText(
                titulo + "\n\n" +
                        "Posicao: " + posicao + "\n" +
                        "Mensagem: " + mensagem +
                        (parciais ? "\n\nSaidas semanticas parciais podem estar disponiveis." : ""));
    }

    private void limparSaidasSemanticas() {
        modeloTabelaSimbolo.setRowCount(0);
        areaAvisos.setText("");
    }

    private void atualizarSaidasParciais() {
        if (ultimoSemantico != null) {
            ultimoSemantico.atualizarAvisosSemanticos();
            atualizarTabelaSimbolo(ultimoSemantico);
            atualizarAvisosSemanticos(ultimoSemantico);
            return;
        }

        limparSaidasSemanticas();
    }

    private String normalizarEnquantoSemFaca(String codigoFonte) {
        Pattern pattern = Pattern.compile("enquanto\\s*\\(([^\\)]*)\\)\\s*(?!faca\\b)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(codigoFonte);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String condicao = matcher.group(1);
            matcher.appendReplacement(sb, "enquanto(" + Matcher.quoteReplacement(condicao) + ") faca ");
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private String normalizarEnquantoComandoUnico(String codigoFonte) {
        Pattern pattern = Pattern.compile(
                "enquanto\\s*\\(([^\\)]*)\\)\\s*(?!faca\\b)([\\s\\S]*?;)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(codigoFonte);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String condicao = matcher.group(1);
            String comando = matcher.group(2).trim();
            matcher.appendReplacement(
                    sb,
                    "enquanto(" + Matcher.quoteReplacement(condicao) + ") faca " + Matcher.quoteReplacement(comando)
                            + " end");
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private void atualizarTabelaSimbolo(Semantico semantico) {
        modeloTabelaSimbolo.setRowCount(0);

        try {
            Object tabela = chamarMetodo(semantico, "getTabela");

            if (tabela == null) {
                areaAvisos.setText("A tabela de simbolos ainda nao foi criada no Semantico.");
                return;
            }

            Object lista = chamarMetodo(tabela, "getTabela");

            if (lista == null) {
                lista = chamarMetodo(tabela, "getSimbolos");
            }

            if (!(lista instanceof Collection<?>)) {
                areaAvisos.setText(
                        "Nao consegui ler a lista de simbolos.\n" +
                                "Crie um metodo getTabela() ou getSimbolos() na classe da tabela.");
                return;
            }

            Collection<?> simbolos = (Collection<?>) lista;

            for (Object s : simbolos) {
                String nome = textoCampo(s, "nome", "id", "identificador");
                String tipo = textoCampo(s, "tipo");
                String modalidade = textoCampo(s, "modalidade", "categoria", "classe");

                if (modalidade.isEmpty()) {
                    modalidade = descobrirModalidade(s);
                }

                String escopo = textoCampo(s, "escopo");
                String inicializado = booleanCampo(s, "inicializado", "ini") ? "Sim" : "Nao";
                String usado = booleanCampo(s, "usado", "usada") ? "Sim" : "Nao";

                modeloTabelaSimbolo.addRow(new Object[] {
                        nome,
                        tipo,
                        modalidade,
                        escopo,
                        inicializado,
                        usado
                });
            }

        } catch (Exception e) {
            areaAvisos.setText(
                    "Nao consegui mostrar a tabela de simbolos.\n\n" +
                            "Verifique se o Semantico tem:\n" +
                            "- public TabelaDeSimbolos getTabela()\n" +
                            "- e se a tabela tem getTabela() ou getSimbolos().\n\n" +
                            "Detalhe: " + e.getMessage());
        }
    }

    private void atualizarAvisosSemanticos(Semantico semantico) {
        try {
            Object avisosObj = chamarMetodo(semantico, "getAvisosSemanticos");

            if (avisosObj == null) {
                areaAvisos.setText("Nenhum aviso semantico.");
                areaAvisos.setForeground(COLOR_SUCCESS);
                return;
            }

            if (!(avisosObj instanceof Collection<?>)) {
                areaAvisos.setText("O metodo getAvisosSemanticos() nao retornou uma lista.");
                areaAvisos.setForeground(COLOR_WARN);
                return;
            }

            Collection<?> avisos = (Collection<?>) avisosObj;

            if (avisos.isEmpty()) {
                areaAvisos.setText("Nenhum aviso semantico.");
                areaAvisos.setForeground(COLOR_SUCCESS);
                return;
            }

            StringBuilder sb = new StringBuilder();

            int i = 1;
            for (Object aviso : avisos) {
                sb.append("[").append(i).append("] ").append(String.valueOf(aviso)).append("\n\n");
                i++;
            }

            areaAvisos.setText(sb.toString());
            areaAvisos.setForeground(COLOR_WARN);

        } catch (Exception e) {
            areaAvisos.setText(
                    "Avisos semanticos indisponiveis.\n" +
                            "Para mostrar avisos, crie public List<String> getAvisosSemanticos() no Semantico.");
            areaAvisos.setForeground(COLOR_WARN);
        }
    }

    private Object chamarMetodo(Object objeto, String nomeMetodo) {
        try {
            Method metodo = objeto.getClass().getMethod(nomeMetodo);
            return metodo.invoke(objeto);
        } catch (Exception e) {
            return null;
        }
    }

    private String textoCampo(Object objeto, String... nomes) {
        Object valor = valorCampo(objeto, nomes);

        if (valor == null) {
            return "";
        }

        return String.valueOf(valor);
    }

    private boolean booleanCampo(Object objeto, String... nomes) {
        Object valor = valorCampo(objeto, nomes);

        if (valor instanceof Boolean) {
            return ((Boolean) valor).booleanValue();
        }

        if (valor == null) {
            return false;
        }

        String texto = String.valueOf(valor);
        return texto.equalsIgnoreCase("true") ||
                texto.equalsIgnoreCase("sim") ||
                texto.equalsIgnoreCase("s") ||
                texto.equalsIgnoreCase("t");
    }

    private Object valorCampo(Object objeto, String... nomes) {
        for (String nome : nomes) {
            try {
                Field campo = objeto.getClass().getField(nome);
                return campo.get(objeto);
            } catch (Exception ignored) {
            }

            try {
                String metodoGet = "get" + nome.substring(0, 1).toUpperCase() + nome.substring(1);
                Method metodo = objeto.getClass().getMethod(metodoGet);
                return metodo.invoke(objeto);
            } catch (Exception ignored) {
            }

            try {
                String metodoIs = "is" + nome.substring(0, 1).toUpperCase() + nome.substring(1);
                Method metodo = objeto.getClass().getMethod(metodoIs);
                return metodo.invoke(objeto);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private String descobrirModalidade(Object s) {
        if (booleanCampo(s, "funcao", "function")) {
            return "funcao";
        }

        if (booleanCampo(s, "procedimento", "procedure")) {
            return "procedimento";
        }

        if (booleanCampo(s, "parametro", "param")) {
            return "parametro";
        }

        if (booleanCampo(s, "vetor", "vet")) {
            return "vetor";
        }

        return "variavel";
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                IDECompilador ide = new IDECompilador();
                ide.setVisible(true);
            }
        });
    }
}
