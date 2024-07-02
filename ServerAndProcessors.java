/*import java.util.*;

class Program implements Comparable<Program> {
    int downloadTime;
    int executionTime;
    int id;

    public Program(int downloadTime, int executionTime, int id) {
        this.downloadTime = downloadTime;
        this.executionTime = executionTime;
        this.id = id;
    }

    @Override
    public int compareTo(Program other) {
        if (executionTime == other.executionTime) {
            return Integer.compare(downloadTime, other.downloadTime);
        }
        return Integer.compare(executionTime, other.executionTime);
    }
}

public class ServerAndProcessors {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int m = scanner.nextInt(); // количество процессоров
        int n = scanner.nextInt(); // количество программ

        List<Program> programs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int downloadTime = scanner.nextInt();
            int executionTime = scanner.nextInt();
            programs.add(new Program(downloadTime, executionTime, i + 1));
        }

        Collections.sort(programs);
        int minTime = programs.get(n - 1).executionTime * 2;

        List<Integer>[] processors = new ArrayList[m];
        for (int i = 0; i < m; i++) {
            processors[i] = new ArrayList<>();
        }

        for (Program program : programs) {
            int minIndex = findMinIndex(processors);
            processors[minIndex].add(program.id);
        }

        System.out.println(minTime);
        for (int i = 0; i < m; i++) {
            for (int programId : processors[i]) {
                System.out.println(programId + " " + (i + 1));
            }
        }
    }

    private static int findMinIndex(List<Integer>[] processors) {
        int minIndex = 0;
        int minValue = processors[0].size();
        for (int i = 1; i < processors.length; i++) {
            if (processors[i].size() < minValue) {
                minIndex = i;
                minValue = processors[i].size();
            }
        }
        return minIndex;
    }
}


import java.util.*;
import java.io.*;

public class ServerAndProcessors {
        public static void main(String[] args) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            // Читаем входные данные
            int m = Integer.parseInt(br.readLine()); // Число процессоров
            int n = Integer.parseInt(br.readLine()); // Число программ

            // Создаем массивы для хранения времен загрузки и выполнения программ
            long[] downloadTimes = new long[n];
            long[] executionTimes = new long[n];

            // Заполняем массивы временами
            for (int i = 0; i < n; i++) {
                String[] line = br.readLine().split(" ");
                downloadTimes[i] = Long.parseLong(line[0]);
                executionTimes[i] = Long.parseLong(line[1]);
            }

            // Запускаем алгоритм
            long result = schedulePrograms(m, n, downloadTimes, executionTimes);

            // Выводим результат
            System.out.println(result);

            // Выводим распределение программ по процессорам
            schedulePrograms(m, n, downloadTimes, executionTimes, true);
        }

        private static long schedulePrograms(int m, int n, long[] downloadTimes, long[] executionTimes) {
            return schedulePrograms(m, n, downloadTimes, executionTimes, false);
        }

        private static long schedulePrograms(int m, int n, long[] downloadTimes, long[] executionTimes, boolean printResults) {
            // Создаем массив для хранения времени завершения каждого процессора
            long[] processorFinishTimes = new long[m];

            // Распределяем программы по процессорам
            for (int i = 0; i < n; i++) {
                // Находим процессор с минимальным временем завершения
                int minFinishTimeProcessor = 0;
                for (int j = 1; j < m; j++) {
                    if (processorFinishTimes[j] < processorFinishTimes[minFinishTimeProcessor]) {
                        minFinishTimeProcessor = j;
                    }
                }

                // Загружаем данные программы и запускаем ее на процессоре
                long finishTime = Math.max(processorFinishTimes[minFinishTimeProcessor], downloadTimes[i]) + executionTimes[i];
                processorFinishTimes[minFinishTimeProcessor] = finishTime;

                if (printResults) {
                    System.out.println((i + 1) + " " + (minFinishTimeProcessor + 1));
                }
            }

            // Возвращаем время завершения последней программы
            long maxFinishTime = 0;
            for (long finishTime : processorFinishTimes) {
                maxFinishTime = Math.max(maxFinishTime, finishTime);
            }
            return maxFinishTime;
        }*/

import java.io.*;
import java.util.*;

public class ServerAndProcessors {
    static class Program implements Comparable<Program>{
        int id;
        long downloadTime;
        long executionTime;
        int proces;

        Program(int id, long downloadTime, long executionTime) {
            this.id = id;
            this.downloadTime = downloadTime;
            this.executionTime = executionTime;
            this.proces = 0;
        }
        @Override
        public int compareTo(Program other) {
            // Сначала сравниваем downloadTime
            if (this.executionTime != other.executionTime) {
                return Long.compare(other.executionTime, this.executionTime); // Невозрастающий порядок
            } else {
                // Если downloadTime одинаков, сравниваем executionTime
                return Long.compare(other.downloadTime, this.downloadTime); // Невозрастающий порядок
            }
        }
    }

    static class Processor {
        int id;
        long availableTime;

        Processor(int id, long availableTime) {
            this.id = id;
            this.availableTime = availableTime;
        }
        public int getId() {
            return id;
        }

        public long getAvailableTime() {
            return availableTime;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        FastReader in = new FastReader();
        PrintWriter out = new PrintWriter("output.txt");

        int res = 0;

        int m = in.nextInt(); //процессора
        int n = in.nextInt(); //программы

        List<Program> programs = new ArrayList<>();
        PriorityQueue<Processor> processors = new PriorityQueue<>(Comparator.comparingLong(Processor::getAvailableTime)
                .thenComparingInt(Processor::getId));
        for (int i = 1; i <= n; i++) {
            long downloadTime = in.nextLong();
            long executionTime = in.nextLong();
            programs.add(new Program(i, downloadTime, executionTime));
            if (i <= m ) processors.add(new Processor(i, 0));
        }

        // Сортировка программ по времени скачивания данных
        Collections.sort(programs);

        if (m < n ) {
            Program tempProg = programs.get(0);
            Processor tempProc;
            int m1 = 1;
            for (int i = 1; i < n; i++, m1++) {
                if (m1 > m) m1 = 1;
                res += tempProg.downloadTime;
                tempProc = processors.poll();
                tempProg.proces = tempProc.id;
                tempProc.availableTime += tempProg.executionTime;
                processors.add(tempProc);
                tempProg = programs.get(i);
            }
            res += tempProg.downloadTime;
            tempProc = processors.poll();
            tempProg.proces = tempProc.id;
            tempProc.availableTime += tempProg.executionTime;
        } else {
            Program tempProg;
            Processor tempProc;
            for (int i = 1; i <= n; i++) {
                tempProg = programs.get(i - 1);
                res += tempProg.downloadTime;
                tempProc = processors.poll();
                tempProg.proces = tempProc.id;
                tempProc.availableTime += tempProg.executionTime;
                processors.add(tempProc);
            }
        }
        res += 10;

        out.println(res);
        for (Program program : programs) {
            out.println(program.id + " " + program.proces);
        }
        out.flush();
    }
    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader() throws FileNotFoundException {
            br = new BufferedReader(new FileReader("input.txt"));
        }
        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
        long nextLong() {
            return Long.parseLong(next());
        }
    }
}