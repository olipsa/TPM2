import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
	// write your code here
        OptimisticList<Integer> list=new OptimisticList<>();
        long startTime = System.nanoTime();
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100000; i++) {
            int finalI = i;
            threadPool.submit(new Runnable() {
                public void run() {
                    list.add(finalI+1);
                }
            });
        }
        for(int i=0;i<25000;i+=2) {
            int finalI = i;
            threadPool.submit(new Runnable() {
                public void run() {
                    list.remove(finalI + 1);
                }
            });
        }
        // once you've submitted your last job to the service it should be shut down
        threadPool.shutdown();
        // wait for the threads to finish if necessary
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        System.out.println("List contains following values:");
        list.printList();
        System.out.println("Execution time: "+(float)duration/1000+"s\n");

        //Testing OptimisticList with versioning
        OptimisticListVersioning<Integer>list2=new OptimisticListVersioning<>();
        startTime = System.nanoTime();
        threadPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100000; i++) {
            int finalI = i;
            threadPool.submit(new Runnable() {
                public void run() {
                    list2.add(finalI+1);
                }
            });
        }
        for(int i=0;i<25000;i+=2) {
            int finalI = i;
            threadPool.submit(new Runnable() {
                public void run() {
                    list2.remove(finalI + 1);
                }
            });
        }
        // once you've submitted your last job to the service it should be shut down
        threadPool.shutdown();
        // wait for the threads to finish if necessary
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        endTime = System.nanoTime();
        duration = (endTime - startTime)/1000000;
        System.out.println("List contains following values:");
        list2.printList();
        System.out.println("Execution time: "+(float)duration/1000+"s");



    }
}
