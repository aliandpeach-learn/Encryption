package com.yk.encrypt.tool.component;

import com.yk.encrypt.tool.rsa.RSA2048Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RSAGeneratePanel extends JPanel {

    private Map<Integer, int[]> mouse;
    private Object lock;

    private AtomicInteger key = new AtomicInteger(0);

    private boolean flag = false;

    public RSAGeneratePanel(Map<Integer, int[]> mouse, Object lock, MainFrame mainFrame) {
        super();
        this.mouse = mouse;
        this.setBackground(Color.gray);
        this.setPreferredSize(new Dimension(mainFrame.getWidth(), mainFrame.getHeight() - 50));
        this.setEnabled(false);
        this.setVisible(false);
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!flag) {
                    return;
                }
                if (key.get() > 10) {
                    KeyPair keyPair = RSA2048Util.generateRSA2048(mouse);
                    flag = false;
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("private.key", "key");
                    fileChooser.setFileFilter(filter);
                    int option = fileChooser.showSaveDialog(null);
                    if (option == JFileChooser.APPROVE_OPTION) {    //假如用户选择了保存
                        File file = fileChooser.getSelectedFile();
                        try {
                            FileWriter fw = new FileWriter(file);
                            fw.write(new BigInteger(1, keyPair.getPrivate().getEncoded()).toString(16));
                            fw.close();
                        } catch (IOException exx) {
                            exx.printStackTrace();
                        }
                    }
                    return;
                }
                mouse.put(key.incrementAndGet(), new int[]{e.getX(), e.getY()});
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                mainFrame.getGenerateProgressPanel().getjProgressBar().setValue(key.get() * 10);
            }
        });
    }

    public class GenerateProgressPanel extends JPanel {
        private JProgressBar jProgressBar;

        public GenerateProgressPanel(MainFrame mainFrame) {
            super(new BorderLayout());
            this.setBackground(Color.WHITE);
            this.setBorder(new EmptyBorder(1, 1, 1, 1));
            this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            jProgressBar = new JProgressBar();
//            jProgressBar.setBackground(Color.RED);
            jProgressBar.setPreferredSize(new Dimension(mainFrame.getWidth() - 10, 25));
            jProgressBar.setStringPainted(true);
            this.add(jProgressBar, BorderLayout.CENTER);
            jProgressBar.setValue(0);
        }

        public JProgressBar getjProgressBar() {
            return jProgressBar;
        }

        public void setjProgressBar(JProgressBar jProgressBar) {
            this.jProgressBar = jProgressBar;
        }
    }

    public class ControlPanel extends JPanel {
        private JButton button;

        public ControlPanel(MainFrame mainFrame) {
            super();
            button = new JButton("生成密钥");
            this.add(button);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mainFrame.getGenerateProgressPanel().getjProgressBar().setValue(0);
                    flag = true;
                    key = new AtomicInteger(0);
                }
            });
        }
    }
}
