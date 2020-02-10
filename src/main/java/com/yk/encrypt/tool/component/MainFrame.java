package com.yk.encrypt.tool.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ldap.Control;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private static Logger logger = LoggerFactory.getLogger("component");
    private Map<Integer, int[]> mouse = new HashMap<>();

    private final Object lock = new Object();

    private static final int WITH = 600;
    private static final int HEIGHT = 400;

    private JTabbedPane jTabbedPane = new JTabbedPane();

    private RSAGeneratePanel rsaGeneratePanel;
    private ContentPanel contentPanel = new ContentPanel();
    private RSAGeneratePanel.GenerateProgressPanel generateProgressPanel;
    private RSAGeneratePanel.ControlPanel controlPanel;

    public MainFrame() {
        super();
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setSize(600, 320);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        this.setBounds((screenWidth - WITH) / 2, (screenHeight - HEIGHT) / 2, WITH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        this.add(rsaGeneratePanel);
        this.add(contentPanel);

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu file = new JMenu("文件");
        menuBar.add(file);
        JMenuItem file2privatekey = new JMenuItem("选择私钥");
        file.add(file2privatekey);
//        file.addSeparator();
        JMenu tool = new JMenu("工具");
        JMenuItem tool2generate = new JMenuItem("生成密钥");
        tool.add(tool2generate);
        menuBar.add(tool);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                logger.info("tool2generate addMouseListener mousePressed");
                contentPanel.setEnabled(false);
                contentPanel.setVisible(false);
                rsaGeneratePanel.setVisible(true);
                rsaGeneratePanel.setEnabled(true);
                MainFrame.this.remove(contentPanel);
                MainFrame.this.add(rsaGeneratePanel);
                MainFrame.this.add(generateProgressPanel);
                MainFrame.this.add(controlPanel);

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                logger.info("tool2generate addMouseListener mouseReleased");
            }
        };
        tool2generate.addMouseListener(mouseAdapter);

        MouseAdapter file2privatekeyMouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                logger.info("file2privatekeyMouseAdapter addMouseListener mousePressed");
                contentPanel.setEnabled(true);
                contentPanel.setVisible(true);
                rsaGeneratePanel.setVisible(false);
                rsaGeneratePanel.setEnabled(false);
                MainFrame.this.add(contentPanel);
                MainFrame.this.remove(rsaGeneratePanel);
                MainFrame.this.remove(generateProgressPanel);
                MainFrame.this.remove(controlPanel);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                logger.info("file2privatekeyMouseAdapter addMouseListener mouseReleased");
            }
        };
        file2privatekey.addMouseListener(file2privatekeyMouseAdapter);

        this.setVisible(true);
        //等同于 setDefaultCloseOperation
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        rsaGeneratePanel = new RSAGeneratePanel(mouse, lock, this);
        generateProgressPanel = rsaGeneratePanel.new GenerateProgressPanel(this);
        controlPanel = rsaGeneratePanel.new ControlPanel(this);
    }


    public RSAGeneratePanel getRsaGeneratePanel() {
        return rsaGeneratePanel;
    }

    public void setRsaGeneratePanel(RSAGeneratePanel rsaGeneratePanel) {
        this.rsaGeneratePanel = rsaGeneratePanel;
    }

    public ContentPanel getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(ContentPanel contentPanel) {
        this.contentPanel = contentPanel;
    }

    public RSAGeneratePanel.GenerateProgressPanel getGenerateProgressPanel() {
        return generateProgressPanel;
    }

    public void setGenerateProgressPanel(RSAGeneratePanel.GenerateProgressPanel generateProgressPanel) {
        this.generateProgressPanel = generateProgressPanel;
    }
}
