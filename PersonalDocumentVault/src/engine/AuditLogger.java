package engine;

import interfaces.Auditable;
import utils.FileHandler;
import utils.UIUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AuditLogger implements Auditable {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static AuditLogger instance;

    private AuditLogger() {}

    public static AuditLogger getInstance() {
        if (instance == null) instance = new AuditLogger();
        return instance;
    }

    @Override
    public void logAction(String username, String action, String detail) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] "
                + "[" + username + "] " + action + " | " + detail;
        FileHandler.appendAuditLog(entry);
    }

    @Override
    public void showRecentLogs(int count) {
        List<String> logs = FileHandler.loadAuditLog();
        UIUtil.printSectionTitle("Audit Log (Last " + count + " entries)");
        if (logs.isEmpty()) {
            UIUtil.printInfo("No audit entries found.");
            return;
        }
        int start = Math.max(0, logs.size() - count);
        for (int i = start; i < logs.size(); i++) {
            System.out.println(UIUtil.CYAN + "  " + logs.get(i) + UIUtil.RESET);
        }
    }
}
