package Thread;

import java.util.ArrayList;
import java.util.Collection;


public class Threadpool {
	static boolean islogging = true;
	protected Thread[] threads = null;
	Collection assignments = new ArrayList(10); // ³õÊ¼Îª3¸öÔªËØ£¬¶¯Ì¬Ôö¼õ¡£
	protected Finish done = new Finish();
	private static Threadpool pool = null;
	// thread pool size
	static int threadSize = 5;

	/**
	 * ÃèÊö£º construct function
	 * 
	 * @param size
	 *            refers to the size of the thread pool
	 */
	private Threadpool(int size) {
		threads = new WorkerThread[size];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new WorkerThread(this);
			threads[i].start();
		}
	}

	/**
	 * ÃèÊö£º initiate the Thread pool size is 10
	 * 
	 */
	public static Threadpool getInstance() {
		if (pool == null) {
			pool = new Threadpool(threadSize); // 10
		}
		return pool;
	}

	/**
	 * ÃèÊö£º register the task to be execute, add r to the task list array
	 * 
	 * @param Runnable
	 *            thread r
	 * @return void
	 */
	public synchronized void assign(Runnable r) {
		done.workerBegin();
		assignments.add(r);
		notify();
	}

	/**
	 * ÃèÊö£º get r from the task list array and remove it
	 * 
	 * @param Runnable
	 *            thread r
	 * @return next Runnable r in the task list
	 */
	public synchronized Runnable getAssignment() {
		try {

			while (!assignments.iterator().hasNext())
				wait();

			Runnable r = (Runnable) assignments.iterator().next();
			assignments.remove(r);
			return r;
		} catch (InterruptedException e) {

			done.workerEnd();

			return null;
		}

	}

    public void complete() {

        done.waitBegin ();
        done.waitDone ();
    }

  
    protected void finalize() {

        done.reset ();

        for (int i = 0; i < threads.length; i++) {

            threads [ i ].interrupt ();
            done.workerBegin ();

            //threads[i].destroy();
        }

        done.waitDone ();
    }
}
