package ePortfolio;

import java.text.DecimalFormat;

/**
 * An abstract common parent class for all type of investments (stocks, funds)
 */
public abstract class Investment {

    /**
     * A ticker code that uniquely identifies the investment.
     */
    private final String symbol;

    /**
     * Registered name of the investment in the exchange.
     */
    private final String name;

    /**
     * Quantity purchased by a shareholder.
     */
    private int quantity;

    /**
     * Price spent by the shareholder to purchase this number of shares.
     */
    private double price;

    /**
     * Market value of the investment.
     */
    protected double bookValue;

    /**
     * Initialize an investment. The book value will be automatically calculated
     * based on price and quantity and no commissions involved.
     *
     * @param symbol A ticker code that uniquely identifies the investment
     * @param name Registered name of the investment in the exchange
     * @param quantity Quantity purchased by a shareholder
     * @param price Price spent by the shareholder to purchase this number of
     * shares
     * @throws IllegalArgumentException If any of the fields are in violation
     */
    public Investment(String symbol, String name, int quantity, double price) {
        if (symbol.isEmpty()) {
            throw new IllegalArgumentException("Symbol field is required.");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name field is required.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Positive quantity is required.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Price is required.");
        }

        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        bookValue = price * quantity;
    }

    /**
     * Initialize an investment. The book value will be automatically calculated
     * based on price and quantity and no commissions involved.
     *
     * @param symbol A ticker code that uniquely identifies the investment
     * @param name Registered name of the investment in the exchange
     * @param quantity Quantity purchased by a shareholder
     * @param price Price spent by the shareholder to purchase this number of
     * shares
     * @param bookValue Market value of the investment.
     * @throws IllegalArgumentException If any of the fields are in violation
     */
    public Investment(String symbol, String name, int quantity, double price, double bookValue) {
        if (symbol.isEmpty()) {
            throw new IllegalArgumentException("Symbol field is required.");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name field is required.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Positive quantity is required.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Price is required.");
        }

        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.bookValue = bookValue;
    }

    /**
     * Purchase additional number of shares using the stored price. Book value
     * will be adjusted accordingly.
     *
     * @param additionalQuantity Additional quantity to purchase
     * @throws IllegalArgumentException If quantity is invalid.
     */
    public void add(int additionalQuantity) {
        if (additionalQuantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity.");
        }

        quantity += additionalQuantity;
        bookValue += additionalQuantity * price;
    }

    /**
     * Reduce the number of shares. The shares will be sold back to the fund
     * manager in exchange of cash based on current prices.
     *
     * @param reduceQuantity Number of shares to dispose
     * @return Total amount converted to cash
     * @throws IllegalArgumentException If the quantity is invalid or the number
     * of shares available is not enough
     */
    public double reduce(int reduceQuantity) {
        if (reduceQuantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity.");
        }

        if (reduceQuantity > quantity) {
            throw new IllegalArgumentException("Insufficient shares.");
        }

        int previousQuantity = quantity;

        quantity -= reduceQuantity;
        bookValue *= (double) quantity / previousQuantity;

        return reduceQuantity * price;
    }

    /**
     * Get the gain or loss amount depending on current prices.
     *
     * @return A gain or loss amount
     */
    public double computeUnrealizedGainOrLoss() {
        return quantity * price - bookValue;
    }

    /**
     * Update the price.
     *
     * @param price Updated market price of the investment.
     * @throws IllegalArgumentException If fund price is invalid
     */
    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Positive investment price is required.");
        }

        this.price = price;
    }

    /**
     * Return the ticker symbol.
     *
     * @return Symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Return the name.
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the quantity.
     *
     * @return Quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Return the price.
     *
     * @return Price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Return the book value.
     *
     * @return Book Value
     */
    public double getBookValue() {
        return bookValue;
    }

    /**
     * Return the relevant information of this class that can read as text.
     *
     * @return Details
     */
    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);

        String str = "";
        str += "Symbol: " + symbol + "\n";
        str += "Name: " + name + "\n";
        str += "Shares: " + quantity + "\n";
        str += "Price: $" + decimalFormat.format(price) + "\n";
        str += "Book Value: $" + decimalFormat.format(bookValue) + "\n";
        str += "Unrealized Gain/Loss: $" + decimalFormat.format(computeUnrealizedGainOrLoss());

        return str;
    }
}
