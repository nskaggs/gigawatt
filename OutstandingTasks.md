# Todo Tasks #

## House Keeping ##
  * Add licence and copyright info to each source file.
  * Add readme and licence into to Root.

## Core Functionality ##
  * ~~Limit Orders.~~
    * ~~Implement an FXOrderManager (along the same lines of FXTradeManager) to track current open limit orders and to process ticks to ensure execution.~~
    * ~~Require new tables?  Yup, orders table added~~
  * ~~FXHistoryManager~~
    * ~~Implement an FXOrderManager that would request a registration of every tick from FXMarketManager and then build up FXHistoryPoints, CandlePoints and MinMaxs for each of the supported.~~
    * ~~Link into the RateTable object so it can request the last 500 (365 for Daily)~~
  * Transactions
    * Decide on Database table or Log file to record all transactions.
    * ~~FXTransaction Manager; Bare bones done.~~
    * Build a list of all possible Transactions that can occur and link them in at various points of execution.
    * Map various transaction types to completion codes and string representation.  E.g. Closing a trade could be due to Close Market, Order Filled, Close Position, Margin Call etc.
  * Interest Calculations
    * Interest calculation per trade closure in shared Account.closeTrade().
    * Interest calculation per trade day on account balance
    * ~~Decide a good way to execute a per day COB interest calculation.  This can now be done via TimeEvents in TimeServer~~

## Post Functionality ##
  * Improve error handling where it currently just prints stack traces.
  * Improve comments and javadocs.
    * By improving, I mean actually having some ;)
  * Optimisation
    * Iterations
    * Margin Call calculations.
  * EventManager mode where strategies are called via EventManager on rates and account actions instead of polling method.
    * Some input from API users probably required.
  * Refactoring to more maintainable, encapsulated code.
    * Rethink method of UtilAPI.
    * Change Account to stub class with composition and implement FXAccount
  * Improve test coverage, especially around calculations.
    * This probably should not be post functionality.
  * Parameters being passed into strategies via xml config.
  * ~~Oanda FXTick data MarketData reader.~~
  * Engine hook process.
    * ~~Add to deinit() to current and future engine processes.~~
  * Statistics calculator
  * ~~Time Events.  TimeServer allows for TimeEvents to be executed at specific date/times with ability for recurrances every X secs~~
  * ~~end date functionality to stop back test before end of ticks.~~

## Client side framework ##
  * Easy reuse of strategies with params.
  * Framework for logging, tick/ohlc capture.
  * TA-Liv technical indicator processing from FXHistoryPoints
  * Failover/Restarting - CoLocation module too?
  * Email and SMS events.




