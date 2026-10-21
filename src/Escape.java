public class Escape {

    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), cols = w.cols();
        int[][] g = Graphs.open(w, broken);          // открытые соседи каждой клетки

        int deadEnds = 0;
        for (int i = 0; i < n; i++) if (g[i].length == 1) deadEnds++; // ровно одна открытая дверь

        boolean[] seen = new boolean[n];             // помечена ли клетка
        int[] parent = new int[n];                   // откуда пришли (для пути)
        int[] dir = new int[n];                      // какое направление пробуем следующим (0..3)
        int[] stack = new int[n];                    // свой стек вместо рекурсии
        int sp = 0;                                  // размер стека
        int exit = n - 1;                            // выход

        stack[sp++] = 0;                             // кладём старт
        seen[0] = true;
        parent[0] = -1;                              // у старта нет родителя
        int visited = 1;                             // старт посчитан

        while (sp > 0 && !seen[exit]) {              // пока есть что делать и выход не помечен
            int u = stack[sp - 1];                   // смотрим верхнюю клетку, не снимая
            boolean moved = false;
            while (dir[u] < 4 && !moved) {           // пробуем следующие направления
                int d = dir[u]++;                    // текущее направление и сдвиг на следующее
                int v;
                if (d == 0) v = u - cols;                                  // вверх
                else if (d == 1) v = (u % cols == cols - 1) ? -1 : u + 1;  // вправо (не за край строки)
                else if (d == 2) v = u + cols;                             // вниз
                else v = (u % cols == 0) ? -1 : u - 1;                     // влево (не за край строки)
                if (v < 0 || v >= n || seen[v] || !contains(g[u], v)) continue; // вне поля / была / стена цела
                seen[v] = true;                      // помечаем
                visited++;                           // считаем помеченные
                parent[v] = u;                       // запоминаем, откуда пришли
                stack[sp++] = v;                     // идём вглубь
                moved = true;
            }
            if (!moved) sp--;                        // тупик: возвращаемся назад
        }

        int len = 0;
        for (int v = exit; v != -1; v = parent[v]) len++;   // считаем длину пути от выхода к старту
        int[] path = new int[len];
        int i = len - 1;
        for (int v = exit; v != -1; v = parent[v]) path[i--] = v; // заполняем с конца: получится 0 -> выход
        return new Result(path, visited, deadEnds);
    }

    private static boolean contains(int[] arr, int x) {      // есть ли x в списке (у клетки максимум 4 соседа)
        for (int y : arr) if (y == x) return true;
        return false;
    }
}