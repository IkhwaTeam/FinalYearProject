package com.example.ikhwa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnboardAdapter extends RecyclerView.Adapter<OnboardAdapter.OnboardingViewHolder> {

    private final List<OnboardItem> onboardingItems;

    public OnboardAdapter(List<OnboardItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onboard_item, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        boolean isFirst = position == 0;
        boolean isLast = position == onboardingItems.size() - 1;
        holder.bind(onboardingItems.get(position), isFirst, isLast);
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, description;
        private final ImageView imageView, logoView;
        private final Button btnGetStarted;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            description = itemView.findViewById(R.id.textDescription);
            imageView = itemView.findViewById(R.id.imageView);
            logoView = itemView.findViewById(R.id.logoView);
            btnGetStarted = itemView.findViewById(R.id.btnGetStarted);
        }

        void bind(OnboardItem item, boolean isFirst, boolean isLast) {
            title.setText(item.getTitle());
            description.setText(item.getDescription());

            // Always show the main image
            imageView.setImageResource(item.getImageRes());

            // Show logo only on the first screen
            logoView.setVisibility(isFirst ? View.VISIBLE : View.GONE);

            // Show "Get Started" only on the last screen
            if (isLast) {
                btnGetStarted.setVisibility(View.VISIBLE);
                btnGetStarted.setOnClickListener(v -> {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, SelectionActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                });
            } else {
                btnGetStarted.setVisibility(View.GONE);
            }
        }
    }
}
