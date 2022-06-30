package tools;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bean.ScoreRateBean;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class ScoreTool {

    public static void setChartViewData(List<ScoreRateBean>scoreRateBeanList, LineChartView lineChart) {
        //X轴数据点  各个属性介绍地址：http://www.jianshu.com/p/7e8de03dad79
        List<PointValue> mPointValues = new ArrayList<>();
        //底部时间
        List<AxisValue> axisXBottomValues = new ArrayList<>();
        //获取list里面的最大值最小值
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < scoreRateBeanList.size(); i++) {
            //X轴数据点
            mPointValues.add(new PointValue(i, scoreRateBeanList.get(i).getScore()));
            //底部时间数据
            axisXBottomValues.add(new AxisValue(i).setLabel(scoreRateBeanList.get(i).getDate()));

            //将所有点添加至新的，list中
            list.add((int) scoreRateBeanList.get(i).getScore());
        }
        //数据大小
        int scoreSize = scoreRateBeanList.size();
        //最大的点
        int maxPoint = Collections.max(list);
        //最小的点
        int minPoint = Collections.min(list);

        Line line = new Line(mPointValues);
        //存放线条的集合
        List<Line> lines = new ArrayList<>();

        line.setHasLabels(true);
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状 这里是圆形 （
        //有三种 ：ValueShape.SQUARE(方形)  ValueShape.CIRCLE(圆形)  ValueShape.DIAMOND (菱形)）
        line.setPointRadius(3);//坐标点的大小
        line.setCubic(false);//是否平滑曲线
        line.setFilled(false);//是否填充曲线面积
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        line.setColor(Color.parseColor("#F53C2E"));
        line.setStrokeWidth(2);//设置线的宽度
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        //设置数据的初始值，即所有的数据从baseValue开始计算，默认值为0。
        data.setBaseValue(Float.NEGATIVE_INFINITY);

        //传入底部list数据
        Axis axisX = new Axis();
        //设置底部标题(自行选择) 只能设置在正中间
//        axisX.setName("日期");
        //底部标注是否斜着显示
        axisX.setHasTiltedLabels(false);
        //字体大小
        axisX.setTextSize(12);
        //字体颜色
        axisX.setTextColor(Color.parseColor("#666666"));
        //距离各标签之间的距离 (0-32之间)
        axisX.setMaxLabelChars(4);
        //是否显示坐标线、如果为false 则没有曲线只有点显示
        axisX.setHasLines(true);        axisX.setValues(axisXBottomValues);
        data.setAxisXBottom(axisX);

        //左边参数设置
        Axis axisY = new Axis();
        axisY.setName("（得分）");
        //axisY.setMaxLabelChars(6); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数
        axisY.setTextSize(12);
        axisY.setTextColor(Color.parseColor("#666666"));
        axisY.setHasLines(true);
        //axisY.setValues(axisXLeftValues);
        //设置坐标轴在左边
        data.setAxisYLeft(axisY);

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        //水平缩放
        //lineChart.setZoomType(ZoomType.HORIZONTAL);
        //是否可滑动
        lineChart.setScrollEnabled(true);
        //放入数据源至控件中
        lineChart.setLineChartData(data);

        Viewport v = new Viewport(lineChart.getMaximumViewport());
        //Y轴最大值为 加上20、防止显示不全
        v.top = maxPoint + 20;
        v.right = 10;
        //最小值 Y轴最低点就为0
        v.bottom = 0;//最小值
        //设置最大化的viewport （chartdata）后再调用
        //这2个属性的设置一定要在lineChart.setMaximumViewport(v);这个方法之后,
        // 不然显示的坐标数据是不能左右滑动查看更多数据的
        lineChart.setMaximumViewport(v);
        //左边起始位置 轴
        v.left = 0;
        //如果数据点超过20，显示20个、否则，显示数据本身大小{自己根据需求设置}
        if (scoreSize < 10) {
            // Y轴显示多少数据
            v.right = scoreSize;
        } else {
            v.right = 10;
        }
        //左右滑动
        lineChart.setCurrentViewport(v);
    }
}
