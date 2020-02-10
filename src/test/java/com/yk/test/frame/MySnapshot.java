package com.yk.test.frame;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * 自己写的截屏程序
 */
public class MySnapshot {

    JFrame jFrame;

    //作为截屏动作的面板
    JPanel jPanel;

    Robot robot;

    BufferedImage saveImage;

    BufferedImage cover;

    BufferedImage imageScreen;

    private int startX;
    private int startY;

    private int endX;
    private int endY;

    boolean flag = false;

    public MySnapshot() {
        this.jFrame = new JFrame();
        //获取当前屏幕的size,frame的宽高设置为当前屏幕大小
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.jFrame.setSize(dimension);
        //去除边框
        this.jFrame.setUndecorated(true);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            return;
        }

        //获取的是整个屏幕的image
        imageScreen = robot.createScreenCapture(new Rectangle(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight()));
        this.jPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                //开始的时候，做遮罩层灰色透明
                RescaleOp ro = new RescaleOp(0.8f, 0, null);
                cover = ro.filter(imageScreen, null);
                //给当前jpanel绘制透明遮罩
                g.drawImage(cover, 0, 0, (int) dimension.getWidth(), (int) dimension.getHeight(), this);
            }
        };

        this.jPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();

                // 第一种逻辑

                //panel上最初是screen画上了遮罩层cover
                //创建一个Image画布，大小是screen，给花布上画上一步的cover，然后再画红色矩形
                //获取最初没有遮罩层的screen的红色矩形部分，再画到image画布上
                //最后把image画布画到panel上这个动作就完成了
/*

                Image temp = MySnapshot.this.jPanel.createImage(MySnapshot.this.jFrame.getWidth(), MySnapshot.this.jFrame.getHeight());
                Graphics g = temp.getGraphics();
                g.drawImage(cover, 0, 0, null);
                int x = Math.min(startX, endX);
                int y = Math.min(startY, endY);
                int width = Math.abs(endX - startX) + 1;
                int height = Math.abs(endY - startY) + 1;
                // 加上1防止width或height0
                g.setColor(Color.RED);
                g.drawRect(x - 1, y - 1, width + 1, height + 1);
                //减1加1都了防止图片矩形框覆盖掉
                saveImage = imageScreen.getSubimage(x, y, width, height);
                g.drawImage(saveImage, x, y, null);
                MySnapshot.this.jPanel.getGraphics().drawImage(temp, 0, 0, MySnapshot.this.jPanel);
*/

                //第二种逻辑
                //在panel上画出矩形
                Graphics g = MySnapshot.this.jPanel.getGraphics();
                g.setColor(Color.RED);

                int _startX = Math.min(startX, endX);
                int _startY = Math.min(startY, endY);
                int _endX = Math.max(startX, endX);
                int _endY = Math.max(startY, endY);
                int _width = _endX - _startX + 1;
                int _height = _endY - _startY + 1;

                System.out.println("_startX=" + _startX + "_startY=" + _startY + "_endX=" + _endX + "_endY=" + _endY);
                System.out.println("_width=" + _width + "_height=" + _height);
//                MySnapshot.this.jPanel.repaint();
                Image temp = new BufferedImage(_width, _height, TYPE_INT_RGB);
                //temp用于填充imageScreen中截取的用户自定义的部分图片
                temp.getGraphics().drawImage(imageScreen.getSubimage(_startX, _startY, _width, _height), 0, 0, null);

                //把周围的screen填充了遮罩的cover重新填充一遍到panel中
                g.drawImage(cover, 0, 0, null);
                //再画矩形到panel中
                g.drawRect(_startX - 1, _startY - 1, _width + 1, _height + 1);
                //最后再画用户截取的部分到panel中
                g.drawImage(temp, _startX, _startY, null);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
//                System.out.println("mouseMoved " + e.getX() + " " + e.getY());
            }
        });

        this.jPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MySnapshot.this.startX = e.getX();
                MySnapshot.this.startY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JFrame subFrame = new JFrame();
                subFrame.setUndecorated(true);
                subFrame.setSize(100, 30);
                JToolBar toolBar = new JToolBar("截图");
                subFrame.setLocation(endX, endY);
                System.out.println("endX=" + endX + " endY=" + endY);
                toolBar.add(new JButton("保存"));
                JButton quit = new JButton("退出");
                quit.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.exit(0);
                    }
                });
                toolBar.add(quit);
                subFrame.add(toolBar);
                subFrame.setVisible(true);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
//              flag = true;
                /*BufferedImage image = null;
                try {
                    image = new Robot().createScreenCapture(new Rectangle(startX, startY, endX - startX, endY - startY));
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
                try {
                    ImageIO.write(image, "jpeg", new FileOutputStream(new File("H:\\" + System.currentTimeMillis() + ".jpg")));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/

                try {
                    ImageIO.write(saveImage, "jpeg", new FileOutputStream(new File("H:\\" + System.currentTimeMillis() + ".jpg")));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        this.jFrame.add(jPanel);
        this.jFrame.setVisible(true);
        this.jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new MySnapshot();
    }
}
