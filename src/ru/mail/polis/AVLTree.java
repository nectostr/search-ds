package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    private final Comparator<E> comparator;
    private int size;
    private Node root;

    public AVLTree() {

        this.comparator = null;
        root = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree);
        System.out.println("first " + tree.first());
        System.out.println("last " + tree.last());
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(5);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.add(15);
        System.out.println(tree.size);
        System.out.println(tree);

        System.out.println("------------");
        Random rnd = new Random();
        tree = new AVLTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new AVLTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
    }

    @Override
    public E first() {

        if (isEmpty()) {
            throw new NoSuchElementException("Set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.val;
    }

    @Override
    public E last() {

        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.val;
    }

    @Override
    public List<E> inorderTraverse() {

        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.val);
        inorderTraverse(curr.right, list);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E value) {

        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        if (root != null) {
            Node curr = root;
            while (curr != null) {
                int cmp = compare(curr.val, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(E value) {

        if (root == null) root = new Node(value);
        else {
            if (contains(value)) return false;
            root=  add(root, value);
        }
        size++;
        return true;
    }

    private Node add(Node node, E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (node == null) return new Node(value);
        int cmp = compare(value, node.val);
        if (cmp < 0) node.left = add(node.left, value);
        else node.right = add(node.right, value);
        return balance(node);
    }

    @Override
    public boolean remove(E value) {

        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            return false;
        }
        if (!contains(value)) return false;
        root= remove(root, value);
        size--;
        return true;
    }

    private Node remove(Node node, E value) {
        if (node == null) return null;
        int cmp = compare(value, node.val);
        if (cmp < 0) node.left = remove(node.left, value);
        else if (cmp > 0) node.right = remove(node.right, value);
        else {
            Node x = node.left;
            Node y = node.right;
            if (y == null) return x;
            Node min = findMin(y);
            min.right = removeMin(y);
            min.left = x;
            return balance(min);
        }
        return balance(node);
    }

    private Node findMin(Node node) {
        return (node.left != null) ? findMin(node.left) : node;
    }

    private Node removeMin(Node node) {
        if (node.left == null) return node.right;
        node.left = removeMin(node.left);
        return balance(node);
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    int height(Node node) {
        return (node != null) ? node.height : 0;
    }

    int bfactor(Node node) {
        return height(node.right) - height(node.left);
    }

    void fixHeight(Node node) {
        int h1 = height(node.left);
        int h2 = height(node.right);
        node.height = ((h1 > h2) ? h1 : h2) + 1;
    }

    private Node leftRotate(Node node) {
        Node x = node.right;
        node.right = x.left;
        x.left = node;
        fixHeight(node);
        fixHeight(x);
        return x;
    }

    private Node rightRotate(Node node) {
        Node x = node.left;
        node.left = x.right;
        x.right = node;
        fixHeight(node);
        fixHeight(x);
        return x;
    }

    private Node balance(Node node) {
        fixHeight(node);
        if (bfactor(node) == 2) {
            if (bfactor(node.right) < 0) node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        if (bfactor(node) == -2) {
            if (bfactor(node.left) > 0) node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        return node;
    }

    @Override
    public String toString() {
        return "AVLT{" + root + "}";
    }

    private class Node {
        int height;
        Node left, right;
        E val;

        public Node(E val) {
            this.val = val;
            left = null;
            right = null;
            height = 1;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(val);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }
}