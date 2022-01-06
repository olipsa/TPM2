import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OptimisticListVersioning<T> {
    /**
     * First list entry
     */
    private Node head;
    private int version;
    Lock versionLock;
    /**
     * Constructor
     */
    public OptimisticListVersioning() {
        this.head  = new Node(Integer.MIN_VALUE);
        this.head.next = new Node(Integer.MAX_VALUE);
        version = 0;
        versionLock = new ReentrantLock();
    }
    /**
     * Add an element.
     * @param item element to add
     * @return true iff element was not there already
     */
    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = this.head;
            Node current = pred.next;
            while (current.key <= key) {
                pred = current; current = current.next;
            }
            pred.lock(); current.lock();
            try {
                versionLock.lock();
                try{
                    if (validate(pred, current,version)) {

                        if (current.key == key) { // present
                            return false;
                        } else {               // not present
                            Node entry = new Node(item);
                            entry.next = current;
                            pred.next = entry;
                            version++;
                            return true;
                        }
                    }
                }finally {
                    versionLock.unlock();
                }


            } finally {                // always unlock
                pred.unlock(); current.unlock();
            }
        }
    }

    /**
     * Remove an element.
     * @param item element to remove
     * @return true iff element was present
     */
    public boolean remove(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = this.head;
            Node current = pred.next;
            while (current.key < key) {
                pred = current; current = current.next;
            }
            pred.lock(); current.lock();
            try {
                versionLock.lock();
                try {
                    if (validate(pred, current, version)) {
                        if (current.key == key) { // present in list
                            pred.next = current.next;
                            version++;

                        } else {               // not present in list
                            return false;
                        }
                    }
                }finally {
                    versionLock.unlock();
                }
            } finally {                // always unlock
                pred.unlock(); current.unlock();
            }
        }
    }

    /**
     * Test whether element is present
     * @param item element to test
     * @return true iff element is present
     */
    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = this.head; // sentinel node;
            Node current = pred.next;
            while (current.key < key) {
                pred = current; current = current.next;
            }
            try {
                pred.lock(); current.lock();
                versionLock.lock();
                if (validate(pred, current,version)) {
                    return (current.key == key);
                }
            } finally {                // always unlock
                pred.unlock(); current.unlock();versionLock.unlock();
            }
        }
    }

    /**
     * Check that prev and current are still in list and adjacent
     * @param pred predecessor node
     * @param current current node
     * @return whther predecessor and current have changed
     */
    private boolean validate(Node pred, Node current, int current_version) {
        if(current_version == this.version) return pred.next == current;
        return false;
    }
    public void printList(){
        Node current = this.head.next;
        while(current.next!=null){
            System.out.print(current.key+" ");
            current = current.next;
        }
        System.out.println();
    }


    /**
     * list node
     */
    private class Node {
        /**
         * actual item
         */
        T item;
        /**
         * item's hash code
         */
        int key;
        /**
         * next node in list
         */
        Node next;
        /**
         * Synchronizes node.
         */
        Lock lock;
        /**
         * Constructor for usual node
         * @param item element in list
         */
        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
            lock = new ReentrantLock();
        }
        /**
         * Constructor for sentinel node
         * @param key should be min or max int value
         */
        Node(int key) {
            this.key = key;
            lock = new ReentrantLock();
        }
        /**
         * Lock entry
         */
        void lock() {lock.lock();}
        /**
         * Unlock entry
         */
        void unlock() {lock.unlock();}
    }
}
