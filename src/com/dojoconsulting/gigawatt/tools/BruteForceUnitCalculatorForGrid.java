package com.dojoconsulting.gigawatt.tools;

import java.util.ArrayList;
import java.util.List;

public class BruteForceUnitCalculatorForGrid {
	private static final double MARGIN_RATE = 0.02;

	public static void main(final String[] args) {
		final BruteForceUnitCalculatorForGrid tool = new BruteForceUnitCalculatorForGrid();
		// GBP/USD with GBP Account  (quoteToHome = USD/GBP, baseToHome = GBP/GBP)
		tool.calculate("GBP/USD", 2.0000, 1.7000, 1000.00, 0.5012, 1.0, 100);

//		 GBP/USD with USD Account  (quoteToHome = USD/USD, baseToHome = GBP/USD)
//		tool.calculate("GBP/USD", 2.0000, 1.7000, 1000.00, 1.0, 2.0, 100);

		// GBP/JPY with GBP Account  (quoteToHome = JPY/GBP, baseToHome = GBP/GBP)
//		tool.calculate("GBP/JPY", 230.00, 210.00, 1000.00, 0.0044, 1.0, 100);

		// GBP/JPY with USD Account  (quoteToHome = JPY/USD, baseToHome = GBP/USD)
//		tool.calculate("GBP/JPY", 230.00, 250.00, 1000.00, 0.0088, 2.00, 100);
	}

	private void calculate(final String ccy, final double startPrice, final double endPrice, final double accountBalance, final double quoteToHomePrice, final double baseToHomePrice, final int pipSpacing) {
		int pipPrecision = 10000;
		if (ccy.contains("JPY")) {
			pipPrecision = 100;
		}

		double priceMovement = ((double) pipSpacing) / ((double) pipPrecision);
		final boolean goingLong = (startPrice > endPrice);
		if (goingLong) {
			priceMovement = priceMovement * -1;
		}

		final int pipRange = (int) (Math.abs(startPrice - endPrice) * pipPrecision);

		final int numberOfOrders = (pipRange / pipSpacing);

		final List<Trade> trades = new ArrayList<Trade>();
		for (int units = 1; units < accountBalance * 50; units++) {
			double currentPrice = startPrice;
			trades.clear();
			for (int j = 0; j < numberOfOrders; j++) {
				if (j != 0) {
					currentPrice += priceMovement;
				}
				final double nav = calculateNav(trades, accountBalance, currentPrice, quoteToHomePrice, units);
				final double marginCall = calculateMarginCall(trades, baseToHomePrice, units);
				if (nav < marginCall) {
					System.out.println(units + " units would cause a margin call after " + trades.size() + " trades by a price of " + currentPrice);
					return;
				}
				if (canAffordTrade(nav, marginCall, baseToHomePrice, units)) {
					trades.add(createTrade(currentPrice));
				} else {
					System.out.println(units + " units would run out of a margin after " + trades.size() + " trades at price of " + currentPrice + ".  Nav [" + nav + "] MC [" + marginCall + "]");
					return;
				}
			}
			final double nav = calculateNav(trades, accountBalance, currentPrice, quoteToHomePrice, units);
			final double marginCall = calculateMarginCall(trades, baseToHomePrice, units);
			System.out.println(units + " units would not cause margin call at price " + endPrice + ".  Nav [" + nav + "] MC [" + marginCall + "]");
		}
	}

	private double calculateMarginCall(final List<Trade> trades, final double baseToHomePrice, final int unitSize) {
		return calculateMarginUsed(trades, baseToHomePrice, unitSize) / 2;
	}

	private double calculateMarginUsed(final List<Trade> trades, final double baseToHomePrice, final int unitSize) {
		final double totalUnits = trades.size() * unitSize;
		final double positionValue = baseToHomePrice * totalUnits;
		return positionValue * MARGIN_RATE;
	}

	private boolean canAffordTrade(final double nav, final double marginCall, final double baseToHomePrice, final int unitSize) {
		final double marginUsed = marginCall * 2;
		final double marginLeft = nav - marginUsed;
		final double marginRequired = baseToHomePrice * unitSize * MARGIN_RATE;
		return (marginLeft > marginRequired);
	}

	private double calculateNav(final List<Trade> trades, final double accountBalance, final double currentPrice, final double quotePrice, final int unitSize) {
		double totalPipLoss = 0;
		for (final Trade t : trades) {
			totalPipLoss += t.getPipLoss(currentPrice);
		}
		final double loss = unitSize * quotePrice * totalPipLoss;
		return accountBalance - loss;
	}

	private Trade createTrade(final double currentPrice) {
		final Trade t = new Trade();
		t.price = currentPrice;
		return t;
	}

	private class Trade {
		private double price;

		public double getPipLoss(final double currentPrice) {
			return Math.abs(price - currentPrice);
		}
	}
}
