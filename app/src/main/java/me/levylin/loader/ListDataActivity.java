package me.levylin.loader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levylin.lib.net.loader.ListLoader;

import java.util.ArrayList;
import java.util.List;

import me.levylin.loader.helper.LoadStateHelper;
import me.levylin.loader.helper.RecyclerViewHelper;
import me.levylin.loader.helper.RefreshHelper;
import me.levylin.loader.model.ListDataModel;

/**
 * 单页数据Activity
 * Created by LinXin on 2016/12/28 15:51.
 */
public class ListDataActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_list_data);
        SwipeRefreshLayout layout = (SwipeRefreshLayout) findViewById(R.id.act_list_data_sfl);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.act_list_data_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> list = new ArrayList<>();
        MyAdapter adapter = new MyAdapter(list);
        recyclerView.setAdapter(adapter);

        ListDataModel model = new ListDataModel(list);
        ListLoader<String, String> loader = new ListLoader<>(this, model);
        loader.setLoadStateHelper(new LoadStateHelper(layout));
        loader.setRefreshViewHelper(new RefreshHelper(layout));
        loader.setListViewHelper(new RecyclerViewHelper(recyclerView));
        loader.load();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<String> data;

        public MyAdapter(List<String> data) {
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.act_list_data_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
