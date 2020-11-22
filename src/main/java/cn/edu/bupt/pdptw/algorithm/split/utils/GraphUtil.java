package cn.edu.bupt.pdptw.algorithm.split.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理图结构工具类
 */

public class GraphUtil<T> {
    static final int mw = Integer.MAX_VALUE;

    public static void main(String[] args) {
        int[][] graph = new int[][]{
                {0, 1, 1, mw, mw},
                {1, 0, 1, mw, mw},
                {1, 1, 0, mw, mw},
                {mw, mw, mw, 0, 1},
                {mw, mw, mw, 1, 0},

        };
        Integer[] vertexes = new Integer[]{1, 2, 3, 4, 5};
        GraphUtil<String> theGraph = new GraphUtil<>(vertexes, graph);

        List<List<Integer>> ergodic = theGraph.ergodic();
        System.out.println(ergodic.toString());
    }

    private static final int MAX_VERTS = 100;
    private Vertex[] vertexList;// 顶点数组
    private int adjMat[][];     // 邻接矩阵
    private int nVerts;         // 当前顶点总数
    private StackX theStack;
    private QueueX theQueueX;

    public GraphUtil() {// 构造图
        vertexList = new Vertex[MAX_VERTS];

        adjMat = new int[MAX_VERTS][MAX_VERTS];
        nVerts = 0;
        for (int i = 0; i < MAX_VERTS; i++) {
            for (int j = 0; j < MAX_VERTS; j++) {
                adjMat[i][j] = 0;
            }
        }
        theStack = new StackX();
        theQueueX = new QueueX();
    }

    public GraphUtil(Integer[] vertexList, int[][] adjMat) {
        if (vertexList.length != adjMat.length) {
            throw new RuntimeException("illegal argument");
        }

        this.adjMat = adjMat;
        this.nVerts = vertexList.length;
        theStack = new StackX();
        theQueueX = new QueueX();

        this.vertexList = new Vertex[nVerts];
        for (int i = 0; i < vertexList.length; i++) {
            this.vertexList[i] = new Vertex<>(vertexList[i]);
        }
    }

    public void addVertex(T lab) {// 添加顶点
        vertexList[nVerts++] = new Vertex<>(lab);
    }

    public void addEdge(int start, int end, int weight) {// 添加边
        adjMat[start][end] = weight;
    }

    public void displayVertex(int v) {// 打印数组中v位置下的顶点名
        System.out.print(vertexList[v].lable + "\t");
    }

    public int getAdjUnvisitedVertex(int v) {// 获取和v邻接的未访问的顶点
        for (int i = 0; i < nVerts; i++) {
            if (adjMat[v][i] > 0 && adjMat[v][i] < Integer.MAX_VALUE && !vertexList[i].wasVisited) {
                return i;
            }
        }
        return -1;
    }

    public List<Vertex> dfs(int start) {// 深度优先搜索
        List<Vertex> result = new ArrayList<>();
        vertexList[start].wasVisited = true;
        //displayVertex(start);
        result.add(vertexList[start]);
        theStack.push(start);

        while (!theStack.isEmpty()) {
            int v = getAdjUnvisitedVertex(theStack.peek());
            if (v == -1) {
                if (theStack.pop() < 0) {
                    break;
                }

            } else {
                vertexList[v].wasVisited = true;
                result.add(vertexList[v]);
                //displayVertex(v);
                theStack.push(v);
            }
        }
        return result;
    }

    public List<List<Integer>> ergodic() {
        for (int i = 0; i < nVerts; i++) {
            vertexList[i].wasVisited = false;
        }

        int i = 0;
        List<List<Integer>> result = new ArrayList<>();
        for (Vertex vertex : vertexList) {
            if (!vertex.wasVisited) {
                List<Vertex> dfs = dfs(i);
                List<Integer> list = new ArrayList<>();
                for (Vertex v : dfs) {
                    list.add((int) v.lable);
                }
                result.add(list);
            }
            i++;
        }
        return result;
    }

