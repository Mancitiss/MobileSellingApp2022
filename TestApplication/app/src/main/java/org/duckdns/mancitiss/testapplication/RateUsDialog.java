package org.duckdns.mancitiss.testapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Objects;

public class RateUsDialog extends Dialog {

    private float UserRate = 0;
    private String orderId;
    private String foodId;
    private Context context;
    private AppCompatActivity activity;
    TextView foodName;

    public RateUsDialog(@NonNull Context context, @NonNull AppCompatActivity activity, @NonNull String orderId, @NonNull String foodId) {
        this(context);
        this.context = context;
        this.activity = activity;
        this.orderId = orderId;
        this.foodId = foodId;
    }

    private RateUsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rate_us_dialog_layout);

        foodName = findViewById(R.id.rateFoodName);
        foodName.setText(Objects.requireNonNull(Models.getInstance().getKnownProducts().get(foodId)).getName());
        final AppCompatButton RateBtn = findViewById(R.id.RateNow_btn);
        final  AppCompatButton LaterBtn = findViewById(R.id.Later_btn);
        final RatingBar RatingBar = findViewById(R.id.Rating_bar);
        final ImageView RatingImage = findViewById(R.id.RatingImage);
        RateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserRate == 0){
                    // do nothing
                }
                else {
                    // send rate to server
                    // dismiss dialog
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {;
                            Connection.Companion.sendRate(activity, context, orderId, foodId, (int) Math.ceil(UserRate));
                        }
                    });
                    thread.start();
                    dismiss();
                }
            }
        });

        LaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hide rating dialog
                dismiss();
            }
        });

        RatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {


                if(v <= 1)
                {
                    RatingImage.setImageResource(R.drawable.one_start);
                }
                else
                {
                    if(v <= 2)
                    {
                        RatingImage.setImageResource(R.drawable.two_start);
                    }
                    else
                    {
                        if(v <= 3)
                        {
                            RatingImage.setImageResource(R.drawable.three_start);
                        }
                        else
                        {
                            if(v <= 4)
                            {
                                RatingImage.setImageResource(R.drawable.four_start);
                            }
                            else
                            {
                                if(v <= 5)
                                {
                                    RatingImage.setImageResource(R.drawable.five_start);
                                }
                                //animate emoji image
                                AnimateImage(RatingImage);
                                //select rating by user
                                UserRate = v;

                            }
                        }
                    }
                }

            }
        });
    }


    private  void AnimateImage(ImageView ratingImage)
    {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }

}
