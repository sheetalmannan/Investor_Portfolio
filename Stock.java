package ePortfolio;

import java.text.DecimalFormat;

/**
 * An investment where shareholders buy a piece of the business.
 *
 * @author Me
 */
public class Stock extends Investment {

    /**
     * Cost that a share holder has to pay whenever they buy or sell shares in
     * the market.
     */
    private final static double EXCHANGE_FEE = 9.99;

    /**
     * Initialize a stock. The book value will be automatically calculated based
     * on price and quantity and no commissions involved.
     *
     * @param stockSymbol A ticker code that uniquely identifies the stock
     * @param stockName Registered name of the stock in the exchange
     * @param stockQuantity Quantity purchased by a shareholder
     * @param stockPrice Price spent by the shareholder to purchase this number
     * of shares
     * @throws IllegalArgumentException If any of the fields are in violation
     */
    public Stock(String stockSymbol, String stockName, int stockQuantity, double stockPrice) {
        super(stockSymbol, stockName, stockQuantity, stockPrice);
        bookValue += EXCHANGE_FEE;
    }

    /**
     * Initialize a stock. The book value will be automatically calculated based
     * on price and quantity and no commissions involved.
     *
     * @param stockSymbol A ticker code that uniquely identifies the stock
     * @param stockName Registered name of the stock in the exchange
     * @param stockQuantity Quantity purchased by a shareholder
     * @param stockPrice Price spent by the shareholder to purchase this number
     * of shares
     * @param bookValue Market value of the investment.
     * @throws IllegalArgumentException If any of the fields are in violation
     */
    public Stock(String stockSymbol, String stockName, int stockQuantity, double stockPrice, double bookValue) {
        super(stockSymbol, stockName, stockQuantity, stockPrice, bookValue);
    }

    /**
     * Purchase additional number of shares using the stored price. Book value
     * will be adjusted accordingly.
     *
     * @param additionalQuantity Additional quantity to purchase
     * @throws IllegalArgumentException If quantity is invalid.
     */
    public void add(int additionalQuantity) {
        super.add(additionalQuantity);
        bookValue += EXCHANGE_FEE;
    }

    /**
     * Sell a number of quantity of the stock at the current price
     *
     * @param reduceQuantity Quantity to sell
     * @return If sell was successful
     * @throws IllegalArgumentException If quantity is invalid
     */
    public double reduce(int reduceQuantity) {
        return super.reduce(reduceQuantity) - EXCHANGE_FEE;
    }

    /**
     * Return a string representation of the stock
     *
     * @return Stock information
     */
    @Override
    public String toString() {
        String str = "Type: Stock\n";
        str += super.toString();
        return str;
    }
}
