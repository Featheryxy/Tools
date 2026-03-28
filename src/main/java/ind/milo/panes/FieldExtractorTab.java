package ind.milo.panes;

import ind.milo.framework.AbstractTab;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Java 字段提取器 Tab
 * 从 Java 字段声明中提取字段名
 * 例如：
 * 输入:
 *     /**
 *      * id
 *      * /
 *     private Integer todoId;
 *     private String todoName;
 * 
 * 输出: todoId, todoName
 * 
 * @author MiloYe
 */
public class FieldExtractorTab extends AbstractTab {
    private Tab fieldExtractorTab = new Tab("字段提取");
    private VBox vBox = new VBox(10);

    private TextArea inputTextArea = new TextArea();
    private TextArea outputTextArea = new TextArea();

    private Button extractBtn = new Button("提取字段");
    private Button clearBtn = new Button("清空");
    private Button copyBtn = new Button("复制");

    private HBox hBox = new HBox(10);

    public FieldExtractorTab() {
        init();
        action();
    }

    @Override
    public void init() {
        fieldExtractorTab.setClosable(false);
        
        // 设置提示文本
        inputTextArea.setPromptText("请粘贴 Java 字段声明代码，例如:\n    /**\n     * id\n     */\n    private Integer todoId;\n    private String todoName;");
        outputTextArea.setPromptText("提取的字段名将显示在这里");
        
        // 设置按钮组
        hBox.getChildren().addAll(extractBtn, copyBtn, clearBtn);
        
        // 设置输出框自动增长
        VBox.setVgrow(outputTextArea, Priority.ALWAYS);
        
        // 添加到主布局
        vBox.getChildren().addAll(inputTextArea, hBox, outputTextArea);
        vBox.setPadding(new Insets(10));
        fieldExtractorTab.setContent(vBox);
    }

    @Override
    public void action() {
        // 提取字段按钮事件
        extractBtn.setOnAction(event -> {
            String inputText = inputTextArea.getText();
            if (inputText.isEmpty()) {
                outputTextArea.setText("请输入 Java 字段声明代码！");
                return;
            }
            
            try {
                String fields = extractFields(inputText);
                outputTextArea.setText(fields);
            } catch (Exception e) {
                outputTextArea.setText("提取失败：" + e.getMessage());
                e.printStackTrace();
            }
        });

        // 清空按钮事件
        clearBtn.setOnAction(event -> {
            inputTextArea.clear();
            outputTextArea.clear();
        });
        
        // 复制按钮事件
        copyBtn.setOnAction(event -> {
            String outputText = outputTextArea.getText();
            if (!outputText.isEmpty() && !"请输入 Java 字段声明代码！".equals(outputText) 
                    && !outputText.startsWith("提取失败：")) {
                final java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
                final java.awt.datatransfer.StringSelection selection = 
                    new java.awt.datatransfer.StringSelection(outputText);
                toolkit.getSystemClipboard().setContents(selection, null);
            }
        });
    }

    /**
     * 从 Java 代码中提取字段名
     * @param code Java 代码
     * @return 逗号分隔的字段名列表
     */
    public String extractFields(String code) {
        StringBuilder fields = new StringBuilder();
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            // 移除行首尾空白
            line = line.trim();
            
            // 跳过空行、注释行、类声明等
            if (line.isEmpty() || 
                line.startsWith("/**") || 
                line.startsWith("*") || 
                line.startsWith("*/") ||
                line.startsWith("//") ||
                line.startsWith("public class") ||
                line.startsWith("private class") ||
                line.startsWith("class") ||
                line.startsWith("{") ||
                line.startsWith("}") ||
                line.startsWith("@")) {
                continue;
            }
            
            // 检查是否是字段声明（包含分号）
            if (line.contains(";")) {
                // 提取字段名
                String fieldName = extractFieldName(line);
                if (fieldName != null && !fieldName.isEmpty()) {
                    if (fields.length() > 0) {
                        fields.append(", ");
                    }
                    fields.append(fieldName);
                }
            }
        }
        
        return fields.toString();
    }

    /**
     * 从单行代码中提取字段名
     * @param line 包含字段声明的行
     * @return 字段名
     */
    private String extractFieldName(String line) {
        // 移除注释（包括行尾的 // 注释）
        if (line.contains("//")) {
            line = line.substring(0, line.indexOf("//"));
        }
        
        // 移除分号及之后的内容
        int semicolonIndex = line.indexOf(';');
        if (semicolonIndex > 0) {
            line = line.substring(0, semicolonIndex).trim();
        }
        
        // 按空格分割，取最后一部分作为字段名
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            return parts[parts.length - 1];
        }
        
        return null;
    }

    @Override
    public Tab getTab() {
        return fieldExtractorTab;
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 测试用例 1: 基本字段
        String test1 = "/**\n" +
                      " * id\n" +
                      " */\n" +
                      "private Integer todoId;\n" +
                      "private String todoName;";
        
        System.out.println("【测试 1】基本字段");
        System.out.println("输入:");
        System.out.println(test1);
        System.out.println("\n输出：todoId, todoName");
        System.out.println();
        
        // 测试用例 2: 多种修饰符
        String test2 = "public String name;\n" +
                      "private int age;\n" +
                      "protected boolean active;";
        
        System.out.println("【测试 2】多种修饰符");
        System.out.println("输入:");
        System.out.println(test2);
        System.out.println("\n输出：name, age, active");
        System.out.println();
        
        // 测试用例 3: 带注释的字段
        String test3 = "/** 用户 ID */\n" +
                      "@NotNull\n" +
                      "private Long userId;\n" +
                      "/** 用户名 */\n" +
                      "@Size(max = 50)\n" +
                      "private String username;\n" +
                      "// 邮箱地址\n" +
                      "private String email;";
        
        System.out.println("【测试 3】带注释的字段");
        System.out.println("输入:");
        System.out.println(test3);
        System.out.println("\n输出：userId, username, email");
        System.out.println();
        
        System.out.println("========== 功能说明 ==========");
        System.out.println("1. 自动识别 Java 字段声明");
        System.out.println("2. 支持各种访问修饰符 (private/public/protected)");
        System.out.println("3. 忽略注释和注解");
        System.out.println("4. 输出逗号分隔的字段名列表");
        System.out.println("5. 支持一键复制到剪贴板");
    }
}
