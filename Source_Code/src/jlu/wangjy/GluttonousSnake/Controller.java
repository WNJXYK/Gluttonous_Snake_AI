package jlu.wangjy.GluttonousSnake;

import jlu.wangjy.GluttonousSnake.GameLogic.Node;
import java.util.LinkedList;


public abstract class Controller {
    private int direction = 0;

    public abstract long keyboard(int d);
    public abstract long program(int n, int k, LinkedList<Node> snake, Node food, Callback cb);

    protected boolean setDirection(int d) {
        if (0<=d && d<4) {
            direction = d;
            return true;
        }else return false;
    }

    public int getDirection() {
        return direction;
    }
}
