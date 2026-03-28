package ind.milo.panes;

import ind.milo.framework.AbstractTab;
import ind.milo.util.NIOUtil;
import ind.milo.util.RegUtil;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MdfTab extends AbstractTab {
    private Tab mdfTab = new Tab("修改单");
    private VBox vBox = new VBox(10);

    private TextField resonTextField = new TextField();
    private TextArea inputTextArea = new TextArea();
    private TextArea outputTextArea = new TextArea();

    private Button transferBtn = new Button("转换");
    private Button fileBtn = new Button("生成文件");
    private Button parseBtn = new Button("解析");

    private HBox hBox = new HBox();
    private Group buttonGroup = new Group();

    public MdfTab(){
        init();
        action();
    }


    @Override
    public void init() {
        mdfTab.setClosable(false);
        hBox.getChildren().addAll(transferBtn, fileBtn,parseBtn);
        VBox.setVgrow(outputTextArea, Priority.ALWAYS);
        vBox.getChildren().addAll(inputTextArea, resonTextField, hBox, outputTextArea);
        vBox.setPadding(new Insets(10));
        mdfTab.setContent(vBox);
    }

    @Override
    public void action() {
        // todo 后续改成线程监听
        transferBtn.setOnAction(actionEvent -> {
            String submitInfo = RegUtil.getSubmitInfo(inputTextArea.getText(), resonTextField.getText());
            outputTextArea.setText(submitInfo);
        });

        fileBtn.setOnAction(actionEvent -> {
            String submitInfo = RegUtil.getSubmitInfo(inputTextArea.getText(), resonTextField.getText());
            NIOUtil.makeFile("nihao, 中国人", "修改单1.txt");
        });

        parseBtn.setOnAction(actionEvent->{
            String inputText = inputTextArea.getText();

        });
    }

    @Override
    public Tab getTab() {
        return mdfTab;
    }

}
