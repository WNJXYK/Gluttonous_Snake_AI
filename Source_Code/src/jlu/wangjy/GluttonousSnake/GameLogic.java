package jlu.wangjy.GluttonousSnake;

import java.util.*;

public class GameLogic {
    // 游戏设置
    public static final int GAME_SIZE = 12;
    public static int bSPEED = 100;
    private static int SPEED = 0;
    // 位置设定
    public static final int dx[] = {-1, 1, 0, 0};
    public static final int dy[] = {0, 0, -1, 1};
    // 游戏状态
    private static final String STATE_NAME[] = {"未开始", "进行中", "胜利", "失败"};
    private static final String STATE_COLOR[] = {"black", "blue", "green", "red"};
    public static final int FREEZE = 0;
    public static final int RUNNING = 1;
    public static final int WIN = 2;
    public static final int DIE = 3;


    // 游戏内容
    public class Node {
        public int x, y;
        public boolean enbled;

        Node() {
            enbled = false;
        }

        Node(int x0, int y0) {
            x = x0;
            y = y0;
            enbled = true;
        }
    }

    private Controller gController;
    private LinkedList<Node> gSnake;
    private Node gFood;
    private int gTime, gStep, gDirection;
    private int gState;

    // 辅助内容
    private boolean map[][] = new boolean[GAME_SIZE + 5][GAME_SIZE + 5];

    GameLogic(Controller c) {
        gController = c;
        gState = FREEZE;
        gSnake = new LinkedList<Node>();
    }

    // 初始化游戏
    private void init() {
        gSnake.clear();
        gSnake.add(new Node(GAME_SIZE / 2, GAME_SIZE / 2 + 1));
        gSnake.add(new Node(GAME_SIZE / 2 + 1, GAME_SIZE / 2 + 1));
        gSnake.add(new Node(GAME_SIZE / 2 + 2, GAME_SIZE / 2 + 1));
        gFood = new Node();
        SPEED = gTime = gStep = gDirection = 0;
        gState = RUNNING;
    }

    // 中止游戏
    private void terminate() {
        gState = FREEZE;
        gSnake.clear();
        if (gFood != null) gFood.enbled = false;
        gTime = gStep = gDirection = 0;
    }

    // 时间分片
    public boolean active(int delay) {
        if (gState != RUNNING) return false;
        gTime += delay;
        if ((SPEED += delay) > bSPEED) SPEED = 0;
        else return false;
        gStep++;
        presolve(false);
        boolean flag = activeSnake();
        presolve(true);
        generateFood();
        return flag;
    }

    // 地图预处理
    private void presolve(boolean addLast) {
        if (gState != RUNNING) return;
        // Map Snake to Map
        for (int i = 1; i <= GAME_SIZE; i++)
            for (int j = 1; j <= GAME_SIZE; j++)
                map[i][j] = false;
        int len = gSnake.size(), cnt = addLast ? -1 : 0;
        for (Node p : gSnake) {
            if ((++cnt) != len) map[p.x][p.y] = true;
        }
    }

    // 激活蛇
    private boolean activeSnake() {
        if (gState != RUNNING) return false;

        // Check Whether switch Direction
        if (gSnake.size() == 1
                || !(gSnake.get(0).x + dx[gController.getDirection()] == gSnake.get(1).x && gSnake.get(0).y + dy[gController.getDirection()] == gSnake.get(1).y)) {
            gDirection = gController.getDirection();
        }

        // Get Next Position
        int x = gSnake.peekFirst().x, y = gSnake.peekFirst().y;
        int nx = x + dx[gDirection], ny = y + dy[gDirection];
        // Check Out
        if (nx < 1 || ny < 1 || nx > GAME_SIZE || ny > GAME_SIZE) {
            gState = DIE;
            return false;
        }
        // Check Kill Self
        if (map[nx][ny]) {
            gState = DIE;
            return false;
        }
        // Active Snake
        if (gFood.enbled && gFood.x == nx && gFood.y == ny) {
            // Eat Food
            gSnake.addFirst(new Node(nx, ny));
            gFood.enbled = false;
        } else {
            // Move
            Node p = gSnake.pollLast();
            p.x = nx;
            p.y = ny;
            gSnake.addFirst(p);
        }

        // Check Win
        if (gSnake.size() == GAME_SIZE * GAME_SIZE) {
            gState = WIN;
            return false;
        }

        return true;
    }

    // 产生食物
    private void generateFood() {
        if (gState != RUNNING) return;
        if (gFood.enbled) return;
        // Rand a legal Position
        Random rand = new Random();
        int x = Math.abs(rand.nextInt()) % GAME_SIZE + 1, y = Math.abs(rand.nextInt()) % GAME_SIZE + 1;
        while (map[x][y]) {
            x = Math.abs(rand.nextInt()) % GAME_SIZE + 1;
            y = Math.abs(rand.nextInt()) % GAME_SIZE + 1;
        }
        // Set Food
        gFood.enbled = true;
        gFood.x = x;
        gFood.y = y;
    }

    // 外部功能设置函数
    public void start() {
        init();
    }

    public void end() {
        terminate();
    }

    public void setSpeed(int speed) {
        bSPEED = speed;
    }

    public void setController(Controller c) {
        gController = c;
    }

    // 外部状态查询函数
    public LinkedList<Node> getSnake() {
        return gSnake;
    }

    public Node getFood() {
        return gFood;
    }

    public int getState() {
        return gState;
    }

    public Map<String, String> getGameInfo() {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put("type", "game");
        ret.put("state", STATE_NAME[gState] + " " + ((int) 100 * gSnake.size() / GAME_SIZE / GAME_SIZE) + "%");
        ret.put("state_color", STATE_COLOR[gState]);
        ret.put("step", String.valueOf(gStep));
        String mTime = "";
        if (gTime > 3600000) mTime = mTime + (gTime / 3600000) + "H ";
        if (gTime > 60000) mTime = mTime + (gTime % 3600000 / 60000) + "M ";
        mTime = mTime + (gTime % 60000 / 1000) + "S ";
        ret.put("time", mTime);
        return ret;
    }

}
