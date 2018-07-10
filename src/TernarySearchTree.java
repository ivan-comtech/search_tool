import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * TernarySearchTree implements trie data structure.
 * This implementation also supports neighbour word search
 * by specifying edit distance.
 *      tst.nearNeighbours(ret, searchTerm, distance);
 * This implementation also supports suggestion or auto complete
 * with edit distance
 *      tst.
 */
public class TernarySearchTree
{
    private static int LEFT = -1;
    private static int MIDDLE = 0;
    private static int RIGHT = 1;

    TernarySearchNode m_root;

    private int m_totalNodes = 0;
    private int m_searchCostMin = Integer.MAX_VALUE;
    private int m_searchCostMax = 0;
    private int m_searchCostAvg = 0;
    private int m_searchCount = 0;

    private int m_searchCostMinNotFound = Integer.MAX_VALUE;
    private int m_searchCostMaxNotFound = 0;
    private int m_searchCostAvgNotFound = 0;
    private int m_searchCountNotFound = 0;

    private HashMap<String, String> m_wordList;

    /**
     * index file name.
     */
    private String m_indexFile;

    /**
     * For writing the index file
     */
    private StringBuilder m_out;

    /**
     * For reading the index file in.
     */
    private BufferedReader m_in;

    /**
     * number of recursive calls for nearNeighbour function.
     */
    private int singleSearchCost = 0;
    /**
     * Constructor
     */
    public TernarySearchTree()
    {
        m_wordList = new HashMap<String, String>();
    }

    public void print()
    {
        System.out.println( "Max cost : " + m_searchCostMax );
        System.out.println( "Min cost : " + m_searchCostMin );
        if( m_searchCount > 0 )
        {
            System.out.println( "Avg cost : " + m_searchCostAvg/m_searchCount );
        }
        System.out.println( "Max cost not found : " + m_searchCostMaxNotFound );
        System.out.println( "Min cost not found : " + m_searchCostMinNotFound );
        if( m_searchCountNotFound > 0 )
        {
            System.out.println( "Avg cost : " + m_searchCostAvgNotFound/m_searchCountNotFound );
        }

    }

    private String getNextElement() throws IOException
    {
        int c;
        String s = "";
        while( ( c = m_in.read() ) != -1 )
        {
            if( c != 32 || s.length() == 0 )
            {
                s += (char)c;
            }
            else
            {
                break;
            }
        }
        return s;
    }
    private TernarySearchNode readSubTree() throws IOException
    {
        String e = getNextElement();

        if( e.equals("#") )
        {
            return null;
        }
        if (e.length() > 1)
        {
            System.out.println( "Something is wrong!! ");
            return null;
        }

        TernarySearchNode node = new TernarySearchNode(e.charAt(0));

        e = getNextElement();

        // Expect a index reference interger
        node.setComplete(Integer.parseInt(e));
        node.m_left = readSubTree();
        node.m_middle = readSubTree();
        node.m_right = readSubTree();
        return node;
    }

    /**
     * Add a word to the TST
     * @param term
     * @param ref
     */
    public void add( final String term, final int ref )
    {
        // no-op if term is null
        if( term == null )
        {
            return;
        }

        // point to the root of the TernarySearchTree
        TernarySearchNode cur = m_root;

        // Iterate through each character from <term>
        int idx = 0;
        int len = term.length();
        TernarySearchNode parent = null;
        int childDir = MIDDLE; // default
        while ( cur != null )
        {
            if( idx >= len )
            {
                // A new word matches a subset of an existing
                // word, mark complete of the new word.
                parent.setComplete(ref);
                break; // end of the term
            }
            parent = cur; // save it

            if( cur.m_ch > term.charAt( idx ) )
            {
                cur = cur.m_left;
                childDir = LEFT;
            }
            else if( cur.m_ch == term.charAt( idx ) )
            {
                cur = cur.m_middle;
                childDir = MIDDLE;
                idx++;
            }
            else if( cur.m_ch < term.charAt( idx ) )
            {
                cur = cur.m_right;
                childDir = RIGHT;
            }
        }

        if( cur == null && idx < len )
        {
            char ch = term.charAt( idx );
            cur = new TernarySearchNode( ch );
            m_totalNodes++;
            if( parent != null )
            {
                if( childDir == MIDDLE )
                {
                    parent.m_middle = cur;
                }
                else if( childDir == LEFT )
                {
                    parent.m_left = cur;
                }
                else if( childDir == RIGHT )
                {
                    parent.m_right = cur;
                }
            }
            if( m_root == null )
            {
                m_root = cur;
            }
            idx++;
            while( idx < len )
            {
                cur.m_middle = new TernarySearchNode( term.charAt( idx++ ) );
                m_totalNodes++;
                cur = cur.m_middle;
            }
            cur.setComplete( ref );
        }
        m_wordList.put(String.valueOf(ref), term);
    }

