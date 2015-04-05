package edu.team7_18842cmu.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import edu.team7_18842cmu.StoredItem;
import edu.team7_18842cmu.activities.R;

/**
 * Created by Nick on 4/5/2015.
 */

public class AnswerAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<StoredItem> mAnswers;


    public AnswerAdapter(Context context, List<StoredItem> answers) {
        mInflater = LayoutInflater.from(context);
        mAnswers = answers;
    }

    @Override
    public int getCount() {
        return mAnswers.size();
    }

    @Override
    public StoredItem getItem(int position) {
        return mAnswers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if(convertView == null) {
            view = mInflater.inflate(R.layout.query_result, parent, false);
            holder = new ViewHolder();
            holder.itemName = (TextView)view.findViewById(R.id.ItemName);
            holder.itemSize = (TextView)view.findViewById(R.id.ItemSize);
            holder.itemPrice = (TextView)view.findViewById(R.id.ItemPrice);
            holder.itemStore = (TextView)view.findViewById(R.id.ItemStore);
            holder.itemDate = (TextView)view.findViewById(R.id.ItemDate);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        StoredItem answer = mAnswers.get(position);
        holder.itemName.setText(answer.getItemName());
        holder.itemSize.setText(answer.getItemSize());
        holder.itemPrice.setText(answer.getItemPrice().toString());
        holder.itemStore.setText(answer.getItemStore());

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String date = format.format(Date.parse(answer.getPurchaseDate().toString()));
        holder.itemDate.setText(date);

        return view;
    }

    private class ViewHolder {
        public TextView itemName, itemSize, itemPrice, itemStore, itemDate;
    }

}