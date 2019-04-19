package Collection;

public class Queue {
	
	private Process[] array;
	private int front;
	private int rear;
	private int capacity;
	private int count;
	
	public Queue(int size) {
		array = new Process[size];
		capacity = size;
		front = 0;
		rear = -1;
		count = 0;
	}
	
	public Process dequeue() {
		if(isEmpty()) {
			System.out.println("Queue is Empty");
		}
		System.out.println("Removing: " + array[front]);
		front = (front + 1) % capacity;
		count--;
		return array[front];
	}
	
	public void enqueue(Process item) {
		if(isFull()) {
			System.out.println("Queue is Full");
		}
		rear = (rear + 1) % capacity;
		array[rear] = item;
		count++;
	}
	
	public Process peek() {
		if(isEmpty()) {
			System.out.println("No element exists on top of queue");
		}
		return array[front];
	}
	
	public Process peekNext() {
		if(!(this.peek() != null && array[front+1] != null)) {
			System.out.println("No elements exists inside");
		}
		return array[front+1];
	}
	
	public int size() {
		return count;
	}
	
	public boolean isEmpty() {
		return (size()==0);
	}
	
	public boolean isFull() {
		return (size()==capacity);
	}	
	
}
