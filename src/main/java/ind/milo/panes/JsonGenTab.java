package ind.milo.panes;

import ind.milo.framework.AbstractTab;
import ind.milo.util.JsonUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * JSON 生成器 Tab
 * 支持将输入格式转换为 JSON 格式
 * 例如：输入 a, b, c[d,c] 
 * 输出对应的 JSON 结构
 * @author MiloYe
 */
public class JsonGenTab extends AbstractTab {
    private Tab jsonGenTab = new Tab("JSON 生成");
    private VBox vBox = new VBox(10);

    private TextArea inputTextArea = new TextArea();
    private TextArea outputTextArea = new TextArea();

    private Button generateBtn = new Button("生成 JSON");
    private Button clearBtn = new Button("清空");

    private HBox hBox = new HBox(10);

    public JsonGenTab() {
        init();
        action();
    }

    @Override
    public void init() {
        jsonGenTab.setClosable(false);
        
        // 设置提示文本
        inputTextArea.setPromptText("请输入内容，例如：a, b, c[d,c]");
        outputTextArea.setPromptText("生成的 JSON 将显示在这里");
        
        // 设置按钮组
        hBox.getChildren().addAll(generateBtn, clearBtn);
        
        // 设置输出框自动增长
        VBox.setVgrow(outputTextArea, Priority.ALWAYS);
        
        // 添加到主布局
        vBox.getChildren().addAll(inputTextArea, hBox, outputTextArea);
        vBox.setPadding(new Insets(10));
        jsonGenTab.setContent(vBox);
    }

    @Override
    public void action() {
        // 生成 JSON 按钮事件
        generateBtn.setOnAction(event -> {
            String inputText = inputTextArea.getText().trim();
            if (inputText.isEmpty()) {
                outputTextArea.setText("请输入内容！");
                return;
            }
            
            try {
                String jsonResult = parseToJson(inputText);
                outputTextArea.setText(jsonResult);
            } catch (Exception e) {
                outputTextArea.setText("解析失败：" + e.getMessage());
                e.printStackTrace();
            }
        });

        // 清空按钮事件
        clearBtn.setOnAction(event -> {
            inputTextArea.clear();
            outputTextArea.clear();
        });
    }

