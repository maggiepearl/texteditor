package editor;
public class Node<Item>{
    public Node prev;
    public Node next;
    public Item item;

    public Node(){
        item = null;
        next = this;
        prev = this;
    }
    public Node(Item item, Node before, Node after){
        this.item = item;
        this.prev = before;
        this.next = after;
    }

    public void setItem(Item i){
        this.item = i;
    }

}