package ind.milo.test;

import ind.milo.panes.FieldExtractorTab;

/**
 * 字段提取器测试
 */
public class FieldExtractorTest {
    public static void main(String[] args) {
        FieldExtractorTab extractor = new FieldExtractorTab();
        
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
        System.out.println("\n输出:");
        System.out.println(extractor.extractFields(test1));
        System.out.println();
        
        // 测试用例 2: 多种修饰符
        String test2 = "public String name;\n" +
                      "private int age;\n" +
                      "protected boolean active;\n" +
                      "static final String CONSTANT = \"value\";";
        
        System.out.println("【测试 2】多种修饰符");
        System.out.println("输入:");
        System.out.println(test2);
        System.out.println("\n输出:");
        System.out.println(extractor.extractFields(test2));
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
        System.out.println("\n输出:");
        System.out.println(extractor.extractFields(test3));
        System.out.println();
        
        // 测试用例 4: 混合代码
        String test4 = "public class User {\n" +
                      "    /** ID */\n" +
                      "    private Integer id;\n" +
                      "    \n" +
                      "    /** 姓名 */\n" +
                      "    private String name;\n" +
                      "    \n" +
                      "    public void doSomething() {\n" +
                      "        // 这是方法\n" +
                      "    }\n" +
                      "}";
        
        System.out.println("【测试 4】混合代码");
        System.out.println("输入:");
        System.out.println(test4);
        System.out.println("\n输出:");
        System.out.println(extractor.extractFields(test4));
        System.out.println();
        
        System.out.println("========== 测试完成 ==========");
    }
}
