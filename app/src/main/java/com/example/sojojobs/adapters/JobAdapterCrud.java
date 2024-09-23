package com.example.sojojobs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sojojobs.R;
import com.example.sojojobs.models.Job;
import com.example.sojojobs.ui.JobPostingFragment;

import java.util.List;

public class JobAdapterCrud extends RecyclerView.Adapter<JobAdapterCrud.JobViewHolder> {

    private List<Job> jobList;
    private FragmentManager fragmentManager;
    private JobPostingFragment jobPostingFragment;

    public JobAdapterCrud(List<Job> jobList, FragmentManager fragmentManager, JobPostingFragment jobPostingFragment) {
        this.jobList = jobList;
        this.fragmentManager = fragmentManager;
        this.jobPostingFragment = jobPostingFragment;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_crud, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);

        // Check if job object is not null and has valid data
        if (job != null) {
            holder.tvJobTitle.setText(job.getJobTitle() != null ? job.getJobTitle() : "No Title");
            holder.tvCompanyName.setText(job.getCompanyName() != null ? job.getCompanyName() : "No Company");
            holder.tvLocation.setText(job.getLocation() != null ? job.getLocation() : "No Location");
            holder.tvSalary.setText(job.getSalary() != null ? job.getSalary() : "No Salary");

            holder.btnEditJob.setOnClickListener(v -> {
                jobPostingFragment.showEditJobDialog(job);
            });

            holder.btnDeleteJob.setOnClickListener(v -> {
                jobPostingFragment.deleteJob(job);
            });
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvCompanyName, tvLocation, tvSalary;
        Button btnEditJob, btnDeleteJob;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvSalary = itemView.findViewById(R.id.tv_salary);
            btnEditJob = itemView.findViewById(R.id.btn_edit);
            btnDeleteJob = itemView.findViewById(R.id.btn_delete);
        }
    }
}
