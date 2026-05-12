public class Simbolo {
    String nome;
    String tipo;
    String modalidade;
    String escopo;
    boolean inicializado = false;

    public Simbolo(String nome, String tipo, String modalidade, String escopo) {
        this.nome = nome;
        this.tipo = tipo;
        this.modalidade = modalidade;
        this.escopo = escopo;
    }
}