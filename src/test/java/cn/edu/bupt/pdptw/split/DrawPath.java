package cn.edu.bupt.pdptw.split;

import cn.edu.bupt.pdptw.algorithm.split.algo.BufferClustering;
import cn.edu.bupt.pdptw.algorithm.split.model.AlgoPara;
import cn.edu.bupt.pdptw.algorithm.split.model.ClusterResult;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DrawPath extends JPanel {
    private int width = 100;
    private int height = 100;

    private int r = 2;
    private MyFrame frame;


    public DrawPath(int width, int height, MyFrame frame) {
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.frame = frame;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.white);
        g.setFont(new Font("宋体", Font.BOLD, 16));
        frame.setTitle("聚类结果");
        d1(g);
        //d2(g);
    }

    private void d2(Graphics g) {
        DefaultConfigReader loader = new DefaultConfigReader();
        Configuration configuration = Configuration.defaultCfg("pdptw100/lc101.txt", "1/pdptw100/lc101.json");
        try {
            List<Request> requests = loader.loadRequests(configuration);
            List<Vehicle> vehicles = loader.loadVehicles(configuration);

            BufferClustering clustering = new BufferClustering();
            List<ClusterResult> results = clustering.clustering(vehicles, requests, new AlgoPara());

            for (ClusterResult result : results) {
                List<Vehicle> vehicleList = result.getVehicleList();
                List<Request> requestList = result.getRequestList();
                LinkedList<Point> points = new LinkedList<>();
                for (Vehicle vehicle : vehicleList) {
                    points.add(new Point(vehicle.getLocation().getX(), vehicle.getLocation().getY()));
                }

                for (Request vehicle : requestList) {
                    points.add(new Point(vehicle.getLocation().getX(), vehicle.getLocation().getY()));
                }

                drawBound(g, points);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void drawBound(Graphics g, LinkedList<Point> points) {
        Graphics2D g2d = (Graphics2D) g.create();
        points.add(points.get(0));
        Random random = new Random();
        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置画笔颜色
        g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), 64));
        float[] dash = new float[]{5, 10};
        BasicStroke bs2 = new BasicStroke(
                1,                      // 画笔宽度/线宽
                BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER,
                10.0f,
                dash,                   // 虚线模式数组
                0.0f
        );
        g2d.setStroke(bs2);

        int nPoints = points.size();
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];

        for (int i = 0; i < nPoints; i++) {
            Point point = points.get(i);
            xPoints[i] = (int) point.getX() * 5 + 50;
            yPoints[i] = (int) point.getY() * 5 + 50;
        }

        //g2d.drawPolyline(xPoints, yPoints, nPoints);
        g2d.fillPolygon(xPoints, yPoints, nPoints);
    }

    private void d1(Graphics g) {
        DefaultConfigReader loader = new DefaultConfigReader();
        Configuration configuration = Configuration.defaultCfg("pdptw100/lr101.txt", "3/pdptw100/lr101.json");
        try {
            java.util.List<Request> requests = loader.loadRequests(configuration);
            List<Vehicle> vehicles = loader.loadVehicles(configuration);
            for (Vehicle vehicle : vehicles) {
                Location location = vehicle.getLocation();
                draLx(g, location);
            }

            for (Request request : requests) {
                if (request.getType().equals(RequestType.PICKUP)) {
                    drawTriangle(g, request.getLocation(), true);
                } else {
                    drawTriangle(g, request.getLocation());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void draLx(Graphics g, Location point) {
        int rr = 4;
        double x = point.getX() * 5 + 50;
        double y = point.getY() * 5 + 50;

        int[] xx = new int[5];
        int[] yy = new int[5];

        xx[0] = (int) (x - rr / Math.sqrt(2));
        xx[2] = (int) (x + rr / Math.sqrt(2));
        xx[1] = (int) (x);
        xx[3] = (int) (x);
        xx[4] = (int) (x - rr / Math.sqrt(2));

        yy[0] = (int) y;
        yy[2] = (int) y;
        yy[1] = (int) (y - rr / Math.sqrt(2));
        yy[3] = (int) (y + rr / Math.sqrt(2));
        yy[4] = (int) (y);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        g2d.drawPolygon(xx, yy, 5);
        g2d.fillPolygon(xx, yy, 5);
    }

    private void drawTriangle(Graphics g, Location point, boolean... type) {
        int rr = 4;
        double x = point.getX() * 5 + 50;
        double y = point.getY() * 5 + 50;

        int[] xx = new int[4];
        int[] yy = new int[4];

        xx[0] = (int) (x - rr / 2);
        xx[1] = (int) (x + rr / 2);
        xx[2] = (int) (x);
        xx[3] = (int) (x - rr / 2);

        yy[0] = (int) (y - Math.sqrt(3) * rr / 6);
        yy[1] = (int) (y - Math.sqrt(3) * rr / 6);
        yy[2] = (int) (y - Math.sqrt(3) * rr * 5 / 6);
        yy[3] = (int) (y - Math.sqrt(3) * rr / 6);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (type.length > 0 && type[0]) {
            g2d.setColor(Color.red);
        } else {
            g2d.setColor(Color.GREEN);
        }

        g2d.drawPolygon(xx, yy, 4);
        g2d.fillPolygon(xx, yy, 4);
    }


}
