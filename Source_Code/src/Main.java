import jlu.wangjy.GluttonousSnake.*;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;


public class Main extends JFrame implements Callback {
    private GameView game; // GameView
    private static int SCREENHEIGHT, SCREENWIDTH;
    private static final int BRODER = 5;
    private static final int CTRLWIDTH = 200, CTRLHEIGHT = 65;
    private JarUtil tool = new JarUtil(Main.class);
    private JLabel gameInfo; // 游戏信息
    private DefaultListModel comListModel = new DefaultListModel();//步骤模型
    private JScrollPane comListPanel;
    private JList comList = new JList();//步骤列表


    Main() {
        super("贪吃蛇自动寻路程序模拟系统");
        setLayout(null);

        // Read Config File
        int gmSize = GameView.GAME_BLOCK * (GameLogic.GAME_SIZE + 2);
        Properties prop = new Properties();
        try{
            InputStream in = new BufferedInputStream(new FileInputStream("config.properties"));
            prop.load(in);
            if (prop.containsKey("WIDTH")) {
                SCREENWIDTH=Integer.parseInt(prop.getProperty("WIDTH"));
            } else SCREENWIDTH = gmSize + BRODER * 2 + CTRLWIDTH;
            if (prop.containsKey("HEIGHT")) {
                SCREENHEIGHT=Integer.parseInt(prop.getProperty("HEIGHT"));
            } else SCREENHEIGHT = gmSize+BRODER;
            in.close();
        } catch(Exception e){
            System.out.println(e.getMessage());
            SCREENWIDTH = gmSize + BRODER * 2 + CTRLWIDTH;
            SCREENHEIGHT = gmSize+BRODER;
        }
        setSize(SCREENWIDTH, SCREENHEIGHT);

        // Save Config File
        addComponentListener(new ComponentAdapter(){
            @Override public void componentResized(ComponentEvent e){
                try{
                    FileOutputStream oFile = new FileOutputStream("config.properties", false);
                    prop.setProperty("WIDTH", String.valueOf((int)e.getComponent().getBounds().getWidth()));
                    prop.setProperty("HEIGHT", String.valueOf((int)e.getComponent().getBounds().getHeight()));
                    prop.store(oFile, "Resized");
                    oFile.close();
                }
                catch(Exception er){
                    System.out.println(er.getMessage());
                }
            }
        });




        // Add Control Button
        JButton ctrl = new JButton("Start/Terminate");
        add(ctrl);
        ctrl.setBounds(gmSize + BRODER, BRODER, CTRLWIDTH, CTRLHEIGHT);
        ctrl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.switchState();
                game.requestFocus();
            }
        });

        // Add Game Info
        gameInfo = new JLabel("");
        gameInfo.setHorizontalAlignment(JLabel.CENTER);
        add(gameInfo);
        gameInfo.setBounds(gmSize + BRODER, BRODER * 2 + CTRLHEIGHT, CTRLWIDTH, CTRLHEIGHT);

        // Add Speed Control
        JLabel SpeedDelayedLabel = new JLabel("贪吃蛇速度时间(100ms):");
        SpeedDelayedLabel.setHorizontalAlignment(JLabel.CENTER);
        JSlider SpeedDelayed = new JSlider(5, 600, 100);
        SpeedDelayed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (SpeedDelayed.getValue() <= 550) {
                    SpeedDelayedLabel.setText("贪吃蛇速度时间(" + String.valueOf(SpeedDelayed.getValue() > 500 ? 500 : SpeedDelayed.getValue()) + "ms):");
                } else {
                    SpeedDelayedLabel.setText("贪吃蛇暂停");
                }
                game.setSpeed(SpeedDelayed.getValue());
                game.requestFocus();
            }
        });
        add(SpeedDelayed);
        add(SpeedDelayedLabel);
        SpeedDelayedLabel.setBounds(gmSize + BRODER, BRODER * 3 + CTRLHEIGHT * 2, CTRLWIDTH, CTRLHEIGHT / 2);
        SpeedDelayed.setBounds(gmSize + BRODER, BRODER * 3 + CTRLHEIGHT * 2 + CTRLHEIGHT / 2, CTRLWIDTH, CTRLHEIGHT / 2);

        // Add Controller Switch
        JPanel ctrlMode = new JPanel();
        // Keyboard
        JLabel blackLabel = new JLabel("<html><p style=\"font-family:times;font-size:13px;text-align:center\">贪吃蛇控制方式切换</p></html>");
        JRadioButton keyboardController = new JRadioButton("键盘方式控制", true);
        keyboardController.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop Game
                game.terminate();
                // Set Keyboard Controller
                game.setController(new Controller() {
                    public long program(int n, int k, LinkedList<GameLogic.Node> snake, GameLogic.Node food, Callback c0) { return -2; }
                    public long keyboard(int d) { setDirection(d); return 0; }
                });
            }
        });
        // Program
        JFileChooser pathProgramChooser = new JFileChooser();
        JRadioButton programController = new JRadioButton("程序控制（无程序）");
        programController.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop Game
                game.terminate();
                pathProgramChooser.setCurrentDirectory(new File(tool.getJarPath()));
                int returnVal = pathProgramChooser.showOpenDialog(Main.this);
                if (returnVal == JFileChooser.APPROVE_OPTION && pathProgramChooser.getSelectedFile().canExecute()) {
                    programController.setText("程序控制(" + pathProgramChooser.getSelectedFile().getName() + ")");
                    // Set Program Controller
                    game.setController(new ProgramController(pathProgramChooser.getSelectedFile().getAbsolutePath()));
                    System.out.println(pathProgramChooser.getSelectedFile().getAbsolutePath());
                } else {
                    keyboardController.doClick();
                    programController.setText("程序控制（无程序）");
                    String res = "";
                    if (returnVal != JFileChooser.APPROVE_OPTION) res = "选择被取消";
                    else {
                        if (!pathProgramChooser.getSelectedFile().canExecute()) {
                            res = "程序不可执行";
                        } else res = "未知错误";
                    }
                    JOptionPane.showMessageDialog(null, "原因: " + res, "选择程序失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        ButtonGroup controllerGroup = new ButtonGroup();
        controllerGroup.add(keyboardController);
        controllerGroup.add(programController);
        ctrlMode.add(blackLabel);
        ctrlMode.add(keyboardController);
        ctrlMode.add(programController);
        add(ctrlMode);
        ctrlMode.setBounds(gmSize + BRODER, BRODER * 3 + CTRLHEIGHT * 3, CTRLWIDTH, CTRLHEIGHT + CTRLHEIGHT/2);

        // Recoder
        comList.setModel(comListModel);
        comList.setBorder(BorderFactory.createTitledBorder("Controller Recoder"));
        comListPanel = new JScrollPane(comList);
        //StepRecorder
        add(comListPanel);
        comListPanel.setBounds(gmSize + BRODER, BRODER * 4 + CTRLHEIGHT * 4 + CTRLHEIGHT/2, CTRLWIDTH, gmSize - (BRODER * 4 + CTRLHEIGHT * 4 + CTRLHEIGHT/2));

        // Add Game View
        game = new GameView(this);
        add(game);
        game.setBounds(0, 0, gmSize, gmSize);

        setVisible(true);
        game.setFocusable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) { Main main = new Main(); }

    @Override
    public void callback(Map<String, String> response) {
        if (response.get("type").equals("game")) {
            gameInfo.setText("<html>" +
                    "<p style=\"font-family:times;font-size:13px;color:" + response.get("state_color") + ";text-align:center\">游戏状态: " + response.get("state") + "</p>" +
                    "<p style=\"font-family:times;font-size:11px;text-align:center\">游戏时间: " + response.get("time") + "</p>" +
                    "<p style=\"font-family:times;font-size:11px;text-align:center\">行走步数: " + response.get("step") + "</p>" +
                    "</html>");
        }
        if (response.get("type").equals("ctrl")) comListModel.add(0, response.get("info"));
        if (comListModel.size()>100) comListModel.remove(100);
    }
}
