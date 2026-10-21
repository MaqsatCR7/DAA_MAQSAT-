public class Zones {

    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);          // горки, выходящие из клетки
        int[][] in = Graphs.slidesInto(s);           // горки, входящие в клетку (обратный граф)

        // ПРОХОД 1: порядок завершения клеток
        boolean[] seen = new boolean[n];
        int[] order = new int[n];                    // клетки в порядке завершения
        int cnt = 0;
        int[] ptr = new int[n];                      // какую горку клетки смотрим следующей
        int[] stack = new int[n];
        for (int start = 0; start < n; start++) {    // запускаем DFS из каждой непосещённой
            if (seen[start]) continue;
            int sp = 0;
            stack[sp++] = start;
            seen[start] = true;
            while (sp > 0) {
                int u = stack[sp - 1];
                if (ptr[u] < out[u].length) {                    // остались горки
                    int v = s.to()[out[u][ptr[u]++]];            // куда ведёт следующая
                    if (!seen[v]) { seen[v] = true; stack[sp++] = v; } // новая: идём туда
                } else {                                         // горки кончились
                    order[cnt++] = u;                            // клетка завершена
                    sp--;
                }
            }
        }

        // ПРОХОД 2: по обратному графу, начиная с последней завершённой
        int[] zoneOf = new int[n];
        java.util.Arrays.fill(zoneOf, -1);           // -1 = зоны ещё нет
        int zone = 0;
        for (int i = n - 1; i >= 0; i--) {
            int start = order[i];
            if (zoneOf[start] != -1) continue;       // уже в какой-то зоне
            int sp = 0;
            stack[sp++] = start;
            zoneOf[start] = zone;
            while (sp > 0) {
                int u = stack[--sp];
                for (int e : in[u]) {                // идём назад по горкам
                    int v = s.from()[e];             // откуда горка приходит
                    if (zoneOf[v] == -1) { zoneOf[v] = zone; stack[sp++] = v; }
                }
            }
            zone++;                                  // следующая зона получит следующий номер
        }
        return zoneOf;
    }

    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();
        // раскладываем клетки по зонам (counting sort)
        int[] begin = new int[zoneCount + 1];
        for (int c = 0; c < n; c++) begin[zoneOf[c] + 1]++;           // размеры зон
        for (int z = 0; z < zoneCount; z++) begin[z + 1] += begin[z]; // начала зон
        int[] fill = begin.clone();
        int[] cells = new int[n];
        for (int c = 0; c < n; c++) cells[fill[zoneOf[c]]++] = c;     // клетки зоны z лежат в cells[begin[z]..begin[z+1]-1]

        int[][] out = Graphs.slidesFrom(s);
        long INF = Long.MAX_VALUE / 4;               // "бесконечность" (деление на 4 против переполнения)
        long[] dist = new long[zoneCount];           // самая дешёвая цена до зоны
        int[] stages = new int[zoneCount];           // самая длинная цепочка, заканчивающаяся в зоне
        java.util.Arrays.fill(dist, INF);
        java.util.Arrays.fill(stages, 1);            // сама зона = 1 этап
        dist[zoneOf[0]] = 0;                         // старт бесплатный

        for (int z = 0; z < zoneCount; z++) {        // зоны по порядку номеров (топологический порядок)
            for (int i = begin[z]; i < begin[z + 1]; i++) {
                for (int e : out[cells[i]]) {        // горки этой клетки
                    int y = zoneOf[s.to()[e]];       // зона, куда ведёт горка
                    if (y == z) continue;            // горка внутри зоны: бесплатна, пропускаем
                    stages[y] = Math.max(stages[y], stages[z] + 1);        // удлиняем цепочку
                    if (dist[z] < INF) dist[y] = Math.min(dist[y], dist[z] + s.time()[e]); // релаксация
                }
            }
        }
        int best = 0;
        for (int st : stages) best = Math.max(best, st);   // самая длинная цепочка в графе зон
        return new long[]{best, dist[zoneOf[n - 1]]};      // {Flow stages, Fastest route cost}
    }
}