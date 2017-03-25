/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

/**
 *
 * @author Thierry
 */

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;


public class DotParser  {

  /** These holds the nodes and edges of the graph */
  protected ArrayList<Knoten> m_nodes;
  protected ArrayList<kante> m_edges;

  /** This is the input containing DOT stream to be parsed */
  protected Reader m_input;
  /** This holds the name of the graph if there is any otherwise it is null */
  protected String m_graphName;

  /**
   * 
   * Dot parser Constructor
   * 
   * @param input - The input, if passing in a string then encapsulate that in
   *          String reader object
   * @param nodes - Vector to put in GraphNode objects, corresponding to the
   *          nodes parsed in from the input
   * @param edges - Vector to put in GraphEdge objects, corresponding to the
   *          edges parsed in from the input
   */
  public DotParser(Reader input, ArrayList<Knoten> nodes,
    ArrayList<kante> edges) {
    m_nodes = nodes;
    m_edges = edges;
    m_input = input;
  }

  /**
   * This method parses the string or the InputStream that we passed in through
   * the constructor and builds up the m_nodes and m_edges vectors
   * 
   * @return - returns the name of the graph
   */
  public String parse() {
    StreamTokenizer tk = new StreamTokenizer(new BufferedReader(m_input));
    setSyntax(tk);

    graph(tk);

    return m_graphName;
  }

  /**
   * This method sets the syntax of the StreamTokenizer. i.e. set the
   * whitespace, comment and delimit chars.
   * 
     * @param tk
   */
  protected void setSyntax(StreamTokenizer tk) {
    tk.resetSyntax();
    tk.eolIsSignificant(false);
    tk.slashStarComments(true);
    tk.slashSlashComments(true);
    tk.whitespaceChars(0, ' ');
    tk.wordChars(' ' + 1, '\u00ff');
    tk.ordinaryChar('[');
    tk.ordinaryChar(']');
    tk.ordinaryChar('{');
    tk.ordinaryChar('}');
    tk.ordinaryChar('-');
    tk.ordinaryChar('>');
    tk.ordinaryChar('/');
    tk.ordinaryChar('*');
    tk.quoteChar('"');
    tk.whitespaceChars(';', ';');
    tk.ordinaryChar('=');
  }

  /*************************************************************
   * 
   * Following methods parse the DOT input and mimic the DOT language's grammar
   * in their structure
   * 
   ************************************************************* 
     * @param tk
   */
  protected void graph(StreamTokenizer tk) {
    try {
      tk.nextToken();

      if (tk.ttype == StreamTokenizer.TT_WORD) {
        if (tk.sval.equalsIgnoreCase("digraph")) {
          tk.nextToken();
          if (tk.ttype == StreamTokenizer.TT_WORD) {
            m_graphName = tk.sval;
            tk.nextToken();
          }

          while (tk.ttype != '{') {
            System.err.println("Error at line " + tk.lineno()
              + " ignoring token " + tk.sval);
            tk.nextToken();
            if (tk.ttype == StreamTokenizer.TT_EOF) {
              return;
            }
          }
          stmtList(tk);
        } else if (tk.sval.equalsIgnoreCase("graph")) {
          System.err.println("Error. Undirected graphs cannot be used");
        } else {
          System.err.println("Error. Expected graph or digraph at line "
            + tk.lineno());
        }
      }
    } catch (Exception ex) {
    }

    // int tmpMatrix[][] = new int[m_nodes.size()][m_nodes.size()];
    // for(int i=0; i<m_edges.size(); i++)
    // tmpMatrix[((GraphEdge)m_edges.get(i)).src]
    // [((GraphEdge)m_edges.get(i)).dest] =
    // ((GraphEdge)m_edges.get(i)).type;
    // for(int i=0; i<m_nodes.size(); i++) {
    // GraphNode n = (GraphNode)m_nodes.get(i);
    // n.edges = tmpMatrix[i];
    // }

    // Adding parents, and those edges to a node which are coming out from it
 /*   int noOfEdgesOfNode[] = new int[m_nodes.size()];
    int noOfPrntsOfNode[] = new int[m_nodes.size()];
      for (kante e : m_edges) {
          noOfEdgesOfNode[e.src]++;
          noOfPrntsOfNode[e.dest]++;
      }
      for (kante m_edge : m_edges) {
          kante e = m_edge;
          Knoten n = m_nodes.get(e.src);
          Knoten n2 = m_nodes.get(e.dest);
          if (n.kanten == null) {
              n.kanten = new int[noOfEdgesOfNode[e.src]][2];
              for (int k = 0; k < n.kanten.length; k++) {
                  n.kanten[k][1] = 0;
              }
          }   if (n2.ElternIDs == null) {
              n2.ElternIDs = new int[noOfPrntsOfNode[e.dest]];
              for (int k = 0; k < n2.ElternIDs.length; k++) {
                  n2.ElternIDs[k] = -1;
              }
          }   int k = 0;
          while (n.kanten[k][1] != 0) {
              k++;
          }   n.kanten[k][0] = e.dest;
          n.kanten[k][1] = e.type;
          k = 0;
          while (n2.ElternIDs[k] != -1) {
        k++;
      }   n2.ElternIDs[k] = e.src;
      }*/
  }

