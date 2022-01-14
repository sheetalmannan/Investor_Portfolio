package ePortfolio;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * An investor's portfolio for managing their stocks and mutual funds
 *
 * @author Me
 */
public class Portfolio {

    /**
     * For user input purposes.
     */
    private static Scanner in = new Scanner(System.in);

    /**
     * List to store the investments bought by an investor.
     */
    private ArrayList<Investment> investments;

    /**
     * File name to store investments list
     */
    private String fileName;

    /**
     * Map, storing investment indices of name keywords
     */
    Map<String, List<Integer>> indexMap;

    /**
     * Initialize investor's portfolio.
     */
    public Portfolio(String filename) {
        investments = new ArrayList<>();
        this.fileName = filename;
        this.indexMap = new HashMap<>();
        tryLoad();
    }

    /**
     * Return the list of investments.
     *
     * @return List of investments
     */
    public ArrayList<Investment> getInvestments() {
        return new ArrayList<>(investments);
    }

    /**
     * Adds given investment to the investments list, updating index map
     *
     * @param investment to add
     */
    public void addToList(Investment investment) {
        int index = investments.size();
        String[] tokens = investment.getName().split("\\s+");
        for (String token : tokens) {
            indexMap.merge(token.toLowerCase(), new ArrayList<>(Collections.singleton(index)), (a, b) -> {
                a.addAll(b);
                return a;
            });
        }
        investments.add(investment);
    }

    /**
     * Removes given investment from the investments list, updating index map
     *
     * @param investment to add
     */
    public void removeFromList(Investment investment) {
        int index = investments.indexOf(investment);
        if (index < 0) {
            throw new IllegalStateException();
        }

        Map<String, List<Integer>> updatedIndexMap = new HashMap<>();
        for (String key : indexMap.keySet()) {
            List<Integer> curr = indexMap.get(key);
            List<Integer> updated = new ArrayList<>();
            for (int i : curr) {
                if (i < index) {
                    updated.add(i);
                } else if (i > index) {
                    updated.add(i - 1);
                }
            }
            if (updated.size() > 0) {
                updatedIndexMap.put(key, updated);
            }
        }
        indexMap = updatedIndexMap;
        investments.remove(index);
    }

    /**
     * Find the stock by symbol.
     *
     * @param symbol Target symbol
     * @return Stock
     */
    public Stock findStock(String symbol) {
        for (Investment investment : investments) {
            if (investment instanceof Stock && investment.getSymbol().equalsIgnoreCase(symbol)) {
                return (Stock) investment;
            }
        }

        return null;
    }

    /**
     * Find the mutual fund by symbol.
     *
     * @param symbol Target symbol
     * @return Mutual fund
     */
    public MutualFund findMutualFund(String symbol) {
        for (Investment investment : investments) {
            if (investment instanceof MutualFund && investment.getSymbol().equalsIgnoreCase(symbol)) {
                return (MutualFund) investment;
            }
        }

        return null;
    }

    /**
     * Found appropriate collection of investments by key filter
     *
     * @param keyFilter Keywords to find
     * @return list of matching investments
     */
    private List<Investment> getInvestmentsByKeyFilter(String keyFilter) {
        String[] keys = keyFilter.split("\\s+");
        Collection<Integer> foundInvestmentIndices = null;
        for (String key : keys) {
            if (foundInvestmentIndices == null) {
                foundInvestmentIndices = new ArrayList<>(indexMap.get(key.toLowerCase()));
            } else {
                foundInvestmentIndices = foundInvestmentIndices.stream()
                        .distinct()
                        .filter(indexMap.get(key.toLowerCase())::contains)
                        .collect(Collectors.toList());
            }
        }

        List<Investment> result = new ArrayList<>();
        for (int i : foundInvestmentIndices) {
            result.add(investments.get(i));
        }
        return result;
    }

    /**
     * Find all investments that matches the given search criteria.
     *
     * @param symbolFilter Target symbol
     * @param keyFilter Target keywords
     * @param lowPrice Target minimum price
     * @param highPrice Target maximum price
     * @return List of matching investment
     */
    public ArrayList<Investment> searchInvestments(String symbolFilter, String keyFilter, double lowPrice, double highPrice) {
        ArrayList<Investment> foundInvestments = new ArrayList<>();

        // Extract by keywords
        if (!keyFilter.isEmpty()) {
            foundInvestments.addAll(getInvestmentsByKeyFilter(keyFilter));
        } else {
            foundInvestments.addAll(investments);
        }

        // Extract by symbol
        if (!symbolFilter.isEmpty()) {
            for (Investment investment : investments) {
                if (!investment.getSymbol().equalsIgnoreCase(symbolFilter)) {
                    foundInvestments.remove(investment);
                }
            }
        }

        // Extract by price range
        for (int i = investments.size() - 1; i >= 0; i--) {
            Investment investment = investments.get(i);

            if (investment.getPrice() < lowPrice || investment.getPrice() > highPrice) {
                foundInvestments.remove(i);
            }
        }
        
        return foundInvestments;
    }

    private void tryLoad() {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                boolean isStock = false;
                String line = scanner.nextLine();
                String tmp;
                tmp = line.split("=")[1].trim();
                if (tmp.substring(1, tmp.length() - 1).equals("stock")) {
                    isStock = true;
                }

                line = scanner.nextLine();
                tmp = line.split("=")[1].trim();
                String symbol = tmp.substring(1, tmp.length() - 1);

                line = scanner.nextLine();
                tmp = line.split("=")[1].trim();
                String name = tmp.substring(1, tmp.length() - 1);

                line = scanner.nextLine();
                tmp = line.split("=")[1].trim();
                int quantity = Integer.parseInt(tmp.substring(1, tmp.length() - 1));

                line = scanner.nextLine();
                tmp = line.split("=")[1].trim();
                double price = Double.parseDouble(tmp.substring(1, tmp.length() - 1));

                line = scanner.nextLine();
                tmp = line.split("=")[1].trim();
                double bookValue = Double.parseDouble(tmp.substring(1, tmp.length() - 1));

                if (isStock) {
                    addToList(new Stock(symbol, name, quantity, price, bookValue));
                } else {
                    addToList(new MutualFund(symbol, name, quantity, price, bookValue));
                }

                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Cannot open file '" + fileName + "'");
        }
    }

    public void trySave() {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (Investment investment : investments) {
                if (investment instanceof Stock) {
                    writer.println("type = \"stock\"");
                } else {
                    writer.println("type = \"mutualfund\"");
                }
                writer.println("symbol = \"" + investment.getSymbol() + "\"");
                writer.println("name = \"" + investment.getName() + "\"");
                writer.println("quantity = \"" + investment.getQuantity() + "\"");
                writer.println("price = \"" + String.format("%.2f", investment.getPrice()) + "\"");
                writer.println("bookvalue = \"" + String.format("%.2f", investment.getBookValue()) + "\"");
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Cannot open file '" + fileName + "'");
        }
    }

    /**
     * Start the portfolio program
     *
     * @param args Unused arguments
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        String filename = "input.txt";

        if (args.length > 0) {
            filename = args[0];
        }

        Portfolio portfolio = new Portfolio(filename);
        new PortfolioFrame(portfolio).setVisible(true);
    }
}
