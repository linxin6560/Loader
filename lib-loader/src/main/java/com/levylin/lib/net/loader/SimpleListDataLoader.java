package com.levylin.lib.net.loader;


import com.levylin.lib.net.INetworkView;
import com.levylin.lib.net.loader.model.SimpleListModel;

import java.util.List;

/**
 * 简化列表数据加载器
 * Created by LinXin on 2016/10/24 15:00.
 */
public class SimpleListDataLoader<ITEM> extends ListDataLoader<List<ITEM>, ITEM> {
    public SimpleListDataLoader(INetworkView view, SimpleListModel<ITEM> listModel) {
        super(view, listModel);
    }
}
