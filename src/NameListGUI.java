import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class NameListGUI extends JFrame
{
    private static final int INVALID_VALUE = -1;
    private static final int EXIT_STATUS = 0;
    private static final int TEXT_FIELD_COLUMNS = 20;
    private static final int TEXT_FIELD_HEIGHT = 25;
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 300;
    private static final int TEXT_FIELD_WIDTH = 150;
    private static final String WINDOW_TITLE = "Name List Application";
    private static final String INPUT_LABEL_TEXT = "Input";
    private static final String INPUT_PROMPT = "Type a name or quit:";
    private static final String ADD_BUTTON_TEXT = "OK";
    private static final String CANCEL_BUTTON_TEXT = "Cancel";
    private static final String SAVE_NAMES_MENU_TEXT = "Save Names";
    private static final String DELETE_MENU_TEXT = "Delete Selected Name";
    private static final String EXIT_MENU_TEXT = "Exit";
    private static final String SAVED_MESSAGE = "SAVED!!";
    private static final String SAVE_ERROR_MESSAGE = "Error saving names to file.";
    private static final String SAVE_FILE_NAME = "names.txt";

    private DefaultListModel<String> listModel;
    private JList<String> nameList;
    private JTextField nameField;
    private JMenuItem deleteMenuItem, saveNamesMenuItem, exitMenuItem;
    private JLabel statusLabel;

    public NameListGUI()
    {
        super(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

        listModel = new DefaultListModel<>();
        nameList = new JList<>(listModel);
        nameField = new JTextField(TEXT_FIELD_COLUMNS);
        JButton addButton = new JButton(ADD_BUTTON_TEXT);
        JButton cancelButton = new JButton(CANCEL_BUTTON_TEXT);
        statusLabel = new JLabel("");

        // Add action listeners
        addButton.addActionListener(e -> addName());
        cancelButton.addActionListener(e -> clearNameField());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Top row with blue background and "Input" label
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.BLUE);
        JLabel inputLabel = new JLabel(INPUT_LABEL_TEXT);
        inputLabel.setForeground(Color.WHITE);
        topPanel.add(inputLabel);

        // Close button at top right corner
        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.RED); // Optional: Change color of the close button
        closeButton.addActionListener(e -> dispose()); // Close the frame when clicked
        topPanel.add(closeButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Middle row with "Type a name or quit" label, input field, and buttons
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new FlowLayout());
        JLabel nameLabel = new JLabel(INPUT_PROMPT);
        nameField.setPreferredSize(new Dimension(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT));
        middlePanel.add(nameLabel);
        middlePanel.add(nameField);
        middlePanel.add(addButton);
        middlePanel.add(cancelButton);
        panel.add(middlePanel, BorderLayout.CENTER);

        // Status label for showing "SAVED!!" message
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusLabel.setForeground(Color.GREEN); // Set color to green
        statusPanel.add(statusLabel);
        panel.add(statusPanel, BorderLayout.SOUTH);

        getContentPane().add(panel);

        // Initialize list selection listener
        nameList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e)
            {
                if (!e.getValueIsAdjusting())
                {
                    String selectedName = nameList.getSelectedValue();
                    if (selectedName != null)
                    {
                        showCapitalizedPopup(selectedName);
                    }
                }
            }
        });

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.BLUE); // Set background color to blue

        JMenu fileMenu = new JMenu("File");

        saveNamesMenuItem = new JMenuItem(SAVE_NAMES_MENU_TEXT);
        saveNamesMenuItem.addActionListener(e -> showSavedNames());
        fileMenu.add(saveNamesMenuItem);

        deleteMenuItem = new JMenuItem(DELETE_MENU_TEXT);
        deleteMenuItem.addActionListener(e -> deleteSelectedName());
        fileMenu.add(deleteMenuItem);

        exitMenuItem = new JMenuItem(EXIT_MENU_TEXT);
        exitMenuItem.addActionListener(e -> System.exit(EXIT_STATUS));
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        setVisible(true);
    }

    private void addName()
    {
        String newName = nameField.getText().trim();
        if (!newName.isEmpty())
        {
            listModel.addElement(newName);
            nameField.setText("");
            statusLabel.setText(SAVED_MESSAGE);
            saveToFile(); // Automatically save after adding a name
        }
    }

    private void clearNameField()
    {
        nameField.setText("");
        statusLabel.setText(""); // Clear status message
    }

    private void deleteSelectedName()
    {
        int selectedIndex = nameList.getSelectedIndex();
        if (selectedIndex != INVALID_VALUE)
        {
            listModel.remove(selectedIndex);
            statusLabel.setText(""); // Clear status message
            saveToFile(); // Automatically save after deleting a name
        }
    }

    private void saveToFile()
    {
        try (PrintWriter writer = new PrintWriter(SAVE_FILE_NAME))
        {
            for (int i = 0; i < listModel.getSize(); i++)
            {
                writer.println(listModel.getElementAt(i));
            }
            // Show saved message in status label
            statusLabel.setText(SAVED_MESSAGE);
        }

        catch (FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(this, SAVE_ERROR_MESSAGE, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSavedNames()
    {
        JFrame savedNamesFrame = new JFrame("Saved Names");
        savedNamesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultListModel<String> savedListModel = new DefaultListModel<>();
        for (int i = 0; i < listModel.getSize(); i++)
        {
            savedListModel.addElement(listModel.getElementAt(i));
        }

        JList<String> savedNamesList = new JList<>(savedListModel);
        savedNamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        savedNamesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e)
            {
                if (!e.getValueIsAdjusting())
                {
                    String selectedName = savedNamesList.getSelectedValue();
                    if (selectedName != null)
                    {
                        showCapitalizedPopup(selectedName);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(savedNamesList);
        savedNamesFrame.getContentPane().add(scrollPane);
        savedNamesFrame.pack();
        savedNamesFrame.setLocationRelativeTo(null); // Center on screen
        savedNamesFrame.setVisible(true);
    }

    private void showCapitalizedPopup(final String name)
    {
        String capitalized = name.toUpperCase();
        JOptionPane.showMessageDialog(this, capitalized, "Selected Name", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(final String[] args)
    {
        SwingUtilities.invokeLater(NameListGUI::new);
    }
}
