package com.red.innopolis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    String str = reader.readLine(); // Построчно читаем файл f
                    
                    while (str != null) {
    
                        Pattern p = Pattern.compile("-?\\d+");
                        Matcher m = p.matcher(str);
                        
                        while (m.find()) {
                            Integer number = Integer.valueOf(m.group());
                            if (number > 0 && number % 2 == 0)
                                Resource.incrementSum();
                        }
                        Resource.printSum();
                        try {
                            Thread.sleep(1000);
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
//          e.printStackTrace();
            System.out.println("All threads are Interrupted!");
        }
    }
    
    //    Метод для вывода мапы на консоль
    private synchronized void printOrder () {
        String leftAlignFormat = "| %-10s | %-9d |%n"; // Строка форматированного вывода
        System.out.format("+------------+-----------+%n");
        System.out.format("|   Words    |  Entries  |%n");
        System.out.format("+------------+-----------+%n");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.format(leftAlignFormat, "    " + entry.getKey(), entry.getValue());
            System.out.format("+------------+-----------+%n");
        }
    }
}
