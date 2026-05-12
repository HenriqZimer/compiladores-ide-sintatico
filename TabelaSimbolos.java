import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {

    private List<Simbolo> tabela = new ArrayList<>();

    public boolean existe(String nome, String escopo) {
        for (Simbolo s : tabela) {
            if (s.nome.equals(nome) && s.escopo.equals(escopo)) {
                return true;
            }
        }
        return false;
    }

    public void inserir(String nome, String tipo, String modalidade, String escopo) throws SemanticError {

        if (existe(nome, escopo)) {
            throw new SemanticError("Erro: identificador '" + nome + "' já declarado no escopo '" + escopo + "'.");
        }

        tabela.add(new Simbolo(nome, tipo, modalidade, escopo));
    }
    
    public Simbolo buscar(String nome, String escopo) {
        // Primeiro tenta no escopo atual
        for (Simbolo s : tabela) {
            if (s.nome.equals(nome) && s.escopo.equals(escopo)) {
                return s;
            }
        }
        // Se não encontrar e não for escopo global, busca no escopo global
        if (!escopo.equals("global")) {
            for (Simbolo s : tabela) {
                if (s.nome.equals(nome) && s.escopo.equals("global")) {
                    return s;
                }
            }
        }
        return null;
    }

    public void marcarUsado(String nome, String escopo) throws SemanticError {
        Simbolo s = buscar(nome, escopo);
        if (s == null) {
            throw new SemanticError("Erro: identificador '" + nome + "' não foi declarado.");
        }
        s.usado = true;
    }

    public void marcarInicializado(String nome, String escopo) throws SemanticError {
        Simbolo s = buscar(nome, escopo);
        if (s == null) {
            throw new SemanticError("Erro: identificador '" + nome + "' não foi declarado.");
        }
        s.inicializado = true;
    }

    public String getTipo(String nome, String escopo) throws SemanticError {
        Simbolo s = buscar(nome, escopo);
        if (s == null) {
            throw new SemanticError("Erro: identificador '" + nome + "' não foi declarado.");
        }
        return s.tipo;
    }

    public List<Simbolo> verificarNaoUsados() {
        List<Simbolo> naoUsados = new ArrayList<>();
        for (Simbolo s : tabela) {
            if (!s.usado && !s.modalidade.equals("funcao") && !s.modalidade.equals("parametro")) {
                naoUsados.add(s);
            }
        }
        return naoUsados;
    }

    public void imprimir() {
        System.out.println("\n===== TABELA DE SIMBOLOS =====");
        if (tabela.isEmpty()) {
            System.out.println("Tabela de símbolos vazia.");
        } else {
            for (Simbolo s : tabela) {
                System.out.println(s);
            }
        }
        System.out.println("==============================\n");
    }

    public List<Simbolo> getTabela() {
        return new ArrayList<>(tabela);
    }

    public void limpar() {
        tabela.clear();
    }
}