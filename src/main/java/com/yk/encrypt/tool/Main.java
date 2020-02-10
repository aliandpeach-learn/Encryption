package com.yk.encrypt.tool;

import com.yk.encrypt.tool.component.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {

    static {
        System.setProperty("encypttool.logs.home",
                System.getProperty("user.dir") + File.separator + "logs");
    }

    private static Logger logger = LoggerFactory.getLogger("main");


    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        logger.info("logger...");
    }
}
