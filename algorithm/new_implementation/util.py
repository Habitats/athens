__author__ = 'alessandro'

import networkx as nx
import matplotlib.pyplot as plt

def read_file( filename ):
    g = nx.Graph()
    temp = 0
    with open(filename) as f:
        for line in f:
            l = line.split()
            i = l[0]
            j = l[1]
            w = int(l[2])
            if ( ('?' in i) | ('?' in j) ):
                continue
            g.add_edge( i, j, weight = w )
    f.close()
    return g

def read_file_and_split( filename, k ):
    edges = []
    with open(filename) as f:
        for line in f:
            l = line.split()
            i = l[0]
            j = l[1]
            w = int(l[2])
            if ( ('?' in i) | ('?' in j) ):
                continue
            edges.append( (i,j,w) )
    f.close()
    edge_sets = split_list( edges, len(edges) / k )
    graphs = []
    for edge_set in edge_sets:
        g = nx.Graph()
        for e in edge_set:
            g.add_edge( e[0], e[1], weight = e[2] )
        graphs.append(g)
    return graphs

def split_list( l, n ):
    """Yield successive n-sized chunks from l."""
    for i in xrange(0, len(l), n):
        yield l[i:i+n]

def write_graph( g, n ):
    f = open( 'output'+'_'+str(n), 'w+' )
    for e in g.edges():
        f.write(e[0]+" "+e[1]+" "+str(g.get_edge_data(e[0],e[1])['weight'])+"\n" )
    f.close()

def main():

    nx.draw_networkx(read_file('../output_5'))
    plt.show()

if __name__ == '__main__':
    main()


