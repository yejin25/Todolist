package kr.ac.jbnu.se.mobile.oneulro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends BaseAdapter {
    List<Item> todo;

    ItemAdapter(List<Item> todo){
        this.todo = todo;
    }

    private static class ViewHolder {
        TextView id;
        TextView text;
       // TextView status;
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
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.todo_item, null);

            ItemAdapter.ViewHolder holder = new ItemAdapter.ViewHolder();
            holder.id = (TextView)view.findViewById(R.id.todoId);
            holder.text = (TextView)view.findViewById(R.id.todoText);
         //   holder.status = (TextView)view.findViewById(R.id.time);
            view.setTag(holder);
        }

        Item addItem = todo.get(position);

        if (addItem != null) {
            ItemAdapter.ViewHolder holder = (ItemAdapter.ViewHolder)view.getTag();
            holder.id.setText(addItem.getId());
            holder.text.setText(addItem.getText());
        }
        return view;
    }
}
