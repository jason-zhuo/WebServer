package Thread;

public class Finish {
	private int     activeThreads = 0;
    private boolean started = false;

	/**
	 * ÃèÊö£º wait until all the work is done
	 * 
	 */
    public synchronized void waitDone() {

        try {

            while (activeThreads > 0) {

                wait ();
            }
        }
        catch (InterruptedException e) {

        }
    }

    /**
     * ÃèÊö£ºwait until the work is begin
     */
    public synchronized void waitBegin() {

        try {

            while (!started) {

                wait ();
            }
        }
        catch (InterruptedException e) {

        }
    }

    /**
     * ÃèÊö£ºstart new thread, increase the number of active thread by 1
     */
    public synchronized void workerBegin() {

        activeThreads++;
        started = true;
        notify ();
    }

    /**
     * ÃèÊö£ºend a thread, decrease the number of active thread by 1
     */
    public synchronized void workerEnd() {

        activeThreads--;
        notify ();
    }

    /**
     * ÃèÊö£º reset the number of active thread to 0
     */
    public synchronized void reset() {

        activeThreads = 0;
    }
}
