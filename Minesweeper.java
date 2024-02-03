import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;



public class Minesweeper extends JFrame {
    private static final int MINE = 10;
    private static final int SIZE = 500;
    private static Cell[] reusableStorage = new Cell[8];
    private int totalMines = 10;
    private int flaggedCells = 0;

    private int gridSize;
    private Cell[][] cells;
    private JFrame frame;
    private JButton reset;
    private JButton giveUp;
    private JButton finish;
    private JButton panic;

    private JLabel minesCounterLabel;

    private void redefineAndInsertImage(String caminhoDaImagem, int largura, int altura) {
        ImageIcon imagemOriginal = new ImageIcon(caminhoDaImagem);
        Image imagemRedimensionada = imagemOriginal.getImage().getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
        reset.setIcon(new ImageIcon(imagemRedimensionada));
    }

    private final ActionListener actionListener = actionEvent -> {
        Object source = actionEvent.getSource();
        if (source == reset) {
            redefineAndInsertImage("img/happy.png",25,20);
            createMines();
        } 
        else if (source == giveUp) {
            revealBoardAndDisplay("Você desistiu.");
        }
        else if (source == finish){
            System.exit(0);
        } 
        else if (source == panic){
            try {
                displayFullScreenImage("img/panic.png");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        else {
            redefineAndInsertImage("img/happy.png",25,20);
            handleCell((Cell) source);
        }
    };

    // Adicione o método updateMinesCounterLabel() na classe Minesweeper
    private void updateMinesCounterLabel() {
        int remainingMines = totalMines - flaggedCells;
        // Atualize o rótulo do contador de minas
        minesCounterLabel.setText("Minas: " + remainingMines);
    }

    private class Cell extends JButton {
        // icons
        /*ImageIcon flag = new ImageIcon("C:\\Users\\leand\\Área de Trabalho\\new\\img\\flag.png");
        ImageIcon bomb = new ImageIcon("C:\\Users\\leand\\Área de Trabalho\\new\\img\\bomb.png");
        ImageIcon sadFace = new ImageIcon("img/sad.png");
        ImageIcon happyFace = new ImageIcon("img/happy.png");*/

        private int value;
        private final int row;
        private final int col;

        Cell(final int row, final int col, final ActionListener actionListener) {
            this.row = row;
            this.col = col;
            addActionListener(actionListener);
            // Add the following code to handle right-click events
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        handleRightClick();
                    }
                }
            });
            setText("");
        }

        private void handleRightClick() {
            if (!isEnabled()) {
                return; // Célula já revelada ou marcada
            }
    
            if (getText().equals("M")) {
                setText(""); // Desmarcar a célula se já estiver marcada
                flaggedCells--;
            } else if (flaggedCells < 10) {  // Verifique o limite máximo de bandeiras
                setText("M"); // Marcar a célula
                flaggedCells++;
            }
    
            updateMinesCounterLabel();
        }
        
        

        int getValue() {
            return value;
        }

        void setValue(int value) {
            this.value = value;
        }

        boolean isAMine() {
            return value == MINE;
        }

        void reset() {
            setValue(0);
            setEnabled(true);
            setText("");
        }

        void reveal() {
            setEnabled(false);
            setText(isAMine() ? "X" : String.valueOf(value));
        }

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

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Cell cell = (Cell) obj;
            return row == cell.row &&
                    col == cell.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    private Minesweeper(final int gridSize) {
        this.gridSize = gridSize;
        cells = new Cell[gridSize][gridSize];

        frame = new JFrame("Minesweeper");
        frame.setUndecorated(true); 

        initializeButtonPanel();
        initializeGrid();

        frame.setSize(SIZE, SIZE);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // adds panic mode too 
    private void initializeButtonPanel() {
        JPanel buttonPanel = new JPanel();
    
        reset = new JButton("Reset");
        giveUp = new JButton("Desistir");
        finish = new JButton("Encerrar");
        panic = new JButton("Panic");
        redefineAndInsertImage("img/happy.png",25,20);
    
        reset.addActionListener(actionListener);
        giveUp.addActionListener(actionListener);
        finish.addActionListener(actionListener);
        panic.addActionListener(actionListener);
        
        buttonPanel.add(reset);
        buttonPanel.add(giveUp);
        buttonPanel.add(finish);
        buttonPanel.add(panic);
    
        // Inicialize a variável minesCounterLabel
        minesCounterLabel = new JLabel("Minas: " + totalMines);
        buttonPanel.add(minesCounterLabel);
    
        frame.add(buttonPanel, BorderLayout.NORTH);
    }
    

    private void initializeGrid() {
        Container grid = new Container();
        grid.setLayout(new GridLayout(gridSize, gridSize));

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new Cell(row, col, actionListener);
                grid.add(cells[row][col]);
            }
        }

        grid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    handleGridRightClick(e);
                }
            }
        });

        createMines();
        frame.add(grid, BorderLayout.CENTER);
    }

    private void handleGridRightClick(MouseEvent e) {
        Component source = e.getComponent();
        if (source instanceof Cell) {
            Cell cell = (Cell) source;
            cell.handleRightClick();
        }
    }

    private void resetAllCells() {
        flaggedCells = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reset();
            }
        }
    }

    private void createMines() {
        redefineAndInsertImage("img/happy.png",25,20);
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

    private void handleCell(Cell cell) {
        if (cell.getText().equals("M")) {
            return; // Não permita revelar uma célula marcada com bandeira
        }
    
        if (cell.isAMine()) {
            cell.setForeground(Color.RED);
            cell.reveal();
            revealBoardAndDisplay("Você clicou em uma mina!");
            return;
        }
    
        if (cell.getValue() == 0) {
            Set<Cell> positions = new HashSet<>();
            positions.add(cell);
            cascade(positions);
        } else {
            cell.reveal();
        }
        
        checkForWin();
    }

    private void revealBoardAndDisplay(String message) {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reveal();
            }
        }
        redefineAndInsertImage("img/sad.png",25,20);
        JOptionPane.showMessageDialog(
                frame, message, "Fim de Jogo",
                JOptionPane.ERROR_MESSAGE
        );
    
        createMines();
        flaggedCells = 0;
        updateMinesCounterLabel();
    }
    

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
            createMines();  // Resetar o jogo
        }
    }
    
    private static void run(final int gridSize) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) { }
        new Minesweeper(gridSize);
    }

    public static void displayFullScreenImage(String imagePath) throws Exception{
        File file = new File(imagePath);
        Image image = ImageIO.read(file);
        JLabel label = new JLabel(new ImageIcon(image));

        JFrame fullScreenFrame = new JFrame();
        fullScreenFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fullScreenFrame.getContentPane().add(label, BorderLayout.CENTER);
        fullScreenFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fullScreenFrame.setUndecorated(true); // Remove bordas do JFrame
        fullScreenFrame.setVisible(true);
    
    }

    public static void main(String[] args) {
        final int gridSize = 9;
        SwingUtilities.invokeLater(() -> Minesweeper.run(gridSize));
    }
}