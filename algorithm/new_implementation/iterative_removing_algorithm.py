__author__ = 'alessandro'

import basic_algorithm as b
import networkx as nx
import matplotlib.pyplot as plt
import util as u

def iterative_removing_algorithm( g, k ):

    regions = list()

    current = nx.Graph(g)

    for i in range(1,k):
        h = b.algorithm_basic( current, i )
        regions.append(h)
        for n in h.nodes():
            g.remove_node(n)
        current = nx.Graph(g)

def main():
    g = u.read_file("../data/NewYorkOneWeek.txt")
    results = iterative_removing_algorithm(g,5)
    print 'done'
    for r in results:
        nx.draw_networkx(r)
        plt.show()


if __name__ == '__main__':
    main()