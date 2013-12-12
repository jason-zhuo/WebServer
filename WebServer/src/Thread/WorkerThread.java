package Thread;

class WorkerThread extends Thread {

	public boolean busy;
	public Threadpool owner;

	WorkerThread(Threadpool o) {

		owner = o;
	}

	public void run() {

		Runnable target = null;

		do {

			target = owner.getAssignment();

			if (target != null) {

				target.run();
				owner.done.workerEnd();
			}
		} while (target != null);
	}
}
