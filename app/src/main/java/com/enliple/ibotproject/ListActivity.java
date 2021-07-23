package com.enliple.ibotproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends Activity {
    public static final String API_KEY = "02";

    public int ITEM_SDK = 0;
    public int ITEM_CUSTOM_BUTTON = 1;

    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ArrayList<ListData> array = new ArrayList<>();

        ListData data = new ListData();
        data.setTitle("Use sdk with iBot Button");
        data.setType(ITEM_SDK);
        array.add(data);

        data = new ListData();
        data.setTitle("Use sdk with User Custom Button");
        data.setType(ITEM_CUSTOM_BUTTON);
        array.add(data);

        listView = findViewById(R.id.listView);
        ListAdapter adapter = new ListAdapter(ListActivity.this, array);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( position == ITEM_SDK ) {
                    startActivity(new Intent(ListActivity.this, MainActivity.class));
                } else if ( position == ITEM_CUSTOM_BUTTON ) {
                    startActivity(new Intent(ListActivity.this, UserCustomButtonActivity.class));
                }
            }
        });

    }


    public class ListAdapter extends BaseAdapter {

        private ArrayList<ListData> data;
        private Context context;

        public ListAdapter(Context context, ArrayList<ListData> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount()
        {
            return data.size();
        }

        @Override
        public Object getItem(int position)
        {
            return data.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;

            TextView title;
            if ( view == null ) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_list, parent, false);

                title = view.findViewById(R.id.title);

                holder = new ViewHolder();

                holder.title = title;

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();

                title = holder.title;
            }

            title.setText(data.get(position).getTitle());

            return view;
        }

        public class ViewHolder {
            private TextView title;
        }
    }

    public class ListData {
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        private String title;
        private int type;


    }
}
