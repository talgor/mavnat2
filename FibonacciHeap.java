import java.util.Arrays;

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
	public int maxRank;

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
		this.maxRank = 0;
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

	private static void link(HeapNode childNode, HeapNode parentNode) {
		// Remove childNode from its current list (which is the root list)
		childNode.prev.next = childNode.next;
		childNode.next.prev = childNode.prev;

		// Make childNode a child of parentNode
		childNode.parent = parentNode;
		childNode.prev = childNode;
		childNode.next = childNode;

		if (parentNode.child == null) {
			parentNode.child = childNode;
		} else {
			// Add childNode to parentNode's child list
			childNode.next = parentNode.child;
			childNode.prev = parentNode.child.prev;
			parentNode.child.prev.next = childNode;
			parentNode.child.prev = childNode;
		}

		parentNode.rank++;
	}

	/**
	 * 
	 * Delete the minimal item.
	 * Return the number of links.
	 *
	 */
	public int deleteMin() {
		HeapNode minNode = this.min;

		if (minNode == null) {
			return 0;
		}

		if (minNode.child != null) {
			HeapNode currentChild = minNode.child;
			do {
				currentChild.parent = null;
				currentChild = currentChild.next;
			} while (currentChild != minNode.child);

			HeapNode minPrev = minNode.prev;
			HeapNode childPrev = minNode.child.prev;
			minPrev.next = minNode.child;
			minNode.child.prev = minPrev;
			childPrev.next = minNode;
			minNode.prev = childPrev;
		}

		minNode.prev.next = minNode.next;
		minNode.next.prev = minNode.prev;

		int links;
		if (minNode == minNode.next) {
			this.min = null;
			links = 0;
		} else {
			this.min = minNode.next;
			links = consolidate();
		}

		this.size--;
		totalLinks = totalLinks + links;
		return links;
	}

	/**
	 * Internal method to consolidate the root list.
	 * This version uses only pointer manipulation and a fixed-length array,
	 * with no other data structures like ArrayList.
	 */
	private int consolidate() {
		// CHANGED: This entire block is refactored to remove the ArrayList.
		if (this.min == null) {
			return 0;
		}

		int linkCount = 0;
		int arraySize = ((int) Math.floor(Math.log(size) / Math.log((1 + Math.sqrt(5)) / 2))) + 5;
		HeapNode[] rankTable = new HeapNode[arraySize];

		// --- Step 1: Iterate through roots and consolidate ---
		// Break the circular root list to safely iterate
		HeapNode startNode = this.min;
		startNode.prev.next = null; // Break the circle before the start

		HeapNode currentNode = startNode;
		while (currentNode != null) {
			HeapNode nextNode = currentNode.next; // Store the next root before modification

			// Isolate the current node to prepare for potential linking
			currentNode.prev = currentNode;
			currentNode.next = currentNode;

			int rank = currentNode.rank;
			HeapNode A = currentNode;

			while (rank >= rankTable.length) {
				rankTable = Arrays.copyOf(rankTable, rank + 2);
			}

			while (rankTable[rank] != null) {
				HeapNode B = rankTable[rank];
				rankTable[rank] = null;

				if (A.key > B.key) {
					HeapNode temp = A;
					A = B;
					B = temp;
				}

				link(B, A);
				linkCount++;
				rank++;

				while (rank >= rankTable.length) {
					rankTable = Arrays.copyOf(rankTable, rank + 2);
				}
			}
			rankTable[rank] = A;

			currentNode = nextNode; // Move to the next root from the original list
		}

		// --- Step 2: Rebuild the root list from the rank table ---
		this.min = null;
		this.maxRank = 0;

		for (HeapNode node : rankTable) {
			if (node != null) {
				if (this.min == null) {
					this.min = node; // The first node becomes the minimum
				} else {
					// Add the node back into the root list
					node.prev = this.min;
					node.next = this.min.next;
					this.min.next.prev = node;
					this.min.next = node;

					if (node.key < this.min.key) {
						this.min = node;
					}
				}
				if (node.rank > this.maxRank) {
					this.maxRank = node.rank;
				}
			}
		}
		return linkCount;
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
		// We need x.key < min.key, so the diff needs to be greater than the difference
		// between x.key and the current minimum
		decreaseKey(x, x.key - this.min.key + 1);
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
		if (heap2 == null)
			return;
		if (heap2.min != null) {
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
		}
		this.maxRank = Math.max(this.maxRank, heap2.maxRank);
		this.size += heap2.size;
		this.numTrees += heap2.numTrees;
		this.totalLinks += heap2.totalLinks;
		this.totalCuts += heap2.totalCuts;

		//Clear heap2's state after meld ---
		heap2.min = null;
		heap2.size = 0;
		heap2.numTrees = 0;
		heap2.totalLinks = 0;
		heap2.totalCuts = 0;
		heap2.maxRank = 0; // Reset maxRank for the now-empty heap
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
