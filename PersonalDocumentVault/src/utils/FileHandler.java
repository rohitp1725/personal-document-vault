package utils;

import models.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FileHandler {

    private static final String DATA_DIR = "data/";

    static {
        new File(DATA_DIR).mkdirs();
        new File(DATA_DIR + "history/").mkdirs();
        new File(DATA_DIR + "backups/").mkdirs();
        new File(DATA_DIR + "reminders/").mkdirs();
    }

    // ─── Users ───────────────────────────────────────────────
    public static void saveUser(User user) {
        String path = DATA_DIR + "users.txt";
        List<String> lines = readLines(path);
        List<String> updated = new ArrayList<>();
        boolean found = false;
        for (String line : lines) {
            if (line.startsWith(user.getUsername() + "|")) {
                updated.add(user.toFileString());
                found = true;
            } else {
                updated.add(line);
            }
        }
        if (!found) updated.add(user.toFileString());
        writeLines(path, updated);
    }

    public static User loadUser(String username) {
        for (String line : readLines(DATA_DIR + "users.txt")) {
            String[] p = line.split("\\|", 9);
            if (p.length >= 9 && p[0].equals(username)) {
                String role = p[5];
                User u;
                switch (role) {
                    case "MINOR":  u = new MinorUser(p[0], p[1], p[2], p[3], p[4], p[7], p[8]);  break;
                    case "SENIOR": u = new SeniorUser(p[0], p[1], p[2], p[3], p[4], p[7], p[8]); break;
                    default:       u = new AdultUser(p[0], p[1], p[2], p[3], p[4], p[7], p[8]);  break;
                }
                if (p[6].equals("1")) u.setLocked(true);
                return u;
            }
        }
        return null;
    }

    public static boolean userExists(String username) {
        for (String line : readLines(DATA_DIR + "users.txt")) {
            if (line.startsWith(username + "|")) return true;
        }
        return false;
    }

    // ─── Documents ───────────────────────────────────────────
    public static void saveDocuments(String username, List<Document> docs) {
        List<String> lines = new ArrayList<>();
        for (Document d : docs) lines.add(d.toFileString());
        writeLines(DATA_DIR + "vault_" + username + ".txt", lines);
    }

    public static List<Document> loadDocuments(String username) {
        List<Document> docs = new ArrayList<>();
        for (String line : readLines(DATA_DIR + "vault_" + username + ".txt")) {
            Document d = parseDocument(line);
            if (d != null) docs.add(d);
        }
        return docs;
    }

    private static Document parseDocument(String line) {
        String[] p = line.split("\\|", 11);
        if (p.length < 11) return null;
        try {
            LocalDate issue  = p[4].equals("N/A") ? null : LocalDate.parse(p[4], Document.DATE_FORMAT);
            LocalDate expiry = p[5].equals("N/A") ? null : LocalDate.parse(p[5], Document.DATE_FORMAT);
            LocalDate added  = LocalDate.parse(p[6], Document.DATE_FORMAT);
            int version      = Integer.parseInt(p[9]);
            String type      = p[10];
            switch (type) {
                case "Identity":  return new IdentityDocument(p[0],p[1],p[2],issue,expiry,added,p[7],p[8],version);
                case "Medical":   return new MedicalDocument(p[0],p[1],p[2],issue,expiry,added,p[7],p[8],version);
                case "Financial": return new FinancialDocument(p[0],p[1],p[2],issue,expiry,added,p[7],p[8],version);
                case "Education": return new EducationDocument(p[0],p[1],p[2],issue,expiry,added,p[7],p[8],version);
                case "Property":  return new PropertyDocument(p[0],p[1],p[2],issue,expiry,added,p[7],p[8],version);
                default:          return new IdentityDocument(p[0],p[1],p[2],issue,expiry,added,p[7],p[8],version);
            }
        } catch (Exception e) { return null; }
    }

    // ─── Version History ────────────────────────────────────
    public static void saveVersionSnapshot(String username, VersionedDocument vd) {
        String path = DATA_DIR + "history/history_" + username + ".txt";
        List<String> lines = readLines(path);
        lines.add(vd.toFileString());
        writeLines(path, lines);
    }

    public static List<VersionedDocument> loadVersionHistory(String username) {
        List<VersionedDocument> list = new ArrayList<>();
        for (String line : readLines(DATA_DIR + "history/history_" + username + ".txt")) {
            VersionedDocument vd = VersionedDocument.fromFileString(line);
            if (vd != null) list.add(vd);
        }
        return list;
    }

    // ─── Reminders ───────────────────────────────────────────
    public static void saveReminders(String username, List<Reminder> reminders) {
        List<String> lines = new ArrayList<>();
        for (Reminder r : reminders) lines.add(r.toFileString());
        writeLines(DATA_DIR + "reminders/reminders_" + username + ".txt", lines);
    }

    public static List<Reminder> loadReminders(String username) {
        List<Reminder> list = new ArrayList<>();
        for (String line : readLines(DATA_DIR + "reminders/reminders_" + username + ".txt")) {
            Reminder r = Reminder.fromFileString(line);
            if (r != null) list.add(r);
        }
        return list;
    }

    // ─── Audit Log ───────────────────────────────────────────
    public static void appendAuditLog(String entry) {
        String path = DATA_DIR + "audit_log.txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, true))) {
            pw.println(entry);
        } catch (IOException ignored) {}
    }

    public static List<String> loadAuditLog() {
        return readLines(DATA_DIR + "audit_log.txt");
    }

    // ─── Family Network ──────────────────────────────────────
    public static void saveFamilyLink(String user1, String user2) {
        String path = DATA_DIR + "family_network.txt";
        List<String> lines = readLines(path);
        String entry = user1 + "<->" + user2;
        String entryRev = user2 + "<->" + user1;
        for (String l : lines) {
            if (l.equals(entry) || l.equals(entryRev)) return;
        }
        lines.add(entry);
        writeLines(path, lines);
    }

    public static List<String> getFamilyMembers(String username) {
        List<String> members = new ArrayList<>();
        for (String line : readLines(DATA_DIR + "family_network.txt")) {
            String[] parts = line.split("<->");
            if (parts.length == 2) {
                if (parts[0].equals(username)) members.add(parts[1]);
                else if (parts[1].equals(username)) members.add(parts[0]);
            }
        }
        return members;
    }

    // ─── Backup ──────────────────────────────────────────────
    public static void createBackup(String username, List<Document> docs) {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String path = DATA_DIR + "backups/vault_backup_" + username + "_" + timestamp + ".txt";
        List<String> lines = new ArrayList<>();
        lines.add("# Backup created: " + timestamp);
        for (Document d : docs) lines.add(d.toFileString());
        writeLines(path, lines);
    }

    // ─── Helpers ─────────────────────────────────────────────
    private static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("#")) lines.add(line.trim());
            }
        } catch (IOException ignored) {}
        return lines;
    }

    private static void writeLines(String path, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, false))) {
            for (String l : lines) pw.println(l);
        } catch (IOException ignored) {}
    }
}
