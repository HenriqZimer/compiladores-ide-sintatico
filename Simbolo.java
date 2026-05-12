public class Simbolo {
    String nome;
    String tipo;
    String modalidade;
    String escopo;
    boolean inicializado = false;
    boolean usado = false;
    int tamanho = 1; // Para vetores (padrão 1 para variáveis)

    public Simbolo(String nome, String tipo, String modalidade, String escopo) {
        this.nome = nome;
        this.tipo = tipo;
        this.modalidade = modalidade;
        this.escopo = escopo;
    }

    @Override
    public String toString() {
        return "Nome: " + nome +
               " | Tipo: " + tipo +
               " | Modalidade: " + modalidade +
               " | Escopo: " + escopo +
               " | Inicializado: " + (inicializado ? "Sim" : "Não") +
               " | Usado: " + (usado ? "Sim" : "Não") +
               " | Tamanho: " + tamanho;
    }
}