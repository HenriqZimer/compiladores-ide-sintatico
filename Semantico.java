import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Semantico implements Constants {

    private TabelaDeSimbolos tabela = new TabelaDeSimbolos();
    private Stack<String> pilhaEscopos = new Stack<>();

    private String tipoAtual = "";
    private String escopoAtual = "global";
    private String ultimoId = "";

    private boolean lendoParametros = false;
    private int posParametro = 0;

    private List<String> avisosSemanticos = new ArrayList<>();

    public Semantico() {
        pilhaEscopos.push("global");
    }

    public void executeAction(int action, Token token) throws SemanticError {

        // Use essa linha para testar se as ações do .gals estão sendo chamadas.
        // Depois que estiver funcionando, pode comentar ou apagar.
        String lexema = token == null ? "<null>" : token.getLexeme();
        System.out.println("Acao chamada: #" + action + " token: " + lexema);

        switch (action) {

            case 1:
                // Guarda o tipo atual
                tipoAtual = token.getLexeme();
                break;

            case 2:
                // Insere variavel, vetor ou parametro
                if (isIdentificador(token)) {
                    inserirIdentificador(token);
                }
                break;

            case 3:
                // Marca o ultimo identificador como vetor
                marcarComoVetor();
                break;

            case 4:
                // Marca o ultimo identificador como inicializado
                marcarComoInicializado(ultimoId);
                break;

            case 5:
                // Verifica uso de identificador em expressao
                if (isIdentificador(token)) {
                    verificarUso(token);
                }
                break;

            case 6:
                // Declara procedimento e entra no escopo dele
                if (isIdentificador(token)) {
                    declararProcedimento(token);
                }
                break;

            case 7:
                // Declara funcao e entra no escopo dela
                if (isIdentificador(token)) {
                    declararFuncao(token);
                }
                break;

            case 8:
                // Comeca leitura de parametros
                lendoParametros = true;
                posParametro = 0;
                break;

            case 9:
                // Termina leitura de parametros
                lendoParametros = false;
                break;

            case 10:
                // Sai do escopo atual
                sairEscopo();
                break;

            case 11:
                // Verifica identificador usado em comando, leitura, atribuicao ou chamada
                if (isIdentificador(token)) {
                    ultimoId = token.getLexeme();
                    verificarUso(token);
                }
                break;

            case 12:
                // Marca identificador como inicializado por atribuicao
                if (!ultimoId.isEmpty()) {
                    marcarComoInicializado(ultimoId);
                }
                break;

            case 13:
                // Marca identificador como inicializado por leitura
                if (!ultimoId.isEmpty()) {
                    marcarComoInicializado(ultimoId);
                }
                break;

            default:
                throw new SemanticError("Acao semantica nao implementada: #" + action);
        }
    }

    private void inserirIdentificador(Token token) throws SemanticError {
        String id = token.getLexeme();
        ultimoId = id;

        Simbolo simbolo = new Simbolo(id, tipoAtual, escopoAtual);

        if (lendoParametros) {
            simbolo.parametro = true;
            posParametro++;
            simbolo.posicaoParametro = posParametro;
            simbolo.inicializado = true;
        }

        tabela.inserir(simbolo);
    }

    private void marcarComoVetor() {
        Simbolo s = tabela.ultimo();

        if (s != null) {
            s.vetor = true;
        }
    }

    private void marcarComoInicializado(String id) throws SemanticError {
        Simbolo s = tabela.buscar(id, pilhaEscopos);

        if (s == null) {
            throw new SemanticError("Identificador '" + id + "' nao declarado");
        }

        s.inicializado = true;
    }

    private void verificarUso(Token token) throws SemanticError {
        String id = token.getLexeme();

        Simbolo s = tabela.buscar(id, pilhaEscopos);

        if (s == null) {
            throw new SemanticError("Identificador '" + id + "' nao declarado");
        }

        s.usado = true;
    }

    private void declararProcedimento(Token token) throws SemanticError {
        String id = token.getLexeme();
        ultimoId = id;

        Simbolo simbolo = new Simbolo(id, "procedimento", "global");
        simbolo.procedimento = true;
        simbolo.inicializado = true;

        tabela.inserir(simbolo);

        entrarEscopo(id);
    }

    private void declararFuncao(Token token) throws SemanticError {
        String id = token.getLexeme();
        ultimoId = id;

        Simbolo simbolo = new Simbolo(id, tipoAtual, "global");
        simbolo.funcao = true;
        simbolo.inicializado = true;

        tabela.inserir(simbolo);

        entrarEscopo(id);
    }

    private void entrarEscopo(String novoEscopo) {
        pilhaEscopos.push(novoEscopo);
        escopoAtual = novoEscopo;
    }

    private void sairEscopo() {
        if (pilhaEscopos.size() > 1) {
            pilhaEscopos.pop();
        }

        escopoAtual = pilhaEscopos.peek();
    }

    public void imprimirTabela() {
        tabela.imprimir();
    }

    public TabelaDeSimbolos getTabela() {
        return tabela;
    }

    public List<String> getAvisosSemanticos() {
        return avisosSemanticos;
    }

    public void atualizarAvisosSemanticos() {
        avisosSemanticos.clear();

        for (Simbolo s : tabela.getSimbolos()) {
            if (s.funcao || s.procedimento) {
                continue;
            }

            if (!s.usado) {
                avisosSemanticos.add(
                        "Identificador '" + s.id + "' declarado e nao utilizado (escopo: " + s.escopo + ").");
            }

            if (!s.inicializado && !s.parametro) {
                avisosSemanticos.add(
                        "Identificador '" + s.id + "' declarado mas nunca inicializado (escopo: " + s.escopo
                                + ").");
            }
        }
    }

    private boolean isIdentificador(Token token) {
        return token != null && token.getId() == t_ID_TK;
    }
}