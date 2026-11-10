public class Token {
    private final TipoToken tipo;
    private String texto;
    private int linha;
    private int coluna;

    public Token(TipoToken tipo, String texto, int linha, int coluna) {
        this.tipo = tipo;
        this.texto = texto;
        this.linha = linha;
        this.coluna = coluna;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public String getTexto() {
        return texto;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    @Override
    public String toString() {
        return getTipo() + " " + getTexto() + " (linha " + getLinha() + ", coluna " + getColuna() + ")";
    }
}
