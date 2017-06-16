package com.red.innopolis;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by _red_ on 16.06.17.
 */
public class ResourceThread extends Thread {
    private static volatile boolean stopALL = false;
    private String path;
//    private String compiler; // Регулярное выражение для поиска в строке
//    private String spliter; // Разделитель слов
    private int numbersOfThread; // Кол-во потоков
    private volatile Map<String, Integer> map = new ConcurrentHashMap<>();
    
    ResourceThread (String path, int numbersOfThread) {
        this.path = path;
        this.numbersOfThread = numbersOfThread;
    }
    
    @Override
    public void run () {
        try {
            File directory = new File(path);
            // Убедимся, что директория найдена и это реально директория, а не файл.
            if (directory.exists() && directory.isDirectory()) {
                processDirectory(directory);
            } else {
                System.out.println("Не удалось найти директорию по указанному пути.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void processDirectory (File directory) {
        // Получаем список доступных файлов в указанной директории.
        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println("Нет доступных файлов для обработки.");
            return;
        } else {
            System.out.println("Количество файлов для обработки: " + files.length);
        }

//      Непосредственно многопоточная обработка файлов. Создаем наш экзекутор
        ExecutorService service = Executors.newFixedThreadPool(this.numbersOfThread);

//      Проверем валидность файлов
        for (final File f : files) {
            if (!f.isFile()) {
                continue;
            }

//          Собственно наш пулл
            service.execute(() -> {
                try {
                    String str;
                    long num = 0;
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    while (( str = reader.readLine()) != null) {
                        num = Arrays.stream(str.split(" ")).
                                mapToInt(Integer::parseInt).filter(o -> o > 0 && o % 2 == 0).sum();
                        Resource.superIncrementSum(num);
                        Resource.printSum();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException ex) { ex.printStackTrace(); }
            });
        }
        // Новые задачи более не принимаем, и завершаем оставшиеся.
        service.shutdown();
        // Ждем завершения
        try {
            service.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("All threads are Interrupted!");
        }
    }
}
