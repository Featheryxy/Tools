package ind.milo.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ind.milo.entity.TaskItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @Date 2022/10/8 17:01
 * @Created by Milo
 */
@Slf4j
public class FreemarkerUtil {
    private  String ftlPath;
    private  String ftlName;
    private Configuration cfg;
    private Template template;

    public FreemarkerUtil(String ftlPath, String ftlName) {
        this.ftlPath = ftlPath;
        this.ftlName = ftlName;
//        config();
    }

    private void setConfig() throws IOException {
        cfg = new Configuration(Configuration.getVersion());
        cfg.setDirectoryForTemplateLoading(new File(ftlPath));
    }

    public String exce(Object dataModel) throws IOException, TemplateException {
        setConfig();
        template = cfg.getTemplate(ftlName);

        StringWriter stringWriter = new StringWriter();
        template.process(dataModel, stringWriter);
        String resultStr = stringWriter.toString();
        System.out.println(resultStr);
        return resultStr;
    }


    public static void main(String[] args) throws TemplateException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String ftlPath = "F:\\JavaFX\\tools\\src\\main\\resources\\ftl";
        String ftlName = "mdf.ftl";
        FreemarkerUtil freemarkerUtil = new FreemarkerUtil(ftlPath, ftlName);
//        Map map = new HashMap();
//        map.put("hello", "Hello FreeMarker!");
//        map.put("name", "这是一段带有攻击性的字符串请点击");
//        freemarkerUtil.exce(map);
        TaskItem taskItem = new TaskItem("123", "干活");
        Map<String, String> stringMap = BeanUtils.describe(taskItem);

        freemarkerUtil.exce(stringMap);
    }
}
