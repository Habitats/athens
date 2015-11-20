__author__ = 'alessandro'

import sys

import copy
from utility import *
from random import *
from threading import *

# find the densest subgraph of g with less than x nodes
def algorithm_basic( g, output_file_name ):
    x = 10
    h =  None
    d = -1

    while( len(g.edges)>0 ):
        g.remove_node()
        if (g.density > d) & (g.n_nodes() <= x) & (g.n_nodes() >= 2) :
            h = copy.deepcopy(g)
            d = h.density

    h.output_file(output_file_name)
    return h

def algorithm_random( edges, output_file_name, k ):

    shuffled = sorted(edges, key=lambda k: random())
    chunk_size = len(shuffled) / k
    edges_sets = split_list( shuffled, chunk_size )

    graphs = []

    for ed in edges_sets:
        degrees = compute_degrees(ed)
        graphs.append(Graph(ed,degrees))

    x = 1
    for g in graphs:
        thread = Thread(target=algorithm_basic, args=(g, output_file_name+"_"+str(x)))
        thread.start()
        x += 1


















def algorithm_removing ( g, output_file_name, k ):
    for i in range(1,k):

        h = algorithm_basic( g, output_file_name )

        for n in h.degrees().keys():
            g.remove_node(n)


def main():
    # algorithm_basic(read_file('NewYorkOneWeek.txt'),'output')
    graphs = []
    [ed,deg] = read_file("NewYorkOneWeek.txt")
    print max(deg.values())
    algorithm_basic( Graph(ed,deg), "output_random")

if __name__ == '__main__':
    main()

