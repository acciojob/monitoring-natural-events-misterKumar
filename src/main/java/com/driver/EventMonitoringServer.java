package com.driver;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventMonitoringServer {
    private static final int THREAD_POOL_SIZE = 5;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private static final AtomicBoolean highMagnitudeEventDetected = new AtomicBoolean(false);

    public static void main(String[] args) {
        try {
            startServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    private static void startServer() throws InterruptedException {
        while (true) {
            if (shutdownLatch.getCount() == 0 || highMagnitudeEventDetected.get()) {
                break; // Exit loop if shutdown is requested or high magnitude event is detected
            }
            int eventId = getNextEventId();
            processEvent(eventId);
        }
    }

    private static int getNextEventId() {
        // Simulated method to get the next event ID from sensors
        return (int) (Math.random() * 10) + 1; // Assuming events IDs range from 1 to 10
    }

    private static void processEvent(int eventId) {
        System.out.println("Event " + eventId + " processed.");
        if (eventId % 5 == 0) {
            System.out.println("High magnitude event detected!");
            highMagnitudeEventDetected.set(true);
        }
    }

    private static void waitForShutdownSignal() throws InterruptedException {
        while (true) {
            String userInput = getUserInput();
            if ("shutdown".equals(userInput)) {
                shutdownLatch.countDown(); // Signal to stop the server
                break;
            }
        }
    }

    private static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static void stopServer() {
        try {
            // Wait for the server to shut down gracefully or until timeout (10 seconds)
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!executorService.isTerminated()) {
                System.err.println("Forcing shutdown...");
                executorService.shutdownNow(); // Force shutdown if not terminated
            }
            System.out.println("Shutting down the server gracefully...");
        }
    }
}
