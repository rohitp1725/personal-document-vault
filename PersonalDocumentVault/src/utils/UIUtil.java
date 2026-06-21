package utils;

public class UIUtil {

    // ANSI Colors
    public static final String RESET  = "\u001B[0m";
    public static final String BOLD   = "\u001B[1m";
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String CYAN   = "\u001B[36m";
    public static final String WHITE  = "\u001B[37m";
    public static final String PURPLE = "\u001B[35m";

    public static void printHeader(String title) {
        int width = 60;
        String border = "=".repeat(width);
        int padding = (width - title.length() - 2) / 2;
        String spaces = " ".repeat(Math.max(0, padding));
        System.out.println(CYAN + BOLD);
        System.out.println("+" + border + "+");
        System.out.println("|" + spaces + " " + title + " " + spaces + (title.length() % 2 == 0 ? "" : " ") + "|");
        System.out.println("+" + border + "+" + RESET);
    }

    public static void printSectionTitle(String title) {
        System.out.println(BLUE + BOLD + "\n--- " + title + " ---" + RESET);
    }

    public static void printSuccess(String msg) {
        System.out.println(GREEN + BOLD + "  [OK] " + msg + RESET);
    }

    public static void printError(String msg) {
        System.out.println(RED + BOLD + "  [!!] " + msg + RESET);
    }

    public static void printWarning(String msg) {
        System.out.println(YELLOW + BOLD + "  [!] " + msg + RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(CYAN + "  [i] " + msg + RESET);
    }

    public static void printDivider() {
        System.out.println(BLUE + "-".repeat(62) + RESET);
    }

    public static void printThinDivider() {
        System.out.println("  " + "-".repeat(56));
    }

    public static void printMenuOption(int num, String label) {
        System.out.println(WHITE + "  [" + num + "] " + label + RESET);
    }

    public static void printMenuOption(String key, String label) {
        System.out.println(WHITE + "  [" + key + "] " + label + RESET);
    }

    public static void printPrompt(String msg) {
        System.out.print(YELLOW + "  >> " + msg + ": " + RESET);
    }

    public static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("  ██████╗  ██████╗  ██████╗    ██╗   ██╗ █████╗ ██╗   ██╗██╗  ████████╗");
        System.out.println("  ██╔══██╗██╔═══██╗██╔════╝    ██║   ██║██╔══██╗██║   ██║██║  ╚══██╔══╝");
        System.out.println("  ██║  ██║██║   ██║██║         ██║   ██║███████║██║   ██║██║     ██║   ");
        System.out.println("  ██║  ██║██║   ██║██║         ╚██╗ ██╔╝██╔══██║██║   ██║██║     ██║   ");
        System.out.println("  ██████╔╝╚██████╔╝╚██████╗    ╚████╔╝ ██║  ██║╚██████╔╝███████╗██║   ");
        System.out.println("  ╚═════╝  ╚═════╝  ╚═════╝     ╚═══╝  ╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝   ");
        System.out.println(RESET);
        System.out.println(YELLOW + "              Personal Document Vault  v1.0" + RESET);
        System.out.println(BLUE  + "         Secure | Smart | Family-Connected" + RESET);
        System.out.println();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void pressEnterToContinue(java.util.Scanner sc) {
        System.out.print(YELLOW + "\n  Press ENTER to continue..." + RESET);
        sc.nextLine();
    }
}
