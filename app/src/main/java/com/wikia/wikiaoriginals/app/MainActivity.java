package com.wikia.wikiaoriginals.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.squareup.picasso.Picasso;
import com.wikia.wikiaoriginals.R;
import com.wikia.wikiaoriginals.service.JsonRetrofitSpiceService;
import com.wikia.wikiaoriginals.service.model.Wiki;
import com.wikia.wikiaoriginals.service.requests.RequestPopularWikis;
import com.wikia.wikiaoriginals.view.XYImageView;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static com.wikia.wikiaoriginals.service.requests.RequestPopularWikis.PopularWikis;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity{
    private final static String STATE_POPULAR_WIKIS = "PopularWikis";

    protected SpiceManager spiceManager = new SpiceManager(JsonRetrofitSpiceService.class);
    protected WikiListAdapter wikiListAdapter = null;

    private boolean isGettingNewWikis = false;

    @InjectView(R.id.rv_wiki_list) RecyclerView rvWikiList;
    @InjectView(R.id.ll_connection_error) LinearLayout llConnectionError;
    @InjectView(R.id.b_try_again) Button bTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        rvWikiList.setHasFixedSize(true);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            rvWikiList.setLayoutManager(new GridLayoutManager(this, 2));
        else
            rvWikiList.setLayoutManager(new GridLayoutManager(this, 1));

        //If state is not restored, request a new page
        if(!restoreState(savedInstanceState))
            requestNewPage();

        bTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewPage();
                setConnectionError(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //SAVE Popular Wikis
        if(wikiListAdapter != null)
            outState.putSerializable(STATE_POPULAR_WIKIS, wikiListAdapter.getData());
    }

    /**
     * Check if the state of the activity is stored because of restarting(orientation change, activity restarts)
     * @param savedInstanceState Saved instance bundle
     * @return
     */
    private boolean restoreState(Bundle savedInstanceState){
        if(savedInstanceState != null){
            wikiListAdapter = new WikiListAdapter(this, (PopularWikis)savedInstanceState.getSerializable(STATE_POPULAR_WIKIS));
            rvWikiList.setAdapter(wikiListAdapter);
            return true;
        }
        return false;
    }

    /**
     * It gets new items for next batch. Also checks if it is already waiting a respond
     * to prevent sending multiple requests same time
     */
    private void requestNewPage(){
        if(isGettingNewWikis) return;

        //Determine next batch number
        final int batch = wikiListAdapter == null ? 1 : wikiListAdapter.getData().getCurrentbatch() + 1;

        sendRequestToSpice(new RequestPopularWikis(batch), new RequestListener<PopularWikis>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                isGettingNewWikis = false;
                setConnectionError(true);
            }

            @Override
            public void onRequestSuccess(PopularWikis popularWikis) {
                if(wikiListAdapter == null)
                    rvWikiList.setAdapter(wikiListAdapter = new WikiListAdapter(MainActivity.this, popularWikis));
                else
                    wikiListAdapter.update(popularWikis);
                isGettingNewWikis = false;
            }
        });

        isGettingNewWikis = true;
    }

    private void sendRequestToSpice(RetrofitSpiceRequest request, RequestListener listener){
        spiceManager.execute(request, listener);
    }

    /**
     * Manages the visibility of connection error box
     * @param visible
     */
    private void setConnectionError(boolean visible){
        if(visible)
            llConnectionError.setVisibility(View.VISIBLE);
        else
            llConnectionError.setVisibility(View.GONE);
    }

    /**
     * Adapter of the layout.
     * It defines two view types(Wiki and Loading).
     */
    protected static final class WikiListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final static int VIEW_TYPE_WIKI = 0;
        private final static int VIEW_TYPE_LOADING = 1;

        private final MainActivity activity;
        private final LayoutInflater layoutInflater;
        private PopularWikis popularWikis;

        private WikiListAdapter(MainActivity activity, PopularWikis popularWikis) {
            this.activity = activity;
            this.layoutInflater = LayoutInflater.from(activity);
            this.popularWikis = popularWikis;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case VIEW_TYPE_WIKI:
                    return new WikiViewHolder(layoutInflater.inflate(R.layout.main_wiki_item, parent, false));
                default:
                    return new LoadingViewHolder(layoutInflater.inflate(R.layout.main_wiki_item_loading, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)){
                case VIEW_TYPE_WIKI: {
                    final WikiViewHolder wikiViewHolder = (WikiViewHolder) holder;
                    final Wiki wiki = popularWikis.getWikis().get(position);

                    //Get Thumbnail
                    wikiViewHolder.ivThumbnail.setImageDrawable(null);
                    if(wiki.getImage() != null && !wiki.getImage().isEmpty())
                        Picasso.with(activity).load(wiki.getImage()).resize(480, 240).centerCrop().into(wikiViewHolder.ivThumbnail);

                    //Set Title and url
                    wikiViewHolder.tvTitle.setText(wiki.getTitle());
                    wikiViewHolder.tvWikiUrl.setText(wiki.getUrl());

                    //Open browser when item clicked
                    wikiViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wiki.getUrl()));
                            activity.startActivity(browserIntent);
                        }
                    });
                    break;
                }
                case VIEW_TYPE_LOADING:
                    activity.requestNewPage();
                    break;
            }
        }

        @Override
        public int getItemCount() {
            final int size = popularWikis.getWikis().size();
            return popularWikis.getNext() > 0 ? size + 1 : size;
        }

        @Override
        public int getItemViewType(int position) {
           return position == popularWikis.getWikis().size() ? VIEW_TYPE_LOADING : VIEW_TYPE_WIKI;
        }

        public void update(PopularWikis popularWikis){
            final int oldSize = this.popularWikis.getWikis().size();
            popularWikis.getWikis().addAll(0, this.popularWikis.getWikis());
            this.popularWikis = popularWikis;
            notifyItemRangeChanged(oldSize, popularWikis.getWikis().size());
        }

        public PopularWikis getData(){
            return popularWikis;
        }
    }

    /**
     * View holder for wiki items on the list
     */
    private static final class WikiViewHolder extends RecyclerView.ViewHolder{
        public XYImageView ivThumbnail;
        public TextView tvTitle;
        public TextView tvWikiUrl;

        public WikiViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = (XYImageView)itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
            tvWikiUrl = (TextView)itemView.findViewById(R.id.tv_wiki_url);
        }
    }

    /**
     * View Holder for the loading item will be showed end of the list
     */
    private static final class LoadingViewHolder extends RecyclerView.ViewHolder{
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}