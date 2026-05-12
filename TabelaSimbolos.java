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
            throw new SemanticError("Erro: identificador '" + nome + "' já declarado.");
        }

        tabela.add(new Simbolo(nome, tipo, modalidade, escopo));
    }
    
    public Simbolo buscar(String nome, String escopo) {
        for (Simbolo s : tabela) {
            if (s.nome.equals(nome) && s.escopo.equals(escopo)) {
                return s;
            }
        }
        return null;
    }

    public void imprimir() {
        System.out.println("----- TABELA DE SIMBOLOS -----");
        for (Simbolo s : tabela) {
            System.out.println(
                "Nome: " + s.nome +
                " | Tipo: " + s.tipo +
                " | Modalidade: " + s.modalidade +
                " | Escopo: " + s.escopo
            );
        }
    }
}