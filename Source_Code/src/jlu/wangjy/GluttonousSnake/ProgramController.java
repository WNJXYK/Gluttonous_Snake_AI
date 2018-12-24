package jlu.wangjy.GluttonousSnake;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static java.lang.Runtime.getRuntime;


public class ProgramController extends Controller {
    private static final String[] DIRECTION = {"W", "S", "A", "D"};
    private String programPath;

    public ProgramController(String programPath) {
        this.programPath = programPath;
    }

    @Override
    public long keyboard(int d) {
        return -2;
    }

    @Override
    public long program(int n, int k, LinkedList<GameLogic.Node> snake, GameLogic.Node food, Callback cb) {
        try {
            // Input.txt
            PrintStream file = new PrintStream(new FileOutputStream("input.txt"));
            file.println(n + " " + k);
            for (GameLogic.Node p : snake) file.println(p.x + " " + p.y);
            file.println(food.x + " " + food.y);
            file.flush();
            file.close();

            long startTime = System.currentTimeMillis();
            Process process = getRuntime().exec(programPath);
            // Input
            PrintStream output = new PrintStream(process.getOutputStream());
            output.println(n + " " + k);
            for (GameLogic.Node p : snake) output.println(p.x + " " + p.y);
            output.println(food.x + " " + food.y);
            output.flush();
            // Log
            BufferedReader cerr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String logInfo;
            while((logInfo=cerr.readLine())!=null){
                Map<String, String> res = new HashMap<String, String>();
                res.put("type", "ctrl");
                res.put("info", "Program Debug " + logInfo);
                cb.callback(res);
            }
            // Output
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int d = Integer.parseInt(input.readLine());
            // Wait
            process.waitFor();
            int r = process.exitValue();
            if (r != 0) return -1;
            // Set Direction
            Map<String, String> res = new HashMap<String, String>();
            long endTime = System.currentTimeMillis();
            res.put("type", "ctrl");
            if (setDirection(d)) {
                res.put("info", "Program Control " + DIRECTION[d] + "(" + (endTime - startTime) + "ms)");
            }else{
                res.put("info", "Program Output Not in Range: " + d + "(" + (endTime - startTime) + "ms)");
            }
            cb.callback(res);
            // Return
            return endTime - startTime;
        } catch (Exception err) {
            System.out.println("Program Error: " + err.getMessage());
            Map<String, String> res = new HashMap<String, String>();
            res.put("type", "ctrl");
            res.put("info", "Program Error: " + err.getMessage());
            cb.callback(res);
            return -1;
        }
    }
}
