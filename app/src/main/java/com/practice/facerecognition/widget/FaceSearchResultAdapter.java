package com.practice.facerecognition.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.practice.facerecognition.R;
import com.practice.facerecognition.faceserver.CompareResult;
import com.practice.facerecognition.faceserver.FaceServer;
import com.practice.facerecognition.util.DatabaseHelper;

import java.io.File;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class FaceSearchResultAdapter extends RecyclerView.Adapter<FaceSearchResultAdapter.CompareResultHolder> {
    private List<CompareResult> compareResultList;
    private LayoutInflater inflater;

    // todo 为了调整人脸签到界面左上角图片下的文字内容
    private Context context;

    public FaceSearchResultAdapter(List<CompareResult> compareResultList, Context context) {
        // todo 为了调整人脸签到界面左上角图片下的文字内容
        this.context = context;

        inflater = LayoutInflater.from(context);
        this.compareResultList = compareResultList;
    }

    @NonNull
    @Override
    public CompareResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recycler_item_search_result, null, false);
        CompareResultHolder compareResultHolder = new CompareResultHolder(itemView);
        compareResultHolder.textView = itemView.findViewById(R.id.tv_item_name);
        compareResultHolder.imageView = itemView.findViewById(R.id.iv_item_head_img);
        return compareResultHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CompareResultHolder holder, int position) {
        if (compareResultList == null) {
            return;
        }
        File imgFile = new File(FaceServer.ROOT_PATH + File.separator + FaceServer.SAVE_IMG_DIR + File.separator + compareResultList.get(position).getUserName() + FaceServer.IMG_SUFFIX);
        Glide.with(holder.imageView)
                .load(imgFile)
                .into(holder.imageView);

        // todo 在这里调整人脸签到界面左上角下的文字内容
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        String name = dbHelper.getStudentNameByStudentNum(compareResultList.get(position).getUserName());

        if (name != null) {
            holder.textView.setText(name);
        }
        else {
            holder.textView.setText("未知");
        }
    }

    @Override
    public int getItemCount() {
        return compareResultList == null ? 0 : compareResultList.size();
    }

    class CompareResultHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        CompareResultHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