  protected void stmtList(StreamTokenizer tk) throws Exception {
    tk.nextToken();
    if (tk.ttype == '}' || tk.ttype == StreamTokenizer.TT_EOF) {
      return;
    } else {
      stmt(tk);
      stmtList(tk);
    }
  }

  protected void stmt(StreamTokenizer tk) {
    // tk.nextToken();

    if (tk.sval.equalsIgnoreCase("graph") || tk.sval.equalsIgnoreCase("node")
      || tk.sval.equalsIgnoreCase("edge")) {
      ; // attribStmt(k);
    } else {
      try {
        nodeID(tk);
        int nodeindex = m_nodes.indexOf(new Knoten(tk.sval, null));
        tk.nextToken();

        if (tk.ttype == '[') {
          nodeStmt(tk, nodeindex);
        } else if (tk.ttype == '-') {
          edgeStmt(tk, nodeindex);
        } else {
          System.err.println("error at lineno " + tk.lineno() + " in stmt");
        }
      } catch (Exception ex) {
        System.err.println("error at lineno " + tk.lineno()
          + " in stmtException");
        ex.printStackTrace();
      }
    }
  }

  protected void nodeID(StreamTokenizer tk) throws Exception {

    if (tk.ttype == '"' || tk.ttype == StreamTokenizer.TT_WORD
      || (tk.ttype >= 'a' && tk.ttype <= 'z')
      || (tk.ttype >= 'A' && tk.ttype <= 'Z')) {
      if (m_nodes != null && !(m_nodes.contains(new Knoten(tk.sval, null)))) {
        m_nodes.add(new Knoten(tk.sval, tk.sval));
        // System.out.println("Added node >"+tk.sval+"<");
      }
    } else {
      throw new Exception();
    }

    // tk.nextToken();
  }

  protected void nodeStmt(StreamTokenizer tk, final int nindex)
    throws Exception {
    tk.nextToken();

    Knoten temp = m_nodes.get(nindex);

    if (tk.ttype == ']' || tk.ttype == StreamTokenizer.TT_EOF) {
      return;
    } else if (tk.ttype == StreamTokenizer.TT_WORD) {

      if (tk.sval.equalsIgnoreCase("label")) {

        tk.nextToken();
        if (tk.ttype == '=') {
          tk.nextToken();
          if (tk.ttype == StreamTokenizer.TT_WORD || tk.ttype == '"') {
            temp.label = tk.sval;
          } else {
            System.err.println("couldn't find label at line " + tk.lineno());
            tk.pushBack();
          }
        } else {
          System.err.println("couldn't find label at line " + tk.lineno());
          tk.pushBack();
        }
      }

      else if (tk.sval.equalsIgnoreCase("color")) {

        tk.nextToken();
        if (tk.ttype == '=') {
          tk.nextToken();
          if (tk.ttype == StreamTokenizer.TT_WORD || tk.ttype == '"') {
            ;
          } else {
            System.err.println("couldn't find color at line " + tk.lineno());
            tk.pushBack();
          }
        } else {
          System.err.println("couldn't find color at line " + tk.lineno());
          tk.pushBack();
        }
      }

      else if (tk.sval.equalsIgnoreCase("style")) {

        tk.nextToken();
        if (tk.ttype == '=') {
          tk.nextToken();
          if (tk.ttype == StreamTokenizer.TT_WORD || tk.ttype == '"') {
            ;
          } else {
            System.err.println("couldn't find style at line " + tk.lineno());
            tk.pushBack();
          }
        } else {
          System.err.println("couldn't find style at line " + tk.lineno());
          tk.pushBack();
        }
      }
    }
    nodeStmt(tk, nindex);
  }

