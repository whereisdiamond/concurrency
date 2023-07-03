import java.util.Random;

public class Concurrency {

    public static void main(String[] args) {
        int[] nums = genRanNums(200000000, 1, 10);

        // Compute sum using multiple threads
        long startPara = System.currentTimeMillis();
        long sumPara = computeParallel(nums);
        long endPara = System.currentTimeMillis();
        long totalPara = endPara - startPara;

        // Compute sum using single thread
        long startSingle = System.currentTimeMillis();
        long sumSingle = computeSingle(nums);
        long endSingle = System.currentTimeMillis();
        long totalSingle = endSingle - startSingle;

        // Display results
        System.out.println("Sum using multiple threads: " + sumPara);
        System.out.println("Total time using multiple threads: " + totalPara + " milliseconds");
        System.out.println("Sum using single thread: " + sumSingle);
        System.out.println("Total time using single thread: " + totalSingle + " milliseconds");
    }

    /**
     * Generates an array of random numbers within a given range.
     *
     * @param size The size of the array
     * @param min  The minimum value of the random numbers
     * @param max  The maximum value of the random numbers
     * @return An array of random numbers
     */
    public static int[] genRanNums(int size, int min, int max) {
        int[] numbers = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            numbers[i] = random.nextInt(max - min + 1) + min;
        }
        return numbers;
    }

    /**
     * Computes the sum of an array of numbers using multiple threads.
     *
     * @param numbers The array of numbers
     * @return The sum of the numbers
     */
    public static long computeParallel(int[] numbers) {
        long sum = 0;
        int numThreads = Runtime.getRuntime().availableProcessors();
        int chunkSize = numbers.length / numThreads;

        Thread[] threads = new Thread[numThreads];
        SumThread[] sumThreads = new SumThread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int startIndex = i * chunkSize;
            int endIndex = (i == numThreads - 1) ? numbers.length : (i + 1) * chunkSize;
            sumThreads[i] = new SumThread(numbers, startIndex, endIndex);
            threads[i] = new Thread(sumThreads[i]);
            threads[i].start();
        }

        try {
            for (int i = 0; i < numThreads; i++) {
                threads[i].join();
                sum += sumThreads[i].getSum();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sum;
    }

    /**
     * Computes the sum of an array of numbers using a single thread.
     *
     * @param numbers The array of numbers
     * @return The sum of the numbers
     */
    public static long computeSingle(int[] numbers) {
        long sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }

    /**
     * Thread class to compute the sum of a portion of an array.
     */
    public static class SumThread implements Runnable {
        private int[] numbers;
        private int startIndex;
        private int endIndex;
        private long sum;

        public SumThread(int[] numbers, int startIndex, int endIndex) {
            this.numbers = numbers;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public void run() {
            for (int i = startIndex; i < endIndex; i++) {
                sum += numbers[i];
            }
        }

        public long getSum() {
            return sum;
        }

        public void setSum(long sum) {
            this.sum = sum;
        }
    }
}
