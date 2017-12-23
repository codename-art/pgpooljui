package com.codenameart.pgpooljui;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Artem on 20.10.2017.
 * Java class instead of Groovy for method references, waiting Groovy 3
 */
@SpringUI
public class WebUI extends UI {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ReleaseService releaseService;

    private Grid<Account> mainGrid = new Grid<>(Account.class);
    private Grid<Instance> grid = new Grid<>(Instance.class);
    private Button release = new Button("Release accounts from selected instance");
    private Button activate = new Button("Activate newly added accounts");
    private CheckBox mergeStatus = new CheckBox("Merge workers axis");
    private CheckBox totalColumn = new CheckBox("Show total accounts");
    private Panel reportHolder = new Panel();
    private Panel statusChartHolder = new Panel();
    private Panel releasePanel = new Panel();
    private RadioButtonGroup<ChartType> reportType = new RadioButtonGroup<>("Chart data");
    private RadioButtonGroup<WorkerType> reportAccType = new RadioButtonGroup<>("Account type");
    private RadioButtonGroup<BanType> reportBanType = new RadioButtonGroup<>("Ban type");
    private RadioButtonGroup<BanType> releaseBanType = new RadioButtonGroup<>("Ban type");
    private RadioButtonGroup<WorkerType> releaseAccType = new RadioButtonGroup<>("Account type");
    private TextField releaseTimeout = new TextField();
    private Button unBanButton = new Button("Unban!");

    private Button reloadLabelsButton = new Button("Reload");
    private Button reloadGrid1Button = new Button("Reload");
    private Button reloadGrid2Button = new Button("Reload");

    @Override
    protected void init(VaadinRequest request) {
        Page.Styles styles = Page.getCurrent().getStyles();
        // inject the new color as a style
        styles.add(".center { text-align: center; } .center > div {display: inline-block;}");
        styles.add(".controls .v-checkbox {margin-top: 9px;}");

        updateLabels();
        updateInstanceGrid();
        mainGrid.setColumns("username", "level", "shadowbanned", "warn", "banned", "captcha", "last_modified");
        grid.setColumns("systemId", "activeWorkers", "activeHlvl", "lastUpdated");
        grid.setSortOrder(GridSortOrder.desc(grid.getColumn("activeHlvl")));
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(instance ->
                Notification.show(instance.getSystemId() + " items selected")));

        reloadLabelsButton.addClickListener(event -> updateLabels());

        reloadGrid1Button.addClickListener(event -> updateInstanceGrid());

        reloadGrid2Button.addClickListener(event -> updateMainGrid());


        release.addClickListener(event -> {
            grid.getSelectedItems()
                    .stream()
                    .findFirst()
                    .ifPresent(instance -> {
                        int count = accountService.releaseInstance(instance);
                        if (count > 0) {
                            Notification.show(
                                    "Successfully released " + count + " accounts from " + instance.getSystemId()
                            );
                            updateInstanceGrid();
                        } else {
                            Notification.show(
                                    "No accounts was released from" + instance.getSystemId()
                            );
                        }
                    });
        });

        activate.addClickListener(event -> {
                    Notification.show(accountService.activateAccounts() + " accounts activated");
                    updateLabels();
                }
        );

        HorizontalLayout statusControls = new HorizontalLayout(reloadLabelsButton, mergeStatus, totalColumn);
        statusControls.addStyleName("controls");
        VerticalLayout layout = new VerticalLayout(
                statusControls,
                statusChartHolder,
                new Label("<hr />", ContentMode.HTML),
                reloadGrid1Button,
                grid,
                release,
                new Label("<hr />", ContentMode.HTML),
                reloadGrid2Button,
                mainGrid,
                activate,
                new Label("<hr />", ContentMode.HTML),
                new HorizontalLayout(reportType, reportAccType, reportBanType),
                reportHolder,
                new Label("<hr />", ContentMode.HTML),
                releasePanel
        );
        layout.setWidth("100%");
        mainGrid.setWidth("100%");
        grid.setWidth("100%");
        setContent(layout);


        reportAccType.setItems(WorkerType.values());
        reportAccType.setSelectedItem(WorkerType.ALL);
        reportAccType.addSelectionListener(event -> event.getSelectedItem().ifPresent(wt -> selectReportChart()));

        reportBanType.setItems(BanType.values());
        reportBanType.setSelectedItem(BanType.SHADOW);
        reportBanType.addSelectionListener(event -> event.getSelectedItem().ifPresent(wt -> selectReportChart()));

