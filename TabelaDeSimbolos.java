import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TabelaDeSimbolos {

    private List<Simbolo> simbolos = new ArrayList<>();

    public void inserir(Simbolo simbolo) throws SemanticError {
        if (existeNoEscopo(simbolo.id, simbolo.escopo)) {
            throw new SemanticError(
                    "Identificador '" + simbolo.id + "' já declarado no escopo '" + simbolo.escopo + "'");
        }

        simbolos.add(simbolo);
    }

    public boolean existeNoEscopo(String id, String escopo) {
        for (Simbolo s : simbolos) {
            if (s.id.equals(id) && s.escopo.equals(escopo)) {
                return true;
            }
        }
        return false;
    }

    public Simbolo buscar(String id, String escopoAtual) {
        for (int i = simbolos.size() - 1; i >= 0; i--) {
            Simbolo s = simbolos.get(i);
            if (s.id.equals(id) && s.escopo.equals(escopoAtual)) {
                return s;
            }
        }

        for (int i = simbolos.size() - 1; i >= 0; i--) {
            Simbolo s = simbolos.get(i);
            if (s.id.equals(id) && s.escopo.equals("global")) {
                return s;
            }
        }

        return null;
    }

    public Simbolo buscar(String id, Stack<String> pilhaEscopos) {
        for (int i = pilhaEscopos.size() - 1; i >= 0; i--) {
            String escopo = pilhaEscopos.get(i);

            for (int j = simbolos.size() - 1; j >= 0; j--) {
                Simbolo s = simbolos.get(j);
                if (s.id.equals(id) && s.escopo.equals(escopo)) {
                    return s;
                }
            }
        }

        return null;
    }

    public Simbolo ultimo() {
        if (simbolos.isEmpty()) {
            return null;
        }

        return simbolos.get(simbolos.size() - 1);
    }

    public void imprimir() {
        System.out.println("===== TABELA DE SÍMBOLOS =====");

        for (Simbolo s : simbolos) {
            System.out.println(s);
        }
    }

    public List<Simbolo> getSimbolos() {
        return simbolos;
    }
}