package com.leothosthoren.go4lunch.controler.fragments;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.leothosthoren.go4lunch.R;
import com.leothosthoren.go4lunch.adapter.RestaurantRVAdapter;
import com.leothosthoren.go4lunch.base.BaseFragment;
import com.leothosthoren.go4lunch.base.RecyclerViewBuilder;
import com.leothosthoren.go4lunch.model.RestaurantItem;
import com.leothosthoren.go4lunch.utils.HttpRequestTools;
import com.leothosthoren.go4lunch.utils.ItemClickSupport;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantViewFragment extends BaseFragment implements RecyclerViewBuilder {

    @BindView(R.id.recycler_view_id)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    //VAR
    private RestaurantRVAdapter mAdapter;
    private ArrayList<RestaurantItem> mRestaurantItemsList;
    private HttpRequestTools mTools = new HttpRequestTools();

    @Override
    protected BaseFragment newInstance() {
        return new RestaurantViewFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_restaurant_list;
    }

    @Override
    protected void configureDesign() {
        this.configureRecyclerView();
        this.configureSwipeRefreshLayout();
        this.mTools.progressBarHandler(mProgressBar, getContext());
        this.configureOnclickRecyclerView();
    }

    @Override
    protected void updateDesign() {
        this.updateUI();

    }

    // -------------------------------------------------------------------------------------------//
    //                                      CONFIGURATION                                         //
    // -------------------------------------------------------------------------------------------//

    @Override
    public void configureRecyclerView() {
        this.mRestaurantItemsList = new ArrayList<>();
        this.mAdapter = new RestaurantRVAdapter(this.mRestaurantItemsList, Glide.with(this));
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /*
     * @method configureSwipeRefreshLayout
     *
     * When the screen is swipe, the http request is executed
     * */
    @Override
    public void configureSwipeRefreshLayout() {
        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                http request to execute
                mTools.updateUIWhenStartingHTTPRequest(mProgressBar);
            }
        });
    }

    // -------------------------------------------------------------------------------------------//
    //                                      ACTION                                                //
    // -------------------------------------------------------------------------------------------//

    private void configureOnclickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.id.item_restaurant_layout)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        RestaurantItem restaurantItem = mAdapter.getRestaurantItem(position);

                        Toast.makeText(getContext(), "CLICK on position: " + position + " name: "+ restaurantItem.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // -------------------------------------------------------------------------------------------//
    //                                      UI                                                    //
    // -------------------------------------------------------------------------------------------//

    @Override
    public void updateUI(/*ArrayList<RestaurantItem> restaurantItems*/) {
        this.mTools.updateUIWhenStopingHTTPRequest(mSwipeRefreshLayout, mProgressBar);

        mRestaurantItemsList.add(new RestaurantItem("Le Chalutier", "Française", "15 rue des Ardeches",
                "Open", 120, 2,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRTb97dCdvbN7JMYo3vYzIa5ib7ihCLzzr9wORiAmPWfemek1Qv", 3));
        mRestaurantItemsList.add(new RestaurantItem("Pizza Roberto", "Italienne", "1 rue des Gentilly",
                "Open at 1pm", 70, 5,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSyGhyR__TMnS93qBoyR6cKddzWjjpRWfFfVaEpgDD7BjIkiRBh", 1.5));
        mRestaurantItemsList.add(new RestaurantItem("Kebab Futur", "Turc", "70 rue des boulets",
                "Closed", 12, 0,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSI-zPkYqtvaQxd5nCaWct9CmlKq9w1HZNiCZkLU1ixl-UuQIiW", 2));
//        mRestaurantItemsList.addAll(restaurantItems);
        mAdapter.notifyDataSetChanged();
    }

}