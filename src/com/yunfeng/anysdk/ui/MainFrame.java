package com.yunfeng.anysdk.ui;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setBounds(0, 0, 860, 480);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static final void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

}
