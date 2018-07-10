public class TernarySearchNode implements TSTNode
{
    public TernarySearchNode m_left;
    public TernarySearchNode m_middle;
    public TernarySearchNode m_right;
    public char m_ch;

    /**
     * Reference to the complete word.
     * -1 indicate a intermediate node.
     */
    private long m_ref;


    public TernarySearchNode( char ch )
    {
        m_ch = ch;
        m_ref = -1;
    }

    /**
     *  Set complete on the node.
     */
    public void setComplete( long ref )
    {
        m_ref = ref;
    }

    public long getRef()
    {
        return m_ref;
    }

    public TSTNode getLeftNode()
    {
        return m_left;
    }

    public void setLeftNode( TernarySearchNode node )
    {
        m_left = node;
    }

    public TSTNode getMiddleNode()
    {
        return m_middle;
    }

    public void setMiddleNode( TernarySearchNode node )
    {
        m_middle = node;
    }

    public TSTNode getRightNode()
    {
        return m_right;
    }

    public void setRightNode( TernarySearchNode node )
    {
        m_right = node;
    }

    public char value()
    {
        return m_ch;
    }
}
