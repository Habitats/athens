__author__ = 'alessandro'

import operator

# edge data structure
class Edge:
    def __init__(self,i,j,w):
        self.i = i;
        self.j = j;
        self.w = w;

    def __str__(self):
        return "("+str(self.i)+","+str(self.j)+","+str(self.w)+") "

    def __repr__(self):
        return "("+str(self.i)+","+str(self.j)+","+str(self.w)+") "

# graph data structure
class Graph:
    def __init__(self, edges, degrees ):
        # list of tuples: ( node_i, node_j, weight_edge )
        self.edges = edges
        # dictionary: node_i --> degree_i
        self.degrees = degrees
        # ordered list of nodes: from the lowest to the highest
        self.sort_nodes()
        self.compute_density()

    def sort_nodes(self ):
        self.nodes = [ node for  node,degree in sorted( self.degrees.items(), key = operator.itemgetter(1) ) ]

    # it removes the one with least degree
    def remove_node(self):
        n = self.nodes[0]
        print n
        # print "removing "+str(n)
        self.degrees.pop(n)
        edges_n = filter( lambda x : (x.i == n) | (x.j == n), self.edges )
        # print edges_n
        to_decrease = []
        for e in edges_n:
            if e.i == n:
                to_decrease.append((e.j,e.w))
            else:
                to_decrease.append((e.i,e.w))
        # print to_decrease
        self.edges = [ e for e in self.edges if e not in edges_n ]
        for x in to_decrease:
            try:
                self.degrees[x[0]] = self.degrees[x[0]] - x[1]
            except Exception:
                print x
        self.sort_nodes()
        self.compute_density()

    def __str__(self):
        return "nodes sorted >> "+str(self.nodes)+"\n edges >> "+str(self.edges)+\
               "\n degrees >> "+str(self.degrees)+"\n density >> "+str(self.density)

    def compute_density(self):
        tot = 0
        for x in self.degrees.items():
            tot = tot + x[1]
        #print self.degrees
        #print self.nodes
        self.density =  tot / float(len(self.nodes))

    def output_file(self,filename):
        f = open( filename, 'w' )
        for e in self.edges:
            f.write(str(e)+"\n")

    def n_nodes(self):
        return len(self.nodes)

