from graph import *

__author__ = 'alessandro'

def read_file( filename ):
    edges = []
    degrees = {}
    with open(filename) as f:
        for line in f:
            l = line.split()
            i = l[0]
            j = l[1]
            if ( ('?' in i) | ('?' in j) ):
                print line
                continue
            e = Edge(l[0],l[1],int(l[2]) )
            edges.append(e)
            if e.i in degrees:
                degrees[e.i] += e.w
            else:
                degrees[e.i] = e.w
            if e.j in degrees:
                degrees[e.j] += e.w
            else:
                degrees[e.j] = e.w
    return edges, degrees

def split_list( l, n ):
    """Yield successive n-sized chunks from l."""
    for i in xrange(0, len(l), n):
        yield l[i:i+n]

def compute_degrees( edges ):
    degrees = {}
    for e in edges:
        if e.i in degrees:
            degrees[e.i] = degrees[e.i] + e.w
        else:
            degrees[e.i] = e.w
        if e.j in degrees:
            degrees[e.j] = degrees[e.j] + e.w
        else:
            degrees[e.j] = e.w
    return degrees








