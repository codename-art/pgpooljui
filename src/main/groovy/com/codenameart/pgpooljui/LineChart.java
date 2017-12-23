package com.codenameart.pgpooljui;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.JavaScript;
import com.vaadin.server.Page;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Artem on 27.10.2017.
 */
@DesignRoot
@JavaScript("https://www.gstatic.com/charts/loader.js")
public class LineChart extends CustomComponent {

    Label curveChart;

    public LineChart(String title, List<String> columns, List<BlindAccountReport> list, ChartType chartType) {
        Design.read(this);
        curveChart.setWidth(100, Unit.PERCENTAGE);
        curveChart.setStyleName("center");

        String data = list.stream().map(report -> {
            switch (chartType) {
                case DATE:
                    return report.toStringWithDate();
                case TIMEOUT:
                    return report.toStringWithTimeout();
            }
            return "";
        }).collect(Collectors.joining(","));

        String columnTitles = columns.stream().map(s -> "'"+s+"'").collect(Collectors.joining(","));

        Page.getCurrent().getJavaScript().execute("google.charts.load('current', {'packages':['corechart']});\n" +
                "            google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "\n" +
                "            function drawChart() {\n" +
                "                var tmpData = [["+columnTitles+"],"+data+"];\n" +
                "                var data = google.visualization.arrayToDataTable(tmpData);\n" +
                "\n" +
                "                var options = {\n" +
                "                    title: '"+title+"',\n" +
                "                    width: 900,\n" +
                "                    height: 500,\n" +
                "                    pointSize: 4\n" +
                "                };\n" +
                "\n" +
                "                var chart = new google.visualization.LineChart(document.getElementById('curveChart'));\n" +
                "\n" +
                "                chart.draw(data, options);\n" +
                "            }");
    }


}
