package ru.dargen.evoplus.util.common;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.dargen.evoplus.EvoPlus;

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.dargen.evoplus.EvoPlus.folder;

@UtilityClass
public class FileUtil {

    @SneakyThrows
    public void setContent(String path, Consumer<BufferedWriter> writer) {
        checkAndMkdirFolder();

        val file = new File(folder, path);

        if (!file.exists())
            file.createNewFile();

        val fileOutputStream = new FileOutputStream(file);
        val outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        val bufferedWriter = new BufferedWriter(outputStreamWriter);

        writer.accept(bufferedWriter);

        bufferedWriter.flush();
        bufferedWriter.close();
    }

    @SneakyThrows
    public void setStringContent(String path, String content) {
        setContent(path, writer -> {
            try {
                writer.write(content);
            } catch (Throwable t) {
                EvoPlus.instance().getLogger().error("Error while write to file", t);
            }
        });
    }

    @SneakyThrows
    public <T> T getContent(String path, Function<BufferedReader, T> transform) {
        checkAndMkdirFolder();

        val file = new File(folder, path);

        if (!file.exists())
            return null;

        val fileInputStream = new FileInputStream(file);
        val inputStreamReader = new InputStreamReader(fileInputStream);
        val bufferedReader = new BufferedReader(inputStreamReader);

        val result = transform.apply(bufferedReader);

        bufferedReader.close();

        return result;
    }

    public String getStringContent(String path) {
        return getContent(path, reader -> reader.lines().collect(Collectors.joining("\n")));
    }

    public void checkAndMkdirFolder() {
        if (!folder.exists())
            folder.mkdirs();

        if (!folder.isDirectory()) {
            folder.delete();
            checkAndMkdirFolder();
        }
    }

}
