package com.practice.easyexam.app.view.createdTests;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.ContentExamItem;
import com.practice.easyexam.app.model.ContentQuestionItem;
import com.practice.easyexam.app.model.HeaderItem;
import com.practice.easyexam.app.model.HistoryItem;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreatedTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_CONTENT = 1;

    private List<HistoryItem> items;
    private List<HistoryItem> filteredItems;
    private OnItemListener onItemListener;

    public CreatedTestAdapter(List<Test> tests) {
        this.items = convertRoomsToHistoryItems(tests);
        this.filteredItems = groupHistoryItemsByDate(items);
    }

    void updateData(List<Test> tests) {
        this.items = convertRoomsToHistoryItems(tests);
        this.filteredItems = groupHistoryItemsByDate(items);
        notifyDataSetChanged();
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    @Override
    public int getItemViewType(int position) {
        return filteredItems.get(position).getItemType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = layoutInflater.inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View contentView = layoutInflater.inflate(R.layout.item_history_host, parent, false);
            return new ContentViewHolder(contentView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderItem headerItem = (HeaderItem) filteredItems.get(position);
            ((HeaderViewHolder) holder).bind(headerItem.getDate());
        } else if (holder instanceof ContentViewHolder) {
            ContentQuestionItem contentExamItem = (ContentQuestionItem) filteredItems.get(position);
            ((ContentViewHolder) holder).bind(contentExamItem.getTest(), onItemListener);
        }
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView headerTextView;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.timeHeaderTextView);
        }

        void bind(String date) {
            headerTextView.setText(date);
        }
    }

    private static class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubject, tvDate;
        private CardView cvParent;

        ContentViewHolder(@NonNull View v) {
            super(v);
            tvSubject = v.findViewById(R.id.textView23);
            tvDate = v.findViewById(R.id.textView25);
            cvParent = v.findViewById(R.id.cvItemHistory);
        }

        void bind(Test test, OnItemListener onClickItemHistory) {
            tvSubject.setText(test.getName());
            tvDate.setText(test.getCreateDate());

            cvParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItemHistory.onItemClick(test);
                }
            });
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<HistoryItem> filteredList = new ArrayList<>();
                List<HistoryItem> tmpList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    // No filter applied, show all items
                    filteredList.addAll(groupHistoryItemsByDate(items));
                } else {
                    // Apply the filter based on item name or date
                    String filterPattern = constraint.toString().toLowerCase();

                    for (HistoryItem historyItem : items) {
                        if (historyItem instanceof ContentQuestionItem && ((ContentQuestionItem) historyItem).getTest().getName().toLowerCase().trim().contains(filterPattern)) {
                            Log.d("ContentQuestionItem", ((ContentQuestionItem) historyItem).getTest().getName().toLowerCase().trim());
//                            filteredList.add(historyItem);
                            tmpList.add(historyItem);
                        }
                    }
                    filteredList.addAll(groupHistoryItemsByDate(tmpList));
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems.clear();
                filteredItems.addAll((List<HistoryItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    private List<HistoryItem> convertRoomsToHistoryItems(List<Test> tests) {
        List<HistoryItem> historyItems = new ArrayList<>();
        for (Test test : tests) {
            // Assuming ContentItem is your item type for Room
            ContentQuestionItem contentExamItem = new ContentQuestionItem(test);
            historyItems.add(contentExamItem);
        }
        return historyItems;
    }

    private List<HistoryItem> groupHistoryItemsByDate(List<HistoryItem> historyItems) {
        List<HistoryItem> groupedItems = new ArrayList<>();
        Map<String, List<HistoryItem>> groupedHistoryItems = new HashMap<>();
        // Group history items by date
        for (HistoryItem historyItem : historyItems) {
            String date = historyItem.getDate(); // Assuming getDate() returns a date string
            if (!groupedHistoryItems.containsKey(date)) {
                groupedHistoryItems.put(date, new ArrayList<>());
            }
            groupedHistoryItems.get(date).add(historyItem);
        }
        // Sort the entries based on date in descending order (most recent first)
        List<Map.Entry<String, List<HistoryItem>>> sortedEntries = new ArrayList<>(groupedHistoryItems.entrySet());
        Collections.sort(sortedEntries, (entry1, entry2) -> {
            String date1 = entry1.getKey();
            String date2 = entry2.getKey();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date dateObj1 = sdf.parse(date1);
                Date dateObj2 = sdf.parse(date2);
                // Compare dates in descending order
                return dateObj2.compareTo(dateObj1);
            } catch (ParseException e) {
                e.printStackTrace(); // Handle the exception according to your needs
                return 0;
            }
        });
        // Create HistoryItems by alternating between HeaderItem and ContentItem
        for (Map.Entry<String, List<HistoryItem>> entry : sortedEntries) {
            String date = entry.getKey();
            List<HistoryItem> itemsForDate = entry.getValue();
            // Add HeaderItem for the date
            groupedItems.add(new HeaderItem(date));
            // Add ContentItems for each history item
            groupedItems.addAll(itemsForDate);
        }
        return groupedItems;
    }
}

interface OnItemListener {
    void onItemClick(Test test);
}