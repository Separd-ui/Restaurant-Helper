package com.example.fastship;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fastship.Adapters.EntryPagerAdapter;

public class EntryActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private EntryPagerAdapter entryPagerAdapter;
    private Button b_next,b_back,b_skip;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean("switch_preference_start",true))
        {
            startActivity(new Intent(EntryActivity.this,StartActivity.class));
        }

        viewPager=findViewById(R.id.viewPager_entry);
        b_next=findViewById(R.id.b_next);
        b_back=findViewById(R.id.b_back);
        b_skip=findViewById(R.id.b_skip);
        dotsLayout=findViewById(R.id.dotsLayout);
        entryPagerAdapter=new EntryPagerAdapter(this);
        viewPager.setAdapter(entryPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage=position;
                addDots(position);
                if(position==0)
                {
                    b_back.setVisibility(View.GONE);
                }
                else if(position==1)
                {
                    b_back.setVisibility(View.VISIBLE);
                    if(b_next.getText().toString().equals("ко входу"))
                        b_next.setText("вперёд");
                }
                else
                {
                    b_next.setText("ко входу");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        b_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this,StartActivity.class));
                finish();
            }
        });
        b_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b_next.getText().toString().equals("ко входу"))
                {
                    startActivity(new Intent(EntryActivity.this,StartActivity.class));
                    finish();
                }
                else
                    viewPager.setCurrentItem(currentPage+1);
            }
        });
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage-1);
            }
        });

        addDots(0);

    }

    private void addDots(int position)
    {
        dots=new TextView[3];
        dotsLayout.removeAllViews();

        for(int i=0;i<dots.length;i++)
        {
            dots[i]=new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.transparent_white));

            dotsLayout.addView(dots[i]);
        }

        dots[position].setTextColor(getResources().getColor(R.color.white));
    }

}