import java.util.*;

public class Maze {

    public static int[] breakWalls(Input.Walls w) {
        DisjointSet d = new DisjointSet(w.cells());   // DSU на все клетки
        int[] res = new int[w.cells() - 1];           // сломанных будет ровно R*C-1
        int c = 0;                                    // сколько сломали
        for (int k = 0; k < w.count(); k++) {         // стены в порядке файла
            if (d.union(w.a()[k], w.b()[k])) res[c++] = k; // клетки были раздельно: ломаем, запоминаем индекс
        }
        return Arrays.copyOf(res, c);                 // обрезаем до реальной длины
    }

    public static int[] cheapestMaze(Input.Walls w) {  // Kruskal
        Integer[] idx = new Integer[w.count()];       // индексы стен (Integer, чтобы сортировать с компаратором)
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        Arrays.sort(idx, (x, y) -> Integer.compare(w.cost()[x], w.cost()[y])); // сортировка по цене
        DisjointSet d = new DisjointSet(w.cells());
        int[] res = new int[w.cells() - 1];
        int c = 0;
        for (int k : idx) {                           // от дешёвой к дорогой
            if (d.union(w.a()[k], w.b()[k])) res[c++] = k; // ломаем, если не создаём цикл
        }
        return Arrays.copyOf(res, c);
    }

    public static long primCost(Input.Walls w) {       // Prim
        int n = w.cells();
        List<List<int[]>> adj = new ArrayList<>();    // список соседей каждой клетки
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int k = 0; k < w.count(); k++) {         // каждая стена в обе стороны
            adj.get(w.a()[k]).add(new int[]{w.b()[k], w.cost()[k]}); // {сосед, цена}
            adj.get(w.b()[k]).add(new int[]{w.a()[k], w.cost()[k]});
        }
        boolean[] inTree = new boolean[n];            // клетка уже в дереве?
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> Integer.compare(x[1], y[1])); // дешёвое сверху
        pq.add(new int[]{0, 0});                      // старт с клетки 0, цена 0
        long total = 0;                               // сумма цен
        while (!pq.isEmpty()) {
            int[] e = pq.poll();                      // самое дешёвое доступное ребро
            int v = e[0];
            if (inTree[v]) continue;                  // клетка уже внутри: ребро бесполезно
            inTree[v] = true;                         // добавляем клетку в дерево
            total += e[1];                            // платим за ребро
            for (int[] nb : adj.get(v)) {
                if (!inTree[nb[0]]) pq.add(nb);       // кладём рёбра к новым клеткам
            }
        }
        return total;
    }
}