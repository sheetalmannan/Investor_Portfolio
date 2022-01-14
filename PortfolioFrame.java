package ePortfolio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Window for managing portfolio.
 *
 * @author Me
 */
public class PortfolioFrame extends JFrame implements ActionListener {

    /**
     * For formatting purposes.
     */
    private static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    /**
     * Flips the panels depending on the commands.
     */
    private Map<String, JPanel> panels = new HashMap<>();

    /**
     * The current panel being viewed.
     */
    private JPanel currentPanel = new JPanel(new BorderLayout());

    /**
     * The portfolio to manage.
     */
    private Portfolio portfolio;

    /**
     * Initialize the UI.
     *
     * @param portfolio The portfolio to manage
     */
    public PortfolioFrame(Portfolio portfolio) {
        setTitle("ePortfolio");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setLayout(new BorderLayout());

        this.portfolio = portfolio;

        currentPanel.add(new JLabel("<html>"
                + "Welcome to ePortfolio<br /><br />"
                + "Choose a command from the 'Commands' menu to buy or sell "
                + "an investment, update prices for all investments, get gain "
                + "for the portfolio, search for relevant investments, or quit "
                + "the program."
                + "</html>"));

        add(BorderLayout.CENTER, currentPanel);

        // Create the different panels for the commands
        panels.put("Buy an Investment", new BuyInvestmentPanel());
        panels.put("Sell an Investment", new SellInvestmentPanel());
        panels.put("Update an Investment", new UpdateInvestmentPanel());
        panels.put("Get Gains", new GetGainsPanel());
        panels.put("Search Investments", new SearchInvestmentPanel());

        // Create a commands menu
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu commandsMenu = new JMenu("Commands");
        menuBar.add(commandsMenu);

        // Create the menu items that will handle the commands
        String[] menuItemNames = {"Buy an Investment", "Sell an Investment", "Update an Investment", "Get Gains", "Search Investments"};

        for (String menuItemName : menuItemNames) {
            JMenuItem menuItem = new JMenuItem(menuItemName);
            menuItem.addActionListener(this);
            commandsMenu.add(menuItem);
        }

        // Add the exit menu
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(this);
        commandsMenu.add(exitMenuItem);
    }

    /**
     * Show the appropriate panel on the selected command
     *
     * @param e Event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("Exit")) {
            // Terminate program
            portfolio.trySave();
            System.exit(0);
        }

        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = panels.get(e.getActionCommand());
        add(BorderLayout.CENTER, currentPanel);
        revalidate();
        repaint();

        if (currentPanel instanceof GetGainsPanel) {
            ((GetGainsPanel) currentPanel).update();
        } else if (currentPanel instanceof UpdateInvestmentPanel) {
            ((UpdateInvestmentPanel) currentPanel).initialize();
        }
    }

    /**
     * Panel that holds the user interface components for buying investments.
     */
    private class BuyInvestmentPanel extends JPanel implements ActionListener {

        /**
         * Selection whether buying a stock or mutual fund.
         */
        private JComboBox typeField = new JComboBox(new String[]{"Stock", "Mutual Fund"});

        /**
         * Unique symbol of the stock or mutual fund.
         */
        private JTextField symbolField = new JTextField(10);

        /**
         * Full name of the stock or mutual fund.
         */
        private JTextField nameField = new JTextField(10);

        /**
         * Quantity of shares.
         */
        private JTextField quantityField = new JTextField(10);

        /**
         * Price per share.
         */
        private JTextField priceField = new JTextField(10);

        /**
         * Message log.
         */
        private JTextArea messagesField = new JTextArea();

