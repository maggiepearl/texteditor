package editor;
import javafx.scene.text.Text;
public class LinkedListDeque<Item>{
    public Node sentinel;
    public int size;
    public Node cursorPoint;

    public LinkedListDeque(){
        this.size = 0;
        this.sentinel = new Node<Item>();
        this.cursorPoint = this.sentinel;
    }

    public void updateCursorForwards(){
        cursorPoint = cursorPoint.next;
    }
    public void updateCursorBackwards(){
        cursorPoint = cursorPoint.prev;
    }
    public Item returnCursorPoint(){
        return (Item)cursorPoint.item;
    }
    public Item returnCursorPointNext(){
        return (Item)cursorPoint.next.item;
    }
    public Node createNode (Item item, Node prev, Node next){
        return new Node(item, prev, next);
    }

    public void addFirst(Item item){
        Node old = sentinel.next;
        if (size == 0){
            sentinel.next = new Node(item, sentinel, sentinel);
            sentinel.prev = sentinel.next;
        }
        else{
            sentinel.next = new Node(item, sentinel, old);
            old.prev = sentinel.next;
        }
        size += 1;
    }

    public void addLast(Item item){
        Node old = sentinel.prev;
        if (size == 0){
            sentinel.next = new Node(item, sentinel,sentinel);
            sentinel.prev = sentinel.next;
        }
        else{
            sentinel.prev = new Node(item, old, sentinel);
            old.next = sentinel.prev;
        }
        size += 1;
    }


    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        Node current = sentinel.next;
        while (current != sentinel){
            System.out.print(((Text)current.item).getText() + " ");
            current = current.next;
        }
        System.out.println();
    }
    public Item removeFirst(){
        if (isEmpty()) {
            return null;
        }
        Node returnedNode = sentinel.next;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return (Item)returnedNode.item;
    }
    public Item removeLast(){
        if (isEmpty()){
            return null;
        }
        Node returnedNode = sentinel.prev;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return (Item)returnedNode.item;
    }
    public Item get(int index){
        Node current = sentinel.next;
        int i = 0;
        while (i < index){
            current = current.next;
            i += 1;
        }
        return (Item)current.item;
    }

    public Item returnPrev(){
        return (Item) cursorPoint.prev.item;
    }
    public Item returnNext(){
        return (Item)cursorPoint.next.item;
    }



}