    /**
     * Check if the given term is in the TST. Exact match.
     * @param term
     * @return
     */
    public boolean contains( String term )
    {
        int cost = 0;
        if( term == null || term.length() <= 0 )
        {
            // no-op if empty string
            return false;
        }
        TernarySearchNode cur = m_root;
        int idx = 0;
        int len = term.length();
        while( cur != null )
        {
            cost++;
            if( idx >= len )
            {
                setStats( cost, false );
                return false;
            }
            char ch = term.charAt( idx );
            if( cur.m_ch > ch )
            {
                cur = cur.m_left;
            }
            else if( cur.m_ch < ch )
            {
                cur = cur.m_right;
            }
            else // equal case
            {
                idx++;
                if( idx >= len )
                {
                    if( cur.getRef() != -1 )
                    {
                        /**
                         * When reach last char of the term,
                         * check complete state of the tree.
                         */
                        setStats( cost, true );
                        return true;
                    }
                    else
                    {
                        // term is longer than anything in the tree
                        setStats( cost, false );
                        return false;
                    }
                }
                cur = cur.m_middle;
            }
        }
        if( idx == len )
        {
            setStats( cost, true );
            return true;
        }
        setStats( cost, false );
        return false;
    }

    private void setStats( int cost, boolean found )
    {
        if( found )
        {
            if( cost < m_searchCostMin )
            {
                m_searchCostMin = cost;
            }
            if( cost > m_searchCostMax )
            {
                m_searchCostMax = cost;
            }
            m_searchCount++;
            m_searchCostAvg += cost;
        }
        else
        {
            if( cost < m_searchCostMinNotFound )
            {
                m_searchCostMinNotFound = cost;
            }
            if( cost > m_searchCostMaxNotFound )
            {
                m_searchCostMaxNotFound = cost;
            }
            m_searchCountNotFound++;
            m_searchCostAvgNotFound += cost;
        }

    }
    public int getTotalNodes()
    {
        return m_totalNodes;
    }

    /**
     * Calculate the height of the tree from the given node.
     *
     * @param node
     * @return
     */
    private int height( TernarySearchNode node )
    {
        if( node != null )
        {
            int h_l = height(node.m_left);
            int h_m = height(node.m_middle);
            int h_r = height(node.m_right);
            return Math.max(h_r, Math.max(h_l, h_m)) + 1;
        }
        return 0;
    }

    /**
     * Calculate the height of the entire TST.
     * @return
     */
    public int height()
    {
        return height(m_root);
    }

    /**
     * Return all the matched words from TST with the given edit distance. When edit
     * distance is 0, The result is as same as exact match.
     * @param ret
     * @param word
     * @param distance
     */
    public void nearNeighbours( HashSet<String> ret, String word, int distance )
    {
        nearNeighbourSearch( ret, word, 0, m_root, distance );
        setStats(singleSearchCost, ret.size() > 0 );
    }