    public void bfs(int start) {// 广度优先搜索
        vertexList[start].wasVisited = true;
        displayVertex(start);
        theQueueX.insert(start);
        int v2;

        while (!theQueueX.isEmpty()) {
            int v1 = theQueueX.remove();

            while ((v2 = getAdjUnvisitedVertex(v1)) != -1) {
                vertexList[v2].wasVisited = true;
                displayVertex(v2);
                theQueueX.insert(v2);
            }
        }

        for (int j = 0; j < nVerts; j++) {
            vertexList[j].wasVisited = false;
        }
    }

    public void dijkstra() { //最短路径
        int[] dis = new int[nVerts];    //距离表
        int[] prevs = new int[nVerts];  //前置节点

        vertexList[0].wasVisited = true;
        System.arraycopy(adjMat[0], 0, dis, 0, nVerts);

        while (true) {
            int min = Integer.MAX_VALUE;
            int index = -1;
            //找出最近顶点
            for (int i = 0; i < nVerts; i++) {

                if (!vertexList[i].wasVisited) {
                    if (dis[i] < min) {
                        index = i;
                        min = dis[i];
                    }
                }
            }

            //结束条件
            if (index == -1) break;

            //更新距离表
            vertexList[index].wasVisited = true;
            for (int i = 0; i < nVerts; i++) {
                if (adjMat[index][i] != Integer.MAX_VALUE) {
                    if (dis[i] > (min + adjMat[index][i])) {
                        dis[i] = min + adjMat[index][i];
                        prevs[i] = index;
                    }
                }
            }
        }
        //输出结果
        System.out.println(dis[nVerts - 1]);
        printPrevs(prevs, nVerts - 1);
    }

    private void printPrevs(int[] prev, int i) {
        if (i > 0) {
            printPrevs(prev, prev[i]);
        }
        System.out.print(vertexList[i].lable.toString() + "\t");

    }

    public int[][] floyd() {
        int[][] C = new int[nVerts][nVerts];
        for (int i = 0; i < nVerts; i++) {
            System.arraycopy(adjMat[i], 0, C[i], 0, nVerts);
        }

        for (int k = 0; k < nVerts; k++) {
            for (int i = 0; i < nVerts; i++) {
                for (int j = 0; j < nVerts; j++) {
                    if (C[i][k] != Integer.MAX_VALUE && C[k][j] != Integer.MAX_VALUE && C[i][k] + C[k][j] < C[i][j]) {
                        C[i][j] = C[i][k] + C[k][j];
                    }

                }
            }
        }

        for (int i = 0; i < nVerts; i++) {
            for (int j = 0; j < nVerts; j++) {
                if (i == j) continue;
                if (C[i][j] == mw)
                    System.out.println(i + "到" + j + "之间没路径");
                else
                    System.out.println(i + "到" + j + "之间的最短路径长度为：" + C[i][j]);
            }
        }

        return C;
    }
}

class StackX {// 自定义栈
    private static final int SIZE = 1000;
    private int[] st;
    private int top;

    public StackX() {
        st = new int[SIZE];
        top = -1;
    }


    public void push(int j) {
        st[++top] = j;
    }

    public int pop() {
        if (top == 0) {
            return -1;
        }
        return st[--top];
    }

    public int peek() {
        return st[top];
    }

    public boolean isEmpty() {
        return (top == -1);
    }
}

class QueueX {
    private final int SIZE = 20;
    private int[] queArray;
    private int front;
    private int rear;

    public QueueX() {
        queArray = new int[SIZE];
        front = 0;
        rear = -1;
    }

    public void insert(int j) {// 入队
        if (rear == SIZE - 1) {
            rear = -1;
        }
        queArray[++rear] = j;
    }

    public int remove() {// 出队
        if (!isEmpty()) {
            return queArray[front++];
        } else {
            return -1;
        }
    }


    public boolean isEmpty() {
        return (rear + 1 == front);
    }
}

/**
 * 图的节点
 */
class Vertex<T> {
    /**
     * 节点名称
     */
    public T lable;
    /**
     * 是否已经被遍历
     */
    public boolean wasVisited;

    public Vertex(T lab) {
        lable = lab;
        wasVisited = false;
    }

    @Override
    public String toString() {
        return "" + lable;
    }
}