        /**
         * Initialize the user interface.
         */
        public BuyInvestmentPanel() {
            setLayout(new BorderLayout());

            // Set the input fields            
            Box box = Box.createVerticalBox();
            box.setBorder(BorderFactory.createTitledBorder("Buying an Investment"));
            add(BorderLayout.CENTER, box);

            String[] fieldNames = {"Type", "Symbol", "Name", "Quantity", "Price"};
            Component[] fields = {typeField, symbolField, nameField, quantityField, priceField};

            for (int i = 0; i < fieldNames.length; i++) {
                JPanel fieldPanel = new JPanel();
                fieldPanel.add(new JLabel(fieldNames[i]));
                fieldPanel.add(fields[i]);
                box.add(fieldPanel);
            }

            // Create the buttons
            box = Box.createVerticalBox();
            add(BorderLayout.EAST, box);
            box.setPreferredSize(new Dimension(200, 0));

            JButton resetButton = new JButton("Reset");
            JButton buyButton = new JButton("Buy");

            box.add(resetButton);
            box.add(buyButton);

            resetButton.addActionListener(this);
            buyButton.addActionListener(this);

            // Create the messages
            JPanel messagesPanel = new JPanel(new BorderLayout());
            messagesPanel.setPreferredSize(new Dimension(0, 200));
            messagesPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
            messagesPanel.add(BorderLayout.CENTER, new JScrollPane(messagesField));
            messagesField.setEditable(false);

            add(BorderLayout.SOUTH, messagesPanel);
        }

        /**
         * Clear the fields.
         */
        private void reset() {
            JTextField[] fields = {symbolField, nameField, quantityField, priceField};

            for (JTextField field : fields) {
                field.setText("");
            }

            messagesField.append("Ok: Fields has been cleared.\n");
        }

        /**
         * Purchase a stock.
         */
        private void buyStock() {
            // Get a symbol
            String symbol = symbolField.getText().trim();

            // Get the quantity
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
            } catch (Exception e) {
                messagesField.append("Error: Quantity should be a whole number.\n");
                return;
            }

            Stock stock = portfolio.findStock(symbol);

            if (stock == null) {
                // Case for a new stock
                messagesField.append("New stock detected...\n");

                // Get the name
                String name = nameField.getText().trim();

                // Get the price
                double price;

                try {
                    price = Double.parseDouble(priceField.getText().trim());
                } catch (Exception e) {
                    messagesField.append("Error: Price should be numeric.\n");
                    return;
                }

                // Add to list
                portfolio.addToList(new Stock(symbol, name, quantity, price));
                messagesField.append("Ok: Stock has been added to portfolio.\n");
            } else {
                // Case for existing stock
                messagesField.append("Existing stock detected. Name and price fields are ignored.\n");

                stock.add(quantity);
                messagesField.append("Ok: Stock quantity has been updated.\n");
            }
        }

        /**
         * Purchase a mutual fund.
         */
        private void buyMutualFund() {
            // Get a symbol
            String symbol = symbolField.getText().trim();

            // Get the quantity
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
            } catch (Exception e) {
                messagesField.append("Error: Quantity should be a whole number.\n");
                return;
            }

            MutualFund fund = portfolio.findMutualFund(symbol);

            if (fund == null) {
                // Case for a new fund
                messagesField.append("New mutual fund detected...\n");

                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());

