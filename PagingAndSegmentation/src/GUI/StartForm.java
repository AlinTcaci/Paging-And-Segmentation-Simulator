package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StartForm extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StartForm().setVisible(true));
    }

    public StartForm() {
        setSize(500, 200);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MenuPane menuPane = new MenuPane();
        add(menuPane);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    public class MenuPane extends JPanel {

        public MenuPane() {
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.anchor = GridBagConstraints.NORTH;

            add(new JLabel("<html><h1><strong><i>Paging & Segmentation Simulator</i></strong></h1><hr></html>"), gbc);

            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttons = new JPanel(new GridBagLayout());
            JButton pagingButton = new JButton("Paging Memory");
            JButton segmentationButton = new JButton("Segmentation Memory");

            // Add action listener to pagingButton
            pagingButton.addActionListener(e -> {
                PagingMemoryGUI pagingMemoryGUI = new PagingMemoryGUI();
                pagingMemoryGUI.setVisible(true);
                StartForm.this.dispose();
            });

            // Add action listener to segmentationButton
            segmentationButton.addActionListener(e -> {
                SegmentationMemoryGUI segmentationMemoryGUI = new SegmentationMemoryGUI();
                segmentationMemoryGUI.setVisible(true);
                StartForm.this.dispose();
            });

            buttons.add(pagingButton, gbc);
            buttons.add(segmentationButton, gbc);

            pagingButton.setFocusable(false);
            segmentationButton.setFocusable(false);


            gbc.weighty = 1;
            add(buttons, gbc);
        }
    }

}
