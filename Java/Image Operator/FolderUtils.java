package com.admin.common.util;
import java.io.File;
import org.apache.commons.io.FilenameUtils;

/**
 * @description
 * 文件夹工具
 * @ClassName: ImageUtils 
 * @author ChaoS_Zhang t7_chaos@163.com 
 * @date 2017年3月9日 上午9:34:09
 */
public class FolderUtils {

    /**
     * 创建完整路径
     *
     * @param path
     *            a {@link java.lang.String} object.
     */
    public static final void mkdirs(final String... path) {
        for (String foo : path) {
            final String realPath = FilenameUtils.normalizeNoEndSeparator(foo, true);
            final File folder = new File(realPath);
            if (!folder.exists() || folder.isFile()) {
                folder.mkdirs();
            }
        }
    }

}