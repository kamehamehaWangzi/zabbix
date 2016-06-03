package org.pbccrc.platform.util;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

/**
 * 曲线图的绘制
 */
public class LineXYChart {
	/**
	 * 返回生成图片的文件名
	 * 
	 * @param session
	 * @param pw
	 * @return 生成图片的文件名
	 */
	private TimeSeriesCollection dataset = null;

	public String getLineXYChart(HttpSession session, PrintWriter pw) {
		XYDataset dataset = this.dataset;// 建立数据集
		String fileName = null;
		// 建立JFreeChart
		JFreeChart chart = ChartFactory.createTimeSeriesChart("JFreeChart时间曲线序列图", // title
				"日期", // x-axis label
				"值", // y-axis label
				dataset, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
		);
		// 设置JFreeChart的显示属性,对图形外部部分进行调整
		chart.setBackgroundPaint(Color.gray);// 设置曲线图背景色
		// 设置字体大小，形状
		Font font = new Font("宋体", Font.BOLD, 16);
		TextTitle title = new TextTitle("趋势分析图", font);
		chart.setTitle(title);
		// 副标题
		TextTitle subtitle = new TextTitle("", new Font("黑体", Font.BOLD, 12)); // 定义副标题
		chart.addSubtitle(subtitle);
		chart.setTitle(title); // 标题
		// 设置图示标题字符
		// TimeSeries s1 = new TimeSeries("历史曲线", Day.class);该中文字符
		LegendTitle legengTitle = chart.getLegend();
		legengTitle.setItemFont(font);
		XYPlot plot = (XYPlot) chart.getPlot();// 获取图形的画布
		plot.setBackgroundPaint(Color.lightGray);// 设置网格背景色
		plot.setDomainGridlinePaint(Color.green);// 设置网格竖线(Domain轴)颜色
		plot.setRangeGridlinePaint(Color.white);// 设置网格横线颜色
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));// 设置曲线图与xy轴的距离
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}
		// 设置Y轴
		NumberAxis numAxis = (NumberAxis) plot.getRangeAxis();
		NumberFormat numFormater = NumberFormat.getNumberInstance();
		numFormater.setMinimumFractionDigits(2);
		numAxis.setNumberFormatOverride(numFormater);
		// 设置提示信息
		StandardXYToolTipGenerator tipGenerator = new StandardXYToolTipGenerator("历史信息{1} 16:00,{2})",
				new SimpleDateFormat("MM-dd"), numFormater);
		// 设置X轴（日期轴）
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("MM-dd"));
		ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
		try {
			fileName = ServletUtilities.saveChartAsPNG(chart, 600, 350, info, session);// 生成图片
			// Write the image map to the PrintWriter
			ChartUtilities.writeImageMap(pw, fileName, info, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.flush();
		return fileName;// 返回生成图片的文件名
	}

	/**
	 * 建立生成图形所需的数据集
	 * 
	 * @return 返回数据集
	 */
	public void createDateSet(List date, List vl) {
		dataset = new TimeSeriesCollection();// 时间曲线数据集合
		TimeSeries s1 = new TimeSeries("历史曲线", Second.class);// 创建时间数据源，每一个//TimeSeries在图上是一条曲线
		// s1.add(new Day(day,month,year),value),添加数据点信息
		String strdate = null;
		int mh;
		int dy;
		int hr;
		int mt;
		int sd;
		int yr;
		for (int i = 0; i < date.size(); i++) {
			if (dateJudge(String.valueOf(date.get(i))))// 判断是否为日期
			{
				try {
					yr = Integer.parseInt(String.valueOf(date.get(i)).substring(0, 4));// 年
					strdate = String.valueOf(date.get(i)).substring(5);
					mh = Integer.parseInt(howGet(strdate, "-"));// 月
																// 判断日期的格式来截取数值，如‘2009-9-9
																// 9：9：0’
																// 和‘2009-09-09
																// 09：09：00‘
					strdate = substr(strdate, "-");
					dy = Integer.parseInt(howGet(strdate, " "));// 日
					strdate = substr(strdate, " ");
					hr = Integer.parseInt(howGet(strdate, ":"));// 小时
					strdate = substr(strdate, ":");
					mt = Integer.parseInt(howGet(strdate, ":"));// 分钟
					strdate = substr(strdate, ":");
					sd = Integer.parseInt(strdate);// 秒
					s1.add(new Second(sd, mt, hr, dy, mh, yr), Float.parseFloat(String.valueOf(vl.get(i))));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		this.dataset.addSeries(s1);
	}

	public boolean dateJudge(String strDate)// 判断是否为日期
	{
		boolean flag = false;
		if (strDate.indexOf("-") > 0) {
			strDate = strDate.substring(strDate.indexOf("-") + 1);
			if (strDate.indexOf("-") > 0) {
				strDate = strDate.substring(strDate.indexOf("-") + 1);
				if (strDate.indexOf(" ") > 0) {
					strDate = strDate.substring(strDate.indexOf(" ") + 1);
					if (strDate.indexOf(":") > 0) {
						strDate = strDate.substring(strDate.indexOf(":") + 1);
						if (strDate.indexOf(":") > 0) {
							flag = true;
							return flag;
						}
					}
				}
			}
		}
		return flag;
	}

	public String howGet(String need, String sign) {
		switch (need.indexOf(sign)) {
		case 1:
			need = need.substring(0, 1);
			break;
		case 2:
			need = need.substring(0, 2);
			break;
		default:
			need = null;
			break;
		}
		return need;
	}

	public String substr(String datestr, String sign) {
		switch (datestr.indexOf(sign)) {
		case 1:
			datestr = datestr.substring(2);
			break;
		case 2:
			datestr = datestr.substring(3);
			break;
		default:
			datestr = null;
			break;
		}
		return datestr;
	}
}
