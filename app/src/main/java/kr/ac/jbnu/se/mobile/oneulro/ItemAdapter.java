package kr.ac.jbnu.se.mobile.oneulro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends BaseAdapter {
    List<Item> todo;
    View view;

    ItemAdapter(List<Item> todo){
        this.todo = todo;
    }

    private static class ViewHolder {
        TextView id;
        TextView text;
        CheckBox check;
    }

    @Override
    public int getCount() {
        return todo.size();
    }

    @Override
    public Object getItem(int position) {
        return todo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.todo_item, null);

            ItemAdapter.ViewHolder holder = new ItemAdapter.ViewHolder();
            holder.id = (TextView)view.findViewById(R.id.todoId);
            holder.text = (TextView)view.findViewById(R.id.todoText);
            holder.check = (CheckBox)view.findViewById(R.id.check);

            view.setTag(holder);
        }

        Item addItem = todo.get(position);

        if (addItem != null) {
            ItemAdapter.ViewHolder holder = (ItemAdapter.ViewHolder)view.getTag();
            holder.id.setText(addItem.getId());
            holder.text.setText(addItem.getText());
            holder.check.setChecked(Boolean.valueOf(addItem.getStatus()));
        }

        ItemAdapter.ViewHolder holder = (ItemAdapter.ViewHolder)view.getTag();
        holder.check.setClickable(false);
        holder.check.setFocusable(false);

        return view;
    }



}
