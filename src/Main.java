import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String codigo = Files.readString(Path.of("exemplos/erro-declaracao-depois-comando.txt"));

        Lexer lexer = new Lexer(codigo);
        List<Token> tokens = lexer.tokenizar();

        Parser parser = new Parser(tokens);
        parser.programa();

        System.out.println("Codigo sintaticamente correto!");
    }
}