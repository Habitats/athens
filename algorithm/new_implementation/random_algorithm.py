__author__ = 'alessandro'

import basic_algorithm as b
import networkx as nx
import matplotlib.pyplot as plt
import util as u

def random_algorithm( graphs ):

    regions = list()

    k = 1
    for g in graphs:
        h = b.algorithm_basic( g, k )
        regions.append(h)
        k += 1

def main():
    graphs = u.read_file_and_split("../data/NewYorkOneWeek.txt", 5)
    results = random_algorithm( graphs )
    print 'done'
    for r in results:
        nx.draw_networkx(r)
        plt.show()


if __name__ == '__main__':
    main()
