import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String codigo = Files.readString(Path.of("exemplos/valido.txt"));

        AnalisadorLexico analisadorLexico = new AnalisadorLexico(codigo);
        List<Token> tokens = analisadorLexico.tokenizar();

        AnalisadorSintatico analisadorSintatico = new AnalisadorSintatico(tokens);
        analisadorSintatico.programa();

        System.out.println("Codigo sintaticamente correto!");
    }
}