                // Add to list
                portfolio.addToList(new Stock(symbol, name, quantity, price));
                messagesField.append("Ok: Mutual fund has been added to portfolio.\n");
            } else {
                // Case for existing fund
                messagesField.append("Existing mutual fund detected. Name and price fields are ignored.\n");

                fund.add(quantity);
                messagesField.append("Ok: Mutual fund quantity has been updated.\n");
            }
        }

        /**
         * Handle the buying of investment.
         *
         * @param e Event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("Reset")) {
                reset();
            } else if (e.getActionCommand().equalsIgnoreCase("Buy")) {
                try {
                    if (typeField.getSelectedItem().equals("Stock")) {
                        buyStock();
                    } else if (typeField.getSelectedItem().equals("Mutual Fund")) {
                        buyMutualFund();
                    }
                } catch (Exception ex) {
                    messagesField.append("Error: " + ex.getMessage() + "\n");
                }
            }
        }
    }

    /**
     * Panel that holds the user interface components for selling investments.
     */
    private class SellInvestmentPanel extends JPanel implements ActionListener {

        /**
         * Unique symbol of the stock or mutual fund.
         */
        private JTextField symbolField = new JTextField(10);

        /**
         * Quantity of how many to sell.
         */
        private JTextField quantityField = new JTextField(10);

        /**
         * Message log.
         */
        private JTextArea messagesField = new JTextArea();

        /**
         * Initialize the user interface.
         */
        public SellInvestmentPanel() {
            setLayout(new BorderLayout());

            // Set the input fields            
            Box box = Box.createVerticalBox();
            box.setBorder(BorderFactory.createTitledBorder("Selling an Investment"));
            add(BorderLayout.CENTER, box);

            String[] fieldNames = {"Symbol", "Quantity"};
            Component[] fields = {symbolField, quantityField};

            for (int i = 0; i < fieldNames.length; i++) {
                JPanel fieldPanel = new JPanel();
                fieldPanel.add(new JLabel(fieldNames[i]));
                fieldPanel.add(fields[i]);
                box.add(fieldPanel);
            }

            // Create the buttons
            box = Box.createVerticalBox();
            add(BorderLayout.EAST, box);
            box.setPreferredSize(new Dimension(200, 0));

            JButton resetButton = new JButton("Reset");
            JButton buyButton = new JButton("Sell");

            box.add(resetButton);
            box.add(buyButton);

            resetButton.addActionListener(this);
            buyButton.addActionListener(this);

            // Create the messages
            JPanel messagesPanel = new JPanel(new BorderLayout());
            messagesPanel.setPreferredSize(new Dimension(0, 200));
            messagesPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
            messagesPanel.add(BorderLayout.CENTER, new JScrollPane(messagesField));
            messagesField.setEditable(false);

            add(BorderLayout.SOUTH, messagesPanel);
        }

        /**
         * Clear the fields.
         */
        private void reset() {
            JTextField[] fields = {symbolField, quantityField};

            for (JTextField field : fields) {
                field.setText("");
            }

            messagesField.append("Ok: Fields cleared.\n");
        }

        /**
         * Sell an investment
         */
        private void sell() {
            // Get a symbol
            String symbol = symbolField.getText().trim();

            // Get the quantity
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
            } catch (Exception e) {
                messagesField.append("Error: Quantity should be a whole number.\n");
                return;
            }

            // Find the investment
            Investment investment = portfolio.findStock(symbol);

            if (investment == null) {
                investment = portfolio.findMutualFund(symbol);
            }

            if (investment == null) {
                messagesField.append("Error: Investment does not exist.\n");
                return;
            }

            // Do sell the mentioned quantity
            messagesField.append("Ok: Realized Gain/Loss: $" + decimalFormat.format(investment.reduce(quantity)) + "\n");

            if (investment.getQuantity() == 0) {
                portfolio.removeFromList(investment);
            }
        }

        /**
         * Handle the actions.
         *
         * @param e Event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("Reset")) {
                reset();
            } else if (e.getActionCommand().equalsIgnoreCase("Sell")) {
                try {
                    sell();
                } catch (Exception ex) {
                    messagesField.append("Error: " + ex.getMessage() + "\n");
                }
            }
        }
    }

    /**
     * Panel that holds the user interface components for updating investments.
     */
    private class UpdateInvestmentPanel extends JPanel implements ActionListener {

        /**
         * Unique symbol of the stock or mutual fund.
         */
        private JTextField symbolField = new JTextField(10);

        /**
         * Name of the stock.
         */
        private JTextField nameField = new JTextField(10);

        /**
         * Price at how much to sell per share.
         */
        private JTextField priceField = new JTextField(10);

        /**
         * Message log.
         */
        private JTextArea messagesField = new JTextArea();

        /**
         * Investments for viewing
         */
        private ArrayList<Investment> investments;

        /**
         * Current index of investment being viewed
         */
        private int currentIndex = -1;

        /**
         * Initialize the user interface
         */
        public UpdateInvestmentPanel() {
            setLayout(new BorderLayout());

            // Set the input fields            
            Box box = Box.createVerticalBox();
            box.setBorder(BorderFactory.createTitledBorder("Updating an Investment"));
            add(BorderLayout.CENTER, box);

            String[] fieldNames = {"Symbol", "Name", "Price"};
            Component[] fields = {symbolField, nameField, priceField};

            for (int i = 0; i < fieldNames.length; i++) {
                JPanel fieldPanel = new JPanel();
                fieldPanel.add(new JLabel(fieldNames[i]));
                fieldPanel.add(fields[i]);
                box.add(fieldPanel);
            }

            symbolField.setEditable(false);
            nameField.setEditable(false);

            // Create the buttons
            box = Box.createVerticalBox();
            add(BorderLayout.EAST, box);
            box.setPreferredSize(new Dimension(200, 0));

            JButton previousButton = new JButton("Previous");
            JButton nextButton = new JButton("Next");
            JButton saveButton = new JButton("Save");

            box.add(previousButton);
            box.add(nextButton);
            box.add(saveButton);

            previousButton.addActionListener(this);
            nextButton.addActionListener(this);
            saveButton.addActionListener(this);

            // Create the messages
            JPanel messagesPanel = new JPanel(new BorderLayout());
            messagesPanel.setPreferredSize(new Dimension(0, 200));
            messagesPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
            messagesPanel.add(BorderLayout.CENTER, new JScrollPane(messagesField));
            messagesField.setEditable(false);

            add(BorderLayout.SOUTH, messagesPanel);
        }

        /**
         * Initialize the investments for viewing
         */
        public void initialize() {
            investments = portfolio.getInvestments();

            if (investments.isEmpty()) {
                currentIndex = -1;
                return;
            }

            currentIndex = 0;
            showInvestment();
        }

        /**
         * Display the current investment
         */
        private void showInvestment() {
            Investment investment = investments.get(currentIndex);
            symbolField.setText(investment.getSymbol());
            nameField.setText(investment.getName());
            priceField.setText(investment.getPrice() + "");
        }

        /**
         * Show a previous stock.
         */
        private void previous() {
            if (currentIndex <= 0) {
                messagesField.append("There is no previous investment.\n");
                return;
            }

            currentIndex--;
            showInvestment();
        }

        /**
         * Show a next stock.
         */
        private void next() {
            if (currentIndex >= investments.size() - 1) {
                messagesField.append("There is no next investment.\n");
                return;
            }

            currentIndex++;
            showInvestment();
        }

        /**
         * Update the price.
         */
        private void save() {
            // Get the price
            double price;

            try {
                price = Double.parseDouble(priceField.getText().trim());
            } catch (Exception e) {
                messagesField.append("Error: Price should be numeric.\n");
                return;
            }

            // Do update
            try {
                Investment investment = investments.get(currentIndex);
                investment.setPrice(price);
                messagesField.append("Ok: Price has been updated.\n");
            } catch (Exception e) {
                messagesField.append("Error: " + e.getMessage() + "\n");
            }
        }

        /**
         * Handle event.
         *
         * @param e Event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("Previous")) {
                previous();
            } else if (e.getActionCommand().equalsIgnoreCase("Next")) {
                next();
            } else if (e.getActionCommand().equalsIgnoreCase("Save")) {
                save();
            }
        }
    }

    /**
     * Panel that holds the user interface components for calculating gain
     */
    private class GetGainsPanel extends JPanel {

        /**
         * Field for displaying the total gain.
         */
        private JTextField totalGainField = new JTextField(20);

        /**
         * Field for displaying individual gains.
         */
        private JTextArea individualGainsField = new JTextArea();

        /**
         * Initialize the user interface.
         */
        public GetGainsPanel() {
            setLayout(new BorderLayout());

            JPanel gainPanel = new JPanel();
            gainPanel.setBorder(BorderFactory.createTitledBorder("Getting Total Gain"));
            gainPanel.add(new JLabel("Total Gain"));
            gainPanel.add(totalGainField);
            add(BorderLayout.CENTER, gainPanel);

            totalGainField.setEditable(false);

            // Create the messages
            JPanel individualGainsPanel = new JPanel(new BorderLayout());
            individualGainsPanel.setPreferredSize(new Dimension(0, 300));
            individualGainsPanel.setBorder(BorderFactory.createTitledBorder("Individual Gains"));
            individualGainsPanel.add(BorderLayout.CENTER, new JScrollPane(individualGainsField));
            individualGainsField.setEditable(false);

            add(BorderLayout.SOUTH, individualGainsPanel);
        }

        // Update the fields
        public void update() {
            individualGainsField.setText("");
            double unrealizedGainsOrLosses = 0;

            for (Investment investment : portfolio.getInvestments()) {
                individualGainsField.append(investment.toString() + "\n\n");
                unrealizedGainsOrLosses += investment.computeUnrealizedGainOrLoss();
            }

            totalGainField.setText("$" + decimalFormat.format(unrealizedGainsOrLosses));
        }
    }

    /**
     * Panel that holds the user interface for searching investments.
     */
    private class SearchInvestmentPanel extends JPanel implements ActionListener {

        /**
         * Symbol field for searching.
         */
        private JTextField symbolField = new JTextField(10);

        /**
         * Keywords field for searching.
         */
        private JTextField keywordsField = new JTextField(10);

        /**
         * Minimum price for searching.
         */
        private JTextField lowPriceField = new JTextField(10);

        /**
         * Max price for searching.
         */
        private JTextField highPriceField = new JTextField(10);

        /**
         * Area for displaying search results.
         */
        private JTextArea resultsField = new JTextArea();

        /**
         * Initialize the user interface
         */
        public SearchInvestmentPanel() {
            setLayout(new BorderLayout());

            // Set the input fields            
            Box box = Box.createVerticalBox();
            box.setBorder(BorderFactory.createTitledBorder("Searching an Investment"));
            add(BorderLayout.CENTER, box);

            String[] fieldNames = {"Symbol", "Keywords", "Low Price", "High Price"};
            Component[] fields = {symbolField, keywordsField, lowPriceField, highPriceField};

            for (int i = 0; i < fieldNames.length; i++) {
                JPanel fieldPanel = new JPanel();
                fieldPanel.add(new JLabel(fieldNames[i]));
                fieldPanel.add(fields[i]);
                box.add(fieldPanel);
            }

            // Create the buttons
            box = Box.createVerticalBox();
            add(BorderLayout.EAST, box);
            box.setPreferredSize(new Dimension(200, 0));

            JButton resetButton = new JButton("Reset");
            JButton searchButton = new JButton("Search");

            box.add(resetButton);
            box.add(searchButton);

            resetButton.addActionListener(this);
            searchButton.addActionListener(this);

            // Create the panel for results
            JPanel resultsPanel = new JPanel(new BorderLayout());
            resultsPanel.setPreferredSize(new Dimension(0, 200));
            resultsPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
            resultsPanel.add(BorderLayout.CENTER, new JScrollPane(resultsField));
            resultsField.setEditable(false);

            add(BorderLayout.SOUTH, resultsPanel);
        }

        /**
         * Reset the search fields.
         */
        private void reset() {
            JTextField[] fields = {symbolField, keywordsField, lowPriceField, highPriceField};

            for (JTextField field : fields) {
                field.setText("");
            }

            resultsField.setText("");
        }

        /**
         * Search and filter
         */
        private void search() {
            // Get the entries
            String symbol = symbolField.getText().trim();
            String keywords = keywordsField.getText().trim();

            // Get the low price if provided
            double lowPrice = 0;

            if (!lowPriceField.getText().trim().isEmpty()) {
                try {
                    lowPrice = Double.parseDouble(lowPriceField.getText().trim());

                    if (lowPrice < 0) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    resultsField.setText("Low price should be numeric and positive.\n");
                    return;
                }
            }

            // Get the high price if provided
            double highPrice = Double.MAX_VALUE;

            if (!highPriceField.getText().trim().isEmpty()) {
                try {
                    highPrice = Double.parseDouble(highPriceField.getText().trim());

                    if (highPrice < 0 || highPrice < lowPrice) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    resultsField.setText("High price field should be numeric and positive and higher than the low price.\n");
                    return;
                }
            }
            
            // Perform search
            ArrayList<Investment> investments = portfolio.searchInvestments(symbol, keywords, lowPrice, highPrice);
            
            if(investments.isEmpty()) {
                resultsField.setText("Search returned an empty result.\n");
                return;
            }
            
            resultsField.setText("");
            
            for(Investment investment : investments) {
                resultsField.append(investment.toString() + "\n\n");
            }
        }

        /**
         * Handle actions
         *
         * @param e Event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("Reset")) {
                reset();
            } else if (e.getActionCommand().equalsIgnoreCase("Search")) {
                search();
            }
        }

    }
}
