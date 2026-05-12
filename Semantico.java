public class Semantico implements Constants {

    TabelaSimbolos tabela = new TabelaSimbolos();

    String tipoAtual = "";
    String escopoAtual = "global";

    public void executeAction(int action, Token token) throws SemanticError {

        Simbolo s;

        switch (action) {

            case 1:
                tipoAtual = token.getLexeme();
                break;

            case 2:
                tabela.inserir(token.getLexeme(), tipoAtual, "variavel", escopoAtual);
                break;

            case 3:
                tabela.inserir(token.getLexeme(), tipoAtual, "funcao", "global");
                break;

            case 4:
                tabela.inserir(token.getLexeme(), tipoAtual, "parametro", escopoAtual);
                break;

            case 5:
                s = tabela.buscar(token.getLexeme(), escopoAtual);
                if (s != null) {
                    s.inicializado = true;
                }
                break;

            case 6:
                s = tabela.buscar(token.getLexeme(), escopoAtual);
                if (s != null && !s.inicializado) {
                    System.out.println("Aviso: variável '" + s.nome + "' usada sem inicialização.");
                }
                break;

            case 99:
                tabela.imprimir();
                break;
        }

        System.out.println("Ação #" + action + ", Token: " + token);
    }

    public static void main(String[] args) throws Exception {

        Semantico s = new Semantico();

        s.tipoAtual = "int";
        s.executeAction(2, new Token(0, "x", 0));

        s.tipoAtual = "int";
        s.executeAction(2, new Token(0, "y", 0));


        s.executeAction(6, new Token(0, "x", 0));

        s.executeAction(99, new Token(0, "", 0));
    }
}