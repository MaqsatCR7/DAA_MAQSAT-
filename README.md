# Assignment 3. Maze Escape

Name: Nauanuly Maksat
Group: SE-2512

## How I did it

The maze is a graph: a cell is a vertex, a wall is an edge. I did the project mission by mission and made a commit after each one.

1. **DSU (DisjointSet.java).** Arrays `parent` and `size`. `find` goes up to the root and then compresses the path. `union` joins the smaller tree under the larger one.
2. **Build the maze (Maze.breakWalls).** I go through the walls in the file order and break a wall only if `union` returns true.
3. **Cheapest maze (Maze.cheapestMaze, Maze.primCost).** Kruskal: sort walls by cost, then the same DSU loop. Prim: a priority queue of edges that leave the tree, always take the cheapest one.
4. **Escape (Escape.java).** DFS with my own stack and a `dir` array (up, right, down, left), so there is no recursion. Parent array gives the path.
5. **Zones (Zones.kosaraju).** Pass 1: DFS on slides, cells are written in the order of finishing. Pass 2: cells from the last finished, DFS on the reversed slides, each DFS makes one zone.
6. **Fastest route (Zones.routes).** Zones are already in topological order, so I go through zone 0, 1, 2 ... and relax the edges once. This gives the longest chain (Flow stages) and the cheapest cost.

## Program output

The 13 lines of `java -cp out Main` on my data:

    Broken
    Kept
    Last broken wall
    MST cost
    Heaviest broken wall
    Path length
    DFS visite
    Dead ends
    Zones
    Largest zone
    Is DAG
    Flow stages
    Fastest route cost

My maze:

<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/cc04bbb8-4858-4ce5-b047-7b37c27f8b1f" />


## Questions

**Mission 0. What do find and union do, and what makes them fast?**
`find(x)` returns the root of the group of x. `union(a, b)` joins the groups of a and b and returns false if they were already one group. They are fast because of two tricks: union by size keeps the trees low, and path compression makes every visited cell point directly to the root. Together the cost is almost constant per operation.

**Mission 1. Why is Broken always R*C - 1, and Kept always (R-1)*(C-1)?**
We start with R*C separate groups and every successful union reduces the number of groups by one. We stop at one group, so there are exactly R*C - 1 successful unions. The maze has (R-1)*C + R*(C-1) walls in total, and if we remove R*C - 1 of them, (R-1)*(C-1) stay.

**Mission 2. Kruskal and Prim cost, and why they are equal:**
Kruskal: [..]. Prim: [..]. All wall costs are different, so the minimum spanning tree is unique. Both algorithms find a minimum spanning tree, so their total costs must be the same.

**Mission 3. Why exactly one path? How did I avoid stack overflow?**
The broken walls connect all cells and have no cycle, so the maze is a tree. In a tree there is exactly one path between two vertices, because two different paths would make a cycle. I wrote DFS with my own array as a stack instead of recursion, so the depth is not limited by the Java call stack and a 1000 x 1000 maze works.

**Mission 4. Why does the second pass of Kosaraju go on the reversed graph? Why no cycle exactly when Zones = number of cells?**
The cell that finished last in pass 1 belongs to a zone that has no slides coming from other zones. In the original graph DFS from it could leave this zone and capture other cells. In the reversed graph it cannot leave the zone, so DFS collects exactly one zone. A cycle means several cells can reach each other, so they are in one zone. Without a cycle every cell is alone in its zone, so Zones = n, and the other way round.

**Mission 5. Why is the order of zone numbers enough? What does this pass cost?**
The numbers are a topological order: every slide between zones goes from a smaller number to a larger one. When I reach a zone, all zones that can lead to it are already processed, so its answer is final. No priority queue is needed. The pass looks at every cell and every slide once, so it costs O(n + m).

## AI use

I used Claude (Anthropic) as a helper: to explain the ideas of DSU, Kruskal, Prim, iterative DFS and Kosaraju, to show example code with line by line comments, and to help me fix a Java compile error (a class in a wrong file). I read and checked the code, ran the checks, and I can explain every line.

## Timing (Mission 7)

Milliseconds, 5 runs each, from `java -cp out Bench`.

| maze | Kruskal | Prim |
|---|---|---|
| 100 x 100 | [..] | [..] |
| 300 x 300 | [..] | [..] |
| 1000 x 1000 | [..] | [..] |

The number of cells grows 9 times from 100x100 to 300x300 and 11 times from 300x300 to 1000x1000, and the time grows roughly in the same way, a bit faster than linear. This fits O(m log m): the number of walls m is about 2 times the number of cells, and the sorting or the priority queue adds the log factor. [Write here what your own numbers show.]

## Complexity

n is the number of cells, m the number of walls (or slides).

| algorithm | time | space | assumptions |
|---|---|---|---|
| DSU find and union | amortized O(alpha(n)), almost O(1) | O(n) | union by size and path compression |
| Kruskal | O(m log m) | O(n + m) | the sort of the walls is the main cost |
| Prim | O(m log m) | O(n + m) | binary heap (PriorityQueue) |
| DFS | O(n + m) | O(n) | own stack, no recursion |
| Kosaraju | O(n + m) | O(n + m) | two DFS passes and a reversed graph |
| Route in the zone graph | O(n + m) | O(n + m) | zones in topological order, no priority queue |
