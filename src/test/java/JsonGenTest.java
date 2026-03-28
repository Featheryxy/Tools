package ind.milo.panes;

/**
 * 测试 JSON 生成规则
 */
public class JsonGenTest {
    public static void main(String[] args) {
        JsonGenTab jsonGenTab = new JsonGenTab();
        
        // 测试用例 1: 对象数组
        String input1 = "a, b, c[{d,e}]";
        System.out.println("输入：" + input1);
        System.out.println(jsonGenTab.parseToJson(input1));
        System.out.println();
        
        // 测试用例 2: 字符串数组
        String input2 = "a, b, c[d,e]";
        System.out.println("输入：" + input2);
        System.out.println(jsonGenTab.parseToJson(input2));
        System.out.println();
        
        // 测试用例 3: 混合
        String input3 = "x, y, z[{m,n,o}]";
        System.out.println("输入：" + input3);
        System.out.println(jsonGenTab.parseToJson(input3));
    }
}
