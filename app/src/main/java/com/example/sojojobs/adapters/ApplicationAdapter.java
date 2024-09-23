package com.example.sojojobs.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sojojobs.R;
import com.example.sojojobs.models.Application;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private Context context;
    private List<Application> applicationList;

    public ApplicationAdapter(Context context, List<Application> applicationList) {
        this.context = context;
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applicationList.get(position);

        holder.tvName.setText(application.getName());
        holder.tvEmail.setText(application.getEmail());
        holder.tvPhone.setText(application.getPhone());
        holder.btnOpenCv.setOnClickListener(v -> openCv(application.getCvUrl()));
        holder.btnDownloadCv.setOnClickListener(v -> downloadCv(application.getCvUrl()));
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    private void openCv(String cvUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(cvUrl));
        context.startActivity(intent);
    }

    private void downloadCv(String cvUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(cvUrl));
        context.startActivity(intent);
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail, tvPhone;
        Button btnOpenCv, btnDownloadCv;

        ApplicationViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            btnOpenCv = itemView.findViewById(R.id.btn_open_cv);
            btnDownloadCv = itemView.findViewById(R.id.btn_download_cv);
        }
    }
}
