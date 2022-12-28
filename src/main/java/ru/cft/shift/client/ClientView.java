package ru.cft.shift.client;

import ru.cft.shift.common.ServerSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ClientView extends JFrame {
    private JTextField inputHost;
    private JTextField inputName;
    private JButton loginButton;
    private JTextField inputMessage;
    private JButton sendMessageButton;
    private JTextArea chatTextArea;
    private JTextArea usersTextArea;
    private ClientModel model;

    public void start(ServerSettings settings) {
        model = new ClientModel(this, settings);

        InitView();
        welcomeMessage();
    }

    public void addMessageToChat(String messageText) {
        chatTextArea.append(messageText);
    }

    public void setUsersList(List<String> users) {
        usersTextArea.setText("");
        for (String name : users) {
            usersTextArea.append(name);
            usersTextArea.append(System.lineSeparator());
        }
    }

    public void setIsLoggedState(boolean isLogged) {
        inputHost.setEnabled(!isLogged);
        inputName.setEnabled(!isLogged);
        loginButton.setEnabled(!isLogged);
        inputMessage.setEnabled(isLogged);
        sendMessageButton.setEnabled(isLogged);
        if (!isLogged) {
            usersTextArea.setText("");
        }
    }

    public String getInputHost() {
        return inputHost.getText().trim();
    }

    public String getInputName() {
        return inputName.getText().trim();
    }

    public String getInputMessage() {
        return inputName.getText();
    }

    public void clearInputMessage() {
        inputName.setText("");
        inputMessage.grabFocus();
    }

    public void clearChatTextArea() {
        chatTextArea.setText("");
    }

    private void InitView() {
        JSplitPane splitPane = new JSplitPane();
        JSplitPane chatSplitPane = new JSplitPane();
        JPanel loginPanel = new JPanel();
        JPanel chatPanel = new JPanel();
        JPanel usersPanel = new JPanel();

        setTitle("Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel chatLabel = new JLabel("Chat");
        JScrollPane textAreaScrollPane = new JScrollPane();
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);

        JPanel bottomChatPanel = new JPanel();
        inputMessage = new JTextField();
        sendMessageButton = new JButton("Send");

        JLabel inputHostLabel = new JLabel("Enter host:");
        inputHost = new JTextField("localhost");
        inputHost.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        JLabel inputNameLabel = new JLabel("Enter your name:");
        inputName = new JTextField("John");
        inputName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        loginButton = new JButton("Login");

        JLabel usersLabel = new JLabel("Users in chat:");
        JScrollPane usersScrollPane = new JScrollPane();
        usersTextArea = new JTextArea();
        usersTextArea.setEditable(false);

        setPreferredSize(new Dimension(1000, 500));
        getContentPane().setLayout(new GridLayout());
        getContentPane().add(splitPane);

        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(0.3);
        splitPane.setLeftComponent(loginPanel);
        splitPane.setRightComponent(chatSplitPane);

        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setMinimumSize(new Dimension(200, 500));
        loginPanel.add(inputHostLabel);
        loginPanel.add(inputHost);
        loginPanel.add(inputNameLabel);
        loginPanel.add(inputName);
        loginPanel.add(loginButton);

        chatSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        chatSplitPane.setDividerLocation(0.7);
        chatSplitPane.setLeftComponent(chatPanel);
        chatSplitPane.setRightComponent(usersPanel);

        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setMinimumSize(new Dimension(500, 500));
        chatPanel.add(chatLabel);
        chatPanel.add(textAreaScrollPane);
        textAreaScrollPane.setViewportView(chatTextArea);
        chatPanel.add(bottomChatPanel);

        bottomChatPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        bottomChatPanel.setLayout(new BoxLayout(bottomChatPanel, BoxLayout.X_AXIS));
        bottomChatPanel.add(inputMessage);
        bottomChatPanel.add(sendMessageButton);
        inputMessage.setEnabled(false);
        sendMessageButton.setEnabled(false);

        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        usersPanel.setMinimumSize(new Dimension(150, 500));
        usersPanel.add(usersLabel);
        usersPanel.add(usersScrollPane);
        usersScrollPane.setViewportView(usersTextArea);

        pack();

        loginButton.addActionListener(e -> model.onLoginPress());
        sendMessageButton.addActionListener(e -> model.onSendPress());
        InitExitListener();
        setVisible(true);
    }

    private void InitExitListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                super.windowClosing(ev);
                model.onClose();
            }
        });
    }

    private void welcomeMessage() {
        String startMessageText = "In order to join the chat, you need to login. "
                + "Enter the host you want to join and your name"
                + System.lineSeparator();
        chatTextArea.append(startMessageText);
    }
}
