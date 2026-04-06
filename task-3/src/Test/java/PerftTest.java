import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PerftTest {

    @Test
    void testPerftInitialDepth1() {
        Board board = new Board();
        long nodes = Perft.perft(board, 1);
        assertEquals(20, nodes);
    }

    @Test
    void testPerftInitialDepth2() {
        Board board = new Board();
        long nodes = Perft.perft(board, 2);
        assertEquals(400, nodes);
    }

    @Test
    void testPerftInitialDepth3() {
        Board board = new Board();
        long nodes = Perft.perft(board, 3);
        assertEquals(8902, nodes);
    }

    @Test
    void testPerftInitialDepth4() {
        Board board = new Board();
        long nodes = Perft.perft(board, 4);
        assertEquals(197281, nodes);
    }

    @Test
    void testPerftInitialDepth5() {
        Board board = new Board();
        long nodes = Perft.perft(board, 5);
        assertEquals(4865609, nodes);
    }
    @Test
    void testPerftInitialDepth6() {
        Board board = new Board();
        long nodes = Perft.perft(board, 6);
        assertEquals(119060324, nodes);
    }
}