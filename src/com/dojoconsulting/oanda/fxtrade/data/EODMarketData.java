package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.oanda.fxtrade.api.FXPair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 07:55:52
 * EODMarketData expects a file that has data in the format of dd.mm.yyyy,price
 * <p/>
 * To change the date format, extend this class and call setFormatter from the constructor.  Ensure you call super(FXPair pair, String path)
 */
public class EODMarketData extends GenericFXMarketDataReader {

    public EODMarketData(final FXPair pair, final String path) {
        super(pair, path);
    }

    protected String[] processRecord(final String record) {
        final String[] tokens = record.split(",");
        return new String[]{tokens[0], tokens[1], tokens[1]};
    }

    protected DateFormat getDateFormatter() {
        return new SimpleDateFormat("dd.MM.yyyy");
    }

}
