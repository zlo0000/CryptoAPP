package com.test.cryptoapp;

// type
// 1 - catalog
// 2 - item


import java.io.Serializable;

public class ListItem implements Serializable {


    int type;

    int id;
    int id_cat;

	String name;
	String desc;

	String img;

    int state;

    int price;
    int price2;

    //{"id":"BCNBTC","baseCurrency":"BCN","quoteCurrency":"BTC","quantityIncrement":"100","tickSize":"0.0000000001","takeLiquidityRate":"0.001","provideLiquidityRate":"-0.0001","feeCurrency":"BTC"}

    String symbol_id;
    String baseCurrency;
    String quoteCurrency;

    String data;


    //{"timestamp":"2018-01-21T00:00:00.000Z","open":"12719.35","close":"11493.69","min":"10983.43","max":"12723.86","volume":"6307.35","volumeQuote":"74512279.4649"}

    String timestamp;
    String p_open;
    String p_close;
    String p_max;
    String p_min;


}
