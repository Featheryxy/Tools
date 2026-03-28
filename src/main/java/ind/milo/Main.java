package ind.milo;

import ind.milo.framework.AbstractTab;
import ind.milo.panes.TablePane;
import ind.milo.panes.ToDoTab;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ind.milo.panes.MdfTab;
import ind.milo.panes.SqlTab;
import ind.milo.panes.JsonGenTab;
import ind.milo.panes.FieldExtractorTab;

/**
 * @author MiloYe
 */
public class Main extends Application {
    @Override
    public void init() throws Exception {
        super.init();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TabPane tabPane = new TabPane();

        AbstractTab tablePane = new TablePane();
        AbstractTab mdfTab = new MdfTab();
        AbstractTab sqlTab = new SqlTab();
        AbstractTab toDoTab = new ToDoTab();
        AbstractTab jsonGenTab = new JsonGenTab();
        AbstractTab fieldExtractorTab = new FieldExtractorTab();

        sqlTab.display();
        tablePane.display();
        tabPane.getTabs().addAll(tablePane.getTab(),
                mdfTab.getTab(),
                sqlTab.getTab(),
                toDoTab.getTab(),
                jsonGenTab.getTab(),
                fieldExtractorTab.getTab());

        Separator hSeparator = new Separator(Orientation.HORIZONTAL);


        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        // 设置内容居中靠左对齐
        hBox.setAlignment(Pos.CENTER_LEFT);

        CheckBox c1 = new CheckBox("置顶");

//        ToggleGroup toggleGroup = new ToggleGroup();
//        RadioButton radioButton = new RadioButton("置顶");
//        radioButton.setToggleGroup(toggleGroup);

        hBox.getChildren().add(c1);

        VBox vBox = new VBox(2);
        // 设置 tabPane 自动填满 vBox 剩余空间
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        vBox.getChildren().addAll(hBox, hSeparator, tabPane);

        c1.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    primaryStage.setAlwaysOnTop(true);
                }
                if (!newValue) {
                    primaryStage.setAlwaysOnTop(false);
                }
            }
        });

        Scene scene = new Scene(vBox, 300, 300);
        scene.getStylesheets().add("css/bootstrap3.css");
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(450);
        primaryStage.setTitle("Tools");
        primaryStage.show();
    }

    private void action() {

    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
