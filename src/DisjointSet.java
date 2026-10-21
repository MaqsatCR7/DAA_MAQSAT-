public class DisjointSet {
    private final int[] parent;   // parent[i] = родитель клетки i (у корня это он сам)
    private final int[] size;     // size[r] = сколько клеток в дереве с корнем r
    private int count;            // сколько групп сейчас

    public DisjointSet(int n) {
        parent = new int[n];      // создаём массивы на n клеток
        size = new int[n];
        count = n;                // в начале каждая клетка сама по себе: n групп
        for (int i = 0; i < n; i++) {
            parent[i] = i;        // каждая клетка сама себе корень
            size[i] = 1;          // в группе одна клетка
        }
    }

    public int find(int x) {
        int root = x;                          // начинаем с x
        while (parent[root] != root) root = parent[root]; // идём вверх, пока не дойдём до корня
        while (parent[x] != root) {            // второй проход: сжатие пути
            int next = parent[x];              // запоминаем следующего по цепочке
            parent[x] = root;                  // вешаем x прямо на корень
            x = next;                          // переходим дальше
        }
        return root;                           // возвращаем корень
    }

    public boolean union(int a, int b) {
        int ra = find(a), rb = find(b);        // корни обеих групп
        if (ra == rb) return false;            // уже одна группа: ничего не делаем
        if (size[ra] < size[rb]) { int t = ra; ra = rb; rb = t; } // ra = больший корень
        parent[rb] = ra;                       // меньшее дерево вешаем под большее
        size[ra] += size[rb];                  // размер большего растёт
        count--;                               // групп стало на одну меньше
        return true;                           // объединили
    }

    public boolean connected(int a, int b) { return find(a) == find(b); } // одинаковый корень = одна группа

    public int count() { return count; }       // число групп
}