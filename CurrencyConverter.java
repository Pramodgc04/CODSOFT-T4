
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;

public class CurrencyConverter extends JFrame {

    private JTextField amountField;
    private JComboBox<String> baseCurrencyBox;
    private JComboBox<String> targetCurrencyBox;
    private JLabel resultLabel;

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    public CurrencyConverter() {
        setTitle("Currency Converter");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(20, 20, 100, 25);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(150, 20, 200, 25);
        add(amountField);

        JLabel baseCurrencyLabel = new JLabel("Base Currency:");
        baseCurrencyLabel.setBounds(20, 50, 100, 25);
        add(baseCurrencyLabel);

        String[] currencies = {"USD", "EUR", "INR", "GBP", "AUD", "CAD"};
        baseCurrencyBox = new JComboBox<>(currencies);
        baseCurrencyBox.setBounds(150, 50, 200, 25);
        add(baseCurrencyBox);

        JLabel targetCurrencyLabel = new JLabel("Target Currency:");
        targetCurrencyLabel.setBounds(20, 80, 100, 25);
        add(targetCurrencyLabel);

        targetCurrencyBox = new JComboBox<>(currencies);
        targetCurrencyBox.setBounds(150, 80, 200, 25);
        add(targetCurrencyBox);

        JButton convertButton = new JButton("Convert");
        convertButton.setBounds(150, 110, 100, 30);
        add(convertButton);

        resultLabel = new JLabel("Converted Amount: ");
        resultLabel.setBounds(20, 150, 350, 25);
        add(resultLabel);

        convertButton.addActionListener(e -> {
            try {
                String baseCurrency = (String) baseCurrencyBox.getSelectedItem();
                String targetCurrency = (String) targetCurrencyBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                double exchangeRate = fetchExchangeRate(baseCurrency, targetCurrency);
                double convertedAmount = convertCurrency(amount, exchangeRate);

                resultLabel.setText("Converted Amount: " + String.format("%.2f", convertedAmount) + " " + targetCurrency);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private double fetchExchangeRate(String baseCurrency, String targetCurrency) throws Exception {
        String urlStr = API_URL + baseCurrency;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        conn.disconnect();

        return parseExchangeRate(content.toString(), targetCurrency);
    }

    private double parseExchangeRate(String jsonResponse, String targetCurrency) {
        String targetKey = "\"" + targetCurrency + "\":";
        int targetIndex = jsonResponse.indexOf(targetKey);
        if (targetIndex == -1) {
            throw new RuntimeException("Currency not found in response");
        }
        int startIndex = targetIndex + targetKey.length();
        int endIndex = jsonResponse.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = jsonResponse.indexOf("}", startIndex);
        }
        return Double.parseDouble(jsonResponse.substring(startIndex, endIndex));
    }

    private double convertCurrency(double amount, double exchangeRate) {
        return amount * exchangeRate;
    }

    public static void main(String[] args) {
        new CurrencyConverter();
    

    }
}
