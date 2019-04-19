package Collection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Process {
	
	String fileName = "out.txt";


	private int id;					//ID of the process
	private String state;			//Process state
	private int size;				//Process size
	private int reqTime;			//Process needed time in the CPU
	private int usedTime;			//Process time spent in CPU
	private boolean memUse;			//Process indicator of memory usage
	private int memLoc;				//Process memory location.
	private int priority;			//Process Priority
	private boolean diskUse;		//Process indicator of Disk Usage		
	private int diskLoc;			//Process disk location.
	private static Queue ready = new Queue(10);
	private static Queue blocked = new Queue(10); 
	
	private static ArrayList<Process> PCB = new ArrayList<Process>();	//ArrayList representing PCB
	private final static int memSizeLow = 256;				//Lowest memory size for process
	private final static int memSizeHigh = 1024;			//Highest memory size for process
	private final static int memStep = 64;					//Memory stepping
	private static final int MIN_REQ_TIME = 3; 	 	// Lowest possible CPU time
	private static final int MAX_REQ_TIME = 15;  	// Highest possible CPU time
	private static final int cpuStep = 3; 			//CPU time stepping
	private static int noProcess = 0;
	private static boolean key = true;		//Semaphore key default to false
	
	
	public Process(int size, int time) {
		this.id = genID();
		this.state = "NEW";
		this.size = size;
		this.reqTime = time;
		this.memUse = genBool();
		if(this.memUse) {
			this.memLoc = genMemLoc();
		}
		this.priority = genPriority();
		this.diskUse = genBool();
		PCB.add(this);
	}

	public Process() {
		this(genSize(), genTime());
	}
	
	public int genID() {
		return ++noProcess;
	}
	
	public boolean genBool() {
		Random r = new Random();
		return r.nextBoolean();
	}
	
	public static int genSize() {
		int range = (memSizeHigh/memStep) - (memSizeLow/memStep);
		int x = ((int) Math.random() * (range+1)+ memSizeLow);
		int size = (memStep * x) + memSizeLow;
		return size;
	}
	
	public static int genTime() {
		int randMax = ( MAX_REQ_TIME / cpuStep ) - ( MIN_REQ_TIME / cpuStep ) + 1;
		int n = (int) (Math.random() * (randMax+1) + MIN_REQ_TIME);
		int time = ( cpuStep * n ) + MIN_REQ_TIME;
		return time;
	}
	
	public int genPriority() {
		int priority = (int) ((Math.random() * 9)+ 1);
		return priority;
	}
	
	public int genMemLoc() {
		int range = (int) ((Math.random() * memSizeHigh) + memSizeLow);
		return range;
	}
	
	public String toString() {
		return "PID: "+ getId() + ", State: " + getState() + ", Size:" 
				+ getSize() + ", Request Time: " + getReqTime() + ", Memory Use: " +
				isMemUse() + ", Memory Location: " + getMemLoc() + ", Priority: " + 
				getPriority() + ", Disk: " + isDiskUse() + ", DiskLoc: " + getDiskLoc();
	}
	
	public static void showPCB() {
		for(int i=0;i<PCB.size();i++) {
			System.out.println(PCB.get(i).toString());
		}
	}
	
	public static void execute() throws InterruptedException {
		// TODO Auto-generated method stub
		while(!ready.isEmpty()) {
			Process pReady = ready.peek();
			if(pReady != null) {
				if(pReady.memUse && key == true) {
					semDown(pReady);
					System.out.println("Process " + pReady.getId() + " Process will be executed");
					int time = pReady.getReqTime();
					System.out.print("Processor will require " + time + " seconds");
					pReady.setState("Running");
					System.out.println(pReady.toString());
					TimeUnit.SECONDS.sleep(time);
					System.out.println("Process " + pReady.getId() + " Finished executing");
					pReady.setState("Terminated");
					System.out.println(pReady.toString());
					semUp(pReady);
					System.out.println("Process " + pReady.getId() + " will be deleted now");
					ready.dequeue();
				}else if (pReady.memUse && key == false) {
					System.out.println("Process " + pReady.getId() + " Blocked for I/O event");
					block(pReady);
				}else {
					System.out.println("Process " + pReady.getId() + " Process will be executed");
					int time = pReady.getReqTime();
					System.out.println("Processor will require " + time + " seconds");
					pReady.setState("Running");
					System.out.println(pReady.toString());
					TimeUnit.SECONDS.sleep(time);
					pReady.setState("Terminated");
					System.out.println("Process " + pReady.getId() + " Finished executing");
					System.out.println(pReady.toString());
					System.out.println("Process " + pReady.getId() + " will be deleted now");
					ready.dequeue();
				}
			}else {
				System.out.println("No more processes to execute");
				System.exit(0);
			}
		}
	}

	public static void block(Process pNext) throws InterruptedException {
		// TODO Auto-generated method stub
		pNext.setState("Blocked");
		System.out.println("Process: " + pNext.getId() + " is " + pNext.getState());
		blocked.enqueue(pNext);
	}
	
	public static void admitToReady() {
		for(int i=0;i<PCB.size();i++) {
			if(PCB.get(i).getState() == "NEW") {
				PCB.get(i).setState("Ready");
				ready.enqueue(PCB.get(i));
			}
		}
	}
	
	public static void semDown(Process x) {
		// TODO Auto-generated method stub
		if(key == true) {
			System.out.println("Semaphore is now being used for process " + x.getId());
			key = false;
		}
	}

	public static void semUp(Process y) {
		// TODO Auto-generated method stub
		if(key == false) {
			System.out.println("Process " + y.getId() + " released the Semaphore key");
			key = true;
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getReqTime() {
		return reqTime;
	}

	public void setReqTime(int reqTime) {
		this.reqTime = reqTime;
	}

	public int getUsedTime() {
		return usedTime;
	}

	public void setUsedTime(int usedTime) {
		this.usedTime = usedTime;
	}

	public boolean isMemUse() {
		return memUse;
	}

	public void setMemUse(boolean memUse) {
		this.memUse = memUse;
	}

	public int getMemLoc() {
		return memLoc;
	}

	public void setMemLoc(int memLoc) {
		this.memLoc = memLoc;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getMemSizeLow() {
		return memSizeLow;
	}

	public int getMemSizeHigh() {
		return memSizeHigh;
	}

	public int getMemStep() {
		return memStep;
	}
	
	public boolean isDiskUse() {
		return diskUse;
	}

	public void setDiskUse(boolean diskUse) {
		this.diskUse = diskUse;
	}

	public int getDiskLoc() {
		return diskLoc;
	}

	public void setDiskLoc(int diskLoc) {
		this.diskLoc = diskLoc;
	}

	public Queue getReady() {
		return ready;
	}

	public void setReady(Queue ready) {
		Process.ready = ready;
	}

	public Queue getBlocked() {
		return blocked;
	}
	
	public boolean isKey() {
		return key;
	}
	
	public static void main(String[]args) throws InterruptedException, FileNotFoundException {
		PrintStream console = System.out;
		File file = new File("out.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);
		Process p = new Process();
		Process x = new Process(2048, 4);
		Process y = new Process();
		showPCB();
		admitToReady();
		execute();
	}

}
