package GoL;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;

/**
 * @���� The Game Of Life Applet.��������ϷС����
 * @�÷� ����������cell��ctrl+�����cell�ÿա�����space�л��߳�����/ֹͣ��
 * @author iPanda
 * @date 2017-07-05
 * @��ע Applet��û�ж��� main()������һ�� Applet���򲻻���� main() ������
 */
@SuppressWarnings("serial")
public class GoL extends Applet implements Runnable,MouseListener,MouseMotionListener,KeyListener{
    private final int SIZE = 120;//��ά��Ϸ����Ĵ�С,��SIZE*SIZE������
    private final int CELL_SIZE =8;//ÿ�����ӵı߳���Java����ϵ��λ��
    private Color cell =new Color(32,98,40);
    private Color space =new Color(226,245,226);    
    //���浱�����������е�ϸ������״��
    private boolean[][] life = new boolean[SIZE][SIZE];
    //���浱�����չ��������Ƶ����ĸ������ӵ��ھ���Ŀ
    private int[][] neighbors = new int[SIZE][SIZE];
    private Thread animator;//������ͼ�߳�
    private int delay;//�ӳ���
    private boolean running;//flag����ʶ�̵߳�����״��������������runningΪtrue�����û��жϣ���Ϊfalse��
    /**
     * �̷߳�����
     */
    @Override public void run() {
        long times = System.currentTimeMillis();
        while (Thread.currentThread() == animator) {
            if (running == true) {
                getNeighbors();//��ȡ�ھ���Ŀ
                nextWorld();//�������һ����life����
                repaint();//�ػ�
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
     * Ϊ Applet�ṩ������κγ�ʼ��
     * ��ʼ�����ã����ñ�����ɫ���¼�������
     */
    @Override public void init() {
    	this.setSize(SIZE*CELL_SIZE,SIZE*CELL_SIZE);//���ô��ڴ�С
        animator = new Thread(this);//�����߳�
        delay = 100;//�����ӳ���
        running = false;
        //setBackground(Color.yellow);
        setBackground(new Color(199,237,204));
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }
    /**
     * ���� init()�����󣬸÷������Զ�����
     */
    @Override public void start() {        
        animator.start();       
    }

    @Override public void stop() {
        animator = null;    
    }
    /**
     * repaint()���ô˷����ػ�
     */
    @Override public void paint(Graphics g) {
        update(g);
    }
    //������ɫ��ͼ��
    @Override public void update (Graphics g) {
        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++) {
               g.setColor(life[x][y]?cell:space);//Ϊÿ������������ɫ��trueΪcell��ɫ��falseΪspace��ɫ
               g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);//���ƾ��Σ����Ͻ�λ�úͿ��
            }
    }

    /**
     * ��life�������Ƶ���neighbors���顣
     */
    public void getNeighbors() { 
        for (int r = 0; r < SIZE; r++){//row��
            for (int c = 0; c < SIZE; c++){//col�� 
            	if(r-1 >= 0 && c-1 >= 0   && life[r-1][c-1] ){
                	neighbors[r][c]++;//�ж�����ģ�������0,0��Ϊtrue����1,1���ھ���Ŀ+1;r,cΪ0ʱ��������
                }
                if(r-1 >= 0 && life[r-1][c]){
                	neighbors[r][c]++;//�ж����еģ�������0,1��
                }
                if(r-1 >= 0 && c+1 < SIZE && life[r-1][c+1]){ 
                	neighbors[r][c]++;//�ж����ҵģ�������0,2�� 
                } 
                if(c-1 >= 0 && life[r][c-1]){
                	neighbors[r][c]++;//�ж����еģ�������1,0��
                }
                if(c+1 < SIZE && life[r][c+1]){
                	neighbors[r][c]++;//�ж����еģ�������1,2��
                }
                if(r+1 < SIZE && life[r+1][c]){
                	neighbors[r][c]++;//�ж����еģ�������2,1��
                }
                if(r+1 < SIZE && c+1 < SIZE && life[r+1][c+1]){
                	neighbors[r][c]++;//�ж����ҵģ�������2,2��
                }
                if(r+1 < SIZE && c-1 >=0 && life[r+1][c-1]){
                	neighbors[r][c]++;//�ж�����ģ�������2,0��
                }
            }
        }            
    }
    
    /**
     * nextWorld()���������档
     * ������Ϸ�ĺ����Ǽ������һ����life��������һ���Ķ�ά���硣
     * ͨ���ж���Χÿһ��neighborsԪ��
     */
	public void nextWorld() {
		for (int r = 0; r < SIZE; r++) {// row
			for (int c = 0; c < SIZE; c++) {// col
				// �����жϻ���Ϊ����λ�Ѿ�Ϊtrue������life[r][c] = true
				if (neighbors[r][c] < 2) {
					life[r][c] = false; // �κλ�ϸ��������ھ�<2��������(�˿ڹ���)
				}
				/*
				 * ��Ϊ�Ѿ�Ϊtrue�ʲ����жϣ��ı� if (neighbors[r][c] == 2){ life[r][c] =
				 * true;//�κλ�ϸ��������ھ�=2���������(���������)�� }
				 */
				if (neighbors[r][c] == 3) {
					life[r][c] = true;// �κ�ϸ��������ھ�=3�����(���������+�����ӷ�ֳ)
				}
				if (neighbors[r][c] > 3) {
					life[r][c] = false;// �κλ�ϸ��������ھ�>3��������,�˿ڹ���
				}
				neighbors[r][c] = 0;// ��ʼ��Ϊ0
			}
		}
	}
    /**
     * event handler �¼������� 
     */
    public void mouseClicked(MouseEvent e){}   
    public void mousePressed(MouseEvent e){//������갴���¼�
        int cellX = e.getX()/CELL_SIZE;//��ȡ���ӵ�������(�����X/���ӿ�ȣ�����ȡ�� 0 1 2...
        int cellY = e.getY()/CELL_SIZE;//��ȡ���ӵ�����
        life[cellX][cellY] = !e.isControlDown();//û��down���򻮹��ĸ���life������Ϊtrue
        repaint();//�ػ�
    }
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseDragged(MouseEvent e){//��������϶��¼�
        this.mousePressed(e); 
    }
    public void mouseMoved(MouseEvent e){}     
    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){
        if(e.getKeyChar()==' '){
            running = !running;//�ո���л���ͣ������
            repaint();
        }
    }
    public void keyReleased(KeyEvent e){}
}