package com.wikia.wikiaoriginals.service.requests;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.wikia.wikiaoriginals.service.RetrofitService;
import com.wikia.wikiaoriginals.service.model.Wiki;
import com.wikia.wikiaoriginals.service.model.WikiSummary;
import com.wikia.wikiaoriginals.service.model.WikiSummaryList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wikia.wikiaoriginals.service.requests.RequestPopularWikis.PopularWikis;

/**
 * Defines request parameters and calls of @RetrofitService for getting popular wikis(max 25 currently).
 * It sends two requests(getList, getDetail) to get popular wikis from the Rest Api
 */
public class RequestPopularWikis extends RetrofitSpiceRequest<PopularWikis, RetrofitService>{
    private final int batch;

    /**
     * Retrieves max 25 popular wikis with details
     * @param batch Number of the batch. Starts with 1.
     */
    public RequestPopularWikis(int batch) {
        super(PopularWikis.class, RetrofitService.class);
        this.batch = batch;
    }

    /**
     * This method makes two requests. One after another.
     * First it requests popular wikis in summary(getList)
     * Then it requests details of these wikis(getDetail)
     */
    @Override
    public PopularWikis loadDataFromNetwork() throws Exception {
        final WikiSummaryList wikiSummaryList = getService().wikiSummaries(RetrofitService.CONTROLLER, RetrofitService.METHOD_GETLIST, batch);
        final List<WikiSummary> wikiSummaries = wikiSummaryList.getItems();

        //First we request list of popular wikis
        //Then we put ids of these wikis in a String to get their details
        final StringBuilder sbIdList = new StringBuilder();
        for(int i=0; i<wikiSummaries.size(); i++){
            sbIdList.append(wikiSummaries.get(i).getId());
            if(i != wikiSummaries.size() - 1)
                sbIdList.append(",");
        }

        //Get details of these wikis
        final Map<Integer, Wiki> wikiMap = getService().wikis(RetrofitService.CONTROLLER, RetrofitService.METHOD_GETDETAILS, sbIdList.toString()).getItems();

        //Put these wikis in a order which is from @wikiSummaryList
        final List<Wiki> wikis = new ArrayList<>();
        for(WikiSummary wikiSummary : wikiSummaries)
            wikis.add(wikiMap.get(wikiSummary.getId()));

        return new PopularWikis(wikis, wikiSummaryList.getCurrentBatch(), wikiSummaryList.getNext());
    }

    /**
     * POJO to put two service requests(getList and getDetail) results together to send
     * as a single result for the retrofit request callback
     */
    public static final class PopularWikis implements Serializable{
        private final List<Wiki> wikis;
        private final int currentbatch;
        private final int next;

        public PopularWikis(List<Wiki> wikis, int currentbatch, int next) {
            this.wikis = wikis;
            this.currentbatch = currentbatch;
            this.next = next;
        }

        public List<Wiki> getWikis() {
            return wikis;
        }

        public int getCurrentbatch() {
            return currentbatch;
        }

        public int getNext() {
            return next;
        }
    }
}
