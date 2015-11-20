__author__ = 'alessandro'

import networkx as nx
import util as u
import numpy as num
import matplotlib.pyplot as plt

def algorithm_basic( g, n ):

    threshold = 10
    h = None
    current_density = -1

    while( g.number_of_nodes() > 1 ):
        deg = g.degree( weight = 'weight' )
        g.remove_node( min(deg, key=deg.get) )
        temp_density = density(g)
        if ( temp_density > current_density ) & ( g.number_of_nodes() <= threshold ) & ( g.number_of_nodes() >= 2 ) :
            h = nx.Graph(g)
            current_density = temp_density

    u.write_graph(h, n)
    return h

def density( g ):
    sum = 0
    for n in g.degree(weight = 'weight').values():
        sum = sum + n
    den = sum / g.number_of_nodes()
    return den

def main():
    g = u.read_file("../data/demo")
    densest = algorithm_basic(g,'')
    print 'done'
    nx.draw_networkx(densest)
    plt.show()

if __name__ == '__main__':
    main()

