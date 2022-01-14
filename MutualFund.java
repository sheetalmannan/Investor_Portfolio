package ePortfolio;

/**
 * An investment program funded by shareholders that trades in diversified
 * holdings and is professionally managed.
 *
 * @author Me
 */
public class MutualFund extends Investment{

    /**
     * Cost that is charged to a shareholder when they will have to sell the
     * share back to the fund manager.
     */
    private static final double DISPOSE_FEE = 45;

    /**
     * Initialize a mutual fund. The book value will be automatically calculated
     * based on price and quantity and no commissions involved.
     *
     * @param fundSymbol A ticker code that uniquely identifies the mutual fund
     * @param fundName Registered name of the mutual fund in the exchange
     * @param fundQuantity Quantity purchased by a shareholder
     * @param fundPrice Price spent by the shareholder to purchase this number
     * of mutual fund shares
     * @throws IllegalArgumentException If any of the fields are in violation
     */
    public MutualFund(String fundSymbol, String fundName, int fundQuantity, double fundPrice) {
        super(fundSymbol, fundName, fundQuantity, fundPrice);
    }

    /**
     * Initialize a mutual fund. The book value will be automatically calculated
     * based on price and quantity and no commissions involved.
     *
     * @param fundSymbol A ticker code that uniquely identifies the mutual fund
     * @param fundName Registered name of the mutual fund in the exchange
     * @param fundQuantity Quantity purchased by a shareholder
     * @param fundPrice Price spent by the shareholder to purchase this number
     * of mutual fund shares
     * @param bookValue Market value of the investment.
     * @throws IllegalArgumentException If any of the fields are in violation
     */
    public MutualFund(String fundSymbol, String fundName, int fundQuantity, double fundPrice, double bookValue) {
        super(fundSymbol, fundName, fundQuantity, fundPrice, bookValue);
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
        return super.reduce(reduceQuantity) - DISPOSE_FEE;
    }

    /**
     * Return the relevant information of this class that can read as text.
     *
     * @return Details
     */
    @Override
    public String toString() {
        String str = "Type: Mutual Fund\n";
        str += super.toString();
        return str;
    }
}
