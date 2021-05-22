package com.example.fastship.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.fastship.R;

import org.jetbrains.annotations.NotNull;

public class EntryPagerAdapter extends androidx.viewpager.widget.PagerAdapter {
    private Context context;
    private int[] imageArray={R.drawable.delivery_logo_v5,R.drawable.quality_logo_v3,R.drawable.pizza_logo};
    private int[] headerArray={R.string.header_1,R.string.header_2,R.string.header_3};
    private int[] descArray={R.string.fast_del,R.string.quality,R.string.fast_tasty};

    public EntryPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return descArray.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view==object;
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
        View view= LayoutInflater.from(context).inflate(R.layout.pager_view,container,false);
        ImageView imageView=view.findViewById(R.id.pager_image);
        TextView textView=view.findViewById(R.id.pager_text);
        TextView header=view.findViewById(R.id.text_header);
        header.setText(headerArray[position]);
        imageView.setImageResource(imageArray[position]);
        textView.setText(descArray[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
        container.removeView((View) object);
    }

}
