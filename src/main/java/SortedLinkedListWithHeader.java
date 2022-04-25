import java.util.Iterator;
import java.util.NoSuchElementException;

// lista simplemente encadenada, no acepta repetidos (false e ignora) ni nulls (exception)
public class SortedLinkedListWithHeader<T extends Comparable<? super T>> implements SortedListService<T>{

    private Node root;
    private Header header = new Header();

    // iterativa
//	@Override
    public boolean insert1(T data) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");

        Node prev = null;
        Node current= root;

        while( current != null && current.data.compareTo(data) <0    ) {
            // avanzo
            prev= current;
            current= current.next;
        }

        // repetido?
        if ( current!= null && current.data.compareTo(data) ==0  ) {
            System.err.println(String.format("Insertion failed %s", data));
            return false;
        }


        // insercion segura
        Node aux= new Node(data, current);

        // como engancho??? cambia el root???
        if (current == root)
            // cambie el primero
            root= aux;
        else  // nodo interno
            prev.next= aux;

        return true;
    }



    // recursiva desde afuera
//	@Override
    public boolean insert2(T data) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");

        boolean[] rta= new boolean[1];
        root =insertRec( data, root,  rta);

        return rta[0];
    }


    private Node insertRec(T data, Node current, boolean[] rta ) {
        // repetido?
        if ( current!= null && current.data.compareTo(data) ==0  ) {
            System.err.println(String.format("Insertion failed %s", data));
            rta[0]= false;
            return current;
        }

        if( current != null && current.data.compareTo(data) <0    ) {
            // avanzo
            current.next   = insertRec(data, current.next, rta);
            return current;
        }

        // estoy en parado en el lugar a insertar
        rta[0]= true;
        return new Node(data, current);
    }


    // delega en Node
    @Override
    public boolean insert(T data) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");

        if (root == null) {
            root= new Node(data, null);
            header.setFirst(root);
            header.increaseSize();
            return true;
        }

        boolean[] rta= new boolean[1];
        root =root.insert( data,  rta);

        if (rta[0] == true)
            header.increaseSize();
        
        return rta[0];
    }

    @Override
    public boolean find(T data) {
        return getPos(data) != -1;
    }


    // delete resuelto todo en la clase SortedLinkedList, iterativo
    @Override
    public boolean remove(T data) {
        if(data == null)
            return false;
        Node prev = null;
        Node current = root;
        while( current != null && current.data.compareTo(data) <0    ) {
            // avanzo
            prev= current;
            current= current.next;
        }

        if ( current!= null && current.data.compareTo(data) ==0  ) {
            if(current == root){
                root = root.next;
                header.setFirst(root);
            }
            else{
                prev.next = current.next;
                if(current == header.getLast())
                    header.setLast(prev);
            }
            header.decreaseSize();
            return true;
        }
        return false;
    }



    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public int size() {
        return header.size;
    }


    @Override
    public void dump() {
        Node current = root;

        while (current!=null ) {
            // avanzo
            System.out.println(current.data);
            current= current.next;
        }
    }


    @Override
    public boolean equals(Object other) {
        if (other == null || !  (other instanceof SortedLinkedList) )
            return false;

        @SuppressWarnings("unchecked")
        SortedLinkedListWithHeader<T> auxi = (SortedLinkedListWithHeader<T>) other;

        Node current = root;
        Node currentOther= auxi.root;
        while (current!=null && currentOther != null ) {
            if (current.data.compareTo(currentOther.data) != 0)
                return false;

            // por ahora si, avanzo ambas
            current= current.next;
            currentOther= currentOther.next;
        }

        return current == null && currentOther == null;

    }

    // -1 si no lo encontro
    protected int getPos(T data) {
        Node current = root;
        int pos= 0;

        while (current!=null ) {
            if (current.data.compareTo(data) == 0)
                return pos;

            // avanzo
            current= current.next;
            pos++;
        }
        return -1;
    }

    @Override
    public T getMin() {
        return header.getFirst().data;
    }


    @Override
    public T getMax() {
        return header.getLast().data;
    }

    @Override
    public Iterator<T> iterator() {
        return new SortedLinkedListIterator() {
        };
    }

    private class SortedLinkedListIterator implements Iterator<T>{

        private Node current;

        public SortedLinkedListIterator(){
            current = root;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if(!hasNext())
                throw new NoSuchElementException();
            T toReturn = current.data;
            current = current.next;
            return toReturn;
        }
    }

    private final class Node {
        private T data;
        private Node next;

        private Node(T data) {
            this.data= data;
        }

        private Node(T data, Node next) {
            this.data= data;
            this.next= next;
        }

        private Node insert(T data, boolean[] rta) {

            if ( this.data.compareTo(data) ==0  ) {
                System.err.println(String.format("Insertion failed %s", data));
                rta[0]= false;
                return this;
            }

            if( this.data.compareTo(data) <0    ) {
                // soy el ultimo?
                if (next==null) {
                    rta[0]= true;
                    next   = new Node(data, null);
                    header.setLast(next);
                    return this;
                }


                // avanzo
                next   = next.insert(data, rta);
                return this;
            }


            // estoy en parado en el lugar a insertar
            rta[0]= true;
            return new Node(data, this);
        }

        

    }

    private final class Header{
        private Node first;
        private Node last;
        private int size;

        public Node getLast() {
            return last;
        }

        public Node getFirst() {
            return first;
        }

        public void setFirst(Node first) {
            this.first = first;
        }

        public void setLast(Node last) {
            this.last = last;
        }

        public void increaseSize(){
            this.size += 1;
        }
        public void decreaseSize(){
            this.size -= 1;
        }
    }

    public static void main(String[] args) {
        SortedLinkedList<Integer> l = new SortedLinkedList<>();
        l.insert2(30);
        l.insert2(80);
        l.insert2(40);
        l.insert2(40);

    }

    public static void main2(String[] args) {
        SortedLinkedList<String> l = new SortedLinkedList<>();
        System.out.println("lista " +  (l.isEmpty()? "":"NO") + " vacia");
        System.out.println();

        System.out.println(l.insert("hola"));
        l.dump();
        System.out.println();

        System.out.println("lista " +  (l.isEmpty()? "":"NO") + " vacia");
        System.out.println();

        System.out.println(l.insert("tal"));
        l.dump();
        System.out.println();

        System.out.println(l.insert("ah"));
        l.dump();
        System.out.println();

        System.out.println(l.insert("veo"));
        l.dump();
        System.out.println();

        System.out.println(l.insert("bio"));
        l.dump();
        System.out.println();

        System.out.println(l.insert("tito"));
        l.dump();
        System.out.println();


        System.out.println(l.insert("hola"));
        l.dump();
        System.out.println();


        System.out.println(l.insert("aca"));
        l.dump();
        System.out.println();

        System.out.println(l.size() );

    }
}