package com.practice.easyexam.app.view.history.host;

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
import com.practice.easyexam.app.model.HeaderItem;
import com.practice.easyexam.app.model.HistoryItem;
import com.practice.easyexam.app.model.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class HistoryHostAdapter extends RecyclerView.Adapter<HistoryHostAdapter.ViewHolder> implements Filterable {
//
//    private List<Room> rooms;
//    private List<Room> filteredRooms;
//    onClickItemHistory onClickItemHistory;
//    private static final int VIEW_TYPE_HEADER = 0;
//    private static final int VIEW_TYPE_CONTENT = 1;
//
//    public HistoryHostAdapter(List<Room> rooms) {
//        this.rooms = rooms;
//        this.filteredRooms = rooms;
//    }
//
//    void updateData(List<Room> rooms) {
//        this.rooms = rooms;
//        this.filteredRooms = rooms;
//        notifyDataSetChanged();
//    }
//
//
//    public void setOnClickItemHistory(onClickItemHistory onClickItem) {
//        this.onClickItemHistory = onClickItem;
//    }
//
//    @NonNull
//    @Override
//    public HistoryHostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//        View view = layoutInflater.inflate(R.layout.item_history_host, parent, false);
//        return new HistoryHostAdapter.ViewHolder(view);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//        Room room = filteredRooms.get(position);
//
//        holder.tvSubject.setText(room.getNameRoom());
//        holder.tvDate.setText(room.getCreateTime());
//
//        holder.cvParent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onClickItemHistory.onClickItem(room);
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return filteredRooms.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView tvSubject,tvDate;
//        public CardView cvParent;
//
//        public ViewHolder(View v) {
//            super(v);
//            tvSubject = v.findViewById(R.id.textView23);
//            tvDate = v.findViewById(R.id.textView25);
//            cvParent = v.findViewById(R.id.cvItemHistory);
//        }
//    }
//
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                if (constraint.length() == 0) {
//                    filteredRooms = rooms;
//                } else {
//                    List<Room> filteredList = new ArrayList<>();
//                    String filterPattern = constraint.toString().toLowerCase().trim();
//
//                    for (Room room : rooms) {
//                        if (room.getNameRoom().toLowerCase().contains(filterPattern)) {
//                            filteredList.add(room);
//                        }
//                    }
//                    filteredRooms = filteredList;
//                }
//
//                FilterResults results = new FilterResults();
//                results.values = filteredRooms;
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                // Update the filteredRooms with the filtered data
////                filteredRooms.clear();
////                filteredRooms.addAll((List<Room>) results.values);
//                filteredRooms = (ArrayList<Room>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
//}
//

interface onClickItemHistory {
    void onClickItem(Room room);
}


public class HistoryHostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_CONTENT = 1;

    private List<HistoryItem> items;
    private List<HistoryItem> filteredItems;
    onClickItemHistory onClickItemHistory;

    public HistoryHostAdapter(List<Room> rooms) {
        this.items = convertRoomsToHistoryItems(rooms);
        this.filteredItems = groupHistoryItemsByDate(items);
    }

    void updateData(List<Room> rooms) {
        this.items = convertRoomsToHistoryItems(rooms);
        this.filteredItems = groupHistoryItemsByDate(items);
        notifyDataSetChanged();
    }

    public void setOnClickItemHistory(onClickItemHistory onClickItem) {
        this.onClickItemHistory = onClickItem;
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
            ContentExamItem contentExamItem = (ContentExamItem) filteredItems.get(position);
            ((ContentViewHolder) holder).bind(contentExamItem.getRoom(), onClickItemHistory);
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

        void bind(Room room, onClickItemHistory onClickItemHistory) {
            tvSubject.setText(room.getNameRoom());
            tvDate.setText(room.getCreateTime());

            cvParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItemHistory.onClickItem(room);
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
                        if (historyItem instanceof ContentExamItem && ((ContentExamItem) historyItem).getRoom().getNameRoom().toLowerCase().trim().contains(filterPattern)) {
                            Log.d("NameRoom", ((ContentExamItem) historyItem).getRoom().getNameRoom().toLowerCase().trim());
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

    private List<HistoryItem> convertRoomsToHistoryItems(List<Room> rooms) {
        List<HistoryItem> historyItems = new ArrayList<>();
        for (Room room : rooms) {
            // Assuming ContentItem is your item type for Room
            ContentExamItem contentExamItem = new ContentExamItem(room);
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
        Collections.sort(sortedEntries, (entry1, entry2) -> entry2.getKey().compareTo(entry1.getKey()));

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
