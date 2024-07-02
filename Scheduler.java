import java.io.*;
import java.util.*;

class Program {
    int downloadTime;
    int executionTime;
    int id;
    int numProc;
    int minDifference;

    Program(int downloadTime, int executionTime, int id) {
        this.downloadTime = downloadTime;
        this.executionTime = executionTime;
        this.id = id;
        this.numProc = 0;
        this.minDifference = 0;
    }
}

public class Scheduler {
    public static int m, n;
    public static StringBuilder str = new StringBuilder("");

    public static void main(String[] args) throws IOException {
        FastReader in = new FastReader();
        PrintWriter out = new PrintWriter("output.txt");
        m = in.nextInt(); // количество процессоров
        n = in.nextInt(); // количество программ

        List<Program> programs = new ArrayList<>();
        long maxLongValue = Long.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < n; i++) {
            int downloadTime = in.nextInt();
            int executionTime = in.nextInt();
            if (executionTime < maxLongValue) {
                maxLongValue = executionTime;
                index = i;
            }
            programs.add(new Program(downloadTime, executionTime, i + 1));
        }

        // Указание последней программы
        Program lastProgram = programs.get(index);
        programs.remove(index);

        List<Program> sortedPrograms = applyJohnsonAlgorithm(programs);

        // Добавление последней программы
        sortedPrograms.add(lastProgram);

        // Распределение программ по процессорам и вывод времени завершения последней программы
        schedulePrograms(sortedPrograms, m);
        out.print(str);
        out.flush();
    }

    public static List<Program> applyJohnsonAlgorithm(List<Program> programs) {
        List<Program> group1 = new ArrayList<>();
        List<Program> group2 = new ArrayList<>();

        // Разделение на две группы
        for (Program program : programs) {
            if (program.downloadTime <= program.executionTime) {
                group1.add(program);
            } else {
                group2.add(program);
            }
        }

        // Сортировка первой группы по возрастанию времени загрузки
        group1.sort(Comparator.comparingInt(p -> p.downloadTime));

        // Сортировка второй группы по убыванию времени выполнения
        group2.sort((p1, p2) -> Integer.compare(p2.executionTime, p1.executionTime));

        // Объединение групп
        List<Program> sortedPrograms = new ArrayList<>(group1);
        sortedPrograms.addAll(group2);

        return sortedPrograms;
    }

    public static void schedulePrograms(List<Program> programs, int numProcessors) {
        //PriorityQueue<Processor> processors = new PriorityQueue<>(Comparator.comparingLong(Processor::getEndTime)
         //       .thenComparingInt(Processor::getId));
        ArrayList<Processor> processors = new ArrayList<>();
        if (numProcessors >= n) {
            int res = 0;
            for (int i = 0; i < n; i++) {
                res += programs.get(i).downloadTime;
                //System.out.println(programs.get(i) + " " + (i + 1));
                str.append(programs.get(i) + " " + (i + 1) + "\n");
            }
            res += programs.get(n - 1).executionTime;
            //System.out.println(res);
            str.append(res);
        } else {
            // Приоритетные очереди для отслеживания времени завершения скачивания на каждом процессоре
            //PriorityQueue<Processor> processors = new PriorityQueue<>(numProcessors, Comparator.comparingInt(p -> p.endTime));

            // Инициализация процессоров с временем завершения 0
            for (int i = 0; i < numProcessors; i++) {
                processors.add(new Processor(i + 1, 0));
            }

            int res = 0;
            int serverTime = 0;
            int prevProc = 0;
            for (Program program : programs) {
                res += program.downloadTime;
                List<Processor> availableProcessors = new ArrayList<>();
                Processor withMinDiff = null;
                int minDifference = Integer.MAX_VALUE;

                // Отфильтруем процессоры, endTime которых меньше или равен procTime
                for (Processor processor : processors) {
                    if (processor.endTime <= serverTime) {
                        availableProcessors.add(processor);
                    }
                    else {
                        int difference = Math.abs(processor.endTime - serverTime);
                        if (difference < minDifference) {
                            minDifference = difference;
                            withMinDiff = processor;
                        }
                    }
                }
                Processor needed;
                if (!availableProcessors.isEmpty()) {
                    availableProcessors.sort(Comparator.comparingInt(processor -> processor.id));
                    needed = findNextProcessor(processors, prevProc);
                    program.numProc = needed.id;
                    needed.endTime += program.downloadTime + program.executionTime;
                    prevProc = needed.id;
                } else {
                    program.numProc = withMinDiff.id;
                    res+= minDifference;
                    withMinDiff.endTime += program.downloadTime + program.executionTime;
                    prevProc = withMinDiff.id;
                }
                serverTime += program.downloadTime;
            }
            //System.out.println(res + 1);
            str.append(res + 1 + "\n");
            for (Program program : programs) {
                //System.out.println(program.id + " " + program.numProc);
                str.append(program.id + " " + program.numProc + "\n");
            }
        }
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

    public static Processor findNextProcessor(ArrayList<Processor> processors, int prevProc) {
        // Ищем объект, id которого сразу после prevProc
        for (Processor processor : processors) {
            if (processor.id > prevProc) {
                return processor;
            }
        }
        // Если такого объекта нет, возвращаем первый объект списка
        return processors.get(0);
    }
}

class Processor {
    int id;
    int endTime;
    Processor(int id, int endTime) {
        this.id = id;
        this.endTime = 0;
    }
    public int getId() {
        return id;
    }

    public long getEndTime() {
        return endTime;
    }
}