  protected void edgeStmt(StreamTokenizer tk, final int nindex)
    throws Exception {
    tk.nextToken();

    kante e = null;
    if (tk.ttype == '>') {
      tk.nextToken();
      if (tk.ttype == '{') {
        while (true) {
          tk.nextToken();
          if (tk.ttype == '}') {
            break;
          } else {
            nodeID(tk);
            e = new kante(nindex, m_nodes.indexOf(new Knoten(tk.sval,
              null)), 1);
            if (m_edges != null && !(m_edges.contains(e))) {
              m_edges.add(e);
              // System.out.println("Added edge from "+
              // ((GraphNode)(m_nodes.get(nindex))).ID+
              // " to "+
              // ((GraphNode)(m_nodes.get(e.dest))).ID);
            }
          }
        }
      } else {
        nodeID(tk);
        e = new kante(nindex,
          m_nodes.indexOf(new Knoten(tk.sval, null)), 1);
        if (m_edges != null && !(m_edges.contains(e))) {
          m_edges.add(e);
          // System.out.println("Added edge from "+
          // ((GraphNode)(m_nodes.get(nindex))).ID+" to "+
          // ((GraphNode)(m_nodes.get(e.dest))).ID);
        }
      }
    } else if (tk.ttype == '-') {
      System.err.println("Error at line " + tk.lineno()
        + ". Cannot deal with undirected edges");
      if (tk.ttype == StreamTokenizer.TT_WORD) {
        tk.pushBack();
      }
      return;
    } else {
      System.err.println("Error at line " + tk.lineno() + " in edgeStmt");
      if (tk.ttype == StreamTokenizer.TT_WORD) {
        tk.pushBack();
      }
      return;
    }

    tk.nextToken();

    if (tk.ttype == '[') {
      edgeAttrib(tk, e);
    } else {
      tk.pushBack();
    }
  }

  protected void edgeAttrib(StreamTokenizer tk, final kante e)
    throws Exception {
    tk.nextToken();

    if (tk.ttype == ']' || tk.ttype == StreamTokenizer.TT_EOF) {
      return;
    } else if (tk.ttype == StreamTokenizer.TT_WORD) {

      if (tk.sval.equalsIgnoreCase("label")) {

        tk.nextToken();
        if (tk.ttype == '=') {
          tk.nextToken();
          if (tk.ttype == StreamTokenizer.TT_WORD || tk.ttype == '"') {
            System.err.println("found label " + tk.sval);// e.lbl = tk.sval;
          } else {
            System.err.println("couldn't find label at line " + tk.lineno());
            tk.pushBack();
          }
        } else {
          System.err.println("couldn't find label at line " + tk.lineno());
          tk.pushBack();
        }
      } else if (tk.sval.equalsIgnoreCase("color")) {

        tk.nextToken();
        if (tk.ttype == '=') {
          tk.nextToken();
          if (tk.ttype == StreamTokenizer.TT_WORD || tk.ttype == '"') {
            ;
          } else {
            System.err.println("couldn't find color at line " + tk.lineno());
            tk.pushBack();
          }
        } else {
          System.err.println("couldn't find color at line " + tk.lineno());
          tk.pushBack();
        }
      }

      else if (tk.sval.equalsIgnoreCase("style")) {

        tk.nextToken();
        if (tk.ttype == '=') {
          tk.nextToken();
          if (tk.ttype == StreamTokenizer.TT_WORD || tk.ttype == '"') {
            ;
          } else {
            System.err.println("couldn't find style at line " + tk.lineno());
            tk.pushBack();
          }
        } else {
          System.err.println("couldn't find style at line " + tk.lineno());
          tk.pushBack();
        }
      }
    }
    edgeAttrib(tk, e);
  }

  /**
   * 
   * This method saves a graph in a file in DOT format. However, if reloaded in
   * GraphVisualizer we would need to layout the graph again.
   * 
   * @param filename - The name of the file to write in. (will overwrite)
   * @param graphName - The name of the graph
   * @param nodes - Vector containing all the nodes
   * @param edges - Vector containing all the edges
   */
  public static void writeDOT(String filename, String graphName,
    ArrayList<Knoten> nodes, ArrayList<kante> edges) {
    try {
      FileWriter os = new FileWriter(filename);
      os.write("digraph ", 0, ("digraph ").length());
      if (graphName != null) {
        os.write(graphName + " ", 0, graphName.length() + 1);
      }
      os.write("{\n", 0, ("{\n").length());

      kante e;
        for (kante edge : edges) {
            e = edge;
            os.write(nodes.get(e.src).getID(), 0, nodes.get(e.src).getID().length());
            os.write("->", 0, ("->").length());
            os.write(nodes.get(e.dest).getID() + "\n", 0,
                    nodes.get(e.dest).getID().length() + 1);
        }
      os.write("}\n", 0, ("}\n").length());
      os.close();
    } catch (IOException ex) {
    }
  }

} // DotParser