    private void nearNeighbourSearch( HashSet<String> ret, String word, int offset, TernarySearchNode node, int distance)
    {
        singleSearchCost++;
        if( node == null || distance < 0 || word.length() <= offset )
        {
            return;
        }
        if( distance > 0 || word.charAt( offset ) < node.m_ch )
        {
            nearNeighbourSearch( ret, word, offset, node.m_left, distance);
        }

        if( node.m_middle == null )
        {
            int localOffset = offset;
            if( word.charAt(offset) == node.m_ch )
            {
                localOffset++;
            }
            if( ( word.length() - localOffset ) <= distance )
            {
                // found one
                if( node.getRef() != -1 )
                {
                    ret.add( m_wordList.get(String.valueOf(node.getRef())) );
                }
                else
                {
                    // in trouble
                    System.out.println("Should never be here!!!");
                }
            }
        }
        else if( node.getRef() != -1 && ( word.length() - (offset+1) ) <= distance)
        {
            int localOffset = offset;
            if( word.charAt(offset) == node.m_ch )
            {
                localOffset++;
            }
            if( ( word.length() - localOffset ) <= distance )
            {
                ret.add( m_wordList.get( String.valueOf(node.getRef()) ) );
            }
        }
        else
        {
            if( word.charAt(offset) == node.m_ch )
            {
                nearNeighbourSearch( ret, word, ( offset < word.length() ) ? offset+1 : offset, node.m_middle,  distance );
            }
            else
            {
                nearNeighbourSearch( ret, word, ( offset < word.length() ) ? offset+1 : offset, node.m_middle, distance -1 );
                nearNeighbourSearch( ret, word, offset, node.m_middle, distance -1 );
                nearNeighbourSearch( ret, word, ( offset < word.length() ) ? offset+1 : offset, node, distance -1 );
            }
        }

        if( distance > 0 || word.charAt( offset ) > node.m_ch )
        {
            nearNeighbourSearch( ret, word, offset, node.m_right, distance);
        }
    }


    /**
     * Return all complete word from the given node.
     * @param ret
     * @param node
     */
    public void returnAllResults( HashSet<String> ret, TernarySearchNode node )
    {
        if( node != null )
        {
            if( node.getRef() != -1 )
            {
                ret.add( m_wordList.get(String.valueOf(node.getRef())) );

            }
            returnAllResults( ret, node.m_left );
            returnAllResults( ret, node.m_middle );
            returnAllResults( ret, node.m_right );
        }
    }

    public void nearSearchWithSuffix( HashSet<String> ret, String word, int distance )
    {
        nearSearchWithSuffix( ret, word, 0, m_root, distance );
    }

    public void nearSearchWithSuffix( HashSet<String> ret, String word, int offset, TernarySearchNode node, int distance )
    {
        if( node == null || distance < 0 || word.length() <= offset )
        {
            return;
        }
        if( distance > 0 || word.charAt( offset ) < node.m_ch )
        {
            nearSearchWithSuffix( ret, word, offset, node.m_left, distance);
        }

        if( node.m_middle == null )
        {
            int localOffset = offset;
            if( word.charAt(offset) == node.m_ch )
            {
                localOffset++;

            }
            if( ( word.length() - localOffset ) <= distance )
            {
                // found one
                if( node.getRef() != -1 )
                {
                    ret.add( m_wordList.get(String.valueOf(node.getRef())) );
                }
                else
                {
                    // in trouble, can't have a incomplete leaf node.
                    System.out.println("Should never be here!!!");
                }
            }
            else
            {
                String out;
                if( node.getRef() != -1 )
                {
                    out = m_wordList.get(String.valueOf(node.getRef()));
                    // this to handle the overflow tokens
                    out += ","+ word.substring(localOffset) + "," + distance;
                    ret.add( out );
                }

            }
        }
        else if( node.getRef() != -1 && ( word.length() - (offset+1) ) <= distance)
        {
            int localOffset = offset;
            if( word.charAt(offset) == node.m_ch )
            {
                localOffset++;
            }
            if( ( word.length() - localOffset ) <= distance )
            {
                ret.add( m_wordList.get( String.valueOf(node.getRef()) ) );
            }
        }
        else
        {
            if( word.charAt(offset) == node.m_ch )
            {
                nearSearchWithSuffix( ret, word, ( offset < word.length() ) ? offset+1 : offset, node.m_middle,  distance );
            }
            else
            {
                nearSearchWithSuffix( ret, word, ( offset < word.length() ) ? offset+1 : offset, node.m_middle, distance -1 );
                nearSearchWithSuffix( ret, word, offset, node.m_middle, distance -1 );
                nearSearchWithSuffix( ret, word, ( offset < word.length() ) ? offset+1 : offset, node, distance -1 );
            }
        }

        if( distance > 0 || word.charAt( offset ) > node.m_ch )
        {
            nearSearchWithSuffix( ret, word, offset, node.m_right, distance);
        }
    }

