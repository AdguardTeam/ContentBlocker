package com.adguard.android.contentblocker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.adguard.android.model.FilterList;
import com.adguard.android.service.FilterService;

/**
 *
 */
public class FilterViewAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final FilterService filterService;

    public FilterViewAdapter(Context context, FilterService filterService) {
        this.context = context;
        this.filterService = filterService;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filterService.getFilterListCount();
    }

    @Override
    public Object getItem(int position) {
        return filterService.getFilters().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(layoutInflater, position, convertView, parent);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView, ViewGroup parent) {
        FilterList filterList = filterService.getFilters().get(position);
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.preference_item_checkbox, parent, false);
        } else {
            view = convertView;
        }

        ((TextView) view.findViewById(R.id.title)).setText(filterList.getName());
        ((TextView) view.findViewById(R.id.summary)).setText(filterList.getDescription());
        ((CheckBox) view.findViewById(R.id.checkbox)).setChecked(filterList.isEnabled());
        view.setOnClickListener(this);

        view.setTag(filterList);

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onClick(View v) {
        FilterList list = (FilterList) v.getTag();
        filterService.updateFilterEnabled(list, !list.isEnabled());
        ((CheckBox)v.findViewById(R.id.checkbox)).setChecked(list.isEnabled());
    }
}
