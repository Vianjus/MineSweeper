import java.util.Scanner;

public class MainMine {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Escolha uma opção:");
        System.out.println("1. Campo Minado no Terminal");
        System.out.println("2. Campo Minado com Interface Gráfica (Java Swing)");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                playMinesweeperInTerminal();
                break;
            case 2:
                playMinesweeperWithSwing();
                break;
            default:
                System.out.println("Opção inválida. Encerrando o programa.");
        }

        scanner.close();
    }

    private static void playMinesweeperInTerminal() {
        // Chame o método principal do jogo no terminal aqui
        TerminalMinesweeper.main(new String[]{});
    }

    private static void playMinesweeperWithSwing() {
        // Chame o método principal do jogo com Swing aqui
        Minesweeper.main(new String[]{});
    }
}

