package huffman;

import huffman.tree.Branch;
import huffman.tree.Leaf;
import huffman.tree.Node;

import java.lang.reflect.GenericDeclaration;
import java.sql.Array;
import java.sql.ClientInfoStatus;
import java.util.*;
/**
 * The class implementing the Huffman coding algorithm.
 */
public class Huffman {

    /**
     * Build the frequency table containing the unique characters from the String `input' and the number of times
     * that character occurs.
     *
     * @param   input   The string.
     * @return          The frequency table.
     */
    public static Map<Character, Integer> freqTable (String input) {
        if (input == null || input == ""){ // As the input is null
            return null;                  // It returns null
        }
        Map<Character, Integer> ft = new HashMap<>(); //Creating a map
        for (int i = 0; i < input.length(); i++ ) {
            if (ft.containsKey(input.charAt(i))) {
                ft.put(input.charAt(i),ft.get(input.charAt(i)) + 1);
            }
            else{
                ft.put(input.charAt(i), 1);
            }
        }
        return ft;
    }

    /**
     * Given a frequency table, construct a Huffman tree.
     *
     * First, create an empty priority queue.
     *
     * Then make every entry in the frequency table into a leaf node and add it to the queue.
     *
     * Then, take the first two nodes from the queue and combine them in a branch node that is
     * labelled by the combined frequency of the nodes and put it back in the queue. The right hand
     * child of the new branch node should be the node with the larger frequency of the two.
     *
     * Do this repeatedly until there is a single node in the queue, which is the Huffman tree.
     *
     * @param freqTable The frequency table.
     * @return          A Huffman tree.
     */
    public static Node treeFromFreqTable(Map<Character, Integer> freqTable) {
        if (freqTable == null) {
            return null;
        }
        //Crating an empty priority queue
        PQueue pQueue = new PQueue();
        for (Character i : freqTable.keySet()) {
            Node leaf = new Leaf(i, freqTable.get(i)); //Making a leaf node for every entry in the frequency Table
            pQueue.enqueue(leaf); // Add to the queue
        }
        while (pQueue.size() > 1) {
            Node leftNode = pQueue.dequeue(); // taking out of the queue
            Node rightNode = pQueue.dequeue(); // taking out of the queue
            //combining them in a branch node that is labelled by the combined frequency of the nodes
            Branch branchNode = new Branch(leftNode.getFreq() + rightNode.getFreq(), leftNode, rightNode);
            pQueue.enqueue(branchNode); // put back in the queue
        }
        return pQueue.dequeue();
    }

    /**
     * Construct the map of characters and codes from a tree. Just call the traverse
     * method of the tree passing in an empty list, then return the populated code map.
     *
     * @param tree  The Huffman tree.
     * @return      The populated map, where each key is a character, c, that maps to a list of booleans
     *              representing the path through the tree from the root to the leaf node labelled c.
     */
    public static Map<Character, List<Boolean>> buildCode(Node tree) {
        List<Boolean> populatedCodeMap = new ArrayList<>();
        return (tree.traverse(populatedCodeMap));
    }


    /**
     * Create the huffman coding for an input string by calling the various methods written above. I.e.
     *
     * + create the frequency table,
     * + use that to create the Huffman tree,
     * + extract the code map of characters and their codes from the tree.
     *
     * Then to encode the input data, loop through the input looking each character in the map and add
     * the code for that character to a list representing the data.
     *
     * @param input The data to encode.
     * @return      The Huffman coding.
     */
    public static HuffmanCoding encode(String input) {
        Map<Character, Integer> freqTable = freqTable(input); //Creating the frequency table
        Node treeFromFreqTable = treeFromFreqTable(freqTable); //Creating the huffman tree
        Map<Character, List<Boolean>> code = buildCode(treeFromFreqTable); //Extracting the code map of characters and code from the tree
        List<Boolean> data = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            List<Boolean> charData = code.get(input.charAt(i));
            data.addAll(charData);
        }
        return new HuffmanCoding(code, data);
    }

    /**
     * Reconstruct a Huffman tree from the map of characters and their codes. Only the structure of this tree
     * is required and frequency labels of all nodes can be set to zero.
     *
     * Your tree will start as a single Branch node with null children.
     *
     * Then for each character key in the code, c, take the list of booleans, bs, corresponding to c. Make
     * a local variable referring to the root of the tree. For every boolean, b, in bs, if b is false you want to "go
     * left" in the tree, otherwise "go right".
     *
     * Presume b is false, so you want to go left. So long as you are not at the end of the code so you should set the
     * current node to be the left-hand child of the node you are currently on. If that child does not
     * yet exist (i.e. it is null) you need to add a new branch node there first. Then carry on with the next entry in
     * bs. Reverse the logic of this if b is true.
     *
     * When you have reached the end of this code (i.e. b is the final element in bs), add a leaf node
     * labelled by c as the left-hand child of the current node (right-hand if b is true). Then take the next char from
     * the code and repeat the process, starting again at the root of the tree.
     *
     * @param code  The code.
     * @return      The reconstructed tree.
     */
    public static Node treeFromCode(Map<Character, List<Boolean>> code) {
        Node root = new Branch(0,null, null);
        Set<Character> chars = code.keySet();
        Node currentNode;
        for (Character c:chars){
            currentNode = root;
            List<Boolean> bs = code.get(c);
            int end = 0;
            for (Boolean b:bs) {
                if (b == false){
                    if(end == bs.size()-1){
                        ((Branch) currentNode).setLeft(new Leaf(c, 0));
                    }
                    else if(((Branch) currentNode).getLeft() == null){
                        ((Branch) currentNode).setLeft(new Branch(0,null,null));
                    }
                    currentNode = ((Branch) currentNode).getLeft();
                }else if (b == true){
                    if(end == bs.size()-1){
                        ((Branch) currentNode).setRight(new Leaf(c, 0));
                    }
                    else if(((Branch) currentNode).getRight() == null){
                        ((Branch) currentNode).setRight(new Branch(0,null,null));
                    }
                    currentNode = ((Branch) currentNode).getRight();
                }
                end++;

            }
        }
        return root;
    }

        /**
         * Decode some data using a map of characters and their codes. To do this you need to reconstruct the tree from the
         * code using the method you wrote to do this. Then take one boolean at a time from the data and use it to traverse
         * the tree by going left for false, right for true. Every time you reach a leaf you have decoded a single
         * character (the label of the leaf). Add it to the result and return to the root of the tree. Keep going in this
         * way until you reach the end of the data.
         *
         * @param code  The code.
         * @param data  The encoded data.
         * @return      The decoded string.
         */
        public static String decode(Map<Character, List<Boolean>> code, List<Boolean> data) {
            Node tree = treeFromCode(code);
            Node currentNode = tree;

            StringBuilder decodedResult = new StringBuilder("");
            for (Boolean b: data){
                if (!b){
                    currentNode = ((Branch) currentNode).getLeft();
                } else{
                    currentNode = ((Branch) currentNode).getRight();
                }
                if (currentNode instanceof Leaf) {
                    decodedResult.append( ((Leaf) currentNode).getLabel() ); // Adding character at the end of string builder using append
                    currentNode = tree;
                }
            }

            return decodedResult.toString();
        }
}
