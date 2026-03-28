package ind.milo.test;

/**
 * 字段提取器 - 用户示例测试
 */
public class UserExampleTest {
    
    public static void main(String[] args) {
        System.out.println("========== 用户示例测试 ==========\n");
        
        // 用户提供的示例
        String userExample = "/**\n" +
                            "     * id\n" +
                            "     */\n" +
                            "    private Integer todoId;\n" +
                            "    private String todoName;";
        
        System.out.println("【用户示例】");
        System.out.println("输入:");
        System.out.println(userExample);
        System.out.println("\n预期输出：todoId, todoName");
        System.out.println("实际输出：" + extractFields(userExample));
        System.out.println();
        
        System.out.println("========== 测试完成 ==========");
    }
    
    /**
     * 从 Java 代码中提取字段名
     */
    public static String extractFields(String code) {
        StringBuilder fields = new StringBuilder();
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            
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
            
            if (line.contains(";")) {
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

    private static String extractFieldName(String line) {
        if (line.contains("//")) {
            line = line.substring(0, line.indexOf("//"));
        }
        
        int semicolonIndex = line.indexOf(';');
        if (semicolonIndex > 0) {
            line = line.substring(0, semicolonIndex).trim();
        }
        
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            return parts[parts.length - 1];
        }
        
        return null;
    }
}
