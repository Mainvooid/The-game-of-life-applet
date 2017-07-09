package GoL;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;

/**
 * @名称 The Game Of Life Applet.（生命游戏小程序）
 * @用法 点击鼠标设置cell，ctrl+点击将cell置空。键盘space切换线程运行/停止。
 * @author iPanda
 * @date 2017-07-05
 * @备注 Applet类没有定义 main()，所以一个 Applet程序不会调用 main() 方法。
 */
@SuppressWarnings("serial")
public class GoL extends Applet implements Runnable,MouseListener,MouseMotionListener,KeyListener{
    private final int SIZE = 120;//二维游戏世界的大小,共SIZE*SIZE个格子
    private final int CELL_SIZE =8;//每个格子的边长，Java坐标系单位。
    private Color cell =new Color(32,98,40);
    private Color space =new Color(226,245,226);    
    //保存当代各个格子中的细胞生命状况
    private boolean[][] life = new boolean[SIZE][SIZE];
    //保存当代按照规则组所推导出的各个格子的邻居数目
    private int[][] neighbors = new int[SIZE][SIZE];
    private Thread animator;//声明绘图线程
    private int delay;//延迟量
    private boolean running;//flag，标识线程的运行状况，正在运行则running为true，被用户中断，则为false。
    /**
     * 线程方法体
     */
    @Override public void run() {
        long times = System.currentTimeMillis();
        while (Thread.currentThread() == animator) {
            if (running == true) {
                getNeighbors();//获取邻居数目
                nextWorld();//计算出下一代的life数组
                repaint();//重绘
            } 
            try {
                times += delay;
                Thread.sleep(Math.max(0,times - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                break;
            }
        } 
    }
    
    /**
     * 为 Applet提供所需的任何初始化
     * 初始化设置，设置背景颜色和事件监听器
     */
    @Override public void init() {
    	this.setSize(SIZE*CELL_SIZE,SIZE*CELL_SIZE);//设置窗口大小
        animator = new Thread(this);//创建线程
        delay = 100;//设置延迟量
        running = false;
        //setBackground(Color.yellow);
        setBackground(new Color(199,237,204));
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }
    /**
     * 调用 init()方法后，该方法被自动调用
     */
    @Override public void start() {        
        animator.start();       
    }

    @Override public void stop() {
        animator = null;    
    }
    /**
     * repaint()调用此方法重绘
     */
    @Override public void paint(Graphics g) {
        update(g);
    }
    //绘制颜色和图形
    @Override public void update (Graphics g) {
        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++) {
               g.setColor(life[x][y]?cell:space);//为每个像素设置颜色，true为cell的色，false为space的色
               g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);//绘制矩形，左上角位置和宽高
            }
    }

    /**
     * 从life数组中推导出neighbors数组。
     */
    public void getNeighbors() { 
        for (int r = 0; r < SIZE; r++){//row横
            for (int c = 0; c < SIZE; c++){//col竖 
            	if(r-1 >= 0 && c-1 >= 0   && life[r-1][c-1] ){
                	neighbors[r][c]++;//判断上左的，例：（0,0）为true，则（1,1）邻居数目+1;r,c为0时不做运算
                }
                if(r-1 >= 0 && life[r-1][c]){
                	neighbors[r][c]++;//判断上中的，例：（0,1）
                }
                if(r-1 >= 0 && c+1 < SIZE && life[r-1][c+1]){ 
                	neighbors[r][c]++;//判断上右的，例：（0,2） 
                } 
                if(c-1 >= 0 && life[r][c-1]){
                	neighbors[r][c]++;//判断左中的，例：（1,0）
                }
                if(c+1 < SIZE && life[r][c+1]){
                	neighbors[r][c]++;//判断右中的，例：（1,2）
                }
                if(r+1 < SIZE && life[r+1][c]){
                	neighbors[r][c]++;//判断下中的，例：（2,1）
                }
                if(r+1 < SIZE && c+1 < SIZE && life[r+1][c+1]){
                	neighbors[r][c]++;//判断下右的，例：（2,2）
                }
                if(r+1 < SIZE && c-1 >=0 && life[r+1][c-1]){
                	neighbors[r][c]++;//判断下左的，例：（2,0）
                }
            }
        }            
    }
    
    /**
     * nextWorld()，世代交替。
     * 生命游戏的核心是计算出下一代的life，产生新一代的二维世界。
     * 通过判断周围每一个neighbors元素
     */
	public void nextWorld() {
		for (int r = 0; r < SIZE; r++) {// row
			for (int c = 0; c < SIZE; c++) {// col
				// 以下判断基础为中心位已经为true，即：life[r][c] = true
				if (neighbors[r][c] < 2) {
					life[r][c] = false; // 任何活细胞如果活邻居<2，则死掉(人口过少)
				}
				/*
				 * 因为已经为true故不再判断，改变 if (neighbors[r][c] == 2){ life[r][c] =
				 * true;//任何活细胞如果活邻居=2，则继续活(活格子正常)。 }
				 */
				if (neighbors[r][c] == 3) {
					life[r][c] = true;// 任何细胞如果活邻居=3，则活(活格子正常+死格子繁殖)
				}
				if (neighbors[r][c] > 3) {
					life[r][c] = false;// 任何活细胞如果活邻居>3，则死掉,人口过多
				}
				neighbors[r][c] = 0;// 初始化为0
			}
		}
	}
    /**
     * event handler 事件处理器 
     */
    public void mouseClicked(MouseEvent e){}   
    public void mousePressed(MouseEvent e){//负责鼠标按下事件
        int cellX = e.getX()/CELL_SIZE;//获取格子的行数，(鼠标点的X/格子宽度：向下取整 0 1 2...
        int cellY = e.getY()/CELL_SIZE;//获取格子的列数
        life[cellX][cellY] = !e.isControlDown();//没有down，则划过的格子life都设置为true
        repaint();//重绘
    }
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseDragged(MouseEvent e){//负责鼠标拖动事件
        this.mousePressed(e); 
    }
    public void mouseMoved(MouseEvent e){}     
    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){
        if(e.getKeyChar()==' '){
            running = !running;//空格键切换暂停或运行
            repaint();
        }
    }
    public void keyReleased(KeyEvent e){}
}