public interface TSTNode
{
    /**
     * Mark a node is an end of a word.
     * @param ref id of the word record. Max value ( max of long ).
     */
    void setComplete(long ref);

    /**
     * @return Reference id of the complete word.
     *         Null for intermediate node
     */
    long getRef();

    /**
     * Return left node.
     * @return
     */
    TSTNode getLeftNode();



    /**
     * Return middle node.
     * @return
     */
    TSTNode getMiddleNode();



    /**
     * Return right node.
     * @return
     */
    TSTNode getRightNode();


    /**
     * Return node value, 2 bytes.
     * @return
     */
    char value();
}
