package ind.milo.panes;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import freemarker.template.TemplateException;
import ind.milo.entity.TaskItem;
import ind.milo.framework.AbstractTab;
import ind.milo.framework.UIFactory;
import ind.milo.util.FreemarkerUtil;
import ind.milo.util.JsonUtil;
import ind.milo.util.StringUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date 2022/10/10 11:59
 * @Created by Milo
 */
public class TablePane extends AbstractTab {

    private static final Logger logger = LoggerFactory.getLogger(TablePane.class);

    private Tab tableTab;

    private VBox vBox;
    private HBox hBox;

    private TextArea inputTextArea;
    private Button buildBtn;
    private Button exportBtn;
    private TableView<TaskItem> tableView;
    private List<TaskItem> taskItems;

    private Button btnFetchFromHep;

    @Override
    public void init() {
        tableTab = new Tab("修改单");
        inputTextArea = new TextArea();
        vBox = new VBox(10);
        vBox.setPadding(new Insets(10));
        hBox = new HBox(10);
        buildBtn = UIFactory.getSingleButton("生成");
		exportBtn = UIFactory.getSingleButton("导出");
        btnFetchFromHep = UIFactory.getSingleButton("从效能拉取");
        tableView = new TableView<>();
        // 表格自动填满vbox剩余高度
        VBox.setVgrow(tableView, Priority.ALWAYS);
        setTableView();
        set();
    }

    private void setTableView() {
        // 创建列
        TableColumn<TaskItem, String> column1 = new TableColumn<>("版本");
//        column1.setCellValueFactory(new PropertyValueFactory<>("sprintVersion"));
        column1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getSprintVersion()));
        TableColumn<TaskItem, String> column2 = new TableColumn<>("修改原因");
//        column2.setCellValueFactory(new PropertyValueFactory<>("name"));
        column2.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getName()));

        TableColumn<TaskItem, TaskItem> column3 = new TableColumn<>("操作");
        column3.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
        // 添加列
        column3.setCellFactory(this::getTaskItemTaskItemTableCell);

        tableView.getColumns().addAll(column1, column2, column3);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private TableCell<TaskItem, TaskItem> getTaskItemTaskItemTableCell(TableColumn<TaskItem, TaskItem> param) {
        Button copyBtn = new Button("Copy");
        Button deleteBtn = new Button("Delete");
        HBox hBox = new HBox(10, copyBtn, deleteBtn);

        TableCell<TaskItem, TaskItem> cell = new TableCell<TaskItem, TaskItem>() {
            @Override
            protected void updateItem(TaskItem item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setGraphic(hBox);
                } else {
                    setGraphic(null);
                }
            }
        };
        deleteBtn.setOnAction(e -> onDelete(cell));
        copyBtn.setOnAction(event -> onCopy(cell));
        return cell;
    }

    private void onDelete(TableCell<TaskItem, TaskItem> cell) {
        ObservableList<TaskItem> items = cell.getTableView().getItems();
        TaskItem item = cell.getItem();
        if (null == item) {
            return;
        }
        items.remove(item);
    }

    private void onCopy(TableCell<TaskItem, TaskItem> cell) {
        String ftlPath = "F:\\JavaFX\\tools\\src\\main\\resources\\ftl";
        String ftlName = "mdf.ftl";
        FreemarkerUtil freemarkerUtil = new FreemarkerUtil(ftlPath, ftlName);
        Map<String, String> stringMap = null;
        try {
            stringMap = BeanUtils.describe(cell.getItem());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        stringMap.put("date", LocalDate.now().toString());
        stringMap.put("jobId", "yexy34716");
        String mdfStr = null;
        try {
            mdfStr = freemarkerUtil.exce(stringMap);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        UIFactory.setContent2Clipboard(mdfStr);
    }


    public void set() {
        hBox.getChildren().addAll(buildBtn, btnFetchFromHep,exportBtn);
        vBox.getChildren().addAll(inputTextArea, hBox, tableView);
        tableTab.setClosable(false);
        tableTab.setContent(vBox);
    }

    @Override
    public void action() {
        buildBtn.setOnAction((event) -> {
            if (StringUtil.isNullStr(inputTextArea.getText())) {
                return;
            }

            taskItems = JsonUtil.listTaskItem(inputTextArea.getText());
            // todo 改成stream
            tableView.getItems().clear();
            tableView.getItems().addAll(taskItems);
        });

        exportBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
//            fileChooser.showSaveDialog();


        });

        btnFetchFromHep.setOnAction(this::onFetchFromHep);
    }

    private void onFetchFromHep(ActionEvent actionEvent) {
        btnFetchFromHep.setDisable(true);

        String url = "http://cloudin.proxy.in.hundsun.com/openapi/invoke/defaultFormData";
        Map<String, Object> params = genHepPostBody();

        logger.info("发送hep报文：" + JSON.toJSONString(params));

        // 这里发送Http的代码可以异步处理防止 UI 卡死
        HttpResponse execute;
        String body;
        try {
            execute = HttpRequest.post(url)
                    .timeout(6000)
                    .form(params)
                    .execute();
            body = execute.body();
        } catch (Exception e) {
            logger.error("请求HEP出错", e);
            new Alert(Alert.AlertType.ERROR, "请求HEP出错：" + e.getMessage()).showAndWait();
            return;
        }

        logger.info("hep响应json：" + body);

        dataToView(body);


        btnFetchFromHep.setDisable(false);
    }

    private void dataToView(String body) {
        taskItems = JsonUtil.listTaskItem(body);
        // todo 改成stream
        tableView.getItems().clear();
        tableView.getItems().addAll(taskItems);
    }

    private Map<String, Object> genHepPostBody() {
        Map<String, Object> params = new HashMap<>();
//        params["product_no"] = ""
        params.put("app_id", "dqwhyanulhrmrrnk");
        params.put("app_key", "fbbbee8e31a646d3a3a45f5c0e5b3e9");
        params.put("method", "devtool/fetchTaskList");
        params.put("charset", "utf-8");
        params.put("format", "MD5");
        params.put("timestamp", System.currentTimeMillis());
        params.put("current_user_id", "yexy34716");
        params.put("status_list", "0,4,5,6,8,14,16,17,18");
        String sign =
                SecureUtil.signParams(DigestAlgorithm.MD5, params, "&", "=", true).toUpperCase();
        params.put("sign", sign);
        return params;

    }

    @Override
    public Tab getTab() {
        return tableTab;
    }
}



