package kr.ac.jbnu.se.mobile.oneulro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GroupAdapter extends BaseAdapter {
    List<Group> group;

    GroupAdapter(List<Group> group){
        this.group = group;
    }

    private static class ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView time;
        TextView date;
        TextView year;
        TextView color;
    }

    @Override
    public int getCount() {
        return group.size();
    }

    @Override
    public Object getItem(int position) {
        return group.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.list_item, null);

            ViewHolder holder = new ViewHolder();
            holder.titleTextView = (TextView)view.findViewById(R.id.title);
            holder.descriptionTextView = (TextView)view.findViewById(R.id.text);
            holder.time = (TextView)view.findViewById(R.id.time);
            holder.date = (TextView)view.findViewById(R.id.date);
            holder.year = (TextView)view.findViewById(R.id.year);
            holder.color = (TextView)view.findViewById(R.id.category);

            view.setTag(holder);
        }

         Group addGroup = group.get(position);

        if (addGroup != null) {
            ViewHolder holder = (ViewHolder)view.getTag();
            holder.titleTextView.setText(addGroup.getText());
            holder.descriptionTextView.setText(addGroup.getTitle());
            holder.time.setText(addGroup.getTime());
            holder.date.setText(addGroup.getMonth());
            holder.year.setText(addGroup.getYear());
            holder.color.setBackgroundColor(addGroup.getColor());
        }

        return view;
    }

}
