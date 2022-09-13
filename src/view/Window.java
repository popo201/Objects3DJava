package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;

    public Window() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Objects 3D : " + getClass().getName());

        panel = new Panel();

        add(panel, BorderLayout.CENTER);
        pack();

        setLocationRelativeTo(null);

        JLabel labelHelp = new JLabel();
        String text = "© Pavel Hampl Klávesa H - zobrazení nápovědy ovládání   Klávesa C - smazání obrazovky";
        labelHelp.setText(text);
        labelHelp.setForeground(Color.white);

        panel.add(labelHelp);

        // lepší až na konci, aby to neukradla nějaká komponenta v případně složitějším UI
        panel.setFocusable(true);
        panel.grabFocus(); // důležité pro pozdější ovládání z klávesnice
    }

    public Panel getPanel() {
        return panel;
    }

}