    /**
     * 解析输入文本为 JSON 格式
     * 
     * 规则 1: a, b, c[{d,e}] -> c 是对象数组，每个对象包含属性 d 和 e
     * 输出:
     * {
     * 	"a":"a", 
     * 	"b":"b",
     * 	"c":[
     * 	{
     * 		"d":"d",
     * 		"e":"e"
     * 	}
     * 	]
     * }
     * 
     * 规则 2: a, b, c[d,e] -> c 是字符串数组
     * 输出:
     * {
     * 	"a":"a", 
     * 	"b":"b",
     * 	"c":[
     * 		"d", "e"
     * 	]
     * }
     * 
     * 规则 3: 如果值为数字，则不加引号
     * 例如：1, 2, 3 -> {"1": 1, "2": 2, "3": 3}
     * 
     * 规则 4: 对象的 value 随机生成 (key+两位随机数字)
     * - 简单对象：a, b -> {"a": "a23", "b": "b56"}
     * - 对象数组：c[{d,e}] -> {"d": "d12", "e": "e34"}
     */
    public String parseToJson(String input) {
        // 移除所有空格
        input = input.replaceAll("\\s+", "");
        
        StringBuilder json = new StringBuilder("{");
        
        // 解析各个部分
        String[] parts = parseParts(input);
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            json.append("\n");
            
            // 检查是否包含数组（方括号）
            if (part.contains("[") && part.contains("]")) {
                // 解析数组格式：key[value1,value2,...]
                int bracketStart = part.indexOf('[');
                int bracketEnd = part.indexOf(']');
                String key = part.substring(0, bracketStart);
                String arrayContent = part.substring(bracketStart + 1, bracketEnd);
                
                json.append("\t\"").append(key).append("\": [");
                
                // 检查是否是对象数组格式：{field1,field2}
                if (arrayContent.startsWith("{") && arrayContent.endsWith("}")) {
                    // 对象数组格式：c[{d,e}]
                    String objContent = arrayContent.substring(1, arrayContent.length() - 1);
                    
                    json.append("\n\t\t{\n");
                    String[] objFields = objContent.split(",");
                    for (int j = 0; j < objFields.length; j++) {
                        String field = objFields[j].trim();
                        // 检查字段名是否为数字，如果是数字则值不加引号
                        if (isNumeric(field)) {
                            json.append("\t\t\t\"").append(field).append("\": ").append(field);
                        } else {
                            // 对象数组中的字段，value 随机生成：key + 两位随机数字
                            String randomValue = field + generateRandomTwoDigits();
                            json.append("\t\t\t\"").append(field).append("\": \"").append(randomValue).append("\"");
                        }
                        if (j < objFields.length - 1) {
                            json.append(",\n");
                        } else {
                            json.append("\n");
                        }
                    }
                    json.append("\t\t}\n");
                    json.append("\t]");
                    
                } else {
                    // 字符串数组格式：c[d,e]
                    String[] arrayItems = arrayContent.split(",");
                    json.append("\n");
                    for (int j = 0; j < arrayItems.length; j++) {
                        String item = arrayItems[j].trim();
                        // 检查数组项是否为数字
                        if (isNumeric(item)) {
                            if (j == 0) {
                                json.append("\t\t").append(item);
                            } else {
                                json.append(", ").append(item);
                            }
                        } else {
                            if (j == 0) {
                                json.append("\t\t\"").append(item).append("\"");
                            } else {
                                json.append(", \"").append(item).append("\"");
                            }
                        }
                    }
                    json.append("\n\t]");
                }
                
            } else {
                // 简单键值对（对象）
                // 检查是否为数字，如果是数字则不加引号且不随机
                if (isNumeric(part)) {
                    json.append("\t\"").append(part).append("\": ").append(part);
                } else {
                    // 对象的 value 随机生成：key + 两位随机数字
                    String randomValue = part + generateRandomTwoDigits();
                    json.append("\t\"").append(part).append("\": \"").append(randomValue).append("\"");
                }
            }
            
            // 添加逗号（如果不是最后一个元素）
            if (i < parts.length - 1) {
                json.append(",");
            }
        }
        
        json.append("\n}");
        
        return json.toString();
    }

    /**
     * 生成两位随机数字
     * @return 两位随机数字字符串 (00-99)
     */
    private String generateRandomTwoDigits() {
        int randomNum = (int) (Math.random() * 100);
        return String.format("%02d", randomNum);
    }

    /**
     * 判断字符串是否为数字
     * @param str 待判断的字符串
     * @return 如果是数字返回 true，否则返回 false
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 解析输入的各个部分
     * 处理逗号分隔的字段，包括数组字段
     */
    private String[] parseParts(String input) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        int bracketDepth = 0; // 跟踪方括号深度
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (c == '[') {
                bracketDepth++;
                current.append(c);
            } else if (c == ']') {
                bracketDepth--;
                current.append(c);
            } else if (c == ',' && bracketDepth == 0) {
                // 只有在不在括号内时才分割
                String part = current.toString().trim();
                if (!part.isEmpty()) {
                    parts.add(part);
                }
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        // 添加最后一部分
        String lastPart = current.toString().trim();
        if (!lastPart.isEmpty()) {
            parts.add(lastPart);
        }
        
        return parts.toArray(new String[0]);
    }

    @Override
    public Tab getTab() {
        return jsonGenTab;
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        JsonGenTab jsonGenTab = new JsonGenTab();
        
        // 测试用例 1: 对象数组
        String input1 = "a, b, c[{d,e}]";
        System.out.println("输入：" + input1);
        System.out.println("输出:");
        System.out.println(jsonGenTab.parseToJson(input1));
        System.out.println();
        
        // 测试用例 2: 字符串数组
        String input2 = "a, b, c[d,e]";
        System.out.println("输入：" + input2);
        System.out.println("输出:");
        System.out.println(jsonGenTab.parseToJson(input2));
        System.out.println();
        
        // 测试用例 3: 混合
        String input3 = "x, y, z[{m,n,o}]";
        System.out.println("输入：" + input3);
        System.out.println("输出:");
        System.out.println(jsonGenTab.parseToJson(input3));
    }
}
