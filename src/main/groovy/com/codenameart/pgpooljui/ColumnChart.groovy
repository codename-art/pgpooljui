package com.codenameart.pgpooljui

import com.vaadin.annotations.DesignRoot
import com.vaadin.annotations.JavaScript
import com.vaadin.server.Page
import com.vaadin.server.Sizeable
import com.vaadin.ui.CustomComponent
import com.vaadin.ui.Label
import com.vaadin.ui.declarative.Design

/**
 * Created by Artem on 27.10.2017.
 */
@DesignRoot
@JavaScript("https://www.gstatic.com/charts/loader.js")
class ColumnChart extends CustomComponent {

    Label columnChart

    ColumnChart(Map<String, Integer> workersMap, Map<String, Integer> hlvlMap, boolean merge, boolean total) {
        Design.read(this)
        columnChart.setWidth(100, Sizeable.Unit.PERCENTAGE)
        columnChart.setStyleName("center")

        def mergeStr = ""
        if (!merge) {
            mergeStr =
                    """
                    series: { 
                        0: {targetAxisIndex: 0},
                        1: {targetAxisIndex: 1}
                    },
                    vAxes: {
                        // Adds titles to each axis.
                        0: {title: 'Number of workers'},
                        1: {title: 'Number of hlvl'}
                    },
"""
        }

        def totalStr = ""
        if (total) {
            totalStr = "['Total', ${workersMap["all"]}, ${hlvlMap["all"]}],"
        }


        Page.getCurrent().getJavaScript().execute(
                """
google.charts.load('current', {'packages':['corechart', 'bar']}); 
                            google.charts.setOnLoadCallback(drawChart); 
                 
                 
                            function drawChart() { 
                var data = new google.visualization.DataTable(); 
                      data.addColumn('string', 'Time of Day'); 
                      data.addColumn('number', 'Worker'); 
                      data.addColumn('number', 'High level'); 
                 
                      data.addRows([ 
                        ['Active', ${workersMap["used"]}, ${hlvlMap["used"]}], 
                        ['Ready to use', ${workersMap["ready"]}, ${hlvlMap["ready"]}], 
                        ['Blind', ${workersMap["blind"]}, ${hlvlMap["blind"]}], 
                        ['Temp banned', ${workersMap["ban"]}, ${hlvlMap["ban"]}], 
                        $totalStr
                      ]); 
                 
                var view = new google.visualization.DataView(data); 
                      view.setColumns([0, 1, 
                                       { calc: "stringify", 
                                         sourceColumn: 1, 
                                         type: "string", 
                                         role: "annotation" }, 
                                       2, 
                                       { calc: "stringify", 
                                         sourceColumn: 2, 
                                         type: "string", 
                                         role: "annotation" }]);
                      var options = { 
                        title: 'Accounts', 
                        height: 400, 
                        animation:{ 
                           duration: 1000, 
                           easing: 'out', 
                        },
                        hAxis: { 
                          title: '' 
                        
                        }, 
                        vAxis: { 
                          title: 'Count', 
                        }, 
                        $mergeStr 
                      }; 
                 
                      var chart = new google.visualization.ColumnChart( 
                        document.getElementById('columnChart')); 
                 
                      chart.draw(view, options);
                            }
""")

    }


}
