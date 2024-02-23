package commons;

import java.util.Objects;

public class Currency {

    private int cents;

    private Currencies currency;

    private int convertedValue;


    private enum Currencies{
        USD(1.0),
        EUR(0.92),
        CHF(0.88);

        public double ConversionRateThroughUSD;

        /**
         * Initializes the currencies with associated rate
         * @param rate Exchange rate (hardcoded for now)
         */
        private Currencies(double rate){
            this.ConversionRateThroughUSD = rate;
        }

        public double getConversionRate(){
            return this.ConversionRateThroughUSD;
        }

        public double getConversionRate(String currency){
            switch(currency){
                case "USD":
                    return Currencies.USD.getConversionRate();
                case "EUR":
                    return Currencies.EUR.getConversionRate();
                default:
                    return Currencies.CHF.getConversionRate();
            }
        }
    }
    /**
     *
     * @param cents Current amount of money in cents
     * @param currency Currency in which the expense is paid in
     */
    public Currency(int cents, String currency){
        this.cents = cents;
        this.currency = stringToCurrency(currency);
        this.convertedValue = cents;
    }

    /**
     * Takes an input in decimal form and converts it to a cent-based integer format
     * @param money Current amount of money in decimal form
     * @param currency Currency in which the expense is paid in
     */
    public Currency(double money, String currency){
        this.cents = (int) (money * 100);
        this.currency = stringToCurrency(currency);
        this.convertedValue = (int) money * 100;
    }

    public Currencies stringToCurrency(String currency){
        switch(currency){
            case "USD":
                return Currencies.USD;
            case "EUR":
                return Currencies.EUR;
            default:
                return Currencies.CHF;
        }
    }

    /**
     * Gets amount of cents of instance
     * @return amount of cents
     */
    public int getCents() {
        return cents;
    }

    /**
     * Sets amount of cents of instance
     * @param cents takes an amount of cents and sets the instance to that value
     */
    public void setCents(int cents) {
        this.cents = cents;
    }

    /**
     * Gets the currency in which the expense has been paid
     * @return currency in which the expense has been paid
     */
    public String getCurrency() {
        return currency.name();
    }

    /**
     * Gets the associated converted value of the originally inputted
     * @return converted value
     */
    public double getConvertedValue() {
        return convertedValue;
    }

    /*The conversion below is not nearly what the final version should be. As we have to get the exchangerates via the
    server (and store it in a local cache), and also should be able to query not only the current exchangerates but also
    exchangerates of the past.*/

    /**
     * Way too simplified version of the conversion scheme
     * @param conversionValuta input a valuta to exchange to
     */
    public void conversion(String conversionValuta){
        if (this.currency == stringToCurrency(conversionValuta)){
            return;
        }
        this.convertedValue = (int) (this.conversionToDollar() * currency.getConversionRate(conversionValuta));
        this.currency = stringToCurrency(conversionValuta);
    }

    public int conversionToDollar(){
        if (this.currency == Currencies.USD){
            return this.cents;
        }
        return (int) (this.cents * (1/this.currency.getConversionRate()));
    }

    /**
     * Compares two currencies and checks if they are equal
     * @param o other currency to compare
     * @return boolean outcome of the comparison
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency1 = (Currency) o;
        return getCents() == currency1.getCents() && getConvertedValue() == currency1.getConvertedValue() && getCurrency().equals(currency1.getCurrency());
    }

    /**
     * Returns a unique hashcode for each currency
     * @return integer hashcode of a currency object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getCents(), getCurrency(), getConvertedValue());
    }

    @Override
    public String toString() {
        double stringValue = (double) this.cents/100;
        return stringValue + " " + this.currency.name();
    }
}
