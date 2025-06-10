/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 *
 */
public class FibonacciHeap {
	private HeapNode min;
	private int c;
	private int size;
	private int numTrees;
	private int totalLinks;
	private int totalCuts;

	/**
	 *
	 * Constructor to initialize an empty heap.
	 * pre: c >= 2.
	 *
	 */
	public FibonacciHeap(int c) {
		this.min = null;
		this.c = c;
		this.size = 0;
		this.numTrees = 0;
		this.totalLinks = 0;
		this.totalCuts = 0;
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info) {
		HeapNode node = new HeapNode(key, info);
		if (this.min != null) {
			// Add the new node to the root list
			addToRootList(node);
			if (key < this.min.key) {
				// Update the minNode pointer if necessary
				this.min = node;
			}
		} else {
			// Set the new node as the minNode if the heap was empty
			this.min = node;
		}
		this.size++;
		this.numTrees++;
		return node;
	}

	// Add a node to the root list of the Fibonacci Heap
	// right = next left = prev
	private void addToRootList(HeapNode node) {
		node.prev = min;
		node.next = min.next;
		this.min.next.prev = node;
		this.min.next = node;
	}

	/**
	 * 
	 * Return the minimal HeapNode, null if empty.
	 *
	 */
	public HeapNode findMin() {
		return this.min;
	}

	/**
	 * 
	 * Delete the minimal item.
	 * Return the number of links.
	 *
	 */
	public int deleteMin() {
		return 46; // should be replaced by student code

	}

	/**
	 * 
	 * pre: 0<diff<x.key
	 * 
	 * Decrease the key of x by diff and fix the heap.
	 * Return the number of cuts.
	 * 
	 */
	public int decreaseKey(HeapNode x, int diff) {
		x.key -= diff;
		HeapNode parent = x.parent;

		if (parent != null && x.key < parent.key) {
			cut(x, parent);
			cascadingCut(parent);
		}
		if (x.key < this.min.key) {
			this.min = x;
		}

		return totalCuts;
	}

	private void cut(HeapNode x, HeapNode y) {
		if (x.next == x) {
			y.child = null;
		} else {
			x.next.prev = x.prev;
			x.prev.next = x.next;
			if (y.child == x) {
				y.child = x.next;
			}
		}
		y.rank--;

		x.parent = null;
		x.mark = 0;

		x.next = x;
		x.prev = x;
		addToRootList(x);

		numTrees++;
		totalCuts++;
	}

	private void cascadingCut(HeapNode y) {
		HeapNode z = y.parent;
		if (z != null) {
			if (y.mark < this.c) {
				y.mark++;
			} else {
				cut(y, z);
				cascadingCut(z);
			}
		}
	}

	/**
	 * 
	 * Delete the x from the heap.
	 * Return the number of links.
	 *
	 */
	public int delete(HeapNode x) {
		decreaseKey(x, Integer.MAX_VALUE);
		deleteMin();
		return totalLinks;
	}

	/**
	 * 
	 * Return the total number of links.
	 * 
	 */
	public int totalLinks() {
		return this.totalLinks;
	}

	/**
	 * 
	 * Return the total number of cuts.
	 * 
	 */
	public int totalCuts() {
		return this.totalCuts;
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2) {
		if (heap2 == null || heap2.min == null)
			return;
		if (this.min == null) {
			this.min = heap2.min;
		} else {
			// Merge the circular doubly linked lists
			HeapNode thisNext = this.min.next;
			HeapNode otherNext = heap2.min.next;

			this.min.next = otherNext;
			otherNext.prev = this.min;

			heap2.min.next = thisNext;
			thisNext.prev = heap2.min;

			if (heap2.min.key < this.min.key) {
				this.min = heap2.min;
			}
		}
		this.size += heap2.size;
		this.numTrees += heap2.numTrees;
		this.totalLinks += heap2.totalLinks;
		this.totalCuts += heap2.totalCuts;
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 * 
	 */
	public int size() {
		return this.size;
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees() {
		return this.numTrees;
	}

	/**
	 * Class implementing a node in a Fibonacci Heap.
	 * 
	 */
	public static class HeapNode {
		public int key;
		public String info;
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;
		public int rank;
		public int mark;

		// Constructor to initialize a new node
		public HeapNode(int key, String info) {
			this.key = key;
			this.info = info;
			this.child = null;
			this.next = this;
			this.prev = this;
			this.parent = null;
			this.rank = 0;
			this.mark = 0;
		}
	}
}
