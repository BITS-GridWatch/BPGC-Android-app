package com.macbitsgoa.bitsgridwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class OnboardingSliderAdapter extends PagerAdapter {


    Context context;

    LayoutInflater layoutInflater;

    public OnboardingSliderAdapter(Context context)
    {
        this.context = context;
    }

    public String[] slide_titles = {
            "Welcome to Ujala!" , "The Map" , "The Settings"
    } ;

    public String[] slide_descriptions = {
            "This is an initiative to detect and log power cuts in BITS Pilani, K K Birla Goa Campus. The app monitors your phone and gives you near real-time information about electricity around you." ,
            "The marks on the map show you the areas with presence of power supply. Additionally, you can choose to view the data from past 5 minutes or past 1 hour by tapping on the floating action button at the bottom right." ,
            "The settings page gives you further control over the app, along with options such as changing the theme, toggling the monitoring and viewing your profile."
    } ;

    public int[] slide_icons = {
            R.drawable.ujala_logo , R.drawable.map_logo , R.drawable.settings_logo
    } ;

    @Override
    public int getCount() {
        return slide_titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);



        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        TextView slide_title = view.findViewById(R.id.slide_title);
        TextView slide_desc = view.findViewById(R.id.slide_desc);
        ImageView slide_icon = view.findViewById(R.id.slide_icon);

        slide_title.setText(slide_titles[position]);
        slide_desc.setText(slide_descriptions[position]);
        slide_icon.setImageResource(slide_icons[position]);

        if (position == 0)
            slide_icon.setPadding(300,50,300,0);
        else
            slide_icon.setPadding(380,50,380,10);



        container.addView(view);


        return view;
    }


    @Override

    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((RelativeLayout)object);
    }
}
