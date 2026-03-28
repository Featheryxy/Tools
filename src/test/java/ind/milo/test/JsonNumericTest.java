package ind.milo.test;

/**
 * JSON 生成规则测试 - 数字类型
 */
public class JsonNumericTest {
    
    public static void main(String[] args) {
        // 测试用例 1: 纯数字
        String input1 = "1, 2, 3";
        System.out.println("输入：" + input1);
        System.out.println("输出:");
        System.out.println(parseToJson(input1));
        System.out.println();
        
        // 测试用例 2: 小数
        String input2 = "1.5, 2.8, 3.14";
        System.out.println("输入：" + input2);
        System.out.println("输出:");
        System.out.println(parseToJson(input2));
        System.out.println();
        
        // 测试用例 3: 混合
        String input3 = "name, age, 100";
        System.out.println("输入：" + input3);
        System.out.println("输出:");
        System.out.println(parseToJson(input3));
        System.out.println();
        
        // 测试用例 4: 对象数组中的数字
        String input4 = "user[{id,name,age}]";
        System.out.println("输入：" + input4);
        System.out.println("输出:");
        System.out.println(parseToJson(input4));
        System.out.println();
        
        // 测试用例 5: 字符串数组中的数字
        String input5 = "data[1,2,3]";
        System.out.println("输入：" + input5);
        System.out.println("输出:");
        System.out.println(parseToJson(input5));
    }
    
    /**
     * 解析输入文本为 JSON 格式
     */
    public static String parseToJson(String input) {
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
                            json.append("\t\t\t\"").append(field).append("\": \"").append(field).append("\"");
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
                // 简单键值对
                // 检查是否为数字，如果是数字则不加引号
                if (isNumeric(part)) {
                    json.append("\t\"").append(part).append("\": ").append(part);
                } else {
                    json.append("\t\"").append(part).append("\": \"").append(part).append("\"");
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
     * 判断字符串是否为数字
     * @param str 待判断的字符串
     * @return 如果是数字返回 true，否则返回 false
     */
    private static boolean isNumeric(String str) {
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
    private static String[] parseParts(String input) {
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
}
