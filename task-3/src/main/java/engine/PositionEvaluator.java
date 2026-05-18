package engine;

import model.BoardState;
import model.Side;

public interface PositionEvaluator {
    int evaluate(BoardState state, Side perspective);
}