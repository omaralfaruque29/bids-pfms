package com.bids.pfms.util;

public class CurrencyAmountToWordsConverter {

    private static final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six",
            "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
            "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public static String convertAmountToWords(double amount) {
        long takaPart = (long) amount;
        int paisaPart = (int) Math.round((amount - takaPart) * 100);

        StringBuilder result = new StringBuilder();

        if (takaPart > 0) {
            result.append(convertNumberToWords(takaPart)).append(" Taka");
        }

        if (paisaPart > 0) {
            if (result.length() > 0) result.append(" and ");
            result.append(convertNumberToWords(paisaPart)).append(" Paisa");
        }

        if (result.length() > 0) result.append(" Only");

        return result.toString();
    }

    private static String convertNumberToWords(long number) {
        if (number == 0) return "Zero";

        StringBuilder sb = new StringBuilder();

        if (number >= 10000000) {
            sb.append(convertNumberToWords(number / 10000000)).append(" Crore ");
            number %= 10000000;
        }
        if (number >= 100000) {
            sb.append(convertNumberToWords(number / 100000)).append(" Lakh ");
            number %= 100000;
        }
        if (number >= 1000) {
            sb.append(convertNumberToWords(number / 1000)).append(" Thousand ");
            number %= 1000;
        }
        if (number >= 100) {
            sb.append(convertNumberToWords(number / 100)).append(" Hundred ");
            number %= 100;
        }
        if (number > 0) {
            if (number < 20)
                sb.append(units[(int) number]);
            else {
                sb.append(tens[(int) (number / 10)]);
                if ((number % 10) > 0) {
                    sb.append("-").append(units[(int) (number % 10)]);
                }
            }
        }
        return sb.toString().trim();
    }

}

