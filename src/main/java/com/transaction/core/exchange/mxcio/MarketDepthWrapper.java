package com.transaction.core.exchange.mxcio;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MarketDepthWrapper {

    private List<MarketDepth> asks = new ArrayList<>();

    private List<MarketDepth> bids = new ArrayList<>();
}
