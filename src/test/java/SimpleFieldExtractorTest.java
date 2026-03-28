package ind.milo.test;

/**
 * 字段提取器简单测试（不依赖 JavaFX）
 */
public class SimpleFieldExtractorTest {
    
    public static void main(String[] args) {
        System.out.println("========== 字段提取器测试 ==========\n");
        
        // 测试用例 1: 基本字段
        String test1 = "/**\n" +
                      " * id\n" +
                      " */\n" +
                      "private Integer todoId;\n" +
                      "private String todoName;";
        
        System.out.println("【测试 1】基本字段");
        System.out.println("输入:");
        System.out.println(test1);
        System.out.println("\n预期输出：todoId, todoName");
        System.out.println("实际输出：" + extractFields(test1));
        System.out.println();
        
        // 测试用例 2: 带注解的字段
        String test2 = "/** 用户 ID */\n" +
                      "@NotNull\n" +
                      "private Long userId;\n" +
                      "/** 用户名 */\n" +
                      "@Size(max = 50)\n" +
                      "private String username;\n" +
                      "// 邮箱地址\n" +
                      "private String email;";
        
        System.out.println("【测试 2】带注解的字段");
        System.out.println("输入:");
        System.out.println(test2);
        System.out.println("\n预期输出：userId, username, email");
        System.out.println("实际输出：" + extractFields(test2));
        System.out.println();
        
        // 测试用例 3: 多种修饰符
        String test3 = "public String name;\n" +
                      "private int age;\n" +
                      "protected boolean active;\n" +
                      "static final String CONSTANT = \"value\";";
        
        System.out.println("【测试 3】多种修饰符");
        System.out.println("输入:");
        System.out.println(test3);
        System.out.println("\n预期输出：name, age, active, CONSTANT");
        System.out.println("实际输出：" + extractFields(test3));
        System.out.println();
        
        System.out.println("========== 测试完成 ==========");
    }
    
    /**
     * 从 Java 代码中提取字段名（简化版本，用于测试）
     */
    public static String extractFields(String code) {
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
     */
    private static String extractFieldName(String line) {
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
}
