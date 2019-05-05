package co.comorin.eventdex.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.comorin.eventdex.R;
import co.comorin.eventdex.utils.UserDetails;


public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.MyHolder> {

    private Context context;
    private ArrayList<UserDetails> userDetails;

    public DetailsAdapter(Context context, ArrayList<UserDetails> userDetails) {

        this.context = context;
        this.userDetails = userDetails;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.details_row, viewGroup, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final DetailsAdapter.MyHolder myHolder, int i) {


        // Setting values in Views
        myHolder.name.setText("Name : " + userDetails.get(i).getFirstName() + " " + userDetails.get(i).getLastName());
        myHolder.email.setText("Email : " + userDetails.get(i).getEmail());
        myHolder.orderId.setText("Order Id : " + userDetails.get(i).getOrderIdemId());
        myHolder.orderName.setText("Order Name : " + userDetails.get(i).getOrderItemName());
        myHolder.lastModifiedDate.setText("Last Modified Date : " + userDetails.get(i).getLastModifiesDate());


        // Expand/Collapse functionality
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myHolder.orderDetails.getVisibility() == View.VISIBLE) {
                    myHolder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand, 0);
                    myHolder.orderDetails.animate()
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    myHolder.orderDetails.setVisibility(View.GONE);
                                }
                            });
                } else {
                    myHolder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.collapse, 0);
                    myHolder.orderDetails.animate()
                            .alpha(1.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    myHolder.orderDetails.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return userDetails.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView email, name, orderId, orderName, lastModifiedDate;
        LinearLayout orderDetails;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            orderId = itemView.findViewById(R.id.order_id);
            orderName = itemView.findViewById(R.id.order_name);
            lastModifiedDate = itemView.findViewById(R.id.last_modified_date);
            orderDetails = itemView.findViewById(R.id.order_details);

        }
    }
}