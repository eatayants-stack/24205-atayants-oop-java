import model.Board;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PerftTest {

    @Test
    public void testPerftInitialDepth1() {
        Board board = new Board();
        long nodes = Perft.perft(board, 1);
        assertEquals(20, nodes);
    }

    @Test
    public void testPerftInitialDepth2() {
        Board board = new Board();
        long nodes = Perft.perft(board, 2);
        assertEquals(400, nodes);
    }

    @Test
    public void testPerftInitialDepth3() {
        Board board = new Board();
        long nodes = Perft.perft(board, 3);
        assertEquals(8902, nodes);
    }

    @Test
    public void testPerftInitialDepth4() {
        Board board = new Board();
        long nodes = Perft.perft(board, 4);
        assertEquals(197281, nodes);
    }

    @Test
    public void testPerftInitialDepth5() {
        Board board = new Board();
        long nodes = Perft.perft(board, 5);
        assertEquals(4865609, nodes);
    }
    @Test
    public void testPerftInitialDepth6() {
        Board board = new Board();
        long nodes = Perft.perft(board, 6);
        assertEquals(119060324, nodes);
    }
}