package com.example.syscovid19.ui.data;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.table.PageTableData;
import com.bin.david.form.data.table.TableData;
import com.example.syscovid19.R;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

class DataItem{
    public String name;
    public int now;
    public int confirmed;
    public int dead;
    public int cured;
}

public class DataSubFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private DataSubBackend dataSubBackend;
    private DataAdapter adapter;
    private int checked;
    private SmartTable table;

    DataSubFragment(DataSubBackend d){
        dataSubBackend = d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sub_data, container, false);
        // Inflate the layout for this fragment
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                refreshData();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter();
        recyclerView.setAdapter(adapter);
        return root;
    }

    public void onSuccess() {
        swipeRefreshLayout.setRefreshing(false);
        table.setData(new ArrayList());
        adapter.notifyDataSetChanged();
    }

    public void onError() {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), "获取疫情数据失败", Toast.LENGTH_SHORT).show();
    }

    public void refreshData() {
         dataSubBackend.refreshData().subscribe(new Consumer<Boolean>() {
             @Override
             public void accept(Boolean aBoolean) throws Exception {
                 if (aBoolean)
                     onSuccess();
                 else
                     onError();
             }
         });
    }

    class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch(viewType){
                case 0:
                     view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_smart_table, parent, false);
                     return new SmartTableViewHolder(view);
                default:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_button, parent, false);
                    return new ButtonViewHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            switch(position){
                case 0:
                    table =  ((SmartTableViewHolder) holder).table;
                    TableConfig config = table.getConfig();
                    config.setShowTableTitle(false);
                    config.setShowXSequence(false);
                    config.setShowYSequence(false);
                    final Resources resources = DataSubFragment.this.getResources();
                    DisplayMetrics dm = resources.getDisplayMetrics();
                    config.setMinTableWidth(dm.widthPixels);
                    config.setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {
                        @Override
                        public int getBackGroundColor(CellInfo cellInfo) {
                            if (cellInfo.row == checked)
                                return resources.getColor(R.color.colorAccent);
                            if(cellInfo.row %2 ==0) {
                                return resources.getColor(R.color.grayPrimary);
                            }
                            return resources.getColor(R.color.grayAccent);
                        }
                    });
                    Column<String> nameColumn = new Column<>("地区", "name");
                    Column<Integer> nowColumn = new Column<>("现存确诊", "now");
                    Column<Integer> confirmedColumn = new Column<>("累计确诊", "confirmed");
                    Column<Integer> deadColumn = new Column<>("死亡", "dead");
                    Column<Integer> curedColumn = new Column<>("治愈", "cured");
                    TableData tableData = new TableData("国内疫情数据", dataSubBackend.getDataItemList(), nameColumn, nowColumn, confirmedColumn, deadColumn, curedColumn);
                    tableData.setOnItemClickListener(new PageTableData.OnItemClickListener<Object>() {
                        @Override
                        public void onClick(Column column, String value, Object c, int col, int row) {
                            DataSubFragment.this.checked = row;
                            table.invalidate();
                        }
                    });
                    table.setTableData(tableData);
                    break;
                default:
                    final Button button = ((ButtonViewHolder) holder).button;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataSubBackend.all = true;
                            button.setVisibility(View.INVISIBLE);
                            onSuccess();
                        }
                    });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    class SmartTableViewHolder extends ViewHolder{

        SmartTable table;
        public SmartTableViewHolder(@NonNull View itemView) {
            super(itemView);
            table = (SmartTable) itemView.findViewById(R.id.smart_table);
        }
    }

    class ButtonViewHolder extends ViewHolder{

        Button button;
        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = (Button)itemView.findViewById(R.id.button);
        }
    }
}