        reportType.setItems(ChartType.values());
        reportType.addSelectionListener(event -> event.getSelectedItem().ifPresent(s -> selectReportChart()));
        reportType.setSelectedItem(ChartType.DATE);


        setupReleasePanel();
    }

    private void setupReleasePanel() {
        unBanButton.setEnabled(false);
        releaseBanType.setItems(BanType.SHADOW, BanType.TEMP);
        releaseBanType.setSelectedItem(BanType.SHADOW);
        releaseAccType.setItems(WorkerType.WORKER, WorkerType.HLVL);
        releaseAccType.setSelectedItem(WorkerType.WORKER);
        FormLayout content = new FormLayout(
                releaseBanType,
                releaseAccType,
                releaseTimeout,
                unBanButton
        );
        content.setMargin(true);
        releasePanel.setContent(content);
        ReleaseQuery releaseQuery = new ReleaseQuery();
        Binder<ReleaseQuery> releaseQueryBinder = new Binder<>();
        releaseQueryBinder.forField(releaseTimeout)
                .withValidator(new RegexpValidator("Input a number", "^[0-9]+$"))
                .withConverter(new StringToIntegerConverter("Must enter a number"))
                .bind(ReleaseQuery::getTimeout, ReleaseQuery::setTimeout);
        releaseQueryBinder.bind(releaseBanType, ReleaseQuery::getBanType, ReleaseQuery::setBanType);
        releaseQueryBinder.bind(releaseAccType, ReleaseQuery::getWorkerType, ReleaseQuery::setWorkerType);
        releaseQueryBinder.addStatusChangeListener(event -> {
            boolean isValid = event.getBinder().isValid();
            boolean hasChanges = event.getBinder().hasChanges();
            unBanButton.setEnabled(hasChanges && isValid);
        });
        unBanButton.addClickListener(event -> {
            if (releaseQueryBinder.writeBeanIfValid(releaseQuery)) {
                Notification.show(releaseService.release(releaseQuery) + " accounts have been unbanned");
                updateLabels();
            }
        });
    }

    private void selectReportChart() {
        ChartType chartType = reportType.getSelectedItem().get();
        WorkerType workerType = reportAccType.getSelectedItem().get();
        BanType banType = reportBanType.getSelectedItem().get();
        LineChart content;
        switch (chartType) {
            case DATE:
                content = new LineChart(
                        chartType.getLabel(),
                        Arrays.asList("Date", "Number of Accounts"),
                        accountService.getReport(ChartType.DATE, banType, workerType),
                        ChartType.DATE
                );
                break;
            case TIMEOUT:
                content = new LineChart(
                        chartType.getLabel(),
                        Arrays.asList("Days", "Number of Accounts"),
                        accountService.getReport(ChartType.TIMEOUT, banType, workerType),
                        ChartType.TIMEOUT
                );
                break;
            default:
                return;
        }
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(content);
        layout.setWidth("100%");
        layout.setComponentAlignment(content, Alignment.MIDDLE_CENTER);
        reportHolder.setContent(layout);

    }

    private void updateMainGrid() {
        mainGrid.setItems(accountService.findAll());
    }

    private void updateInstanceGrid() {
        grid.setItems(accountService.getInstanceInfo());
    }

    private void updateLabels() {
        Map<String, Integer> workersMap = new HashMap<>(5);
        workersMap.put("all", accountService.countWorkers());
        workersMap.put("used", accountService.countWorkersUsed());
        workersMap.put("ready", accountService.countWorkersReadyToUse());
        workersMap.put("blind", accountService.countWorkersShadow());
        workersMap.put("ban", accountService.countWorkersBanned());
        Map<String, Integer> hlvlMap = new HashMap<>(5);
        hlvlMap.put("all", accountService.countHlvl());
        hlvlMap.put("used", accountService.countHlvlUsed());
        hlvlMap.put("ready", accountService.countHlvlReadyToUse());
        hlvlMap.put("blind", accountService.countHlvlShadow());
        hlvlMap.put("ban", accountService.countHlvlBanned());


        ColumnChart columnChart = new ColumnChart(workersMap, hlvlMap, mergeStatus.getValue(), totalColumn.getValue());
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(columnChart);
        layout.setWidth("100%");
        layout.setComponentAlignment(columnChart, Alignment.MIDDLE_CENTER);
        statusChartHolder.setContent(layout);
    }
}
