package cn.edu.bupt.pdptw.split;


import javax.swing.*;

public class MyFrame extends JFrame {

    public static final String TITLE = "Java图形绘制";

    public int WIDTH = 600;
    public int HEIGHT = 600;

    public MyFrame(int width, int height) {
        super();
        initFrame();
        this.HEIGHT = height;
        this.WIDTH = width;
    }

    private void initFrame() {
        // 设置 窗口标题 和 窗口大小
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);

        // 设置窗口关闭按钮的默认操作(点击关闭时退出进程)
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // 把窗口位置设置到屏幕的中心
        setLocationRelativeTo(null);

        // 设置窗口的内容面板
        DrawPath panel = new DrawPath(WIDTH, HEIGHT, this);
        setContentPane(panel);
    }

    public static void main(String[] args) {
        // 创建窗口对象
        final MyFrame frame = new MyFrame(1600, 900);
        // 显示窗口
        frame.setVisible(true);

    }

}
