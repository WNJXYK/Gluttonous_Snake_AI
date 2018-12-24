package jlu.wangjy.GluttonousSnake;

import javafx.scene.input.KeyCode;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GameView extends JPanel implements Runnable, KeyListener {
    // 显示设置
    public static final int GAME_BLOCK = 30;
    // 颜色设置
    public static final Color COLOR_FOOD = Color.ORANGE;
    public static final Color COLOR_WALL = Color.BLACK;
    public static final Color COLOR_GROUND = Color.gray;
    public static final Color COLOR_HEAD = Color.GREEN;
    public static final Color COLOR_SNAKE = Color.PINK;
    private Thread mThread; //绘制线程
    private GameLogic mLogic; // 游戏主逻辑
    private BufferedImage image; // 游戏视图
    private Controller mCtrl; // 游戏控制器
    private Callback logCallback; // 日志回掉函数
    private static final int SLEEPTIME = 20;

    public GameView(Callback l0) {
        mCtrl = new Controller() {
            public long keyboard(int d) { setDirection(d); return 0; }
            public long program(int n, int k, LinkedList<GameLogic.Node> snake, GameLogic.Node food, Callback c0) {return -2;}
        }; // 新建键盘控制器
        mLogic = new GameLogic(mCtrl); // 新建主逻辑
        logCallback = l0; // 日志回掉监听借口
        this.addKeyListener(this); // 添加监听器
        mThread = new Thread(this);
        mThread.start(); // 开始线程
        repaint(); // 重新绘制
    }

    // 游戏状态控制函数
    public void terminate() {
        mLogic.end();
    }

    public void start() {
        mLogic.start();
    }

    public void switchState() {
        // Ctrl
        if (mLogic.getState() == GameLogic.FREEZE){
            mLogic.start();
        }else {
            mLogic.end();
        }
    }

    public void setSpeed(int v) {
        if (v > 550) mLogic.setSpeed(365 * 24 * 60 * 60 * 1000);
        else {
            mLogic.setSpeed(v);
        }
    }

    public void setController(Controller c) {
        mCtrl = c;
        mLogic.setController(c);
    }

    // 重载绘制函数
    public void paint(Graphics g) {
        super.paint(g);
        // Draw Wall
        int bgSize = GAME_BLOCK * (GameLogic.GAME_SIZE + 2);
        g.setColor(COLOR_WALL);
        g.fillRect(0, 0, bgSize, bgSize);
        // Draw Ground
        int gmSize = GAME_BLOCK * GameLogic.GAME_SIZE;
        g.setColor(COLOR_GROUND);
        g.fillRect(GAME_BLOCK, GAME_BLOCK, gmSize, gmSize);
        // FREEZE then exit
        if (mLogic.getState() == GameLogic.FREEZE) return;
        // Draw Snake
        boolean hFlag=false;
        int len=mLogic.getSnake().size()*4, now=0;
        for (GameLogic.Node p : mLogic.getSnake()) {
            g.setColor(Color.getHSBColor(350f/360f, 25f/100f, (float)(len-(now++))/(float)len));
            g.fillRect(p.y * GAME_BLOCK, p.x * GAME_BLOCK, GAME_BLOCK, GAME_BLOCK);
            if (!hFlag){
                g.setColor(COLOR_HEAD);
                g.drawRect(p.y * GAME_BLOCK, p.x * GAME_BLOCK, GAME_BLOCK, GAME_BLOCK);
                hFlag=true;
            }
        }
        // Draw Food
        g.setColor(COLOR_FOOD);
        if (mLogic.getFood().enbled)
            g.fillRect(mLogic.getFood().y * GAME_BLOCK, mLogic.getFood().x * GAME_BLOCK, GAME_BLOCK, GAME_BLOCK);
    }

    // 重载线程运行函数
    @Override
    public void run() {
        while (true) {
            try {
                boolean flag=mLogic.active(SLEEPTIME); // 激活贪吃蛇
                if (flag){ // 如果游戏继续执行，就尝试调用程序
                    long ret=mCtrl.program(mLogic.GAME_SIZE, mLogic.getSnake().size(), mLogic.getSnake(), mLogic.getFood(), logCallback); // 程序控制输入
                }
                logCallback.callback(mLogic.getGameInfo()); // 游戏信息回掉
                repaint(); // 重新绘制
                Thread.sleep(SLEEPTIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 重载键盘监听函数
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        long ret=-2; String c="?";
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                ret=mCtrl.keyboard(0); c="W";
                break;
            case KeyEvent.VK_DOWN:
                ret=mCtrl.keyboard(1); c="S";
                break;
            case KeyEvent.VK_LEFT:
                ret=mCtrl.keyboard(2); c="A";
                break;
            case KeyEvent.VK_RIGHT:
                ret=mCtrl.keyboard(3); c="D";
                break;
        }
        // Log Callback
        if (ret!=-2) {
            Map<String, String> res = new HashMap<String, String>();
            res.put("type", "ctrl");
            res.put("info", "Keyboard Input " + c);
            logCallback.callback(res);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
