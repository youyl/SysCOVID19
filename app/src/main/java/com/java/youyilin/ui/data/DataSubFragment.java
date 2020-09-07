package com.java.youyilin.ui.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.java.youyilin.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

import io.reactivex.functions.Consumer;

public class DataSubFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private DataSubBackend dataSubBackend;
    private DataLineBackend dataLineBackend;
    private DataAdapter adapter;
    private int checked = -1;
    private SmartTable table;
    private LineChart lineChart;

    DataSubFragment(DataSubBackend d, DataLineBackend l) {
        dataSubBackend = d;
        dataLineBackend = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("test", "DataSubFragment onCreateView. ");
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
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_data);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter();
        recyclerView.setAdapter(adapter);
        return root;
    }

    public void refreshData() {
        dataSubBackend.refreshData().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean){
                    checked = -1;
                    table.setData(dataSubBackend.getDataItemList());
                    table.setVisibility(View.VISIBLE);
                    lineChart.setVisibility(View.VISIBLE);
                    fetchLineChart();
                }
                else{
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "获取疫情数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fetchLineChart(){
        String name = "";
        if (checked >= 0)
            name = dataSubBackend.getDataItemList().get(checked).name;
        dataLineBackend.fetchData(name).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                swipeRefreshLayout.setRefreshing(false);
                if (aBoolean) {
                    final List<Entry> list = dataLineBackend.getConfirmedList();
                    LineDataSet lineDataSet = new LineDataSet(list, "confirmed");
                    lineDataSet.setColor(Color.parseColor("#F15A4A"));
                    lineDataSet.setLineWidth(1.6f);
                    lineDataSet.setDrawCircles(false);
                    lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

                    LineData lineData = lineChart.getLineData();
                    if (lineData != null){
                        lineData.removeDataSet(0);
                        lineData.addDataSet(lineDataSet);
                    }
                    else{
                        lineData = new LineData(lineDataSet);
                        lineData.setDrawValues(false);
                        lineChart.setData(lineData);
                    }
                    int maxx = 0;
                    for (Entry e : list) {
                        if (e.getY() > maxx)
                            maxx = (int) e.getY();
                    }
                    lineChart.getAxisLeft().setAxisMaximum(maxx + 1);
                    lineChart.notifyDataSetChanged();
                    lineChart.setVisibility(View.VISIBLE);
                    lineChart.invalidate();
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getContext(), "获取疫情数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_smart_table, parent, false);
                    return new SmartTableViewHolder(view);
                default:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_line_chart, parent, false);
                    return new LineChartViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.v("test", "DataSubFragment onBindViewHolder " + position + ". ");
            switch (position) {
                case 0:
                    lineChart = ((LineChartViewHolder) holder).chart;
                    createLineChart();
                    break;
                default:
                    table = ((SmartTableViewHolder) holder).table;
                    createTable();
                    if (dataSubBackend.getDataItemList().size() == 0) {
                        table.setVisibility(View.GONE);
                        lineChart.setVisibility(View.GONE);
                        refreshData();
                    }
                    else{
                        if (dataLineBackend.getConfirmedList() == null)
                            fetchLineChart();
                    }
            }
        }

        private void createTable() {
            TableConfig config = table.getConfig();
            config.setShowTableTitle(false);
            config.setShowXSequence(false);
            config.setShowYSequence(false);
            final Resources resources = DataSubFragment.this.getResources();
            config.setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {
                @Override
                public int getBackGroundColor(CellInfo cellInfo) {
                    if (cellInfo.row == checked)
                        return resources.getColor(R.color.colorAccent);
                    if (cellInfo.row % 2 == 0) {
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
            List<DataItem> list = dataSubBackend.getDataItemList();
            TableData tableData = new TableData("国内疫情数据", list, nameColumn, nowColumn, confirmedColumn, deadColumn, curedColumn);
            tableData.setOnItemClickListener(new PageTableData.OnItemClickListener<Object>() {
                @Override
                public void onClick(Column column, String value, Object c, int col, int row) {
                    DataSubFragment.this.checked = row;
                    table.invalidate();
                    fetchLineChart();
                }
            });
            table.setTableData(tableData);
        }

        private void createLineChart(){
            //显示边界
            lineChart.setDrawBorders(false);
            //无数据时显示的文字
            lineChart.setNoDataText("暂无数据");

            final List<Entry> list = dataLineBackend.getConfirmedList();
            LineData data = null;
            if (list != null){
                //一个LineDataSet就是一条线
                LineDataSet lineDataSet = new LineDataSet(list, "新增确诊");
                //线颜色
                lineDataSet.setColor(Color.parseColor("#F15A4A"));
                //线宽度
                lineDataSet.setLineWidth(1.6f);
                //不显示圆点
                lineDataSet.setDrawCircles(false);
                //线条平滑
                lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

                data = new LineData(lineDataSet);
                //折线图不显示数值
                data.setDrawValues(false);
            }
            //得到X轴
            XAxis xAxis = lineChart.getXAxis();
            //设置X轴的位置（默认在下方)
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //设置X轴坐标之间的最小间隔
            xAxis.setGranularity(1);
            //设置X轴的刻度数量，第二个参数为true,将会画出明确数量（带有小数点），但是可能值导致不均匀
            xAxis.setLabelCount(5, false);
            //设置X轴的值（最小值、最大值、然后会根据设置的刻度数量自动分配刻度显示）
            xAxis.setAxisMinimum(0);
            xAxis.setAxisMaximum(30);
            //不显示网格线
            xAxis.setDrawGridLines(false);
            //设置X轴值为字符串
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int IValue = (int) value;
                    CharSequence format = DateFormat.format("MM-dd",
                            System.currentTimeMillis() - (long) (30 - IValue) * 24 * 60 * 60 * 1000);
                    return format.toString();
                }
            });
            //得到Y轴
            YAxis yAxis = lineChart.getAxisLeft();
            YAxis rightYAxis = lineChart.getAxisRight();
            //设置Y轴是否显示
            rightYAxis.setEnabled(false); //右侧Y轴不显示
            //不显示网格线
            yAxis.setDrawGridLines(false);
            //设置Y轴坐标之间的最小间隔
            yAxis.setGranularity(1);
            //设置y轴的刻度数量
            //+2：最大值n就有n+1个刻度，再加上y轴多一个单位长度
            yAxis.setLabelCount(6, false);
            //设置从Y轴值
            yAxis.setAxisMinimum(0f);
            int maxx = 0;
            if (list != null){
                for (Entry e : list) {
                    if (e.getY() > maxx)
                        maxx = (int) e.getY();
                }
            }
            //+1:y轴多一个单位长度，为了好看
            yAxis.setAxisMaximum(maxx + 1);
            //y轴
            yAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int IValue = (int) value;
                    return String.valueOf(IValue);
                }
            });
            //隐藏描述
            Description description = new Description();
            description.setEnabled(false);
            lineChart.setDescription(description);
            //折线图点的标记
            ConfirmedView markerView = new ConfirmedView(getContext());
            markerView.setChartView(lineChart);
            lineChart.setMarker(markerView);
            //最后设置数据
            lineChart.setData(data);
            //水平轴动画
            lineChart.animateX(1 * 1000);
            //图标刷新
            lineChart.invalidate();
        }

        @Override
        public int getItemViewType(int position) {
            switch(position){
                case 0:
                    return 1;
                default:
                    return 0;
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    class SmartTableViewHolder extends ViewHolder {

        SmartTable table;

        public SmartTableViewHolder(@NonNull View itemView) {
            super(itemView);
            table = (SmartTable) itemView.findViewById(R.id.smart_table);
        }
    }

    class LineChartViewHolder extends ViewHolder {

        LineChart chart;

        public LineChartViewHolder(@NonNull View itemView) {
            super(itemView);
            chart = (LineChart) itemView.findViewById(R.id.line_chart);
        }
    }

    class ConfirmedView extends MarkerView {
        private TextView content;
        public static final int ARROW_SIZE = 40; //箭头的大小

        public ConfirmedView(Context context) {
            super(context, R.layout.line_chart_confirmed_view);//这个布局自己定义
            content = (TextView) findViewById(R.id.confirmed_content);
        }

        //显示的内容
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            content.setText("新增确诊" + (int)e.getY() + "人\n" + format(e.getX()));
            super.refreshContent(e, highlight);
        }

        //标记相对于折线图的偏移量
        @Override
        public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
            MPPointF offset = getOffset();
            LineChart chart = (LineChart)getChartView();
            float width = getWidth();
            float height = getHeight();
            //posY, posX指的是markerView左上角点在图表上面的位置
            //处理Y方向
            if (posY <= height + ARROW_SIZE) {// 如果点y坐标小于markerView的高度，如果不处理会超出上边界，处理了之后这时候箭头是向上的，我们需要把图标下移一个箭头的大小
                offset.y = ARROW_SIZE;
            } else {//否则属于正常情况，因为我们默认是箭头朝下，然后正常偏移就是，需要向上偏移markerView高度和arrow size，再加一个stroke的宽度，因为你需要看到对话框的上面的边框
                offset.y = -height - ARROW_SIZE;
            }
            //处理X方向，分为3种情况，1、在图表左边 2、在图表中间 3、在图表右边
            if (posX > chart.getWidth() - width) { //如果超过右边界，则向左偏移markerView的宽度
                offset.x = -width;
            } else {//默认情况，不偏移（因为是点是在左上角）
                if (posX > width / 2) //如果大于markerView的一半，说明箭头在中间，所以向右偏移一半宽度
                    offset.x = -(width / 2);
                else
                    offset.x = 0;
            }
            return offset;
        }

        @Override
        public void draw(Canvas canvas, float posX, float posY) {
            Paint fillPaint = new Paint();//绘制边框的画笔
            fillPaint.setStyle(Paint.Style.FILL);
            fillPaint.setColor(Color.parseColor("#7F000000"));

            Chart chart = getChartView();
            float width = getWidth();
            float height = getHeight();
            MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);
            int saveId = canvas.save();

            Path path;
            if (posY < height + ARROW_SIZE) {//处理超过上边界
                path = new Path();
                path.moveTo(0, 0);
                if (posX > chart.getWidth() - width) {//超过右边界
                    path.lineTo(width - ARROW_SIZE, 0);
                    path.lineTo(width, - ARROW_SIZE);
                    path.lineTo(width, 0);
                } else {
                    if (posX > width / 2) {//在图表中间
                        path.lineTo(width / 2 - ARROW_SIZE / 2, 0);
                        path.lineTo(width / 2, - ARROW_SIZE);
                        path.lineTo(width / 2 + ARROW_SIZE / 2, 0);
                    } else {//超过左边界
                        path.lineTo(0, - ARROW_SIZE);
                        path.lineTo(0 + ARROW_SIZE, 0);
                    }
                }
                path.lineTo(0 + width, 0);
                path.lineTo(0 + width, 0 + height);
                path.lineTo(0, 0 + height);
                path.lineTo(0, 0);
                path.offset(posX + offset.x, posY + offset.y);
            } else {//没有超过上边界
                path = new Path();
                path.moveTo(0, 0);
                path.lineTo(0 + width, 0);
                path.lineTo(0 + width, 0 + height);
                if (posX > chart.getWidth() - width) {
                    path.lineTo(width, height + ARROW_SIZE);
                    path.lineTo(width - ARROW_SIZE, 0 + height);
                    path.lineTo(0, 0 + height);
                } else {
                    if (posX > width / 2) {
                        path.lineTo(width / 2 + ARROW_SIZE / 2, 0 + height);
                        path.lineTo(width / 2, height + ARROW_SIZE);
                        path.lineTo(width / 2 - ARROW_SIZE / 2, 0 + height);
                        path.lineTo(0, 0 + height);
                    } else {
                        path.lineTo(0 + ARROW_SIZE, 0 + height);
                        path.lineTo(0, height + ARROW_SIZE);
                        path.lineTo(0, 0 + height);
                    }
                }
                path.lineTo(0, 0);
                path.offset(posX + offset.x, posY + offset.y);
            }

            // translate to the correct position and draw
            canvas.drawPath(path, fillPaint);
            canvas.translate(posX + offset.x, posY + offset.y);
            draw(canvas);
            canvas.restoreToCount(saveId);
        }

        //时间格式化（显示今日往前若干天的每一天日期）
        public String format(float x) {
            CharSequence format = DateFormat.format("MM月dd日",
                    System.currentTimeMillis() - (long) (dataLineBackend.getConfirmedList().size() - (int) x) * 24 * 60 * 60 * 1000);
            return format.toString();
        }
    }
}