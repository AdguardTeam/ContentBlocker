package com.adguard.android.contentblocker.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.adguard.android.contentblocker.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Deletable list adapter with custom layout
 */
public class FilterRulesAdapter extends BaseAdapter implements Filterable {

    private final Object lock = new Object();

    private Context context;
    private List<String> originalValues = null;
    private List<String> objects;
    private Set<String> disabledItems;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Integer position = (Integer) buttonView.getTag();
            String item = getItem(position);
            setItemChecked(item, isChecked);
            notifyDataSetChanged();
        }
    };

    private ArrayFilter itemsFilter;

    /**
     * Creates filter rules array adapter
     *
     * @param context       Application context
     * @param values        Initial values
     * @param disabledItems Disabled items
     */
    public FilterRulesAdapter(Context context, List<String> values, Set<String> disabledItems) {
        this.context = context;
        this.objects = values;

        this.disabledItems = disabledItems;
    }

    @Override
    public Filter getFilter() {
        if (itemsFilter == null) {
            itemsFilter = new ArrayFilter();
        }
        return itemsFilter;
    }

    /**
     * Reloads adapter
     *
     * @param values        New values
     * @param disabledItems Disabled items
     */
    public void reload(List<String> values, Set<String> disabledItems) {
        clear();
        addAll(values);
        this.disabledItems = disabledItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.deletable_list_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.itemTextView);
        String rule = getItem(position);
        textView.setText(rule);
        textView.setTextColor(getRuleColor(rule));

        AppCompatCheckBox checkBox = convertView.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(isItemChecked(rule));
        if (isRuleComment(rule)) {
            checkBox.setVisibility(View.INVISIBLE);
        } else {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setTag(position);
            checkBox.setOnCheckedChangeListener(checkedChangeListener);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public String getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    public void add(String rule) {
        if (originalValues != null) {
            originalValues.add(rule);
        } else {
            objects.add(rule);
        }
        notifyDataSetChanged();
    }

    public void remove(String rule) {
        if (originalValues != null) {
            originalValues.remove(rule);
        } else {
            objects.remove(rule);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if (originalValues != null) {
            originalValues = null;
        }
        if (objects != null ){
            objects.clear();
        }
        notifyDataSetChanged();
    }

    public void insert(String rule, int position) {
        if (originalValues != null) {
            originalValues.add(position, rule);
        } else {
            objects.add(position, rule);
        }
        notifyDataSetChanged();
    }

    /**
     * Replaces an item at the specified index with a new value
     *
     * @param item  Item
     * @param index Index
     */
    public void replace(String item, int index) {
        remove(getItem(index));
        insert(item, index);
    }

    /**
     * @return String value (all the items joined with '\\n')
     */
    public String getText() {
        if (originalValues != null) {
            return StringUtils.join(originalValues, '\n');
        } else {
            return StringUtils.join(objects, '\n');
        }
    }

    /**
     * Called when item's checkbox value is changed
     *
     * @param item    List item
     * @param checked True if checked
     */
    protected void setItemChecked(String item, boolean checked) {
        // Nothing, override in descendants
        if (checked) {
            disabledItems.remove(item);
        } else {
            disabledItems.add(item);
        }
    }

    private void addAll(List<String> list) {
        if (originalValues != null) {
            originalValues.addAll(list);
        } else {
            objects.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * Should be overriden
     *
     * @param item List item
     * @return true if it's checked
     */
    private boolean isItemChecked(String item) {
        return !disabledItems.contains(item);
    }

    /**
     * Checks if this is a comment string
     *
     * @param rule Rule text
     * @return true if this is a comment
     */
    private boolean isRuleComment(String rule) {
        return StringUtils.startsWith(rule, "!") ||
                StringUtils.startsWith(rule, "[Adblock");
    }

    /**
     * Syntax highlighting depending on the rule type
     *
     * @param rule Rule text
     * @return Rule color
     */
    private int getRuleColor(String rule) {

        Resources resources = context.getResources();
        if (isRuleComment(rule)) {
            return resources.getColor(R.color.ruleColorCommentLight);
        } else if (StringUtils.startsWith(rule, "@@")) {
            return resources.getColor(R.color.ruleColorWhitelistLight);
        } else if (StringUtils.containsAny(rule, "#%#", "#@%#")) {
            return resources.getColor(R.color.ruleColorJavaScriptLight);
        } else if (StringUtils.containsAny(rule, "#$#", "#@$#")) {
            return resources.getColor(R.color.ruleColorCssInjectLight);
        } else if (StringUtils.containsAny(rule, "##", "#@#")) {
            return resources.getColor(R.color.ruleColorCssLight);
        } else if (StringUtils.containsAny(rule, "$$", "$@$")) {
            return resources.getColor(R.color.ruleColorContentLight);
        }

        return resources.getColor(R.color.ruleColorDefaultLight);
    }

    /**
     * Filter implementation for this adapter
     */
    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final FilterResults results = new FilterResults();

            if (originalValues == null) {
                synchronized (lock) {
                    originalValues = new ArrayList<>(objects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                final ArrayList<String> list;
                synchronized (lock) {
                    list = new ArrayList<>(originalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                final ArrayList<String> values;
                synchronized (lock) {
                    values = new ArrayList<>(originalValues);
                }

                final int count = values.size();
                final ArrayList<String> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final String value = values.get(i).toLowerCase();
                    if (value.contains(prefixString)) {
                        newValues.add(value);
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            objects = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}

