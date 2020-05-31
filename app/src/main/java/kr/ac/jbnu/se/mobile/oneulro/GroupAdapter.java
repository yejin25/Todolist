package kr.ac.jbnu.se.mobile.oneulro;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupAdapter extends BaseAdapter {
    Context mContext= null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Group> group;

    GroupAdapter(Context context, ArrayList<Group> group){
        mContext = context;
        this.group = group;
        mLayoutInflater = LayoutInflater.from(mContext);
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
        View view = mLayoutInflater.inflate(R.layout.list_item,null);

        TextView title = (TextView)view.findViewById(R.id.title);
        TextView text = (TextView)view.findViewById(R.id.text);
 //       TextView color = (TextView)view.findViewById(R.id.categorie);

        title.setText(group.get(position).getTitle());
        text.setText(group.get(position).getText());
//        color.setText(group.get(position).getColor());

        return view;
    }
}
