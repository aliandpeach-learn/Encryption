package com.yk.test.frame;

import javax.swing.*;
import java.awt.*;

public class TestFrame extends JFrame {
    public static void main(String[] args) {
        TestFrame mai = new TestFrame();
        mai.setLayout(new BorderLayout());
        mai.setPreferredSize(new Dimension(400, 200));
        JToolBar jtoolbar = new JToolBar();
        JLabel jl = new JLabel("state");
        jtoolbar.add(jl);
        JPanel jpanel1 = new JPanel();
        JButton jb1 = new JButton("North");

        jpanel1.setPreferredSize(new Dimension(100, 60));//关键代码,设置JPanel的大小
        jpanel1.setBackground(Color.BLUE);
        jpanel1.add(jb1);
        jpanel1.setBorder(BorderFactory.createEtchedBorder());
        JButton jb2 = new JButton("Center");


        mai.add(jpanel1, BorderLayout.EAST);
        mai.add(jb2, BorderLayout.CENTER);
        mai.add(jtoolbar, BorderLayout.SOUTH);
        mai.setSize(300, 400);
        mai.setVisible(true);
        mai.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jpanel1.setPreferredSize(new Dimension(100, 60));
    }
}
