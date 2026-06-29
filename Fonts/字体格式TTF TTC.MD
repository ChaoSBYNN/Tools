这是一个很实用的问题。**TTF** 和 **TTC** 都是字体文件的格式，但它们的核心区别在于 **TTC 是“合集”**。

简单理解：**TTF 是“单张照片”，TTC 是“一本相册”**。下面是具体的对比和关键要点。

### 1. 核心区别：单个 vs. 合集

- **TTF (TrueType Font)**：一个文件只包含 **一套** 字体（如“思源黑体 Regular”）。
- **TTC (TrueType Collection)**：一个文件可以包含 **多套** 字体（如“思源黑体 Regular”、“思源黑体 Bold”、“思源黑体 Light” 全在同一个文件里）。

### 2. 详细对比

| 对比维度 | TTF（单个字体） | TTC（字体集合） |
| :--- | :--- | :--- |
| **文件结构** | 单一字体数据 | 多个字体数据打包在一起 |
| **文件大小** | 相对较小 | 通常比单个 TTF 大，但比多个独立 TTF 的总和**要小**（因为有共享数据） |
| **兼容性** | 极高，几乎所有系统和软件都支持 | 较高，但少数老旧软件（或特定环境的 Java）可能只支持读取 TTC 中的第一个字体 |
| **常见场景** | Windows、macOS 大部分字体 | 日文/中文字体（如微软雅黑、思源黑体）、Office 安装包里的字体 |

### 3. 在 Java / Spring Boot 中使用 TTC 的注意点

这是你在开发中需要特别留意的地方。

- **`Font.createFont()` 只认 TTF**：标准的 `Font.createFont(Font.TRUETYPE_FONT, inputStream)` **只支持 TTF 文件**。如果直接传入 TTC 文件，通常会抛出 `FontFormatException` 或 `IOException`。

- **如何加载 TTC 文件**：Java 提供了一个专门的类 `java.awt.Font` 来处理 TTC，需要使用**带索引参数**的方法，指定你想取用合集里的第几款字体。

**示例代码：加载 TTC 中的特定字体**

假设你的资源目录下有一个 `SourceHanSans.ttc` 文件，里面包含 Regular、Bold、Light 三种字体。

```java
import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;

public class FontUtil {

    /**
     * 加载 TTC 文件中的指定字体
     * @param fontPath 资源路径，如 "fonts/SourceHanSans.ttc"
     * @param fontIndex TTC 中的字体索引 (从 0 开始)
     * @param fontSize 字体大小
     * @return Font 对象
     */
    public static Font loadTtcFromResources(String fontPath, int fontIndex, float fontSize) {
        try {
            // 获取文件输入流
            ClassPathResource resource = new ClassPathResource(fontPath);
            InputStream inputStream = resource.getInputStream();

            // 关键方法：使用 createFont 并传入 fontIndex
            // 第一个参数 Font.TRUETYPE_FONT 对 TTC 同样适用
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            
            // 注意：上面的 createFont 只能读取 TTC 中的第一个字体 (索引0)
            // 要获取指定索引的字体，需要先创建 Font 对象，再通过 deriveFont 转换
            // 实际上更准确的做法是：
            // 使用 File 对象方式，或通过 Font 的静态方法获取全部字体名称
            // 但因为你在用 InputStream，更推荐下面的方法：
            
            // 方案：先通过临时文件或直接使用 createFont 获取基础 Font，
            // 然后利用 Font 的 getFamily() 等方法来确认，但索引控制较麻烦。
            // 最稳妥的方式是先将字体写入临时文件，再用 File 方式读取。
            
            // 这里提供一个使用 File 方式的简洁写法：
            // File tempFile = resource.getFile(); // 注意：JAR 包中运行时可能抛异常
            
            // 如果你确定 TTC 中你需要的字体是第一个，可以这样：
            return font.deriveFont(fontSize); // 默认取索引0
            
            // 如果需要索引非0，建议用 File 方式（下面的方法）
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("宋体", Font.PLAIN, (int) fontSize);
        }
    }

    /**
     * 从 TTC 文件中按索引加载字体（更可靠的写法）
     * 注意：这个方法在打成 JAR 包运行时，如果直接使用 resource.getFile() 会失败，
     * 必须通过 InputStream 复制到临时文件。
     */
    public static Font loadTtcByIndex(String fontPath, int fontIndex, float fontSize) {
        try {
            // 1. 从资源中获取输入流
            InputStream is = FontUtil.class.getClassLoader().getResourceAsStream(fontPath);
            
            // 2. 将输入流复制到临时文件（因为 Font.createFont(File) 支持 TTC）
            File tempFile = File.createTempFile("font", ".ttc");
            tempFile.deleteOnExit();
            java.nio.file.Files.copy(is, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // 3. 使用 File 方式加载，并指定索引
            Font font = Font.createFont(Font.TRUETYPE_FONT, tempFile);
            // 对于 TTC，需要在创建后通过 deriveFont 来“切换”字体吗？不，
            // 实际上 Font.createFont(Font.TRUETYPE_FONT, File) 对于 TTC 默认只读第一个。
            // 要读取指定索引，需要使用 Font 的静态方法: 
            // Font[] fonts = Font.createFonts(File) 或 Font.createFonts(InputStream)
            // 但这两个方法返回 Font 数组。
            
            // 正确的用法是：
            Font[] fonts = Font.createFonts(tempFile); // 返回 TTC 中所有字体
            if (fontIndex >= 0 && fontIndex < fonts.length) {
                return fonts[fontIndex].deriveFont(fontSize);
            } else {
                return fonts[0].deriveFont(fontSize); // 降级到第一个
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("宋体", Font.PLAIN, (int) fontSize);
        }
    }
}
```

### 4. 实际场景建议

- **如果你只是需要一个普通字体**：**优先使用 TTF**，省心，兼容性好，代码简单。
- **如果你需要多种字重（粗细）**：可以考虑使用 **TTC**，因为它更节省空间（共享字形数据）。但加载代码要复杂一些，需要注意 JAR 包环境下对临时文件的处理。
- **中文场景**：很多中文字体（如思源黑体、微软雅黑）都以 TTC 形式提供，因为它包含了 Regular、Bold、Light 等多个字重。

### 总结一句话

**TTF 是单品，TTC 是套装。用 TTF 省事，用 TTC 省空间，但在 Java 里加载 TTC 需要多写几行代码来指定索引。** 在你的 Spring Boot 项目中，如果只是加载一个字体来用，建议直接用 TTF，避免踩坑。
