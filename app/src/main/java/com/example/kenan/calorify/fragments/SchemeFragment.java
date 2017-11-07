package com.example.kenan.calorify.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kenan.calorify.R;
import com.example.kenan.calorify.dal.repos.DayRepository;
import com.example.kenan.calorify.dal.repos.SchemeRepository;
import com.example.kenan.calorify.dl.models.Product;
import com.example.kenan.calorify.fragments.adapters.CustomExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kenan on 10/10/2017.
 */

public class SchemeFragment extends Fragment {

    public static SchemeFragment newInstance() {
        return new SchemeFragment();
    }

    private View view;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> scheme;
    List<List<Product>> schemeData;
    ImageView empty_list_icon;
    TextView empty_list_notif;
    DayRepository dayRepo = new DayRepository();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.menu_scheme_frag, container, false);
        empty_list_icon = (ImageView) view.findViewById(R.id.android_sad);
        empty_list_notif = (TextView) view.findViewById(R.id.notify_empty);
        updateScheme();
        return view;
    }

    private void updateScheme() {

        if (dayRepo.getAllDays().size() == 0) {
            empty_list_icon.setVisibility(View.VISIBLE);
            empty_list_notif.setVisibility(View.VISIBLE);

        } else {
            empty_list_icon.setVisibility(View.GONE);
            empty_list_notif.setVisibility(View.GONE);

            SchemeRepository schemeRepo = new SchemeRepository();
            scheme = schemeRepo.getSchemeOfActiveUser();

            schemeData = schemeRepo.getSchemeDataOfActiveUser();
            expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
            expandableListTitle = new ArrayList<>(dayRepo.getAllDaysAsStrings());
            formatWithCalories();
            expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableListTitle, scheme);
            expandableListView.setAdapter(expandableListAdapter);

            expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                //hier zou je dezelfde modal kunnen openen als je een product scant om
                //details te zien van een geconsumeerde product

                Bundle args = new Bundle();
                SchemeDialogFragment dialog = new SchemeDialogFragment();
                args.putLong("productId", schemeData.get(groupPosition).get(childPosition).getId());
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "SchemeDialogFragment");

                return true;
            });
        }
    }

    private void formatWithCalories() {

        List<String> dateList = dayRepo.getAllDaysAsStrings();
        HashMap<String, List<String>> newScheme = new HashMap<>();
        List<String> newTitleList = new ArrayList<>();
        for (int i = 0; i < schemeData.size(); i++ ){ // Get all the days
            double DayCalories = 0f;
            ArrayList<Product> plist = new ArrayList<>();
            List<String> dailyList = new ArrayList<>();
            for (Product p : plist) { // Get all the Products
                DayCalories += p.getTotalCalories();
                //Set the product Calories
                dailyList.add(String.format("%s %8.2f", p.getBrandName(), p.getTotalCalories()));
            }
            String newGroupText = String.format("%s %8.2f", dateList.get(i), DayCalories);
            newScheme.put(newGroupText, dailyList);
            newTitleList.add(newGroupText);
        }
        scheme = newScheme;
        expandableListTitle = newTitleList;
    }
}
