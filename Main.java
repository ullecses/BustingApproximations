import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader fin = new BufferedReader(new FileReader("input.txt"));
        int m = Integer.parseInt(fin.readLine());
        int n = Integer.parseInt(fin.readLine());
        List<Pair<Integer, Integer>> seq = new ArrayList<>(n);
        Set<Integer> free_cpu = new HashSet<>();
        for (int i = 1; i <= m; ++i) {
            free_cpu.add(i);
        }

        long tl, te;
        MultiMap<Pair<Long, Long>, Integer> programs = new MultiMap<>();
        for (int i = 1; i <= n; ++i) {
            String[] line = fin.readLine().split(" ");
            tl = Long.parseLong(line[0]);
            te = Long.parseLong(line[1]);
            programs.emplace(new Pair<>(te, tl), i);
        }
        fin.close();

        boolean free_from_load = true;
        TreeMap<Long, Pair<Integer, String>> events = new TreeMap<>();
        long time = 0;
        String type;

        for (Iterator<Map.Entry<Pair<Long, Long>, List<Integer>>> it = programs.descendingIterator(); it.hasNext(); ) {
            Map.Entry<Pair<Long, Long>, List<Integer>> entry = it.next();
            while (!free_from_load) {
                while (true) {
                    Map.Entry<Long, Pair<Integer, String>> event = events.firstEntry();
                    time = event.getKey();
                    type = event.getValue().second;
                    if (type.equals("end_load")) {
                        free_from_load = true;
                        events.pollFirstEntry();
                        break;
                    }
                    free_cpu.add(event.getValue().first);
                    events.pollFirstEntry();
                }
            }

            if (!free_cpu.isEmpty()) {
                int cpu = free_cpu.iterator().next();
                events.put(time + entry.getKey().second, new Pair<>(cpu, "end_load"));
                events.put(time + entry.getKey().second + entry.getKey().first, new Pair<>(cpu, "end_exec"));
                seq.add(new Pair<>(entry.getValue().get(0), cpu));
                free_cpu.remove(cpu);
            } else {
                Map.Entry<Long, Pair<Integer, String>> event = events.firstEntry();
                time = event.getKey();
                events.put(time + entry.getKey().second, new Pair<>(event.getValue().first, "end_load"));
                events.put(time + entry.getKey().second + entry.getKey().first, new Pair<>(event.getValue().first, "end_exec"));
                seq.add(new Pair<>(entry.getValue().get(0), event.getValue().first));
                events.pollFirstEntry();
            }
            free_from_load = false;
        }

        time = events.lastKey();
        BufferedWriter fout = new BufferedWriter(new FileWriter("output.txt"));
        fout.write(time + "\n");
        for (Pair<Integer, Integer> p : seq) {
            fout.write(p.first + " " + p.second + "\n");
        }
        fout.close();
    }

    // Helper class to represent a pair
    static class Pair<F extends Comparable<F>, S extends Comparable<S>> implements Comparable<Pair<F, S>> {
        F first;
        S second;

        Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
        @Override
        public int compareTo(Pair<F, S> other) {
            int cmp = this.first.compareTo(other.first);
            if (cmp != 0) {
                return cmp;
            }
            return this.second.compareTo(other.second);
        }
    }
}
class MultiMap<K, V> {
    private final TreeMap<K, List<V>> map = new TreeMap<>();

    // Метод для вставки элемента в MultiMap (аналог emplace)
    public void emplace(K key, V value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    // Метод для получения итератора в обратном порядке
    public Iterator<Map.Entry<K, List<V>>> descendingIterator() {
        return map.descendingMap().entrySet().iterator();
    }

    // Метод для получения итератора с начала
    public Iterator<Map.Entry<K, List<V>>> begin() {
        return map.entrySet().iterator();
    }

    // Метод для удаления элемента по ключу (аналог erase)
    public void erase(K key) {
        map.remove(key);
    }

    // Метод для получения итератора конца
    public Iterator<Map.Entry<K, List<V>>> end() {
        return Collections.emptyIterator();
    }

    // Метод для получения ключа последнего элемента
    public K lastKey() {
        return map.lastKey();
    }

    // Проверка пуст ли MultiMap
    public boolean isEmpty() {
        return map.isEmpty();
    }

    // Метод для получения первого элемента
    public Map.Entry<K, List<V>> firstEntry() {
        return map.firstEntry();
    }

    // Метод для удаления и получения первого элемента
    public Map.Entry<K, List<V>> pollFirstEntry() {
        return map.pollFirstEntry();
    }
}
