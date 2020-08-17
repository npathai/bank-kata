package org.npathai;

import java.util.Scanner;

public class Console {
    private Scanner scanner = new Scanner(System.in);

    public void write(String output) {
        System.out.println(output);
    }

    public String readLine() {
        System.out.print("> ");
        return scanner.nextLine();
    }
}
