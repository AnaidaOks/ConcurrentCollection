package myThread;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static BlockingQueue<String> bqFirst = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> bqSecond = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> bqThird = new ArrayBlockingQueue<>(100);
    public static final int textsLength = 10_000;
    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            for (int i = 0; i < textsLength; i++) {
                try {
                    bqFirst.put(generateText("abc", 100));
                    bqSecond.put(generateText("abc", 100));
                    bqThird.put(generateText("abc", 100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        AtomicInteger aMaxCount = new AtomicInteger();
        AtomicInteger bMaxCount = new AtomicInteger();
        AtomicInteger cMaxCount = new AtomicInteger();
        AtomicReference<String> aText = new AtomicReference<>("");
        AtomicReference<String> bText = new AtomicReference<>("");
        AtomicReference<String> cText = new AtomicReference<>("");

        // count 'a'
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    String text = bqFirst.take();
                    AtomicInteger aCounter = new AtomicInteger(0);
                    for (int j = 0; j < text.length(); j++) {
                        if (text.charAt(j) == 'a') {
                            aCounter.getAndIncrement();
                        }
                    }

                    if (aMaxCount.get() < aCounter.get()) {
                        aMaxCount.set(aCounter.get());
                        aText.set(text);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // count 'b'
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    String text = bqSecond.take();
                    AtomicInteger bCounter = new AtomicInteger(0);
                    for (int j = 0; j < text.length(); j++) {
                        if (text.charAt(j) == 'b') {
                            bCounter.getAndIncrement();
                        }
                    }

                    if (bMaxCount.get() < bCounter.get()) {
                        bMaxCount.set(bCounter.get());
                        bText.set(text);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // count 'c'
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    String text = bqThird.take();
                    AtomicInteger cCounter = new AtomicInteger(0);
                    for (int j = 0; j < text.length(); j++) {
                        if (text.charAt(j) == 'c') {
                            cCounter.getAndIncrement();
                        }
                    }

                    if (cMaxCount.get() < cCounter.get()) {
                        cMaxCount.set(cCounter.get());
                        cText.set(text);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        Thread.sleep(2500);
        System.out.println("Text contains max 'a': " + aMaxCount.get());
        System.out.println(aText.get());
        System.out.println("Text contains max 'b': " + bMaxCount.get());
        System.out.println(bText.get());
        System.out.println("Text contains max 'c': " + cMaxCount.get());
        System.out.println(cText.get());
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        System.out.println("text: " + text);
        return text.toString();
    }
}