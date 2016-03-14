package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import com.blackenedsystems.sportsbook.data.betfair.BetfairDataMappingService;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 14/03/16
 */
@Named("BetfairMarketTypesActor")
@Scope("prototype")
public class BetfairMarketTypesActor extends AbstractActor {

    @Autowired
    private BetfairDataMappingService betfairDataMappingService;

    public BetfairMarketTypesActor() {
        receive(
                ReceiveBuilder
                        .match(ProcessMarketTypes.class, pmt -> {
                            betfairDataMappingService.processMarketTypeList(pmt.marketTypeList);
                        })
                        .build()
        );
    }

    public static class ProcessMarketTypes {
        public final List<MarketType> marketTypeList;

        public ProcessMarketTypes(final List<MarketType> marketTypeList) {
            this.marketTypeList = marketTypeList;
        }
    }
}
