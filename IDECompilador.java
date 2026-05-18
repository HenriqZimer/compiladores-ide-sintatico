import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IDECompilador extends JFrame {

    private JTextArea areaCodigo;
    private JTextArea areaConsole;
    private JButton btnCompilar;

    public IDECompilador() {
        // Configurações da Janela Principal
        setTitle("IDE do Analisador Sintático - T2");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Área do Código Fonte (Requisito: Fonte tamanho 14)
        areaCodigo = new JTextArea();
        areaCodigo.setFont(new Font("Monospaced", Font.PLAIN, 14)); // REQUISITO ATENDIDO
        areaCodigo.setTabSize(4);
        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        scrollCodigo.setBorder(BorderFactory.createTitledBorder("Editor de Código Fonte"));

        // Área do Console/Depuração
        areaConsole = new JTextArea();
        areaConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaConsole.setEditable(false);
        areaConsole.setForeground(Color.RED); // Destaca erros em vermelho
        JScrollPane scrollConsole = new JScrollPane(areaConsole);
        scrollConsole.setBorder(BorderFactory.createTitledBorder("Mensagens de Erro e Depuração"));
        scrollConsole.setPreferredSize(new Dimension(800, 150));

        // Botão de Compilar
        btnCompilar = new JButton("Realizar Análise Sintática (Compilar)");
        btnCompilar.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Ação do Botão
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
        add(scrollCodigo, BorderLayout.CENTER);
        add(scrollConsole, BorderLayout.SOUTH);
    }

    private void compilarCodigo() {
        String codigoFonte = areaCodigo.getText();
        areaConsole.setForeground(Color.BLACK);
        areaConsole.setText("Compilando...\n");

        if (codigoFonte.trim().isEmpty()) {
            areaConsole.setForeground(Color.RED);
            areaConsole.setText("Erro: O código fonte está vazio.");
            return;
        }

        try {
            // Instancia o analisador léxico passando o texto da interface
            Lexico lexico = new Lexico(codigoFonte);
            Sintatico sintatico = new Sintatico();
            Semantico semantico = new Semantico();
            
            // Inicia a análise
            sintatico.parse(lexico, semantico);
            
            // Se passar direto pelo parse sem cair no catch, é sucesso!
            areaConsole.setForeground(new Color(0, 128, 0)); // Verde escuro
            areaConsole.setText("Compilação concluída com sucesso! Nenhum erro encontrado.");

        } catch (LexicalError e) {
            areaConsole.setForeground(Color.RED);
            areaConsole.setText("Erro Léxico na posição " + e.getPosition() + ":\n" + e.getMessage());
        } catch (SyntacticError e) {
            areaConsole.setForeground(Color.RED);
            areaConsole.setText("Erro Sintático na posição " + e.getPosition() + ":\n" + e.getMessage());
        } catch (SemanticError e) {
            areaConsole.setForeground(Color.RED);
            areaConsole.setText("Erro Semântico na posição " + e.getPosition() + ":\n" + e.getMessage());
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