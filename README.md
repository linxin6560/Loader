# 介绍
Loader是一个方便Activity控制加载状态，加载缓存的工具类
# 下载

    compile 'com.github.linxin6560:loader:1.0.7'
    
# 调用前的工作
1.修改BaseActivity使之实现INetWorkView:

    public class BaseActivity extends AppCompatActivity implements INetworkView {

        private DataLoader mLoader;

        @Override
        protected void onDestroy() {
            super.onDestroy();
            destroyLoader();
        }

        @Override
        public void destroyLoader() {
            if (mLoader != null) {
                mLoader.onDestroy();
            }
        }

        @Override
        public void setDataLoader(DataLoader loader) {
            mLoader = loader;
        }
    }
2.根据产品UI，实现ILoadStateHelper,IRefreshViewHelper,IListViewHelper
3.编写Model:
    
    public class SingleDataModel extends Model<String> {

        private String response;

        @Override
        public boolean isEmpty() {
            return TextUtils.isEmpty(response);
        }

        @Override
        public void setData(boolean isRefreshing, String response) {
            this.response = response;
        }

        @Override
        protected Call<String> getModelCall() {
            MainApi api = ApiManager.getInstance().getMainApi();
            return api.getTestCall();
        }
    }
或者

    public class ListDataModel extends ListModel<String, String> {

        public ListDataModel(List<String> list) {
            super(list);
        }

        @Override
        protected boolean ensureHasNext(String response, List<String> mapList) {
            return page < 10;
        }

        @Override
        protected List<String> map(String response) {
            List<String> list = new ArrayList<>();
            int size = mList.size();
            for (int i = 0; i < 20; i++) {
                list.add((i + size) + ":XXXXXXXXXXXXXXXXXXXXXXXXXX");
            }
            return list;
        }

        @Override
        protected Call<String> getModelCall() {
            MainApi api = ApiManager.getInstance().getMainApi();
            return api.getTestCall();
        }
        
        @Override
        protected CacheStrategy<String> getCacheStrategy(Request request) {//如需实现缓存控制
            return new CacheStrategy<String>(request, CacheStrategy.CacheType.READ_CACHE_UPDATE_UI_THEN_NET, 180) {
                @Override
                public boolean isTimeOut() {
                    return false;
                }

                @Override
                public String readCache() {
                    return null;
                }

                @Override
                public void saveCache(String s) {

                }
            };
        }
    }
    
4.调用：

    public class SingleDataActivity extends BaseActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.act_single_data);
            SwipeRefreshLayout layout = (SwipeRefreshLayout) findViewById(R.id.act_single_data_sfl);
            final TextView textView = (TextView) findViewById(R.id.act_single_data_tv);
            SingleDataModel model = new SingleDataModel();
            DataLoader<String> loader = new DataLoader<>(this, model);
            loader.setLoadStateHelper(new LoadStateHelper(layout));
            loader.setRefreshViewHelper(new RefreshHelper(layout));
            loader.setOnLoadSuccessListener(new OnLoadSuccessListener<String>() {
                @Override
                public void onSuccess(boolean isRefreshing, String response) {
                    textView.setText(response);
                }
            });
            loader.load();
        }
    }
    
或者
    
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
    }
    
这样子就可以了。
