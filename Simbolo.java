public class Simbolo {
    public String id;
    public String tipo;
    public boolean inicializado;
    public boolean usado;
    public String escopo;
    public boolean parametro;
    public int posicaoParametro;
    public boolean vetor;
    public boolean matriz;
    public boolean referencia;
    public boolean funcao;
    public boolean procedimento;

    public Simbolo(String id, String tipo, String escopo) {
        this.id = id;
        this.tipo = tipo;
        this.escopo = escopo;
        this.inicializado = false;
        this.usado = false;
        this.parametro = false;
        this.posicaoParametro = 0;
        this.vetor = false;
        this.matriz = false;
        this.referencia = false;
        this.funcao = false;
        this.procedimento = false;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", tipo=" + tipo +
                ", ini=" + inicializado +
                ", usada=" + usado +
                ", escopo=" + escopo +
                ", param=" + parametro +
                ", pos=" + posicaoParametro +
                ", vet=" + vetor +
                ", matriz=" + matriz +
                ", ref=" + referencia +
                ", func=" + funcao +
                ", proc=" + procedimento;
    }
}