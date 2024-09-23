package com.example.sojojobs.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sojojobs.R;
import com.example.sojojobs.models.Job;
import com.example.sojojobs.ui.ApplicationFragment;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<Job> jobList;
    private FragmentManager fragmentManager;

    public JobAdapter(List<Job> jobList, FragmentManager fragmentManager) {
        this.jobList = jobList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.title.setText(job.getJobTitle());
        holder.company.setText(job.getCompanyName());
        holder.location.setText(job.getLocation());
        holder.salary.setText(job.getSalary());

        // Convert the Timestamp to a readable date string
        if (job.getPostedDate() != null) {
            String dateString = DateFormat.format("dd-MM-yyyy hh:mm a", job.getPostedDate().toDate()).toString();
            holder.timestamp.setText(dateString);
        } else {
            holder.timestamp.setText("N/A");
        }

        holder.companyLogo.setImageResource(R.drawable.sojojobs_logo);

        // Set up the Apply button click listener
        holder.applyButton.setOnClickListener(v -> {
            ApplicationFragment applicationFragment = new ApplicationFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, applicationFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {

        TextView title, company, location, salary, timestamp;
        ImageView companyLogo;
        Button applyButton;
        CardView cardView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.job_title);
            company = itemView.findViewById(R.id.job_company);
            location = itemView.findViewById(R.id.job_location);
            salary = itemView.findViewById(R.id.job_salary);
            timestamp = itemView.findViewById(R.id.job_timestamp);
            companyLogo = itemView.findViewById(R.id.iv_company_logo);
            applyButton = itemView.findViewById(R.id.btn_apply);
        }
    }
}
