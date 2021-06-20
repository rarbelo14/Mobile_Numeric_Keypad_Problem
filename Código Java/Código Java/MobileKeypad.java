import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MobileKeypad {

    public static class ResultReadData {
        private final int length;
        private final String[][] keyPad;

        public ResultReadData(int length, String[][] keyPad) {
            this.length = length;
            this.keyPad = keyPad;
        }

        public int getLength() {
            return length;
        }

        public String[][] getKeyPad() {
            return keyPad;
        } 
    }

    public static class Result {
        private final int totalCount;
        private final List<String> listRes;

        public Result(int totalCount, List<String> listRes) {
            this.totalCount = totalCount;
            this.listRes = listRes;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public List<String> getListRes() {
            return listRes;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        int arguments_counter = args.length;
        if (arguments_counter == 0) {
            System.out.println("\nYou must specify an argument. If help is needed, type -h or --help when executing.");
        } else if (arguments_counter == 1 && (args[0].equals("-h") || args[0].equals("--help"))){
            help();
        } else if (args[0].equals("-d") || args[0].equals("--directory")){
            proccess_directory(args);
        } else if (args[0].equals("-f") || args[0].equals("--file")){
            proccess_file(args);
        } else {
            System.exit(0);
        }
    }

    private static void help() {
        System.out.println("\nOptional arguments:");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Short argument          Long argument              Explanation");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("-h                      --help                     Shows this message and exit.");
        System.out.println("\n-d [DIRECTORY]          --directory [DIRECTORY]    directory (proccess many files).");
        System.out.println("-f [FILE]               --file [FILE]              file (proccess a single file).");
        System.out.println("\n-sm                     --memoization              Solve it with Memoization.");
        System.out.println("-st                     --tabulation               Solve it with Tabulation.");
        System.out.println("-check                                             Solve it with Tabulation.");
        System.out.println("\n-t                      --time                     Display time.");
        System.out.println("-nd                      --numberDigits            Display the number of digits (length).");
        System.out.println("-nc                      --numberCombinations      Display the number of possible combinations.");
        System.out.println("-dc                     --displayCompinations      Display the possible combinatons.");
    }

    private static void proccess_directory(String[] commandLineArguments) throws FileNotFoundException {
        String directory_address = commandLineArguments[1] + "/";
        File directory = new File(directory_address);
        for (File file : directory.listFiles()) {
            System.out.println("\nAnalizando el fichero " + file.getName() + ":");
            ResultReadData resultReadData  = readData(file.getAbsolutePath());
            String[][] keyPad = resultReadData.getKeyPad();
            int length = resultReadData.getLength();
            solve(commandLineArguments, length, keyPad);
        }
    }

    private static void proccess_file(String[] commandLineArguments) throws FileNotFoundException {
        String file_address = commandLineArguments[1];
        ResultReadData resultReadData  = readData(file_address);
        int length = resultReadData.getLength();
        String[][] keyPad = resultReadData.getKeyPad();
        solve(commandLineArguments, length, keyPad);
    }

    private static ResultReadData readData(String file_address) throws FileNotFoundException {
        try{   
            File file = new File(file_address);
            Scanner scanner = new Scanner(file);
            int length = 0;
            String[][] keyPad = new String[6][5];
            int count = 0;
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (count > 0){
                    keyPad[count - 1][0] = parts[0];
                    keyPad[count - 1][1] = parts[1];
                    keyPad[count - 1][2] = parts[2];
                    keyPad[count - 1][3] = parts[3];
                    keyPad[count - 1][4] = parts[4];
                }else{
                    length = Integer.parseInt(parts[2]);
                }
                count++;
            }
            scanner.close();
            return new ResultReadData(length, keyPad);
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        }
        return null;
    }
    
    private static void solve(String[] commandLineArguments, int length, String[][] keyPad){
        int totalCount = 0;
        List<String> listRes = null;
        long startTime;
        long elapsedTime = 0;
        float elapsedTimeSeconds = 0;
        if (commandLineArguments[2].equals("-st") || commandLineArguments[2].equals("--tabulation")){
            startTime = System.currentTimeMillis();
            Result result = tabulation(length, keyPad);
            totalCount = result.getTotalCount();
            listRes = result.getListRes();
            elapsedTime = (System.currentTimeMillis() - startTime);
            elapsedTimeSeconds = elapsedTime / 1000.0f;
        } else if (commandLineArguments[2].equals("-sm") || commandLineArguments[2].equals("--memoization")){
            startTime = System.currentTimeMillis();
            Result result = memoization(length, keyPad);
            totalCount = result.getTotalCount();
            listRes = result.getListRes();
            elapsedTime = (System.currentTimeMillis() - startTime);
            elapsedTimeSeconds = elapsedTime / 1000.0f;
        } else if (commandLineArguments[2].equals("-check")){
            Result result = tabulation(length, keyPad);
            int totalCountT = result.getTotalCount();
            List<String> listResT = result.getListRes();
            result = tabulation(length, keyPad);
            int totalCountM = result.getTotalCount();
            List<String> listResM = result.getListRes();
            if (check(totalCountT, listResT, totalCountM, listResM)){
                System.out.println("Tabulation and Memoization both obtain the same results!");
            } else {
                System.out.println("There is something incorrect. Tabulation and Memoization do not obtain the same results.");
            }
        } else {
            System.exit(0);
        }
        if (!commandLineArguments[2].equals("-check")){
            output(commandLineArguments, totalCount, listRes, length, elapsedTimeSeconds);
        }
    }
    
    public static Result tabulation(int length, String[][] keyPad){
        List<String>[][][] table = new ArrayList[6][5][length+1];
        // Casos bases:
        // Para todo k = 0, los valores de la tabla ya están igualados a cero
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k <= length; k++) {
                    table[i][j][k] = new ArrayList<String>(); 
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                table[i][j][0].add("0");
                table[i][j][0].add("");
            }
        }
        if (length > 0){
            // Para k = 1, solo nos interesa establecer a 1 las posiciones i y j que se corresponden con las teclas numéricas.
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    if(keyPad[i][j].equals("-") || keyPad[i][j].equals("*") || keyPad[i][j].equals("#")){
                        table[i][j][1].add("0");
                        table[i][j][1].add("");
                    } else {
                        table[i][j][1].add("1");
                        table[i][j][1].add(keyPad[i][j]);
                    } 
                }
            }
        }
        if(length > 1){
            // Rellenamos la talba con los casos generales a partir de los casos bases.
            for (int k = 2; k <= length; k++) {
                for (int i = 0; i < 6; i++) {
                    for (int j = 0; j < 5; j++) {
                        if(keyPad[i][j].equals("-") || keyPad[i][j].equals("*") || keyPad[i][j].equals("#")){
                            table[i][j][k].add("0");
                            table[i][j][k].add("");
                        } else {
                            List<String> pressSameKey = table[i][j][k-1];
                            List<String> pressUpKey = table[i-1][j][k-1];
                            List<String> pressDownKey = table[i+1][j][k-1];
                            List<String> pressLeftKey = table[i][j-1][k-1];
                            List<String> pressRightKey = table[i][j+1][k-1];
                            int sumComb = Integer.parseInt(pressSameKey.get(0)) + Integer.parseInt(pressUpKey.get(0)) + Integer.parseInt(pressDownKey.get(0)) + Integer.parseInt(pressLeftKey.get(0)) + Integer.parseInt(pressRightKey.get(0));
                            table[i][j][k].add(Integer.toString(sumComb));
                            for (int x = 1; x < pressSameKey.size(); x++) {
                                table[i][j][k].add(keyPad[i][j] + pressSameKey.get(x));
                            }
                            if (!pressUpKey.get(1).equals("")) {
                                for (int x = 1; x < pressUpKey.size(); x++) {
                                    table[i][j][k].add(keyPad[i][j] + pressUpKey.get(x));
                                }
                            }
                            if (!pressDownKey.get(1).equals("")) {
                                for (int x = 1; x < pressDownKey.size(); x++) {
                                    table[i][j][k].add(keyPad[i][j] + pressDownKey.get(x));
                                }
                            }
                            if (!pressLeftKey.get(1).equals("")) {
                                for (int x = 1; x < pressLeftKey.size(); x++) {
                                    table[i][j][k].add(keyPad[i][j] + pressLeftKey.get(x));
                                }
                            }
                            if (!pressRightKey.get(1).equals("")) {
                                for (int x = 1; x < pressRightKey.size(); x++) {
                                    table[i][j][k].add(keyPad[i][j] + pressRightKey.get(x));
                                }
                            }
                        }
                    }
                }
            }
        }
        int totalCount = 0;
        List<String> listRes = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                int index = 0;
                for (String comb: table[i][j][length]) {
                    if (index == 0) {
                        totalCount += Integer.parseInt(comb);
                    } else {
                        if (!comb.equals("")){
                            listRes.add(comb);
                        }
                    }                    
                    index++;
                }
            }
        }
        return new Result(totalCount, listRes);
    }
    
    private static List<String> pressKey(int i, int j, int length, String[][] keyPad, Map<String, List<String>> mem) {
        String key = Integer.toString(i) + " | " + Integer.toString(j) + " | " + Integer.toString(length);
        if(!mem.containsKey(key)){
            if ((length == 0) || (i<1) || (i>4) || (j<1) || (j>3) || (keyPad[i][j].equals("*")) || (keyPad[i][j].equals("#"))){
                List<String> res = new ArrayList<String>();
                res.add("0");
                res.add("");
                mem.put(key, res);
            } else {
                if(length == 1){ // Caso base.
                    List<String> res = new ArrayList<String>();
                    res.add("1");
                    res.add(keyPad[i][j]);
                    mem.put(key, res);
                } else {
                    List<String> pressSameKey = pressKey(i, j, length-1, keyPad, mem);
                    List<String> pressUpKey = pressKey(i-1, j, length-1, keyPad, mem);
                    List<String> pressDownKey = pressKey(i+1, j, length-1, keyPad, mem);
                    List<String> pressLeftKey = pressKey(i, j-1, length-1, keyPad, mem);
                    List<String> pressRightKey = pressKey(i, j+1, length-1, keyPad, mem);
                    List<String> res = new ArrayList<String>();
                    int sumComb = Integer.parseInt(pressSameKey.get(0)) + Integer.parseInt(pressUpKey.get(0)) + Integer.parseInt(pressDownKey.get(0)) + Integer.parseInt(pressLeftKey.get(0)) + Integer.parseInt(pressRightKey.get(0));
                    res.add(Integer.toString(sumComb));
                    int count = 0;
                    for (String comb0: pressSameKey) {
                        if (count > 0){
                            res.add(keyPad[i][j] + comb0);
                        }
                        count++;
                    }
                    if (!pressUpKey.get(1).equals("")){
                        count = 0;
                        for (String comb1: pressUpKey) {
                            if (count > 0){
                                res.add(keyPad[i][j] + comb1);
                            }
                            count++;
                        }
                    }
                    if (!pressDownKey.get(1).equals("")){
                        count = 0;
                        for (String comb2: pressDownKey) {
                            if (count > 0){
                                res.add(keyPad[i][j] + comb2);
                            }
                            count++;
                        }
                    }
                    if (!pressLeftKey.get(1).equals("")){
                        count = 0;
                        for (String comb3: pressLeftKey) {
                            if (count > 0){
                                res.add(keyPad[i][j] + comb3);
                            }
                            count++;
                        }
                    }
                    if (!pressRightKey.get(1).equals("")){
                        count = 0;
                        for (String comb4: pressRightKey) {
                            if (count > 0){
                                res.add(keyPad[i][j] + comb4);
                            }
                            count++;
                        }
                    }
                    mem.put(key, res);
                }
            }
        }
        return mem.get(key);
    }
    
    private static Result memoization(int length, String[][] keyPad) {
        Map<String, List<String>> mem = new HashMap<>();
        int totalCount = 0;
        List<String> listRes = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                List<String> listComb = pressKey(i, j, length, keyPad, mem);
                totalCount += Integer.parseInt(listComb.get(0));
                int count = 0;
                for (String comb: listComb) {
                    if (!comb.equals("") && count > 0){
                        listRes.add(comb);
                    }
                    count++;
                }
            }
        }
        return new Result(totalCount, listRes);
    }
    
    private static void output(String[] commandLineArguments, int totalCount, List<String> listRes, int length, float elapsedTimeSeconds){
        for (int i = 3; i < commandLineArguments.length; i++) {
            if(commandLineArguments[i].equals("-nc") || commandLineArguments[i].equals("--numberCombinations")){
                System.out.println("Te total amount of possible combinations is: " + Integer.toString(totalCount));
            } else if (commandLineArguments[i].equals("-nd") || commandLineArguments[i].equals("--numberDigits")){
                System.out.println("The number of digits per combination is:     " + Integer.toString(length));
            } else if (commandLineArguments[i].equals("-t") || commandLineArguments[i].equals("--time")){
                System.out.println("The elapsed time is:                         " + String.valueOf(elapsedTimeSeconds) + " seconds.");
            } else if (commandLineArguments[i].equals("-dc") || commandLineArguments[i].equals("--displayCombinations")){
                System.out.println("The possible combinations are:");
                if (totalCount == 0){
                    System.out.println("There are no keys to be pressed (Count: 0)");
                } else {
                    int num = 0;
                    while (num < 10){
                        int count = 0;
                        String strDC = "If we start with " + Integer.toString(num) + ", valid number will be: ";
                        for (String comb:listRes) {
                            if (comb.indexOf(Integer.toString(num)) == 0){
                                strDC += comb+ " ";
                                count++;
                            }
                        }
                        strDC += "(Count: " + Integer.toString(count) + ")";
                        System.out.println(strDC);
                        num++;
                    }
                }
            }
        }
    }
    
    private static boolean check(int totalCountT, List<String> listResT, int totalCountM, List<String> listResM) {
        if(totalCountM != totalCountT){
            return false;
        } else {
            if (listResM.size() != listResT.size()){
                return false;
            } else {
                if (listResT.size() != totalCountT || listResM.size() != totalCountM){
                    return false; 
                } else {
                    for (String comb: listResT) {
                        if (!listResM.contains(comb)) {
                            return false;
                        }
                    }
                    for (String comb: listResM) {
                        if (!listResT.contains(comb)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}