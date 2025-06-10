import java.util.Arrays;

/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 *
 */
public class FibonacciHeap
{
	public HeapNode min;
	private int maxRank;
    private int size;
	private int numTrees;
	private int totalLinks;



	
	/**
	 *
	 * Constructor to initialize an empty heap.
	 * pre: c >= 2.
	 *
	 */
	public FibonacciHeap(int c)
	{
		// should be replaced by student code
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info) 
	{    
		return null; // should be replaced by student code
	}

	/**
	 * 
	 * Return the minimal HeapNode, null if empty.
	 *
	 */
	public HeapNode findMin()
	{
		return null; // should be replaced by student code
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
}

	/**
	 * 
	 * pre: 0<diff<x.key
	 * 
	 * Decrease the key of x by diff and fix the heap.
	 * Return the number of cuts.
	 * 
	 */
	public int decreaseKey(HeapNode x, int diff) 
	{    
		return 46; // should be replaced by student code
	}

	/**
	 * 
	 * Delete the x from the heap.
	 * Return the number of links.
	 *
	 */
	public int delete(HeapNode x) 
	{    
		return 46; // should be replaced by student code
	}


	/**
	 * 
	 * Return the total number of links.
	 * 
	 */
	public int totalLinks()
	{
		return 46; // should be replaced by student code
	}


	/**
	 * 
	 * Return the total number of cuts.
	 * 
	 */
	public int totalCuts()
	{
		return 46; // should be replaced by student code
	}


	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2)
	{
		return; // should be replaced by student code   		
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return 46; // should be replaced by student code
	}


	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{
		return 46; // should be replaced by student code
	}

	/**
	 * Class implementing a node in a Fibonacci Heap.
	 *  
	 */
	public static class HeapNode{
		public int key;
		public String info;
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;
		public int rank;
		public int mark;
	}
}