    /**
     * Prefix search, for auto complete.
     * @param ret
     * @param word
     * @param distance
     */
    public void beginWith( HashSet<String> ret, String word, int distance )
    {
        beginWith(ret, word, 0, m_root, distance);
    }
    public void beginWith( HashSet<String> ret, String word, int offset, TernarySearchNode node, int distance )
    {
        if( node == null || distance < 0 )
        {
            return;
        }
        if( word.length() <= offset )
        {
            // print the entire subtree
            returnAllResults( ret, node );
            return;
        }
        // search the left tree
        if( distance > 0 || word.charAt( offset ) < node.m_ch )
        {
            beginWith(ret, word, offset, node.m_left, distance);
        }

        if( node.m_middle == null )
        {
            int localOffset = offset;
            if( word.charAt(offset) == node.m_ch )
            {
                localOffset++;
            }
            if( ( word.length() - localOffset ) <= distance )
            {
                // found one
                if( node.getRef() != -1 )
                {
                    ret.add( m_wordList.get(String.valueOf(node.getRef())) );
                }
                else
                {
                    // in trouble
                    System.out.println("Should never be here!!!");
                }
            }
        }
        else if( node.getRef() != -1 && ( word.length() - (offset+1) ) <= distance)
        {
            int localOffset = offset;
            if( word.charAt(offset) == node.m_ch )
            {
                localOffset++;
            }
            if( ( word.length() - localOffset ) <= distance )
            {
                ret.add( m_wordList.get( String.valueOf(node.getRef()) ) );
            }
        }
        else
        {
            if( word.charAt(offset) == node.m_ch )
            {
                beginWith(ret, word, (offset < word.length()) ? offset + 1 : offset, node.m_middle, distance);
            }
            else
            {
                beginWith(ret, word, (offset < word.length()) ? offset + 1 : offset, node.m_middle, distance - 1);
                beginWith(ret, word, offset, node.m_middle, distance - 1);
                beginWith(ret, word, (offset < word.length()) ? offset + 1 : offset, node, distance - 1);
            }
        }

        // search the right tree
        if( distance > 0 || word.charAt( offset ) > node.m_ch )
        {
            beginWith(ret, word, offset, node.m_right, distance);
        }
    }
    public void index()
    {
        long t1 = System.currentTimeMillis();

        try
        {
            FileInputStream fstream = new FileInputStream( SOURCE_FILE_NAME );
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream( fstream );
            BufferedReader br = new BufferedReader( new InputStreamReader( in ) );

            String strLine;

            int id = 0;
            //Read File Line By Line
            while ( ( strLine = br.readLine() ) != null && id < 600000 )
            {
                String strLower = strLine.toLowerCase();
                if( !contains(strLower) )
                {
                    add(strLower, id);
                    id++;
                }
            }
            in.close();
        }
        catch( Exception e)
        {
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();

        System.out.println( "Index time = " + (t2 - t1 ) + " ms\n");
    }

    static public final void main( String args[] ) throws IOException
    {
        TernarySearchTree tst = new TernarySearchTree();
        tst.index();

        String word = "about";
        int distance = 1;
        HashSet<String> ret = new HashSet<String>();

        // search with edit distance of 1
        System.out.println("Search '" + word + "' with edit distance " + distance);
        tst.nearNeighbours(ret, word, distance);
        for( String s : ret )
        {
            System.out.println( s );
        }

        // suggestion with edit distance of 1
        System.out.println("\nAuto complete '" + word + "' with edit distance " + distance);
        ret = new HashSet<String>();
        tst.beginWith(ret, word, distance);
        for( String s : ret )
        {
            System.out.println( s );
        }
    }
    public static final String SOURCE_FILE_NAME = "C:\\Documents and Settings\\iyang\\Desktop\\ternarySearchTree\\data\\dic.txt";
}
