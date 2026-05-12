import java.util.ArrayList;
import java.util.List;

public class Semantico implements Constants {

    TabelaSimbolos tabela = new TabelaSimbolos();
    private List<String> avisosSemanticos = new ArrayList<>();

    String tipoAtual = "";
    String escopoAtual = "global";

    public void executeAction(int action, Token token) throws SemanticError {

        Simbolo s;

        switch (action) {

            // ========== AÇÃO 1: Capturar tipo (INT_KW, FLOAT_KW, CHAR_KW, STRING_KW, BOOL_KW) ==========
            case 1:
                tipoAtual = token.getLexeme();
                break;

            // ========== AÇÃO 2: Inserir variável simples (ID_TK sem modificadores) ==========
            case 2:
                tabela.inserir(token.getLexeme(), tipoAtual, "variavel", escopoAtual);
                break;

            // ========== AÇÃO 5: Inserir vetor (ID_TK ABRE_COL ... FECHA_COL) ==========
            case 5:
                tabela.inserir(token.getLexeme(), tipoAtual, "vetor", escopoAtual);
                break;

            // ========== AÇÃO 10: Verificar identificador em expressão (fator) ==========
            case 10:
                s = tabela.buscar(token.getLexeme(), escopoAtual);
                if (s == null) {
                    throw new SemanticError("Erro: identificador '" + token.getLexeme() + "' não foi declarado no escopo.");
                }
                tabela.marcarUsado(token.getLexeme(), escopoAtual);
                // Verificar inicialização
                if (!s.inicializado && !s.modalidade.equals("parametro")) {
                    String aviso = "Aviso: a variável '" + s.nome + "' está sendo usada sem ter sido inicializada.";
                    System.out.println(aviso);
                    avisosSemanticos.add(aviso);
                }
                break;

            // ========== AÇÃO 11: Verificar identificador em atribuição/chamada ==========
            case 11:
                s = tabela.buscar(token.getLexeme(), escopoAtual);
                if (s == null) {
                    throw new SemanticError("Erro: identificador '" + token.getLexeme() + "' não foi declarado no escopo.");
                }
                break;

            // ========== AÇÃO 20: Marcar como inicializado (ID_TK ATRIB <expressao>) ==========
            case 20:
                s = tabela.buscar(token.getLexeme(), escopoAtual);
                if (s != null) {
                    s.inicializado = true;
                    tabela.marcarUsado(token.getLexeme(), escopoAtual);
                }
                break;

            // ========== AÇÃO 21: Verificar inicialização em leitura (cmd_leia) ==========
            case 21:
                s = tabela.buscar(token.getLexeme(), escopoAtual);
                if (s == null) {
                    throw new SemanticError("Erro: identificador '" + token.getLexeme() + "' não foi declarado no escopo.");
                }
                // Verificar se será inicializado pela leitura
                s.inicializado = true;
                tabela.marcarUsado(token.getLexeme(), escopoAtual);
                break;

            // ========== AÇÃO 41: Verificar operações válidas em expressões aritméticas ==========
            case 41:
                // Verificação de tipo em operações (pode ser expandido)
                break;

            default:
                System.out.println("Ação semântica #" + action + " não implementada.");
        }
    }

    public void entrarEscopo(String nomeEscopo) {
        escopoAtual = nomeEscopo;
    }

    public void sairEscopo() {
        escopoAtual = "global";
    }

    public TabelaSimbolos getTabela() {
        return tabela;
    }

    public List<String> getAvisosSemanticos() {
        return avisosSemanticos;
    }

    public void exibirAvisosSemanticos() {
        if (!avisosSemanticos.isEmpty()) {
            System.out.println("\n===== AVISOS SEMÂNTICOS =====");
            for (String aviso : avisosSemanticos) {
                System.out.println(aviso);
            }
            System.out.println("=============================\n");
        }
    }

    public static void main(String[] args) throws Exception {

        Semantico s = new Semantico();

        // Teste: inserir variáveis
        s.tipoAtual = "int";
        s.executeAction(2, new Token(0, "x", 0));

        s.tipoAtual = "int";
        s.executeAction(2, new Token(0, "y", 0));

        // Teste: atribuição (ação 20)
        s.executeAction(20, new Token(0, "x", 0));

        // Teste: usar variável (ação 10)
        s.executeAction(10, new Token(0, "x", 0));

        // Teste: usar variável não inicializada
        s.executeAction(10, new Token(0, "y", 0));

        // Exibir tabela
        s.getTabela().imprimir();
        s.exibirAvisosSemanticos();
    }
}