package ind.milo.util;

import java.io.File;
import java.util.List;

public class FileUtil {
    private FileUtil() {

    }

    /**
     * 列出文件夹下的一级文件
     * @param path 文件路径
     */
    public static void listFile(String path){
        File file = new File(path);
        if (!file.isDirectory()) {
            System.out.println();
        }

    }

    public static void main(String[] args) {
        String path = "F:\\GitHubNote\\tools\\src\\main\\java\\ind\\milo\\util";
        listFile(path);
    }
}
