//Tiago Mol, Luiz Eduardo, Leandro Augusto, Vinicius Nunes

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.*;

public class Minesweeper extends JFrame {
    // Constantes para representar uma mina e o tamanho da janela
    private static final int MINE = 10;
    private static final int SIZE = 500;

    // Array para armazenar células reutilizáveis
    private static Cell[] reusableStorage = new Cell[8];

    // Variáveis para o número total de minas e células marcadas
    private int totalMines = 10;
    private int flaggedCells = 0;

    // Variáveis para o tamanho do tabuleiro, array de células e janela principal
    private int gridSize;
    private Cell[][] cells;
    private JFrame frame;

    // Botões e rótulos para interação do usuário
    private JButton reset;
    private JButton giveUp;
    private JButton finish;
    private JButton panic;
    private JLabel minesCounterLabel;

    // Célula atualmente selecionada
    private Cell currentSelectedCell;
    

    private void redefineAndInsertImage(String caminhoDaImagem, int largura, int altura) {
        ImageIcon imagemOriginal = new ImageIcon(caminhoDaImagem);
        Image imagemRedimensionada = imagemOriginal.getImage().getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
        reset.setIcon(new ImageIcon(imagemRedimensionada));
    }

    // Listener para eventos dos botões
    private final ActionListener actionListener = actionEvent -> {
        Object source = actionEvent.getSource();
        if (source == reset) {
            // Redefinir a imagem e criar um novo conjunto de minas
            redefineAndInsertImage("img/happy.png", 25, 20);
            createMines();
        } else if (source == giveUp) {
            // Revelar todo o tabuleiro e exibir mensagem de desistência
            revealBoardAndDisplay("Você desistiu.");
        } else if (source == finish) {
            // Encerrar o programa
            System.exit(0);
        } else if (source == panic) {
            // Exibir imagem em tela cheia durante o jogo
            try {
                displayFullScreenImage("img/panic.png");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Manipular a célula clicada
            handleCell((Cell) source);
        }
    };

    // Atualizar o rótulo que exibe o número de minas restantes
    private void updateMinesCounterLabel() {
        int remainingMines = totalMines - flaggedCells;
        minesCounterLabel.setText("Minas: " + remainingMines);
    }

    // Classe interna representando uma célula no jogo
    private class Cell extends JButton {
        // Valor da célula, posição na grade
        private int value;
        private final int row;
        private final int col;

        // Construtor da célula
        Cell(final int row, final int col, final ActionListener actionListener) {
            this.row = row;
            this.col = col;
            addActionListener(actionListener);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Atualizar célula atualmente selecionada
                    currentSelectedCell = Cell.this;
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // Limpar célula atualmente selecionada ao sair
                    currentSelectedCell = null;
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    // Manipular clique direito do mouse
                    if (SwingUtilities.isRightMouseButton(e)) {
                        handleRightClick();
                    }
                }
            });
            setText("");
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    // Manipular pressionamento da tecla F para marcar/desmarcar
                    if (e.getKeyCode() == KeyEvent.VK_F) {
                        handleFlagKeyPress();
                    }
                }
            });
            setFocusable(true); // Permitir que a célula receba o foco
        }

        // Manipular clique direito do mouse
        private void handleRightClick() {
            if (!isEnabled()) {
                return; // Célula já revelada ou marcada
            }

            if (getText().equals("M")) {
                setText(""); // Desmarcar a célula se já estiver marcada
                flaggedCells--;
            } else if (flaggedCells < 10) {
                setText("M"); // Marcar a célula se não atingiu o limite máximo de bandeiras
                flaggedCells++;
            }

            // Atualizar rótulo de minas restantes
            updateMinesCounterLabel();
        }

        // Obter o valor da célula
        int getValue() {
            return value;
        }

        // Definir o valor da célula
        void setValue(int value) {
            this.value = value;
        }

        // Verificar se a célula é uma mina
        boolean isAMine() {
            return value == MINE;
        }

        // Redefinir a célula para o estado inicial
        void reset() {
            setValue(0);
            setEnabled(true);
            setText("");
        }

        // Revelar o conteúdo da célula
        void reveal() {
            setEnabled(false);
            setText(isAMine() ? "X" : String.valueOf(value));
        }

        // Atualizar a contagem de minas nos vizinhos
        void updateNeighbourCount() {
            getNeighbours(reusableStorage);
            for (Cell neighbour : reusableStorage) {
                if (neighbour == null) {
                    break;
                }
                if (neighbour.isAMine()) {
                    value++;
                }
            }
        }

        // Obter os vizinhos da célula
        void getNeighbours(final Cell[] container) {
            for (int i = 0; i < reusableStorage.length; i++) {
                reusableStorage[i] = null;
            }

            int index = 0;

            for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                for (int colOffset = -1; colOffset <= 1; colOffset++) {
                    if (rowOffset == 0 && colOffset == 0) {
                        continue;
                    }
                    int rowValue = row + rowOffset;
                    int colValue = col + colOffset;

                    if (rowValue < 0 || rowValue >= gridSize || colValue < 0 || colValue >= gridSize) {
                        continue;
                    }

                    container[index++] = cells[rowValue][colValue];
                }
            }
        }

        // Verificar igualdade com outra célula
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Cell cell = (Cell) obj;
            return row == cell.row && col == cell.col;
        }

        // Gerar código de hash baseado na posição da célula
        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    // Construtor principal para iniciar o jogo com um tamanho de grade específico
    private Minesweeper(final int gridSize) {
        this.gridSize = gridSize;
        cells = new Cell[gridSize][gridSize];
        frame = new JFrame("Minesweeper");
        frame.setUndecorated(true); // Remover decoração da janela

        // Inicializar painel de botões
        initializeButtonPanel();

        // Inicializar a grade do jogo
        initializeGrid();

        // Configurar a janela principal
        frame.setSize(SIZE, SIZE);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Inicializar o painel de botões
    private void initializeButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JPanel panicPanel = new JPanel(new BorderLayout());

        reset = new JButton("Resetar");
        giveUp = new JButton("Desistir");
        finish = new JButton("Encerrar");
        panic = new JButton(" \n");
        redefineAndInsertImage("img/happy.png", 25, 20);

        reset.addActionListener(actionListener);
        giveUp.addActionListener(actionListener);
        finish.addActionListener(actionListener);

        buttonPanel.add(reset);
        minesCounterLabel = new JLabel("Minas: " + totalMines);
        buttonPanel.add(minesCounterLabel);
        buttonPanel.add(giveUp);
        buttonPanel.add(finish);
        frame.add(buttonPanel, BorderLayout.NORTH);

        panic.addActionListener(actionListener);
        panicPanel.add(panic);
        frame.add(panicPanel, BorderLayout.SOUTH);
    }

    // Manipular pressionamento da tecla F para marcar/desmarcar
    private void handleFlagKeyPress() {
        if (currentSelectedCell != null) {
            currentSelectedCell.handleRightClick();
        }
    }

    // Inicializar a grade do jogo
    private void initializeGrid() {
        Container grid = new Container();
        grid.setLayout(new GridLayout(gridSize, gridSize));

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new Cell(row, col, actionListener);
                grid.add(cells[row][col]);
                cells[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            handleGridRightClick(e);
                        }
                    }
                });
                cells[row][col].setFocusable(true); // Permitir que cada célula receba o foco
            }
        }

        
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                final Cell cell = cells[row][col];
                cell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        handleGridRightClick(e);
                    }
                }
                });
            }
        }


        createMines();
        frame.add(grid, BorderLayout.CENTER);
    }

    // Manipular clique direito do mouse na grade
    private void handleGridRightClick(MouseEvent e) {
        Component source = e.getComponent();
        if (source instanceof Cell) {
            Cell cell = (Cell) source;
            cell.handleRightClick();
        }
    }

    // Redefinir todas as células para o estado inicial
    private void resetAllCells() {
        flaggedCells = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reset();
            }
        }
    }

    // Criar e distribuir minas aleatórias
    private void createMines() {
        redefineAndInsertImage("img/happy.png", 25, 20);
        resetAllCells();

        final int mineCount = 10;
        final Random random = new Random();

        Set<Integer> positions = new HashSet<>(gridSize * gridSize);
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                positions.add(row * gridSize + col);
            }
        }

        totalMines = mineCount;

        for (int index = 0; index < mineCount; index++) {
            int choice = random.nextInt(positions.size());
            int row = choice / gridSize;
            int col = choice % gridSize;
            cells[row][col].setValue(MINE);
            positions.remove(choice);
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (!cells[row][col].isAMine()) {
                    cells[row][col].updateNeighbourCount();
                }
            }
        }
    }

    // Manipular clique em uma célula do jogo
    private void handleCell(Cell cell) {
        if (cell.getText().equals("M")) {
            return; // Não permitir revelar uma célula marcada com bandeira
        }

        if (cell.isAMine()) {
            cell.setForeground(Color.RED);
            cell.reveal();
            revealBoardAndDisplay("Você clicou em uma mina!");
            return;
        }

        if (cell.getValue() == 0) {
            // Célula vazia, realizar cascata
            Set<Cell> positions = new HashSet<>();
            positions.add(cell);
            cascade(positions);
        } else {
            // Revelar a célula
            cell.reveal();
        }

        // Verificar se o jogador ganhou
        checkForWin();
    }

    // Revelar todo o tabuleiro e exibir mensagem
    private void revealBoardAndDisplay(String message) {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reveal();
            }
        }
        redefineAndInsertImage("img/sad.png", 25, 20);
        JOptionPane.showMessageDialog(
                frame, message, "Fim de Jogo",
                JOptionPane.ERROR_MESSAGE
        );

        // Criar um novo conjunto de minas e reiniciar contadores
        createMines();
        flaggedCells = 0;
        updateMinesCounterLabel();
    }

    // Realizar cascata de revelação de células vazias
    private void cascade(Set<Cell> positionsToClear) {
        while (!positionsToClear.isEmpty()) {
            Cell cell = positionsToClear.iterator().next();
            positionsToClear.remove(cell);
            cell.reveal();

            cell.getNeighbours(reusableStorage);
            for (Cell neighbour : reusableStorage) {
                if (neighbour == null) {
                    break;
                }
                if (neighbour.getValue() == 0 && neighbour.isEnabled()) {
                    positionsToClear.add(neighbour);
                } else {
                    neighbour.reveal();
                }
            }
        }
    }

    // Verificar se todas as células não minadas foram reveladas
    private void checkForWin() {
        boolean won = true;
        outer:
        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                if (!cell.isAMine() && cell.isEnabled()) {
                    won = false;
                    break outer;
                }
            }
        }

        if (won) {
            JOptionPane.showMessageDialog(
                    frame, "Você ganhou!", "Parabéns",
                    JOptionPane.INFORMATION_MESSAGE
            );
            totalMines = 10;
            createMines();  // Resetar o jogo
        }
    }

    // Método estático para iniciar o jogo com um tamanho de grade específico
    private static void run(final int gridSize) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) { }
        new Minesweeper(gridSize);
    }

    // Exibir uma imagem em tela cheia
    public static void displayFullScreenImage(String imagePath) throws Exception {
        File file = new File(imagePath);
        Image image = ImageIO.read(file);
        JLabel label = new JLabel(new ImageIcon(image));

        JFrame fullScreenFrame = new JFrame();
        fullScreenFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fullScreenFrame.getContentPane().add(label, BorderLayout.CENTER);
        fullScreenFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fullScreenFrame.setUndecorated(true); // Remover bordas do JFrame
        fullScreenFrame.setVisible(true);
        fullScreenFrame.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fullScreenFrame.dispose(); // Fechar a janela em tela cheia
            }
        });
    }

    // Método principal para iniciar o programa
    public static void main(String[] args) {
        final int gridSize = 9;
        SwingUtilities.invokeLater(() -> Minesweeper.run(gridSize));
    }
}
