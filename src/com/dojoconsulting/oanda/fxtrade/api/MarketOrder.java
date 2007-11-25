package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.BackTestToolException;

/**
 * A MarketOrder is used to create a spot trade.
 */
public final class MarketOrder extends Order implements Cloneable {

    private int transactionLink;
    private boolean closed;
    private MarketOrder closePrice;

    public MarketOrder() {
    }

    public MarketOrder getClose() {
        return closePrice;
        //todo: getClose() + setClose() - Is this the correct implementation?
    }

    public double getUnrealizedPL(final FXTick tick) {
        double currentRate;
        if (isLong()) {
            currentRate = tick.getBid();
        }
        else {
            currentRate = tick.getAsk();
        }
        double profit = (currentRate - getPrice()) * getUnits();
        return UtilMath.round(profit, 5);
        // todo:  What does this method return if the trade is closed?
    }

    public int getTransactionLink() {
        return transactionLink;
    }

    public double getRealizedPL() {
        if (!closed) {
            return 0;
        }
        double profit = (closePrice.getPrice() - getPrice()) * getUnits();
        return UtilMath.round(profit, 5);
    }

    public String toString() {
        return super.toString();
        //todoproper: Implement toString()
    }


    public Object clone() {
        final MarketOrder order;
        try {
            order = (MarketOrder) super.clone();
            order.setHighPriceLimit(getHighPriceLimit());
            order.setLowPriceLimit(getLowPriceLimit());
            order.setPair((FXPair) getPair().clone());
            order.setPrice(getPrice());
            if (getStopLoss() != null) {
                order.setStopLoss((StopLossOrder) getStopLoss().clone());
            }
            if (getTakeProfit() != null) {
                order.setTakeProfit((TakeProfitOrder) getTakeProfit().clone());

            }
            order.setTimestamp(getTimestamp());
            order.setTransactionNumber(getTransactionNumber());
            order.setUnits(getUnits());
            return order;
        }
        catch (CloneNotSupportedException e) {
            throw new BackTestToolException("Problem cloning MarketOrder");
        }
    }

    void validate(FXTick tick) throws OAException {
        if (isShort()) {
            setPrice(tick.getBid());
        } else if (isLong()) {
            setPrice(tick.getAsk());
        } else {
            throw new OAException("You cannot execute a MarketOrder with 0 units.");
        }
        validateOrders();
    }

    void validateOrders() throws OAException {
        final StopLossOrder stopLoss = getStopLoss();
        final TakeProfitOrder takeProfit = getTakeProfit();
        final double price = getPrice();
        if (stopLoss != null) {
            // If this is a sell, the stopLoss needs to be greater than the execution price
            if (isShort()) {
                if (stopLoss.getPrice() <= price) {
                    throw new OAException("Stop loss must be above quote ( " + price + ")");
                }
            }
            // If this is a buy, the stopLoss needs to be less than than the execution price
            if (isLong()) {
                if (stopLoss.getPrice() >= price) {
                    throw new OAException("Stop loss must be below quote ( " + price + ")");
                }
            }
        }
        if (takeProfit != null) {
            // If this is a sell, the takeProfit needs to be less than the execution price
            if (isShort()) {
                if (takeProfit.getPrice() >= price) {
                    throw new OAException("Take profit must be below quote ( " + price + ")");
                }
            }
            // If this is a buy, the stopLoss needs to be greater than the execution price
            if (isLong()) {
                if (takeProfit.getPrice() <= price) {
                    throw new OAException("Take profit must be above quote ( " + price + ")");
                }
            }
        }
    }

    boolean isLong() {
        return getUnits() > 0;
    }

    boolean isShort() {
        return getUnits() < 0;
    }

    void setClose(final MarketOrder closePrice) {
        this.closePrice = closePrice;
        closed = true;
    }

    void setTransactionLink(final int transactionLink) {
        this.transactionLink = transactionLink;
    }
}