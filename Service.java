package projectOS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Service {
	int numberOfCores, type;
	Socket client;
	String directoryPath, word;

	public Service(int cores, String path, String word, Socket s, int type) throws IOException {
		long totalStart = System.currentTimeMillis();
        this.directoryPath = path;
        numberOfCores = cores;
        Semaphore sem = new Semaphore(1);
        
        ArrayList<File> files = new ArrayList<File>();
		Process process = Runtime.getRuntime().exec("ls " + path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		PrintStream out = new PrintStream(s.getOutputStream());
		String file;
		while ((file = reader.readLine()) != null) {
			Process wc = Runtime.getRuntime().exec("wc -w  " + path + "/" + file);
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(wc.getInputStream()));
			files.add(new File(file, path, Integer.parseInt(reader1.readLine().split(" ")[0])));
		}
        Object lock = new Object();

        ArrayList<Thread> threads = new ArrayList<Thread>();
        
        for (int i = 0; i < numberOfCores; i++) {
            Thread thread;
            if (type == 1) {
            	thread = new Thread(new RRMyRunnable(directoryPath, files, lock, i, word, out, numberOfCores), "Thread " + (i + 1));
            } else {
            	thread = new Thread(new EDMyRunnable(sem, directoryPath, word, files, numberOfCores, i, out), "Thread " + (i + 1));
            }
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        out.println("\nFile information:");
        for (File fileOut : files) {
        	out.println(fileOut.toString());
        }
        
        reader.close();
        out.close();
        long totalEnd = System.currentTimeMillis();
        long totalTime = totalEnd - totalStart;
        System.out.println("Total time taken : " + totalTime + "ms\n");
	}
}

 class RRMyRunnable implements Runnable {
    private String directoryPath;
    private ArrayList<File> files;
    String word;
    PrintStream client;
    private final Object lock;
    private final int threadId;
    int numberOfCores;

    public RRMyRunnable(String directoryPath, ArrayList<File> files2, Object lock, int threadId, String word, PrintStream client, int numberOfCores) {
        this.directoryPath = directoryPath;
        this.files = files2;
        this.lock = lock;
        this.threadId = threadId;
        this.word = word;
        this.client = client;
        this.numberOfCores = numberOfCores;
    }

    @Override
    public void run() {
    	long start = System.currentTimeMillis();
        for (int i = threadId; i < files.size(); i += numberOfCores) {
            synchronized (lock) {
                while ((i % numberOfCores) != threadId) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                int count = searchForWord(directoryPath + "/" + files.get(i).getFileName());
				client.println(Thread.currentThread().getName() + " found '" + word + "' " + count + " times in " + files.get(i).getFileName());

                lock.notifyAll();

                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        synchronized (lock) {
            lock.notifyAll();
        }
    	long end = System.currentTimeMillis();
    	long time = end - start;
    	System.out.println("Time for " + Thread.currentThread().getName() + " : " + time + "ms");
    }

    private int searchForWord(String filePath) {
        int count = 0;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"grep", "-i", word, filePath});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (reader.readLine() != null) {
                count++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
}
 
 class EDMyRunnable implements Runnable {
	    private Semaphore sem;
	    private String threadName;
	    private int numThreads;
	    private static int counter=0;
	    private String path, word;
	    private ArrayList<File> files;
	    private PrintStream client;
	    private int threadID;

	    public EDMyRunnable(Semaphore sem, String path, String word, ArrayList<File> files, int numThreads, int threadID, PrintStream client) {
	        this.sem = sem;
	        this.numThreads = numThreads;
	        this.path = path;
	        this.word = word;
	        this.files = files;
	        this.client = client;
	        this.threadID = threadID;
	    }

	    @Override
	    public void run() {
	    	long start = System.currentTimeMillis();
	        try {
	            sem.acquire();
	            for (int i = counter; i < files.size(); i += numThreads) {
	                int count = searchForWord(path + "/" + files.get(i).getFileName());
					client.println(Thread.currentThread().getName() + " found '" + word + "' " + count + " times in " + files.get(i).getFileName());
	            }
	            counter++;

	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } finally {
	            sem.release();
	        }
	        long end = System.currentTimeMillis();
	        long time = end - start;
	    	System.out.println("Time for " + Thread.currentThread().getName() + " : " + time + "ms");
	    }
	    
	    private int searchForWord(String filePath) {
	        int count = 0;
	        try {
	            Process process = Runtime.getRuntime().exec(new String[]{"grep", "-i", word, filePath});
	            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            while (reader.readLine() != null) {
	                count++;
	            }
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return count;
	    }
	}

