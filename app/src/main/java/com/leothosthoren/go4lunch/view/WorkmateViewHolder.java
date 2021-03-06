package com.leothosthoren.go4lunch.view;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.leothosthoren.go4lunch.R;
import com.leothosthoren.go4lunch.model.firebase.Users;
import com.leothosthoren.go4lunch.utils.App;
import com.leothosthoren.go4lunch.utils.DataConverterHelper;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateViewHolder extends RecyclerView.ViewHolder implements DataConverterHelper {

    // Widgets
    @BindView(R.id.item_workmates_choice)
    TextView mTextViewWorkmateName;
    @BindView(R.id.item_workmates_photo)
    ImageView mImageViewWorkmatePhoto;


    public WorkmateViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateWithWorkmateItem(Users workmateItem, RequestManager glide) {

        //Update workmate profile picture
        if (workmateItem.getUrlPicture() != null) {
            glide.load(workmateItem.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(this.mImageViewWorkmatePhoto);
        }

        //Update workmate name and restaurant choice
        if (workmateItem.getWorkmateSelection() != null
                && formatDate(workmateItem.getWorkmateSelection().getSelectionDate()).equals(formatDate(Calendar.getInstance().getTime()))) {
            this.mTextViewWorkmateName.setText(App.getContext().getResources().getString(R.string.workmate_is_eating,
                    formatFullName(workmateItem.getUsername()),
                    workmateItem.getWorkmateSelection().getRestaurantName()));
            this.mTextViewWorkmateName.setTextAppearance(R.style.textStyle);

        } else {
            this.mTextViewWorkmateName.setText(App.getContext().getResources()
                    .getString(R.string.workmate_default_decision, formatFullName(workmateItem.getUsername())));
            this.mTextViewWorkmateName.setTextAppearance(R.style.textNoStyle);
        }

    }

}

