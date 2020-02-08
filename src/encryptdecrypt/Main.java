package encryptdecrypt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

interface CipherAlgorithm{
    String encode();
    String decode();
}

class UnicodeAlgorithm implements CipherAlgorithm{
    String data;
    int key;

    UnicodeAlgorithm(String data, int key){
        this.data = data;
        this.key = key;
    }

    @Override
    public String encode() {
        StringBuilder result = new StringBuilder();
        for (char c: data.toCharArray()) {
            result.append((char) (c + key));
        }
        return result.toString();
    }

    @Override
    public String decode() {
        StringBuilder result = new StringBuilder();
        for (char c: data.toCharArray()) {
            result.append((char) (c - key));
        }
        return result.toString();
    }
}

class ShiftAlgorithm implements CipherAlgorithm{
    String data;
    int key;

    ShiftAlgorithm(String data, int key){
        this.data = data;
        this.key = key;
    }

    @Override
    public String encode() {
        StringBuilder result = new StringBuilder();
        for (char c: data.toCharArray()) {
            if (Character.isLetter(c)) {
                int max;
                if (Character.isLowerCase(c)) {
                    max = 'z';
                } else {
                    max = 'Z';
                }
                c += key;
                if (c > max) {
                    c -= 26;
                }
            }
            result.append(c);
        }
        return result.toString();
    }

    @Override
    public String decode() {
        StringBuilder result = new StringBuilder();
        for (char c: data.toCharArray()) {
            if (Character.isLetter(c)){
                int min;
                if (Character.isLowerCase(c)){
                    min = 'a';
                } else {
                    min = 'A';
                }
                c -= key;
                if (c < min) {
                    c += 26;
                }
            }
            result.append(c);
        }
        return result.toString();
    }
}

class AlgorithmFactory {
    public static String cipher(String alg, String mode, String data, int key) {
        CipherAlgorithm algorithm;
        switch (alg){
            case "unicode":
                algorithm = new UnicodeAlgorithm(data, key);
                break;
            default:
                algorithm = new ShiftAlgorithm(data, key);
                break;
        }
        switch (mode){
            case "dec":
                return algorithm.decode();
            default:
                return algorithm.encode();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        String mode = "enc";
        String data = "";
        String in = "";
        String out = "";
        String alg = "shift";
        int key = 0;
        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-mode":
                        mode = args[i+1];
                        break;
                    case "-key":
                        key = Integer.parseInt(args[i+1]);
                        break;
                    case "-in":
                        in = args[i+1];
                        break;
                    case "-out":
                        out = args[i+1];
                        break;
                    case "-data":
                        data = args[i+1];
                        break;
                    case "-alg":
                        alg = args[i+1];
                        break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Error: " + e.getClass().getSimpleName());
            return;
        }
        if (data.isEmpty() && !in.isEmpty()) {
            File fileIn = new File(in);
            try (Scanner scanner = new Scanner(fileIn)){
                data = scanner.nextLine();
            } catch (FileNotFoundException e){
                System.out.println("Error: No such File");
                return;
            }
        }
        String result = AlgorithmFactory.cipher(alg, mode, data, key);
        if (out.isEmpty()) {
            System.out.println(result);
        } else {
            try (PrintWriter printWriter = new PrintWriter(out)){
                printWriter.print(result);
            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }
        }
    }
}