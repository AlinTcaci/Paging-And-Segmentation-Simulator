package GUI;

import model.Segments;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SegmentationMemoryGUI extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SegmentationMemoryGUI().setVisible(true));
    }

    public SegmentationMemoryGUI() {
        setTitle("Segmentation Memory");
        setSize(1100, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainPanel mainPanel = new MainPanel();
        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public class MainPanel extends JPanel {

        private JList<String> rowHeader;
        private DefaultTableModel tableModel;
        private final String[] columnNames = {""};
        private JButton createButton; // Declare the create button as a class member
        private final List<Segments> segments;
        private int nextSID = 1;
        private JTable activeSegmentsTable;

        public MainPanel() {
            segments = new ArrayList<>();
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setLayout(new GridBagLayout()); // Use GridBagLayout

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;

            // Create the three main panels
            JPanel tablePanel = createTablePanel();
            JPanel settingsAndAddProcessPanel = createSettingsAndAddSegmentPanel();
            JPanel processesPanel = createSegmentsPanel();

            // Add the tablePanel with constraints
            gbc.gridx = 0;
            gbc.weightx = 0.3; // Adjust the weight as needed
            add(tablePanel, gbc);

            // Add the settingsAndAddProcessPanel with constraints
            gbc.gridx = 1;
            gbc.weightx = 0.15; // Adjust the weight as needed
            add(settingsAndAddProcessPanel, gbc);

            // Add the processesPanel with constraints
            gbc.gridx = 2;
            gbc.weightx = 0.55; // Give more weight to make it wider
            add(processesPanel, gbc);
        }

        private JPanel createTablePanel() {
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(new TitledBorder("Main Memory"));

            Object[][] data = new Object[0][];
            tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Make table non-editable
                    return false;
                }
            };

            // Check if the value is "OS" and color the row light grey
            // Extract SID
            // Fallback in case of parsing error
            JTable table = new JTable(tableModel) {
                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component c = super.prepareRenderer(renderer, row, column);
                    Object value = getModel().getValueAt(row, column);

                    // Check if the value is "OS" and color the row light grey
                    if ("OS".equals(value)) {
                        c.setBackground(Color.LIGHT_GRAY);
                    } else if (value != null && value.toString().contains("SID ")) {
                        try {
                            String sidStr = value.toString().split(",")[0].split(" ")[1]; // Extract SID
                            int sid = Integer.parseInt(sidStr);
                            Segments segmentsBySID = findSegmentsBySID(sid);
                            if (segmentsBySID != null) {
                                c.setBackground(segmentsBySID.getColor());
                            } else {
                                c.setBackground(Color.WHITE);
                            }
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            c.setBackground(Color.WHITE); // Fallback in case of parsing error
                        }
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                    return c;
                }
            };

            table.setFillsViewportHeight(true);
            table.setShowGrid(true);
            table.setGridColor(Color.GRAY);

            String[] rowHeaders = new String[data.length];

            rowHeader = new JList<>(rowHeaders); // Make it an instance variable
            rowHeader.setFixedCellWidth(50);
            rowHeader.setFixedCellHeight(table.getRowHeight());
            rowHeader.setCellRenderer(new RowHeaderRenderer(table));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setRowHeaderView(rowHeader);

            tablePanel.add(scrollPane, BorderLayout.CENTER);

            return tablePanel;
        }

        private void updateTableData(int memorySize, int osSize) {
            // Initialize the table model with the correct number of rows
            Object[][] newData = new Object[memorySize][1];
            for (int i = 0; i < newData.length; i++) {
                newData[i][0] = "Free"; // Initialize all as free initially
            }
            tableModel.setDataVector(newData, columnNames);

            // Now initialize the OS process
            initializeOSSegments(osSize);

            updateSegmentsTable(); // Update the table

            // Update the row headers based on the new memory size
            updateRowHeaders(memorySize);
        }

        private void initializeOSSegments(int osSize) {
            Segments osSegment = new Segments(0, "OS", osSize, Color.LIGHT_GRAY, 0); // SID 0 for OS
            for (int i = 0; i < osSize; i++) {
                updateMemoryTable(i, osSegment); // Update the table for OS pages
            }
            segments.add(osSegment); // Add OS segment to the list
        }

        private void updateRowHeaders(int memorySize) {
            String[] newHeaders = new String[memorySize];
            for (int i = 0; i < memorySize; i++) {
                newHeaders[i] = "@" + (i + 1);
            }
            rowHeader.setListData(newHeaders);
        }

        private static class RowHeaderRenderer extends JLabel implements ListCellRenderer<String> {
            RowHeaderRenderer(JTable table) {
                JTableHeader header = table.getTableHeader();
                setOpaque(true);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setHorizontalAlignment(LEFT); // Align to the left
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                setText((value != null) ? value : "");
                return this;
            }
        }

        private JPanel createSettingsAndAddSegmentPanel() {
            JPanel settingsAndAddSegmentPanel = new JPanel();
            settingsAndAddSegmentPanel.setLayout(new BoxLayout(settingsAndAddSegmentPanel, BoxLayout.Y_AXIS));

            // Add the settings panel
            JPanel settingsPanel = createSettingsPanel();
            settingsAndAddSegmentPanel.add(settingsPanel);

            // Add the add segment panel
            JPanel addSegmentPanel = createAddSegmentPanel();
            settingsAndAddSegmentPanel.add(addSegmentPanel);

            return settingsAndAddSegmentPanel;
        }

        private JPanel createSettingsPanel() {
            JPanel settingsPanel = new JPanel();
            settingsPanel.setBorder(new TitledBorder("General Settings"));
            settingsPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4); // Padding
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0; // Column 0
            gbc.gridy = 0; // Row 0
            gbc.weightx = 0; // No extra space distribution

            // Memory size label and spinner
            JLabel memorySizeLabel = new JLabel("Memory size:");
            JSpinner memorySizeSpinner = new JSpinner(new SpinnerNumberModel(64, 1, 256, 1));
            settingsPanel.add(memorySizeLabel, gbc);

            gbc.gridx = 1; // Column 1
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1; // Extra space distributed to spinner
            settingsPanel.add(memorySizeSpinner, gbc);

            // OS size label and spinner
            gbc.gridx = 0; // Reset to Column 0 for next component
            gbc.gridy = 1; // Next row
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0; // Reset extra space distribution

            JLabel osSizeLabel = new JLabel("OS size:");
            JSpinner osSizeSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 64, 1));
            settingsPanel.add(osSizeLabel, gbc);

            gbc.gridx = 1; // Column 1 for spinner
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1; // Extra space distributed to spinner
            settingsPanel.add(osSizeSpinner, gbc);

            // Confirm button
            gbc.gridx = 0; // Span across both columns
            gbc.gridy = 2; // Next row
            gbc.gridwidth = 2; // Span across two columns
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER; // Center the button

            JButton confirmButton = getjButton(memorySizeSpinner, osSizeSpinner);
            settingsPanel.add(confirmButton, gbc);

            return settingsPanel;
        }

        private JButton getjButton(JSpinner memorySizeSpinner, JSpinner osSizeSpinner) {
            JButton confirmButton = new JButton("Generate");
            confirmButton.addActionListener(e -> {
                int memorySize = (Integer) memorySizeSpinner.getValue();
                int osSize = (Integer) osSizeSpinner.getValue();
                // Reset processes and process ID
                segments.clear();
                nextSID = 1;

                // Update the main memory table and processes table
                updateTableData(memorySize, osSize);
                updateSegmentsTable();
                createButton.setEnabled(true); // Enable the create button
            });
            return confirmButton;
        }

        private JPanel createAddSegmentPanel() {
            JPanel addSegmentPanel = new JPanel(new GridBagLayout());
            addSegmentPanel.setBorder(new TitledBorder("Add Segment"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4); // Add some padding
            gbc.anchor = GridBagConstraints.WEST;

            // Name label and field
            JLabel nameLabel = new JLabel("Name:");
            JTextField nameField = new JTextField(20);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            addSegmentPanel.add(nameLabel, gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            addSegmentPanel.add(nameField, gbc);

            // Size label and spinner
            JLabel sizeLabel = new JLabel("Size:");
            JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            addSegmentPanel.add(sizeLabel, gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            addSegmentPanel.add(sizeSpinner, gbc);

            // Color label and button
            JLabel colorLabel = new JLabel("Color:");
            JButton colorButton = new JButton();
            Random rand = new Random(); // Random object to generate random numbers
            // Set default color
            Color defaultColor = new Color(255, 130, 0);
            colorButton.setBackground(defaultColor);
            colorButton.setOpaque(true);
            colorButton.setBorderPainted(false); // Needed on some look and feels

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 1; // Take up only one column for the label
            addSegmentPanel.add(colorLabel, gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 1; // Take up only one column for the button
            gbc.fill = GridBagConstraints.HORIZONTAL;
            addSegmentPanel.add(colorButton, gbc);

            colorButton.addActionListener(e -> {
                Color chosenColor = JColorChooser.showDialog(null, "Choose a color", colorButton.getBackground());
                if (chosenColor != null) {
                    colorButton.setBackground(chosenColor);
                }
            });

            // Create button
            createButton = new JButton("Create");
            createButton.setEnabled(false);
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2; // Span across two columns
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            addSegmentPanel.add(createButton, gbc);

            // Action listener for create button
            createButton.addActionListener(e -> {
                int segmentSize = (Integer) sizeSpinner.getValue();
                String segmentName = nameField.getText().trim();
                Color segmentColor = colorButton.getBackground();

                if (segmentName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Segment name is mandatory.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop further processing
                }

                if (isSegmentNameExists(segmentName)) {
                    JOptionPane.showMessageDialog(this, "Segment name must be unique.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop further processing
                }

                // Find a random starting address with enough contiguous free space
                int startAddress = findRandomStartAddress(segmentSize);
                if (startAddress == -1) {
                    JOptionPane.showMessageDialog(this, "Not enough contiguous free memory for this segment.");
                    return; // Stop further processing
                }

                Segments newSegment = new Segments(nextSID++, segmentName, segmentSize, segmentColor, startAddress);
                for (int i = 0; i < segmentSize; i++) {
                    updateMemoryTable(startAddress + i, newSegment); // Pass the page number
                }
                segments.add(newSegment);
                updateSegmentsTable();

                Color randomColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
                colorButton.setBackground(randomColor);
            });

            return addSegmentPanel;
        }

        private int findRandomStartAddress(int segmentSize) {
            List<Integer> freeAddresses = getFreeMemoryAddresses();
            Random random = new Random();
            while (!freeAddresses.isEmpty()) {
                int randomIndex = random.nextInt(freeAddresses.size());
                int startAddress = freeAddresses.get(randomIndex);
                // Check if there is enough contiguous free space
                if (isEnoughContiguousSpace(startAddress, segmentSize)) {
                    return startAddress;
                } else {
                    freeAddresses.remove(randomIndex); // Remove the address and try again
                }
            }
            return -1; // Not enough contiguous free space
        }

        private boolean isEnoughContiguousSpace(int startAddress, int segmentSize) {
            for (int i = 0; i < segmentSize; i++) {
                if (startAddress + i >= tableModel.getRowCount() || !"Free".equals(tableModel.getValueAt(startAddress + i, 0))) {
                    return false;
                }
            }
            return true;
        }

        // Helper method to check if a process name already exists
        private boolean isSegmentNameExists(String name) {
            for (Segments segment : segments) {
                if (segment.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        }

        private List<Integer> getFreeMemoryAddresses() {
            List<Integer> freeAddresses = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ("Free".equals(tableModel.getValueAt(i, 0))) {
                    freeAddresses.add(i);
                }
            }
            return freeAddresses;
        }

        private void updateMemoryTable(int address, Segments segments) {
            String cellValue;
            if (segments.getSID() == 0) { // Check if the process is the OS
                cellValue = "OS";
            } else {
                cellValue = "SID " + segments.getSID() + " - " + " (" + segments.getName() + ")";
            }
            tableModel.setValueAt(cellValue, address, 0);
        }

        private Segments findSegmentsBySID(int sid) {
            for (Segments segment : segments) {
                if (segment.getSID() == sid) {
                    return segment;
                }
            }
            return null;
        }

        private JPanel createSegmentsPanel() {
            JPanel segmentsPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout
            segmentsPanel.setBorder(new TitledBorder("Segments Table"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 3.0;

            // Table for active processes
            String[] activeSegmentsColumnNames = {"Address", "Base", "SID", "Name", "Limit", "Color"};
            activeSegmentsTable = new JTable(new DefaultTableModel(new Object[0][6], activeSegmentsColumnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Make table non-editable
                    return false;
                }
            });
            JScrollPane activeSegmentsScrollPane = new JScrollPane(activeSegmentsTable);

            TableColumn colorColumn = activeSegmentsTable.getColumnModel().getColumn(5); // Assuming color is the 7th column
            colorColumn.setCellRenderer(new ColorRenderer());

            // Add the table with constraints
            segmentsPanel.add(activeSegmentsScrollPane, gbc);

            // Back button
            JButton back = new JButton("Back");
            back.addActionListener(e -> {
                StartForm startForm = new StartForm();
                startForm.setVisible(true);
                SegmentationMemoryGUI.this.dispose();
            });

            // Adjust constraints for the back button
            gbc.weighty = 1; // Less weight compared to the table
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.SOUTHEAST;

            // Add the back button with constraints
            segmentsPanel.add(back, gbc);

            return segmentsPanel;
        }

        private void updateSegmentsTable() {
            DefaultTableModel model = (DefaultTableModel) activeSegmentsTable.getModel();
            model.setRowCount(0); // Clear existing rows

            for (Segments segment : segments) {
                Object[] row = new Object[]{
                        segment.getBase_address() + 1,
                        segment.getBase_address() + 1,
                        segment.getSID(),
                        segment.getName(),
                        segment.getLimit(),
                        segment.getColor() // Get the color name
                };
                model.addRow(row);
            }
        }

        static class ColorRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Color color) {
                    setBackground(color);
                    setForeground(color); // You might want to adjust text color for visibility
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                return this;
            }
        }
    }
}

